<template>
  <div class="top-5-routes-per-ticket-number">
    <div class="header p-text-center p-d-block">Clients per plane</div>
    <br />
    <DataTable :value="data">
      <Column header="#" headerStyle="width:3em">
        <template #body="slotProps">
          {{ slotProps.index + 1 }}
        </template>
      </Column>
      <Column field="name" header="Route" />
      <Column field="value" header="Ticker number" />
    </DataTable>
  </div>
</template>

<script>
import { mapState } from "vuex";

import DataTable from "primevue/datatable";
import Column from "primevue/column";

export default {
  name: "RevenuePerMonthInLastYear",
  components: {
    DataTable,
    Column,
  },
  data() {
    return {
      expanded: null,
    };
  },
  computed: {
    ...mapState("session", { session: (state) => state.session }),
    ...mapState("report", {
      rawdata: (state) => state.top5RoutesPerTicketNumber,
    }),
    data() {
      let data = [];
      if (!this.rawdata) return [];
      for (let { origin, destination, ticket_number } of this.rawdata) {
        data.push({
          name: `${origin} - ${destination}`,
          value: ticket_number,
        });
      }
      return data;
    },
  },
  mounted() {
    if (this.session) {
      this.$store.dispatch("report/sendMessage", {
        action: "TOP_5_ROUTES_PER_TICKET_NUMBER",
        token: this.$store.state.session.session.token,
      });
    }
  },
};
</script>
