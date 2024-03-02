import axios, { AxiosError } from 'axios'
import { Profile } from '../models/profile'
import { ApiError } from '../models/api-error'

export class ApiService {
    async fetchSectors() {
        const { data } = await axios.get('/api/sectors').catch(this.handleError)
        return data
    }

    async getProfile() {
        const { data } = await axios.get('/api/profiles').catch(this.handleError)
        return data
    }

    async submitProfile(profile: Profile) {
        const { data } = await axios.post('/api/profiles', profile).catch(this.handleError)
        return data
    }

    private handleError(error: AxiosError<ApiError>): never {
        if (axios.isAxiosError(error) && error.response) {
            throw new ApiError(
                error.response.status,
                error.response.data.message ?? 'Unknown error',
                error.response.data.debugMessage ?? 'An unknown error occurred. Is backend running?',
                error.response.data.validationErrors ?? {}
            )
        }
        throw error
    }
}
