<template>
  <div class="userinformation">
    <br />
    <div class="header p-text-center p-d-block">User Information</div>
    <br />
    <div class="content">
      <div class="p-shadow-1">
        <Toolbar>
          <template #left>
            <Button
              icon="pi pi-user-edit"
              label="Update"
              @click="update()"
              :disabled="!isValid || !isDifferent"
            />
          </template>
          <template #right>
            <Button
              icon="pi pi-undo"
              label="Restore"
              @click="restore()"
              :disabled="!isDifferent"
              class="p-button-warning"
            />
          </template>
        </Toolbar>
        <DataTable
          :value="tableData"
          class="editable-cells-table"
          @cell-edit-complete="onCellEditComplete"
          editMode="cell"
        >
          <Column field="field" header="Field">
            <template #body="slotProps">
              <div :class="{ 'p-error': hasError(slotProps.data.field) }">
                {{ slotProps.data.field }}
              </div>
            </template>
          </Column>
          <Column field="value" header="Value">
            <template #editor="slotProps">
              <InputText
                v-model.trim="slotProps.data[slotProps.column.props.field]"
              />
            </template>
            <template #body="slotProps">
              <div :class="{ modified: hasDifference(slotProps.data.field) }">
                {{ slotProps.data.value }}
              </div>
            </template>
          </Column>
        </DataTable>
      </div>
    </div>
  </div>
</template>

<script>
import { mapState } from "vuex";

import DataTable from "primevue/datatable";
import Column from "primevue/column";
import InputText from "primevue/inputtext";
import Toolbar from "primevue/toolbar";
import Button from "primevue/button";

export default {
  name: "UserInformation",
  components: {
    DataTable,
    Column,
    InputText,
    Toolbar,
    Button,
  },
  data() {
    return {
      name: "",
      lastname: "",
      email: "",
      address: "",
      workphone: "",
      mobilephone: "",
    };
  },
  computed: {
    ...mapState("session", { session: (state) => state.session }),
    ...mapState("user", { data: (state) => state.get }),
    tableData() {
      return [
        { field: "Name", value: this.name },
        { field: "Lastname", value: this.lastname },
        { field: "Email", value: this.email },
        { field: "Address", value: this.address },
        { field: "Workphone", value: this.workphone },
        { field: "Mobilephone", value: this.mobilephone },
      ];
    },
    isNameValid() {
      return [this.name.length > 0].every((e) => e);
    },
    isNameDifferent() {
      return this.name != this.data?.name;
    },
    isLastnameValid() {
      return [this.lastname.length > 0].every((e) => e);
    },
    isLastnameDifferent() {
      return this.lastname != this.data?.lastname;
    },
    isEmailValid() {
      return [this.email.length > 0].every((e) => e);
    },
    isEmailDifferent() {
      return this.email != this.data?.email;
    },
    isAddressValid() {
      return [this.address.length > 0].every((e) => e);
    },
    isAddressDifferent() {
      return this.address != this.data?.address;
    },
    isWorkphoneValid() {
      return [this.workphone.length > 0].every((e) => e);
    },
    isWorkphoneDifferent() {
      return this.workphone != this.data?.workphone;
    },
    isMobilephoneValid() {
      return [this.mobilephone.length > 0].every((e) => e);
    },
    isMobilephoneDifferent() {
      return this.mobilephone != this.data?.mobilephone;
    },
    isValid() {
      return [
        this.isNameValid,
        this.isLastnameValid,
        this.isEmailValid,
        this.isAddressValid,
        this.isWorkphoneValid,
        this.isMobilephoneValid,
      ].every((e) => e);
    },
    isDifferent() {
      return [
        this.isNameDifferent,
        this.isLastnameDifferent,
        this.isEmailDifferent,
        this.isAddressDifferent,
        this.isWorkphoneDifferent,
        this.isMobilephoneDifferent,
      ].some((e) => e);
    },
  },
  watch: {
    data(newValue) {
      this.name = newValue.name;
      this.lastname = newValue.lastname;
      this.email = newValue.email;
      this.address = newValue.address;
      this.workphone = newValue.workphone;
      this.mobilephone = newValue.mobilephone;
    },
    session() {
      if (!this.session) {
        this.$router.push({ name: "Home" });
      }
    },
  },
  methods: {
    onCellEditComplete(event) {
      switch (event.index) {
        case 0:
          this.name = event.data.value;
          break;
        case 1:
          this.lastname = event.data.value;
          break;
        case 2:
          this.email = event.data.value;
          break;
        case 3:
          this.address = event.data.value;
          break;
        case 4:
          this.workphone = event.data.value;
          break;
        case 5:
          this.mobilephone = event.data.value;
          break;
      }
    },
    hasError(field) {
      return !this[`is${field}Valid`];
    },
    hasDifference(field) {
      return this[`is${field}Different`];
    },
    update() {
      this.$confirm.require({
        message: "Are you sure you want to update your information?",
        header: "Are you sure?",
        icon: "pi pi-user-edit",
        accept: () => {
          this.$store.dispatch("user/sendMessage", {
            action: "UPDATE",
            token: this.$store.state.session.session.token,
            name: this.name,
            lastname: this.lastname,
            email: this.email,
            address: this.address,
            workphone: this.workphone,
            mobilephone: this.mobilephone,
          });
        },
      });
    },
    restore() {
      this.name = this.data.name;
      this.lastname = this.data.lastname;
      this.email = this.data.email;
      this.address = this.data.address;
      this.workphone = this.data.workphone;
      this.mobilephone = this.data.mobilephone;
    },
  },
  created() {
    if (!this.session) {
      this.$router.push({ name: "Home" });
    }
  },
  mounted() {
    if (this.session) {
      this.$store.dispatch("user/sendMessage", {
        action: "GET",
        token: this.$store.state.session.session.token,
      });
    }
  },
};
</script>

<style scoped>
.p-inputtext {
  width: 100%;
}
.content {
  display: flex;
  flex-direction: column;
  align-items: center;
}
.content > div {
  max-width: 50vw;
}
.modified {
  color: #63b787;
  font-weight: 500;
}
</style>
