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

<script>
import DataTable from 'primevue/datatable';
import Column from 'primevue/column';

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
  computed: {
    data() {
      const result = []
      if (this.flights == null) return result
      for (const o of this.flights) {
        result.push({
          origin: o.origin,
          destination: o.destination,
          outboundDate: o.outbound_date,
          inboundDate: o.inbound_date ?? 'N/A',
          passengers: `${o.passenger_amount} of ${o.passenger_total}`,
        })
      }
      return result
    }
  },
  async mounted() {
    // Open websocket
    let ws = new WebSocket("ws://localhost:8099/server/flight")
    ws.onopen = () => ws.send(JSON.stringify({ action: "GET_ALL" }))
    ws.onmessage = (event) => {
      let data = JSON.parse(event.data)
      switch(data.action) {
        case "GET_ALL":
          this.flights = data.value
          break
      }
    }
  },
}
</script>

