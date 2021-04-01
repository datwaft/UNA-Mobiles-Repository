<template>
  <div class="flights">
    <div class="header p-text-center p-d-block">
      Flights
    </div>
    <br>
    <Message v-for="message of messages" :severity="message?.severity" :key="message?.content" :closable="message?.closable">
      {{ message?.content }}
    </Message>
    <Toolbar v-if="user != null">
      <template #left>
        <Button icon="pi pi-credit-card" label="Buy"
            :disabled="selectedFlight == null"/>
      </template>
    </Toolbar>
    <DataTable :value="data" :paginator="true" :rows="10"
        paginatorTemplate="CurrentPageReport FirstPageLink PrevPageLink PageLinks NextPageLink LastPageLink RowsPerPageDropdown"
        :rowsPerPageOptions="[10,20,50]"
        currentPageReportTemplate="Showing {first} to {last} of {totalRecords}"
        stateStorage="session" stateKey="dt-state-flights" responsiveLayout="scroll"
        v-model:selection="selectedFlight" dataKey="identifier"
        v-if="user != null">
      <Column selectionMode="single" headerStyle="width: 3em" />
      <Column field="origin" header="Origin" :sortable="true" />
      <Column field="destination" header="Destination" :sortable="true" />
      <Column field="outboundDate" header="Outbound Date" :sortable="true" />
      <Column field="inboundDate" header="Inbound Date" :sortable="true" />
      <Column field="passengers" header="Passengers" :sortable="true" />
    </DataTable>
    <DataTable :value="data" :paginator="true" :rows="10"
        paginatorTemplate="CurrentPageReport FirstPageLink PrevPageLink PageLinks NextPageLink LastPageLink RowsPerPageDropdown"
        :rowsPerPageOptions="[10,20,50]"
        currentPageReportTemplate="Showing {first} to {last} of {totalRecords}"
        stateStorage="session" stateKey="dt-state-flights" responsiveLayout="scroll"
        v-else>
      <Column field="origin" header="Origin" :sortable="true" />
      <Column field="destination" header="Destination" :sortable="true" />
      <Column field="outboundDate" header="Outbound Date" :sortable="true" />
      <Column field="inboundDate" header="Inbound Date" :sortable="true" />
      <Column field="passengers" header="Passengers" :sortable="true" />
    </DataTable>
  </div>
</template>

<script>
import Message from 'primevue/message';
import DataTable from 'primevue/datatable';
import Column from 'primevue/column';
import Toolbar from 'primevue/toolbar';
import Button from 'primevue/button';

export default {
  name: 'Flights',
  components: {
    Message,
    DataTable,
    Column,
    Toolbar,
    Button,
  },
  props: {
    user: Object,
  },
  data() {
    return {
      flights: [],
      messages: [],
      selectedFlight: null,
    }
  },
  computed: {
    data() {
      const result = []
      if (this.flights == null) return result
      for (const o of this.flights) {
        result.push({
          ...o,
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
      switch (data.action) {
        case "GET_ALL":
          this.flights = data.value
          break
      }
    }
    ws.onerror = () => {
      this.messages = [...this.messages, {
        content: "Error: Couldn't connect to server",
        severity: "error",
        closable: true,
      }]
    }
  },
}
</script>
