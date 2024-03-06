import {Vue} from 'vue-class-component'
import {Profile} from '../models/profile'
import {Sector} from '../models/sector'
import {AlertType} from '../models/alert-type'
import {ApiService} from '../services/api-service'
import {CacheService} from '../services/cache-service'
import {Cacheable} from '../decorators/cacheable.decorator'
import {ApiError} from '../models/api-error'

export default class ProfileForm extends Vue {
    profile: Profile = new Profile()
    sectors: Sector[] = []
    sectorMap: Map<number, Sector> = new Map()
    alertMessage: string = ''
    alertType: AlertType = AlertType.SUCCESS
    formSubmitted: boolean = false
    apiService: ApiService = new ApiService()
    cacheService: CacheService = new CacheService()

    get AlertType(): typeof AlertType {
        return AlertType
    }

    get atLeastOneSectorSelected() {
        return this.profile.sectors.length > 0
    }

    private get isNameValid() {
        return !!this.profile.name.trim() && this.profile.name.length <= 30
    }

    async created() {
        this.sectors = await this.fetchSectors()
        this.sectorMap = this.createSectorMap(this.sectors)
        this.sectors = this.indentSectors(this.sectors)
        this.profile = await this.getProfile() ?? this.profile
    }

    @Cacheable('profile')
    async getProfile() {
        try {
            return await this.apiService.getProfile()
        } catch (error) {
            this.handleApiError('Failed to load profile. Please try again.', error)
        }
    }

    @Cacheable('sectors')
    async fetchSectors() {
        try {
            return await this.apiService.fetchSectors()
        } catch (error) {
            this.handleApiError('Failed to load sectors. Please try again.', error)
            return []
        }
    }

    toggleSector(event: Event) {
        const target = event.target as HTMLSelectElement
        if (!target.value) return
        const sectorId = parseInt(target.value)
        const addChildren = (sector: Sector) => {
            this.profile.sectors.push(sector.id)
            sector.children.forEach(addChildren)
        }
        const sector = this.sectorMap.get(sectorId)
        sector?.children.forEach(addChildren)
    }

    async submitForm() {
        this.formSubmitted = true
        if (!this.atLeastOneSectorSelected || !this.isNameValid || !this.profile.agreeToTerms) {
            return
        }
        try {
            const response = await this.apiService.submitProfile(this.profile)
            this.displayAlert(response.status === 201 ? 'Profile saved successfully' : 'Profile updated', AlertType.SUCCESS)
            this.cacheService.setItem<Profile>('profile', this.profile)
        } catch (error) {
            this.handleApiError('An unexpected error occurred. Please try again.', error)
        } finally {
            this.resetAlert()
        }
    }

    private createSectorMap(sectors: Sector[]) {
        const sectorMap: Map<number, Sector> = new Map()
        const addSectorToMap = (sector: Sector) => {
            sectorMap.set(sector.id, sector)
            sector.children?.forEach(child => addSectorToMap(child))
        }
        sectors.forEach(addSectorToMap)
        return sectorMap
    }

    private handleApiError(defaultMessage: string, error: any) {
        if (error instanceof ApiError) {
            if (error.status === 403) {
                return
            }
            const message = `${error.message}. ${error.debugMessage}: ${Object.entries(error.validationErrors)
                .map(([, message]) => message).join(', ')}`
            this.displayAlert(message, AlertType.ERROR)
        } else {
            this.displayAlert(defaultMessage, AlertType.ERROR)
        }
    }

    private indentSectors(sectors: Sector[], level = 0): Sector[] {
        let result: Sector[] = []
        sectors.forEach(sector => {
            result.push({
                ...sector,
                name: '\u00A0'.repeat(level * 3) + sector.name,
            })
            if (sector.children && sector.children.length > 0) {
                result = result.concat(this.indentSectors(sector.children, level + 1))
            }
        })
        return result
    }

    private displayAlert(message: string, type: AlertType) {
        this.alertMessage = message
        this.alertType = type
    }

    private resetAlert() {
        setTimeout(() => {
            this.alertType = AlertType.SUCCESS
            this.alertMessage = ''
        }, 4000)
    }

}
