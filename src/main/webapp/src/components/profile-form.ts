import axios from "axios"
import {Options, Vue} from "vue-class-component"
import {ApiError} from "@/models/api-error"
import {Cacheable} from "@/decorators/cacheable.decorator"

@Options({})
export default class ProfileForm extends Vue {
    profile: Profile = {
        name: '',
        sectors: [],
        agreeToTerms: undefined,
    }
    sectors: Sector[] = []
    showAlert = false
    alertMessage = ''
    alertType = ''
    formSubmitted = false

    get atLeastOneSectorSelected() {
        return this.profile.sectors.length > 0
    }

    get indentedSectors() {
        return this.sectors.map(sector => {
            if (typeof sector.level === 'number') {
                return {
                    ...sector,
                    name: '\u00A0'.repeat(sector.level * 3) + sector.name
                }
            }
            return sector
        })
    }

    created() {
        this.fetchSectors().then((sectors) => {
            this.sectors = sectors
        })
        this.getProfile().catch(error => {
            console.error('Failed to handle profile:', error)
        })
    }

    @Cacheable('sectors')
    async fetchSectors() {
        try {
            const response = await axios.get('/api/sectors')
            return response.data
        } catch (error) {
            this.showAlert = true
            this.alertMessage = "Failed to load sectors. Please try again."
            this.alertType = "error"
        }
    }

    async getProfile() {
        await axios.get(`/api/profiles`)
            .then(response => {
                if (response.status === 200) {
                    this.profile = response.data
                }
            })
            .catch(error => {
                if (error.response && error.response.status === 404) {
                    console.log('Profile not found, ignoring.')
                } else {
                    // Log other errors
                    console.error('Failed to fetch profile:', error)
                }
            })
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
            await axios.post('/api/profiles', this.profile)
            this.alertType = 'success'
            this.alertMessage = "Profile saved successfully"
            this.showAlert = true
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
                    this.alertMessage += ": " + Object.entries(apiError.validationErrors)
                        .map(([field, message]) => `${message}`).join(", ")
                }
            } else {
                this.alertMessage = "An unexpected error occurred. Please try again."
            }
            this.alertType = 'error'
            this.showAlert = true
        } finally {
            setTimeout(() => {
                this.showAlert = false
            }, 5000)
            this.formSubmitted = false
        }
    }
}
