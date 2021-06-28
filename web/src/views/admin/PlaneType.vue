/* eslint-disable prettier/prettier */
<template>
  <PlaneTypeModal v-model:visible="showModal" :selected="selected" :mode="mode" />
  <div class="plane-type">
    <br />
    <div class="header p-text-center p-d-block">Plane Types</div>
    <br />
    <Toolbar>
      <template #left>
        <Button icon="pi pi-plus" label="Create" class="p-button-success" @click="create()" />&nbsp;
        <Button
          icon="pi pi-pencil"
          label="Edit"
          class="p-button-warning"
          :disabled="!canUpdate"
          @click="update()"
        />&nbsp;
        <Button
          icon="pi pi-exclamation-triangle"
          label="Delete"
          class="p-button-danger"
          :disabled="!canUpdate"
          @click="remove()"
        />
      </template>
    </Toolbar>
    <DataTable
      :value="data"
      :paginator="true"
      :rows="10"
      paginatorTemplate="CurrentPageReport FirstPageLink PrevPageLink PageLinks NextPageLink LastPageLink RowsPerPageDropdown"
      :rowsPerPageOptions="[10, 20, 50]"
      currentPageReportTemplate="Showing {first} to {last} of {totalRecords}"
      responsiveLayout="scroll"
      v-model:selection="selected"
      data-key="identifier"
    >
      <Column selectionMode="single" headerStyle="width: 3em" />
      <Column field="year" header="Year" :sortable="true" />
      <Column field="model" header="Model" :sortable="true" />
      <Column field="brand" header="Brand" :sortable="true" />
      <Column field="rows" header="Rows" :sortable="true" />
      <Column field="columns" header="Columns" :sortable="true" />
    </DataTable>
  </div>
</template>

<script>
import { mapState } from "vuex";

import Toolbar from "primevue/toolbar";
import Button from "primevue/button";
import DataTable from "primevue/datatable";
import Column from "primevue/column";

import PlaneTypeModal from "@/components/admin/PlaneTypeModal";

export default {
  name: "PlaneType",
  components: {
    Toolbar,
    Button,
    DataTable,
    Column,
    PlaneTypeModal,
  },
  data() {
    return {
      selected: null,
      showModal: false,
      mode: "create",
    };
  },
  computed: {
    ...mapState("session", { session: (state) => state.session }),
    ...mapState("planeType", { data: (state) => state.data }),
    canUpdate() {
      return !!this.selected;
    },
  },
  watch: {
    session() {
      if (!this.session || this.session.authorization !== "admin") {
        this.$router.push({ name: "Home" });
      }
    },
    data() {
      this.selected = null;
      this.showModal = false;
    },
  },
  methods: {
    create() {
      this.mode = "create";
      this.showModal = true;
    },
    update() {
      this.mode = "update";
      this.showModal = true;
    },
    remove() {
      this.$confirm.require({
        message: "Are you sure you want to proceed?",
        header: "Confirmation",
        icon: "pi pi-exclamation-triangle",
        acceptClass: "p-button-danger",
        accept: () => {
          this.$store.dispatch("planeType/sendMessage", {
            action: "DELETE",
            identifier: this.selected?.identifier,
            token: this.$store.state.session.session.token,
          });
        },
        reject: () => {},
      });
    },
  },
  created() {
    if (!this.session || this.session.authorization !== "admin") {
      this.$router.push({ name: "Home" });
    }
  },
  mounted() {
    if (this.session) {
      this.$store.dispatch("planeType/sendMessage", {
        action: "GET_ALL",
        token: this.$store.state.session.session.token,
      });
    }
  },
};
</script>
