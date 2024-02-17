import axios from 'axios';
import {Options, Vue} from 'vue-class-component';
import {ApiError} from "@/models/api-error";

@Options({})
export default class ProfileForm extends Vue {
    profile = {
        name: '',
        sectors: [] as number[],
        agreeToTerms: null,
    };

    sectors: Array<{ id: number; name: string; children?: Array<{}> }> = [];
    showAlert = false;
    alertMessage = '';
    alertType = '';

    created() {
        this.fetchSectors();
    }

    async fetchSectors() {
        try {
            const response = await axios.get('/api/sectors');
            this.sectors = response.data;
        } catch (error) {
            console.error("Could not fetch sectors", error);
        }
    }

    async submitForm() {
        const payload = {
            name: this.profile.name,
            sectors: this.profile.sectors,
            agreeToTerms: this.profile.agreeToTerms,
        };

        try {
            await axios.post('/api/profile', payload);
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
                    this.alertMessage = `${apiError.message}: `;
                    const validationMessages = Object.entries(apiError.validationErrors)
                        .map(([field, message]) => `${message}`).join(", ");
                    this.alertMessage += validationMessages;
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
