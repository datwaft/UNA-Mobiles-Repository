<template>
  <div class="clients-per-plane">
    <div class="header p-text-center p-d-block">Clients per plane</div>
    <br />
    <DataTable
      :value="data"
      rowGroupMode="rowspan"
      groupRowsBy="name"
      sortMode="single"
      sortField="name"
      :sortOrder="1"
    >
      <Column header="#" headerStyle="width:3em">
        <template #body="slotProps">
          {{ slotProps.index + 1 }}
        </template>
      </Column>
      <Column field="name" header="Plane" />
      <Column field="client" header="Client" />
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
      data: (state) => state.clientsPerPlane,
    }),
  },
  mounted() {
    if (this.session) {
      this.$store.dispatch("report/sendMessage", {
        action: "CLIENTS_PER_PLANE",
        token: this.$store.state.session.session.token,
      });
    }
  },
};
</script>
