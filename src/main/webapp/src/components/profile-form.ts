import axios from 'axios'
import {Vue} from 'vue-class-component'
import {ApiError} from '@/models/api-error'
import {Profile} from '@/models/profile';
import {AlertType} from "@/models/alert-type";
import {ApiService} from "@/services/api-service";

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

    get atLeastOneSectorSelected() {
        return this.profile.sectors.length > 0
    }

    created() {
        this.fetchSectors().then((sectors) => {
            this.sectors = sectors
            const addSectorToMap = (sector: Sector) => {
                this.sectorMap.set(sector.id, sector)
                if (sector.children) {
                    sector.children.forEach(addSectorToMap);
                }
            };
            this.sectors.forEach(addSectorToMap);

            const processSectors = (sectors: Sector[], level = 0) => {
                const result: Sector[] = [];
                sectors.forEach((sector) => {
                    result.push({
                        ...sector,
                        name: '\u00A0'.repeat(level * 3) + sector.name,
                    });
                    if (sector.children && sector.children.length > 0) {
                        result.push(...processSectors(sector.children, level + 1));
                    }
                });
                return result;
            };
            this.indentedSectors = processSectors(this.sectors);
        })
        this.getProfile().catch(error => {
            console.error('Failed to handle profile:', error)
        })
    }

    async fetchSectors() {
        try {
            const {data} = await this.apiService.fetchSectors();
            return data;
        } catch (error) {
            this.showAlert = true
            this.alertMessage = 'Failed to load sectors. Please try again.'
            this.alertType = AlertType.ERROR
        }
    }

    async getProfile() {
        this.apiService.getProfile()
            .then(response => {
                if (response.status === 200) {
                    this.profile = response.data
                }
            })
            .catch(error => {
                if (error.response && error.response.status === 403) {
                    console.log('Profile not found, ignoring.')
                }
            })
    }

    toggleSector(sectorId: number) {
        const addChildren = (sector: Sector) => {
            this.profile.sectors.push(sector.id)
            sector.children.forEach(addChildren)
        }
        const sector = this.sectorMap.get(sectorId);
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
        try {
            this.apiService.submitProfile(this.profile).then(response => {
                this.alertType = AlertType.SUCCESS
                this.alertMessage = response.status == 201 ? 'Profile saved successfully' : 'Profile updated'
                this.showAlert = true
            })
        } catch (error) {
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
                        .map(([field, message]) => `${message}`).join(', ')
                }
            } else {
                this.alertMessage = 'An unexpected error occurred. Please try again.'
            }
            this.alertType = AlertType.ERROR
            this.showAlert = true
        } finally {
            setTimeout(() => {
                this.showAlert = false
                this.alertType = null
            }, 5000)
        }
    }
}
