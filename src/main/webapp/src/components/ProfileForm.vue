<template>
  <div class="container mt-3">
    <div class="row justify-content-center">
      <div class="col-md-6">
        <form @submit.prevent="submitForm">
          <div class="mb-3">
            <label class="form-label" for="name">Name:</label>
            <input id="name" v-model="profile.name" class="form-control" type="text">
          </div>
          <div class="mb-3">
            <label class="form-label" for="sectors">Sectors:</label>
            <select id="sectors" v-model="profile.sectors" class="form-select" multiple>
              <option v-for="sector in sectors" :key="sector.id" :value="sector.id">{{ sector.name }}</option>
            </select>
          </div>
          <div class="mb-3 form-check">
            <input id="terms" v-model="profile.agreeToTerms" class="form-check-input" type="checkbox">
            <label class="form-check-label" for="terms">Agree to Terms</label>
          </div>
          <button class="btn btn-primary" type="submit">Submit</button>
        </form>
      </div>
    </div>
  </div>
</template>

<script lang="ts">
import axios from 'axios';
import {Options, Vue} from 'vue-class-component';

@Options({})
export default class ProfileForm extends Vue {
  profile = {
    name: '',
    sectors: [] as number[],
    agreeToTerms: false,
  };

  sectors: Array<{ id: number; name: string; children?: Array<{}> }> = [];

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
    console.log(this.profile);
  }
}
</script>
