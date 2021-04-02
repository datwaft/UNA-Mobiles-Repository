<template>
  <Dialog
    header="Please, enter your credentials"
    v-model:visible="isVisible"
    :style="{ width: '50vw' }"
  >
    <br />
    <div class="p-fluid">
      <div class="p-field">
        <div class="p-inputgroup">
          <span class="p-inputgroup-addon">
            <i class="pi pi-user" />
          </span>
          <span class="p-float-label">
            <InputText
              id="username"
              v-model.trim="username"
              :class="{ 'p-invalid': !isUsernameValid }"
            />
            <label for="username">Username</label>
          </span>
        </div>
        <small id="username-help" class="p-error" v-if="!isUsernameValid">
          <template v-for="help of usernameHelp">
            {{ help }}
          </template>
        </small>
      </div>
      <br />
      <div class="p-field">
        <div class="p-inputgroup">
          <span class="p-inputgroup-addon">
            <i class="pi pi-key" />
          </span>
          <span class="p-float-label">
            <Password
              id="password"
              v-model.trim="password"
              :feedback="false"
              :class="{ 'p-invalid': !isPasswordValid }"
            />
            <label for="password">Password</label>
          </span>
        </div>
        <small id="password-help" class="p-error" v-if="!isPasswordValid">
          <template v-for="help of passwordHelp">
            {{ help }}
          </template>
        </small>
      </div>
    </div>

    <template #footer>
      <Button
        icon="pi pi-sign-in"
        label="Login"
        @click="login()"
        :disabled="!isValid"
      />
      <Button
        icon="pi pi-times-circle"
        label="Cancel"
        @click="isVisible = false"
      />
    </template>
  </Dialog>
</template>

<script>
import Dialog from "primevue/dialog";
import InputText from "primevue/inputtext";
import Password from "primevue/password";
import Button from "primevue/button";

export default {
  name: "Login",
  components: {
    Dialog,
    InputText,
    Password,
    Button,
  },
  props: {
    visible: Boolean,
  },
  emits: ["update:visible"],
  data() {
    return {
      username: "",
      password: "",
    };
  },
  computed: {
    isVisible: {
      get() {
        return this.visible;
      },
      set(value) {
        this.$emit("update:visible", value);
      },
    },
    isUsernameValid() {
      return [this.username.length > 0].every((e) => e);
    },
    usernameHelp() {
      return [
        this.username.length > 0
          ? null
          : "Username must have at least 1 non-blank character",
      ].filter((e) => e != null);
    },
    isPasswordValid() {
      return [this.password.length > 0].every((e) => e);
    },
    passwordHelp() {
      return [
        this.password.length > 0
          ? null
          : "Password must have at least 1 non-blank character",
      ].filter((e) => e != null);
    },
    isValid() {
      return [this.isUsernameValid, this.isPasswordValid].every((e) => e);
    },
  },
  methods: {
    login() {
      this.$store.dispatch("session/sendMessage", {
        action: "LOGIN",
        username: this.username,
        password: this.password,
      });
    },
  },
};
</script>
