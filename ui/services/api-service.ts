import axios from 'axios'
import { Profile } from '../models/profile'

export class ApiService {
    async fetchSectors() {
        const response = await axios.get('/api/sectors').catch(error => {
            if (axios.isAxiosError(error)) {
                throw new Error(`Error fetching sectors: ${error.message}`)
            }
            throw error
        })
        return response.data
    }

    async getProfile() {
        const response = await axios.get('/api/profiles').catch(error => {
            if (axios.isAxiosError(error)) {
                throw new Error(`Error fetching profile: ${error.message}`)
            }
            throw error
        })
        return response.data
    }

    async submitProfile(profile: Profile) {
        return await axios.post('/api/profiles', profile).catch(error => {
            if (axios.isAxiosError(error)) {
                throw new Error(`Error submitting profile: ${error.message}`)
            }
            throw error
        })
    }

}
