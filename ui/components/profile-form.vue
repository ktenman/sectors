<template>
  <div class="container mt-3">
    <div class="row justify-content-center">
      <div class="col-md-6">
        <form @submit.prevent="submitForm">
          <div class="mb-3">
            <h6>Please enter your name and pick the Sectors you are currently involved in.</h6>
          </div>
          <div class="mb-3">
            <label class="form-label" for="name">Name:</label>
            <input id="name" v-model="profile.name" :maxlength="64" class="form-control" type="text">
            <div v-if="profile.name.length > 30" class="text-danger">Name must not exceed 30 characters.</div>
            <div v-if="formSubmitted && !profile.name.trim()" class="text-danger">Name is required</div>
          </div>
          <div class="mb-3">
            <label class="form-label" for="sectors">Sectors:</label>
            <select id="sectors" v-model="profile.sectors" class="form-select form-control-lg custom-height" multiple
                    @change="toggleSector($event)">
              <option v-for="sector in sectors" :key="sector.id" :value="sector.id">
                {{ sector.name }}
              </option>
            </select>
            <div v-if="formSubmitted && !atLeastOneSectorSelected" class="text-danger">At least one sector must be
              selected.
            </div>
          </div>
          <div class="mb-3 form-check">
            <input id="terms" v-model="profile.agreeToTerms" class="form-check-input" type="checkbox">
            <label class="form-check-label" for="terms">Agree to Terms</label>
            <div v-if="formSubmitted && !profile.agreeToTerms" class="text-danger">You must agree to the terms.</div>
          </div>
          <div class="mb-3">
            <button class="btn btn-primary" type="submit" id="submitFormButton">Submit</button>
          </div>
          <div class="mb-3">
            <div v-if="displayAlert()" id="formAlert" :class="['alert', alertClass()]" role="alert">
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

<style scoped>
.custom-height {
  height: 25vh;
}
</style>
