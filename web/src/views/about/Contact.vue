<template>
  <div class="contact-information window">
    <div class="header p-text-center p-d-block">Contact Information</div>
    <Fieldset legend="Email" :toggleable="true">
      <a :href="'mailto:' + email">
        {{ email }}
      </a>
    </Fieldset>
    <Fieldset legend="Address" :toggleable="true">
      {{ address }}
    </Fieldset>
  </div>
</template>

<script>
import Fieldset from "primevue/fieldset";

export default {
  name: "Contact",
  components: {
    Fieldset,
  },
  data() {
    return {
      email: "david.guevara.sanchez@est.una.ac.cr",
      address:
        "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Sed tincidunt quam et facilisis pellentesque. Donec.",
    };
  },
};
</script>

<style scoped>
.p-fieldset {
  margin-bottom: 1.5rem;
  margin-top: 0.5rem;
}
</style>
