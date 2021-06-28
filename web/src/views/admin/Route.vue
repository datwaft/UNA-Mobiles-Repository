<template>
  <RouteModal v-model:visible="showModal" :selected="selected" :mode="mode" />
  <div class="route">
    <br />
    <div class="header p-text-center p-d-block">Routes</div>
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
      <Column field="origin" header="Origin" :sortable="true" />
      <Column field="destination" header="Destination" :sortable="true" />
      <Column field="duration" header="Duration" :sortable="true" />
      <Column field="price" header="Price" :sortable="true" />
    </DataTable>
  </div>
</template>

<script>
import { mapState } from "vuex";

import Toolbar from "primevue/toolbar";
import Button from "primevue/button";
import DataTable from "primevue/datatable";
import Column from "primevue/column";

import RouteModal from "@/components/admin/RouteModal";

export default {
  name: "Route",
  components: {
    Toolbar,
    Button,
    DataTable,
    Column,
    RouteModal,
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
    ...mapState("route", { data: (state) => state.data }),
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
          this.$store.dispatch("route/sendMessage", {
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
      this.$store.dispatch("route/sendMessage", {
        action: "GET_ALL",
        token: this.$store.state.session.session.token,
      });
    }
  },
};
</script>
