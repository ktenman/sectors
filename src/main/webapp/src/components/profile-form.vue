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
          <div class="mb-3">
            <button class="btn btn-primary" type="submit">Submit</button>
          </div>
          <div class="mb-3">
            <div v-if="showAlert" :class="['alert', alertType === 'success' ? 'alert-success' : 'alert-danger']"
                 role="alert">
              {{ alertMessage }}
            </div>
          </div>
        </form>
      </div>
    </div>
  </div>
</template>

<script lang="ts" src="./profile-form.ts">
</script>
