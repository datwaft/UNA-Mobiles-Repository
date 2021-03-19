<template>
  <div class="flights">
    <DataTable class="p-datatable-sm" :value="data" :paginator="true" :rows="10"
        paginatorTemplate="CurrentPageReport FirstPageLink PrevPageLink PageLinks NextPageLink LastPageLink RowsPerPageDropdown"
        :rowsPerPageOptions="[10,20,50]"
        currentPageReportTemplate="Showing {first} to {last} of {totalRecords}"
        stateStorage="session" stateKey="dt-state-flights"
        responsiveLayout="scroll">
      <Column field="origin" header="Origin" :sortable="true" />
      <Column field="destination" header="Destination" :sortable="true" />
      <Column field="outboundDate" header="Outbound Date" :sortable="true" />
      <Column field="inboundDate" header="Inbound Date" :sortable="true" />
      <Column field="passengers" header="Passengers" :sortable="true" />
    </DataTable>
  </div>
</template>

<script lang="ts">
import DataTable from 'primevue/datatable';
import Column from 'primevue/column';

import { getAllFlights } from '@/server.ts'

export default {
  name: 'Flights',
  components: {
    DataTable,
    Column,
  },
  data() {
    return {
      flights: [],
    }
  },
  methods: {
    formatDate(value) {
      return value.toISOString().slice(0, -14)
    }
  },
  computed: {
    data() {
      const result: object[] = []
      if (this.flights == null) return result
      for (const o of this.flights) {
        result.push({
          origin: o.origin,
          destination: o.destination,
          outboundDate: this.formatDate(o.outboundDate),
          inboundDate: this.formatDate(o.inboundDate),
          passengers: o.passengerAmount + " of " + o.passengerTotal,
        })
      }
      return result
    }
  },
  async mounted() {
    this.flights = await getAllFlights()
  },
}
</script>

