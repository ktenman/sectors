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
    indentedSectors: Sector[] = []
    sectorMap: Map<number, Sector> = new Map()
    showAlert = false
    alertMessage = ''
    alertType: AlertType | null = null
    formSubmitted = false
    apiService: ApiService = new ApiService()
    cacheService: CacheService = new CacheService()

    get atLeastOneSectorSelected() {
        return this.profile.sectors.length > 0
    }

    private get isNameValid() {
        return !!this.profile.name.trim() && this.profile.name.length <= 30
    }

    created() {
        this.fetchSectors().then(sectors => {
            this.processSectorsResponse(sectors)
        })
        this.getProfile().then(profile => {
            if (profile) {
                this.profile = profile
            }
        })
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

    @Cacheable('profile')
    async getProfile() {
        try {
            return await this.apiService.getProfile()
        } catch (error) {
            this.handleApiError('Failed to load profile. Please try again.', error)
            return null
        }
    }

    processSectorsResponse(sectors: Sector[]) {
        this.sectors = sectors
        this.sectors.forEach(sector => this.sectorMap.set(sector.id, sector))
        this.indentedSectors = this.indentSectors(this.sectors)
    }

    indentSectors(sectors: Sector[], level = 0): Sector[] {
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

    toggleSector(event: Event) {
        const target = event.target as HTMLSelectElement
        if (!target.value) return
        const sectorId = parseInt(target.value)
        const addChildren = (sector: Sector) => {
            this.profile.sectors.push(sector.id)
            sector.children.forEach(addChildren)
        }
        const sector = this.sectorMap.get(sectorId)
        if (sector) {
            sector.children.forEach(addChildren)
        }
    }

    async submitForm() {
        this.formSubmitted = true
        if (!this.atLeastOneSectorSelected || !this.isNameValid || !this.profile.agreeToTerms) {
            return
        }
        try {
            const response = await this.apiService.submitProfile(this.profile)
            this.alertType = AlertType.SUCCESS
            this.alertMessage = response.status == 201 ? 'Profile saved successfully' : 'Profile updated'
            this.showAlert = true
        } catch (error) {
            this.handleApiError('An unexpected error occurred. Please try again.', error)
        } finally {
            setTimeout(() => {
                this.showAlert = false
                this.alertType = null
            }, 4_000)
            this.cacheService.setItem<Profile>('profile', this.profile)
        }
    }

    handleApiError(defaultMessage: string, error: any) {
        this.showAlert = true
        if (error instanceof ApiError) {
            this.alertMessage = `${error.message}. ${error.debugMessage}`
            if (Object.keys(error.validationErrors).length > 0) {
                this.alertMessage += ': ' + Object.entries(error.validationErrors)
                    .map(([, message]) => message).join(', ')
            }
            this.alertType = AlertType.ERROR
        } else {
            this.alertMessage = defaultMessage
            this.alertType = AlertType.ERROR
        }
    }

}
