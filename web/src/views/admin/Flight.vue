<template>
  <FlightModal
    v-model:visible="showModal"
    :selected="selected"
    :mode="mode"
    :planes="planes"
    :schedules="schedules"
  />
  <div class="flight">
    <br />
    <div class="header p-text-center p-d-block">Flights</div>
    <br />
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
      <Column field="planeName" header="Plane" :sortable="true" />
      <Column field="outboundDate" header="Outbound Date" :sortable="true" />
      <Column
        field="outboundScheduleName"
        header="Outbound Schedule"
        :sortable="true"
      />
      <Column field="inboundDate" header="Inbound Date" :sortable="true" />
      <Column
        field="inboundScheduleName"
        header="Inbound Schedule"
        :sortable="true"
      />
    </DataTable>
  </div>
</template>

<script>
import { mapState } from "vuex";

import Toolbar from "primevue/toolbar";
import Button from "primevue/button";
import DataTable from "primevue/datatable";
import Column from "primevue/column";

import FlightModal from "@/components/admin/FlightModal";

export default {
  name: "Flight",
  components: {
    Toolbar,
    Button,
    DataTable,
    Column,
    FlightModal,
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
    ...mapState("flight", { rawdata: (state) => state.data }),
    ...mapState("plane", { planes: (state) => state.data }),
    ...mapState("schedule", { rawschedules: (state) => state.data }),
    ...mapState("route", { routes: (state) => state.data }),
    canUpdate() {
      return !!this.selected;
    },
    schedules() {
      let result = [];
      if (!this.rawschedules) return result;
      for (let o of this.rawschedules) {
        result.push({
          ...o,
          origin: this.getScheduleOrigin(o),
          destination: this.getScheduleDestination(o),
          name: this.getScheduleName(o),
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
          planeName: this.planes?.filter((e) => e.identifier == o.plane)[0]
            ?.name,
          outboundDate: o.outbound_date,
          outbound_date: o.outbound_date ?? null,
          outboundScheduleName: this.schedules?.filter(
            (e) => e.identifier == o.outbound_schedule
          )[0]?.name,
          outbound_schedule: o.outbound_schedule ?? null,
          inboundDate: o.inbound_date ?? "N/A",
          inbound_date: o.inbound_date ?? null,
          inboundScheduleName:
            this.schedules?.filter((e) => e.identifier == o.inbound_schedule)[0]
              ?.name ?? "N/A",
          inbound_schedule: o.inbound_schedule ?? null,
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
    getScheduleName(schedule) {
      let route = this.routes?.filter((e) => e.identifier == schedule.route)[0];
      return `${route?.origin} - ${route?.destination} on ${schedule?.weekday}s`;
    },
    getScheduleOrigin(schedule) {
      let route = this.routes?.filter((e) => e.identifier == schedule.route)[0];
      return route?.origin;
    },
    getScheduleDestination(schedule) {
      let route = this.routes?.filter((e) => e.identifier == schedule.route)[0];
      return route?.destination;
    },
  },
  created() {
    if (!this.session || this.session.authorization !== "admin") {
      this.$router.push({ name: "Home" });
    }
  },
  mounted() {
    if (this.session) {
      this.$store.dispatch("flight/sendMessage", {
        action: "GET_ALL",
        token: this.$store.state.session.session.token,
      });
      this.$store.dispatch("plane/sendMessage", {
        action: "GET_ALL",
        token: this.$store.state.session.session.token,
      });
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
