<template>
  <div class="schedules-with-discount">
    <div class="header p-text-center p-d-block">Schedules with discount</div>
    <br />
    <DataTable
      class="p-datatable-sm"
      :value="data"
      :paginator="true"
      :rows="10"
      paginatorTemplate="CurrentPageReport FirstPageLink PrevPageLink PageLinks NextPageLink LastPageLink RowsPerPageDropdown"
      :rowsPerPageOptions="[10, 20, 50]"
      currentPageReportTemplate="Showing {first} to {last} of {totalRecords}"
      stateStorage="session"
      stateKey="dt-state-schedules-with-discount"
      responsiveLayout="scroll"
    >
      <Column field="origin" header="Origin" :sortable="true" />
      <Column field="destination" header="Destination" :sortable="true" />
      <Column field="departureTime" header="Departure Time" :sortable="true" />
      <Column field="weekday" header="Weekday" :sortable="true" />
      <Column field="duration" header="Duration" :sortable="true" />
      <Column field="price" header="Price" :sortable="true" />
      <Column field="discount" header="Discount" :sortable="true" />
      <Column field="finalPrice" header="Final Price" :sortable="true" />
    </DataTable>
  </div>
</template>

<script>
import { mapState } from "vuex";

import DataTable from "primevue/datatable";
import Column from "primevue/column";

export default {
  name: "schedules-with-discount",
  components: {
    DataTable,
    Column,
  },
  computed: {
    ...mapState("schedule", { schedules: (state) => state.data }),
    data() {
      const result = [];
      if (!this.schedules) return result;
      for (const o of this.schedules) {
        result.push({
          ...o,
          departureTime: o.departure_time,
          price: `$${o.price}`,
          discount: `${o.discount * 100}%`,
          finalPrice: `$${o.price - o.price * o.discount}`,
        });
      }
      return result;
    },
  },
};
</script>
