<template>
  <Dialog header="Please, enter your information"
      v-model:visible="isVisible"
      :style="{ width: '50vw' }">
    <br>
    <div class="p-fluid">
      <div class="p-field">
        <div class="p-inputgroup">
          <span class="p-inputgroup-addon">
            <i class="pi pi-user" />
          </span>
          <span class="p-float-label">
            <InputText id="username" v-model.trim="username"
                :class="{ 'p-invalid': !isUsernameValid }" />
            <label for="username">Username</label>
          </span>
        </div>
        <small id="username-help" class="p-error" v-if="!isUsernameValid">
          <template v-for="help of usernameHelp">
            {{ help }}
          </template>
        </small>
      </div>
      <br>
      <div class="p-field">
        <div class="p-inputgroup">
          <span class="p-inputgroup-addon">
            <i class="pi pi-key" />
          </span>
          <span class="p-float-label">
            <Password id="password" v-model.trim="password" :feedback="false"
                :class="{ 'p-invalid': !isPasswordValid }" />
            <label for="password">Password</label>
          </span>
        </div>
        <small id="password-help" class="p-error" v-if="!isPasswordValid">
          <template v-for="help of passwordHelp">
            {{ help }}
          </template>
        </small>
      </div>
      <br>
      <div class="p-field">
        <div class="p-inputgroup">
          <span class="p-inputgroup-addon">
            <i class="pi pi-id-card" />
          </span>
          <span class="p-float-label">
            <InputText id="name" v-model.trim="name"
                :class="{ 'p-invalid': !isNameValid }" />
            <label for="name">Name</label>
          </span>
        </div>
        <small id="name-help" class="p-error" v-if="!isNameValid">
          <template v-for="help of nameHelp">
            {{ help }}
          </template>
        </small>
      </div>
      <br>
      <div class="p-field">
        <div class="p-inputgroup">
          <span class="p-inputgroup-addon">
            <i class="pi pi-id-card" />
          </span>
          <span class="p-float-label">
            <InputText id="lastname" v-model.trim="lastname"
                :class="{ 'p-invalid': !isLastnameValid }" />
            <label for="lastname">Lastname</label>
          </span>
        </div>
        <small id="lastname-help" class="p-error" v-if="!isLastnameValid">
          <template v-for="help of lastnameHelp">
            {{ help }}
          </template>
        </small>
      </div>
      <br>
      <div class="p-field">
        <div class="p-inputgroup">
          <span class="p-inputgroup-addon">
            @
          </span>
          <span class="p-float-label">
            <InputText id="email" v-model.trim="email"
                :class="{ 'p-invalid': !isEmailValid }" />
            <label for="email">Email</label>
          </span>
        </div>
        <small id="email-help" class="p-error" v-if="!isEmailValid">
          <template v-for="help of emailHelp">
            {{ help }}
          </template>
        </small>
      </div>
      <br>
      <div class="p-field">
        <div class="p-inputgroup">
          <span class="p-inputgroup-addon">
            <i class="pi pi-directions" />
          </span>
          <span class="p-float-label">
            <InputText id="address" v-model.trim="address"
                :class="{ 'p-invalid': !isAddressValid }" />
            <label for="address">Address</label>
          </span>
        </div>
        <small id="address-help" class="p-error" v-if="!isAddressValid">
          <template v-for="help of addressHelp">
            {{ help }}
          </template>
        </small>
      </div>
      <br>
      <div class="p-field">
        <div class="p-inputgroup">
          <span class="p-inputgroup-addon">
            <i class="pi pi-mobile" />
          </span>
          <span class="p-float-label">
            <InputText id="mobilephone" v-model.trim="mobilephone"
                :class="{ 'p-invalid': !isMobilephoneValid }" />
            <label for="mobilephone">Mobilephone</label>
          </span>
        </div>
        <small id="mobilephone-help" class="p-error" v-if="!isMobilephoneValid">
          <template v-for="help of mobilephoneHelp">
            {{ help }}
          </template>
        </small>
      </div>
      <br>
      <div class="p-field">
        <div class="p-inputgroup">
          <span class="p-inputgroup-addon">
            <i class="pi pi-briefcase" />
          </span>
          <span class="p-float-label">
            <InputText id="workphone" v-model.trim="workphone"
                :class="{ 'p-invalid': !isWorkphoneValid }" />
            <label for="workphone">Workphone</label>
          </span>
        </div>
        <small id="workphone-help" class="p-error" v-if="!isWorkphoneValid">
          <template v-for="help of workphoneHelp">
            {{ help }}
          </template>
        </small>
      </div>
    </div>

    <template #footer>
      <Button icon="pi pi-user-plus" label="Register" @click="register()"
          :disabled="!isValid" />
      <Button icon="pi pi-times-circle" label="Cancel" @click="isVisible = false" />
    </template>
  </Dialog>
</template>

<script>
import Dialog from 'primevue/dialog';
import InputText from 'primevue/inputtext';
import Password from 'primevue/password';
import Button from 'primevue/button';

export default {
  name: 'Register',
  components: {
    Dialog,
    InputText,
    Password,
    Button,
  },
  props: {
    visible: Boolean,
    userSocket: Object,
  },
  emits: [ 'update:visible' ],
  data() {
    return {
      username: "",
      password: "",
      name: "",
      lastname: "",
      email: "",
      address: "",
      workphone: "",
      mobilephone: "",
    }
  },
  computed: {
    isVisible: {
      get() {
        return this.visible
      },
      set(value) {
        this.$emit('update:visible', value)
      }
    },
    isUsernameValid() {
      return [
        this.username.length > 0,
      ].every(e => e)
    },
    usernameHelp() {
      return [
        this.username.length > 0 ? null : 'Username must have at least 1 non-blank character',
      ].filter((e) => e != null)
    },
    isPasswordValid() {
      return [
        this.password.length > 0,
      ].every(e => e)
    },
    passwordHelp() {
      return [
        this.password.length > 0 ? null : 'Password must have at least 1 non-blank character',
      ].filter((e) => e != null)
    },
    isNameValid() {
      return [
        this.name.length > 0,
      ].every(e => e)
    },
    nameHelp() {
      return [
        this.name.length > 0 ? null : 'Name must have at least 1 non-blank character',
      ].filter((e) => e != null)
    },
    isLastnameValid() {
      return [
        this.lastname.length > 0,
      ].every(e => e)
    },
    lastnameHelp() {
      return [
        this.lastname.length > 0 ? null : 'Lastname must have at least 1 non-blank character',
      ].filter((e) => e != null)
    },
    isEmailValid() {
      return [
        this.email.length > 0,
      ].every(e => e)
    },
    emailHelp() {
      return [
        this.email.length > 0 ? null : 'Email must have at least 1 non-blank character',
      ].filter((e) => e != null)
    },
    isAddressValid() {
      return [
        this.address.length > 0,
      ].every(e => e)
    },
    addressHelp() {
      return [
        this.address.length > 0 ? null : 'Address must have at least 1 non-blank character',
      ].filter((e) => e != null)
    },
    isWorkphoneValid() {
      return [
        this.workphone.length > 0,
      ].every(e => e)
    },
    workphoneHelp() {
      return [
        this.workphone.length > 0 ? null : 'Workphone must have at least 1 non-blank character',
      ].filter((e) => e != null)
    },
    isMobilephoneValid() {
      return [
        this.mobilephone.length > 0,
      ].every(e => e)
    },
    mobilephoneHelp() {
      return [
        this.mobilephone.length > 0 ? null : 'Mobilephone must have at least 1 non-blank character',
      ].filter((e) => e != null)
    },
    isValid() {
      return [
        this.isUsernameValid,
        this.isPasswordValid,
        this.isNameValid,
        this.isLastnameValid,
        this.isEmailValid,
        this.isAddressValid,
        this.isWorkphoneValid,
        this.isMobilephoneValid,
      ].every(e => e)
    }
  },
  methods: {
    register() {
      this.userSocket.send(JSON.stringify({
        action: 'REGISTER',
        username: this.username,
        password: this.password,
        name: this.name,
        lastname: this.lastname,
        email: this.email,
        address: this.address,
        workphone: this.workphone,
        mobilephone: this.mobilephone,
      }))
    }
  },
}
</script>
