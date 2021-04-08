<template>
  <PlaneTypeModal
    v-model:visible="showModal"
    :selected="selected"
    :mode="mode"
  />
  <div class="plane-type">
    <Toolbar>
      <template #left>
        <Button
          icon="pi pi-plus"
          label="Create"
          class="p-button-success"
          @click="create()"
        />
        &nbsp;
        <Button
          icon="pi pi-pencil"
          label="Edit"
          class="p-button-warning"
          :disabled="!canUpdate"
          @click="update()"
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
      dataKey="identifier"
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
