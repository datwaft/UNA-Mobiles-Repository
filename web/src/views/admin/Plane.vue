<template>
  <PlaneModal v-model:visible="showModal" :selected="selected" :mode="mode" :types="types" />
  <div class="plane">
    <br />
    <div class="header p-text-center p-d-block">Planes</div>
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
      <Column field="name" header="Name" :sortable="true" />
      <Column field="typeName" header="Plane Type" :sortable="true" />
    </DataTable>
  </div>
</template>

<script>
import { mapState } from "vuex";

import Toolbar from "primevue/toolbar";
import Button from "primevue/button";
import DataTable from "primevue/datatable";
import Column from "primevue/column";

import PlaneModal from "@/components/admin/PlaneModal";

export default {
  name: "Plane",
  components: {
    Toolbar,
    Button,
    DataTable,
    Column,
    PlaneModal,
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
    ...mapState("plane", { rawdata: (state) => state.data }),
    ...mapState("planeType", { rawtypes: (state) => state.data }),
    canUpdate() {
      return !!this.selected;
    },
    types() {
      let result = [];
      if (!this.rawtypes) return result;
      for (let o of this.rawtypes) {
        result.push({
          ...o,
          name: this.getPlaneTypeName(o),
        });
      }
      return result;
    },
    data() {
      let result = [];
      if (!this.rawdata) return result;
      for (let o of this.rawdata) {
        result.push({
          ...o,
          typeName: this.types.filter((e) => e.identifier == o.type)[0]?.name,
        });
      }
      return result;
    },
  },
  watch: {
    session() {
      if (!this.session || this.session.authorization !== "admin") {
        this.$router.push({ name: "Home" });
      }
    },
    rawdata() {
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
          this.$store.dispatch("plane/sendMessage", {
            action: "DELETE",
            identifier: this.selected?.identifier,
            token: this.$store.state.session.session.token,
          });
        },
        reject: () => {},
      });
    },
    getPlaneTypeName(type) {
      return `${type.brand} ${type.model} from ${type.year}`;
    },
  },
  created() {
    if (!this.session || this.session.authorization !== "admin") {
      this.$router.push({ name: "Home" });
    }
  },
  mounted() {
    if (this.session) {
      this.$store.dispatch("plane/sendMessage", {
        action: "GET_ALL",
        token: this.$store.state.session.session.token,
      });
      this.$store.dispatch("planeType/sendMessage", {
        action: "GET_ALL",
        token: this.$store.state.session.session.token,
      });
    }
  },
};
</script>
