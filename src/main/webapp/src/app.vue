<template>
  <ProfileForm/>
</template>

<script lang="ts">
import {Options, Vue} from 'vue-class-component'
import ProfileForm from "./components/profile-form.vue"

@Options({
  components: {
    ProfileForm,
  },
})
export default class App extends Vue {
}
</script>

<style>
#app {
  font-family: Avenir, Helvetica, Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  text-align: left;
  color: #2c3e50;
  margin-top: 60px;
}
</style>
