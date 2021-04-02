<template>
  <div class="flights">
    <div class="header p-text-center p-d-block">Flights</div>
    <br />
    <Toolbar v-if="session">
      <template #left>
        <Button
          icon="pi pi-credit-card"
          label="Buy"
          :disabled="selectedFlight == null"
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
      stateStorage="session"
      stateKey="dt-state-flights"
      responsiveLayout="scroll"
      v-model:selection="selectedFlight"
      dataKey="identifier"
      v-if="session"
    >
      <Column selectionMode="single" headerStyle="width: 3em" />
      <Column field="origin" header="Origin" :sortable="true" />
      <Column field="destination" header="Destination" :sortable="true" />
      <Column field="outboundDate" header="Outbound Date" :sortable="true" />
      <Column field="inboundDate" header="Inbound Date" :sortable="true" />
      <Column field="passengers" header="Passengers" :sortable="true" />
    </DataTable>
    <DataTable
      :value="data"
      :paginator="true"
      :rows="10"
      paginatorTemplate="CurrentPageReport FirstPageLink PrevPageLink PageLinks NextPageLink LastPageLink RowsPerPageDropdown"
      :rowsPerPageOptions="[10, 20, 50]"
      currentPageReportTemplate="Showing {first} to {last} of {totalRecords}"
      stateStorage="session"
      stateKey="dt-state-flights"
      responsiveLayout="scroll"
      v-else
    >
      <Column field="origin" header="Origin" :sortable="true" />
      <Column field="destination" header="Destination" :sortable="true" />
      <Column field="outboundDate" header="Outbound Date" :sortable="true" />
      <Column field="inboundDate" header="Inbound Date" :sortable="true" />
      <Column field="passengers" header="Passengers" :sortable="true" />
    </DataTable>
  </div>
</template>

<script>
import { mapState } from "vuex";

import DataTable from "primevue/datatable";
import Column from "primevue/column";
import Toolbar from "primevue/toolbar";
import Button from "primevue/button";

export default {
  name: "Flights",
  components: {
    DataTable,
    Column,
    Toolbar,
    Button,
  },
  data() {
    return {
      selectedFlight: null,
    };
  },
  computed: {
    ...mapState("flight", { flights: (state) => state.data }),
    ...mapState("session", { session: (state) => state.data }),
    data() {
      const result = [];
      if (!this.flights) return result;
      for (const o of this.flights) {
        result.push({
          ...o,
          outboundDate: o.outbound_date,
          inboundDate: o.inbound_date ?? "N/A",
          passengers: `${o.passenger_amount} of ${o.passenger_total}`,
        });
      }
      return result;
    },
  },
};
</script>
