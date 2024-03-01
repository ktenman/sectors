import axios from 'axios'
import {Profile} from '../models/profile'

export class ApiService {
    async fetchSectors() {
        return await axios.get('/api/sectors')
    }

    async getProfile() {
        return await axios.get('/api/profiles')
    }

    async submitProfile(profile: Profile) {
        return await axios.post('/api/profiles', profile)
    }
}
