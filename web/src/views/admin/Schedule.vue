<template>
  <ScheduleModal v-model:visible="showModal" :selected="selected" :mode="mode" :routes="routes" />
  <div class="schedule">
    <br />
    <div class="header p-text-center p-d-block">Schedules</div>
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
      <Column field="routeName" header="Route" :sortable="true" />
      <Column field="departureTime" header="Departure Time" :sortable="true" />
      <Column field="weekday" header="Weekday" :sortable="true" />
      <Column field="discount" header="Discount" :sortable="true">
        <template #body="slotProps">{{ slotProps.data[slotProps.column.props.field] * 100 }}%</template>
      </Column>
    </DataTable>
  </div>
</template>

<script>
import { mapState } from "vuex";

import Toolbar from "primevue/toolbar";
import Button from "primevue/button";
import DataTable from "primevue/datatable";
import Column from "primevue/column";

import ScheduleModal from "@/components/admin/ScheduleModal";

export default {
  name: "Schedule",
  components: {
    Toolbar,
    Button,
    DataTable,
    Column,
    ScheduleModal,
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
    ...mapState("schedule", { rawdata: (state) => state.data }),
    ...mapState("route", { rawroutes: (state) => state.data }),
    canUpdate() {
      return !!this.selected;
    },
    routes() {
      let result = [];
      if (!this.rawroutes) return result;
      for (let o of this.rawroutes) {
        result.push({
          ...o,
          name: this.getRouteName(o),
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
          routeName: this.routes.filter((e) => e.identifier == o.route)[0]
            ?.name,
          departureTime: o.departure_time,
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
          this.$store.dispatch("schedule/sendMessage", {
            action: "DELETE",
            identifier: this.selected?.identifier,
            token: this.$store.state.session.session.token,
          });
        },
        reject: () => {},
      });
    },
    getRouteName(route) {
      return `${route.origin} - ${route.destination} at $${route.price}`;
    },
  },
  created() {
    if (!this.session || this.session.authorization !== "admin") {
      this.$router.push({ name: "Home" });
    }
  },
  mounted() {
    if (this.session) {
      this.$store.dispatch("schedule/sendMessage", {
        action: "GET_ALL",
        token: this.$store.state.session.session.token,
      });
      this.$store.dispatch("route/sendMessage", {
        action: "GET_ALL",
        token: this.$store.state.session.session.token,
      });
    }
  },
};
</script>
