import axios from 'axios'
import {Vue} from 'vue-class-component'
import {ApiError} from '@/models/api-error'
import {Profile} from '@/models/profile'
import {AlertType} from '@/models/alert-type'
import {ApiService} from '@/services/api-service'
import {Cacheable} from '@/decorators/cacheable.decorator'
import {CacheService} from '@/services/cache-service'
import {Sector} from '@/models/sector'

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

    created() {
        this.fetchSectors().then(sectors => {
            this.sectors = sectors
            const addSectorToMap = (sector: Sector) => {
                this.sectorMap.set(sector.id, sector)
                if (sector.children) {
                    sector.children.forEach(addSectorToMap)
                }
            }
            this.sectors.forEach(addSectorToMap)

            const processSectors = (sectors: Sector[], level = 0) => {
                const result: Sector[] = []
                sectors.forEach((sector) => {
                    result.push({
                        ...sector,
                        name: '\u00A0'.repeat(level * 3) + sector.name,
                    })
                    if (sector.children && sector.children.length > 0) {
                        result.push(...processSectors(sector.children, level + 1))
                    }
                })
                return result
            }
            this.indentedSectors = processSectors(this.sectors)
        })
        this.getProfile().then(profile => {
            if (profile) {
                this.profile = profile
            }
        }).catch(error => {
            console.error('Failed to handle profile:', error)
        })
    }

    @Cacheable('sectors')
    async fetchSectors() {
        try {
            const {data} = await this.apiService.fetchSectors()
            return data
        } catch (error) {
            this.showAlert = true
            this.alertMessage = 'Failed to load sectors. Please try again.'
            this.alertType = AlertType.ERROR
        }
    }

    @Cacheable('profile')
    async getProfile() {
        try {
            const {data} = await this.apiService.getProfile()
            return data
        } catch (error) {
            if (axios.isAxiosError(error) && error.response && error.response.status === 403) {
                console.log('Profile not found, ignoring.')
            } else {
                this.showAlert = true
                this.alertMessage = 'Failed to load profile. Please try again.'
                this.alertType = AlertType.ERROR
            }
        }
    }

    toggleSector(sectorId: number) {
        const addChildren = (sector: Sector) => {
            this.profile.sectors.push(sector.id)
            sector.children.forEach(addChildren)
        }
        const sector = this.sectorMap.get(sectorId)
        if (sector) {
            sector.children.forEach(addChildren)
        }
    }

    private get isNameValid() {
        return !!this.profile.name.trim() && this.profile.name.length <= 30
    }

    async submitForm() {
        this.formSubmitted = true
        if (!this.atLeastOneSectorSelected || !this.isNameValid || !this.profile.agreeToTerms) {
            return
        }
        await this.apiService.submitProfile(this.profile).then(response => {
            this.alertType = AlertType.SUCCESS
            this.alertMessage = response.status == 201 ? 'Profile saved successfully' : 'Profile updated'
            this.showAlert = true
        }).catch(error => {
            if (axios.isAxiosError(error) && error.response) {
                const apiError = new ApiError(
                    error.response.status,
                    error.response.data.message,
                    error.response.data.debugMessage,
                    error.response.data.validationErrors || {}
                )
                this.alertMessage = `${apiError.message}. ${apiError.debugMessage}`
                if (Object.keys(apiError.validationErrors).length > 0) {
                    this.alertMessage += ': ' + Object.entries(apiError.validationErrors)
                        .map(([, message]) => `${message}`).join(', ')
                }
            } else {
                this.alertMessage = 'An unexpected error occurred. Please try again.'
            }
            this.alertType = AlertType.ERROR
            this.showAlert = true
        }).finally(() => {
            setTimeout(() => {
                this.showAlert = false
                this.alertType = null
            }, 5000)
            this.cacheService.setItem<Profile>('profile', this.profile)
        })
    }
}
