import axios from 'axios'
import {Options, Vue} from 'vue-class-component'
import {ApiError} from "@/models/api-error"

@Options({})
export default class ProfileForm extends Vue {
    profile: Profile = {
        name: '',
        sectors: [],
        agreeToTerms: undefined,
    };
    sectors: Sector[] = [];
    showAlert = false;
    alertMessage = '';
    alertType = '';
    formSubmitted = false;

    get indentedSectors() {
        return this.sectors.map(sector => {
            if (typeof sector.level === 'number') {
                return {
                    ...sector,
                    name: '\u00A0'.repeat(sector.level * 2) + sector.name
                }
            }
            return sector
        })
    }

    processAndFlattenSectors(sectors: Sector[], level: number = 0): Sector[] {
        const flatSectors: Sector[] = [];
        sectors.forEach(sector => {
            const flatSector = {...sector, level};
            flatSectors.push(flatSector);

            if (sector.children && sector.children.length > 0) {
                const childFlatSectors = this.processAndFlattenSectors(sector.children, level + 1);
                flatSectors.push(...childFlatSectors);
            }
        });
        return flatSectors;
    }

    async fetchSectors() {
        const response = await axios.get('/api/sectors');
        this.sectors = this.processAndFlattenSectors(response.data)
    }

    created() {
        this.fetchSectors().catch(() => {
            this.showAlert = true;
            this.alertMessage = "Failed to load sectors. Please try again.";
            this.alertType = "error";
        });
    }

    get atLeastOneSectorSelected() {
        return this.profile.sectors.length > 0;
    }

    private get isNameValid() {
        return !!this.profile.name.trim() && this.profile.name.length <= 30
    }

    async submitForm() {
        this.formSubmitted = true;
        if (!this.atLeastOneSectorSelected || !this.isNameValid || !this.profile.agreeToTerms) {
            return;
        }
        try {
            await axios.post('/api/profiles', this.profile)
            this.alertType = 'success';
            this.alertMessage = "Profile saved successfully";
            this.showAlert = true;
        } catch (error) {
            if (axios.isAxiosError(error) && error.response) {
                const apiError = new ApiError(
                    error.response.status,
                    error.response.data.message,
                    error.response.data.debugMessage,
                    error.response.data.validationErrors || {}
                );
                this.alertMessage = `${apiError.message}: ${apiError.debugMessage}`;
                if (Object.keys(apiError.validationErrors).length > 0) {
                    this.alertMessage += Object.entries(apiError.validationErrors)
                        .map(([field, message]) => `${message}`).join(", ");
                }
            } else {
                this.alertMessage = "An unexpected error occurred. Please try again.";
            }
            this.alertType = 'error';
            this.showAlert = true;
        } finally {
            setTimeout(() => {
                this.showAlert = false;
            }, 5000);
        }
    }
}
