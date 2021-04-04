<template>
  <Tickets
    v-model:visible="displayTickets"
    :selectedPurchase="selectedPurchase"
  />
  <div class="purchase-history">
    <br />
    <div class="header p-text-center p-d-block">Purchase History</div>
    <br />
    <div class="content">
      <div class="p-shadow-1">
        <Toolbar>
          <template #left>
            <Button
              icon="pi pi-shopping-cart"
              label="Reserve"
              class="p-button-success"
              :disabled="
                !selectedPurchase ||
                selectedPurchase?.has_been_reserved === true
              "
              @click="displayTickets = true"
            />
            &nbsp;
            <Button
              icon="pi pi-eye"
              label="View"
              class="p-button-info"
              :disabled="
                !selectedPurchase ||
                selectedPurchase?.has_been_reserved === false
              "
              @click="displayTickets = true"
            />
          </template>
        </Toolbar>
        <DataTable
          :value="tableData"
          :paginator="true"
          :rows="10"
          paginatorTemplate="CurrentPageReport FirstPageLink PrevPageLink PageLinks NextPageLink LastPageLink RowsPerPageDropdown"
          :rowsPerPageOptions="[10, 20, 50]"
          currentPageReportTemplate="Showing {first} to {last} of {totalRecords}"
          stateStorage="session"
          stateKey="dt-state-purchase"
          responsiveLayout="scroll"
          v-model:selection="selectedPurchase"
          dataKey="identifier"
        >
          <Column selectionMode="single" headerStyle="width: 3em" />
          <Column
            field="purchaseTime"
            header="Time of purchase"
            :sortable="true"
          />
          <Column field="origin" header="Origin" :sortable="true" />
          <Column field="destination" header="Destination" :sortable="true" />
          <Column
            field="outboundDate"
            header="Outbound Date"
            :sortable="true"
          />
          <Column field="inboundDate" header="Inbound Date" :sortable="true" />
          <Column
            field="ticketAmount"
            header="Ticket Amount"
            :sortable="true"
          />
          <Column field="totalCost" header="Total Cost" :sortable="true" />
          <Column headerStyle="width: 3em">
            <template #body="slotProps">
              <template v-if="slotProps.data.has_been_reserved === false">
                <Tag icon="pi pi-shopping-cart" severity="success" />
              </template>
            </template>
          </Column>
        </DataTable>
      </div>
    </div>
  </div>
</template>

<script>
import { mapState } from "vuex";

import Toolbar from "primevue/toolbar";
import Button from "primevue/button";
import DataTable from "primevue/datatable";
import Column from "primevue/column";
import Tag from "primevue/tag";

import Tickets from "@/components/Tickets";

export default {
  name: "PurchaseHistory",
  components: {
    Toolbar,
    Button,
    DataTable,
    Column,
    Tag,
    Tickets,
  },
  data() {
    return {
      selectedPurchase: null,
      displayTickets: false,
    };
  },
  computed: {
    ...mapState("session", { session: (state) => state.session }),
    ...mapState("purchase", { view: (state) => state.view }),
    tableData() {
      const result = [];
      if (!this.view) return result;
      for (const o of this.view) {
        result.push({
          ...o,
          purchaseTime: o.timestamp.slice(0, -7),
          outboundDate: o.outbound_date,
          inboundDate: o.inbound_date ?? "N/A",
          ticketAmount: `${o.ticket_amount} tickets`,
          totalCost: `$${o.total_cost}`,
        });
      }
      return result;
    },
  },
  watch: {
    selectedPurchase() {
      this.displayTickets = false;
    },
    session() {
      if (!this.session) {
        this.$router.push({ name: "Home" });
      }
    },
    view() {
      this.selectedPurchase = null;
      this.displayTickets = false;
    },
  },
  created() {
    if (!this.session) {
      this.$router.push({ name: "Home" });
    }
  },
  mounted() {
    if (this.session) {
      this.$store.dispatch("purchase/sendMessage", {
        action: "VIEW_ALL",
        token: this.$store.state.session.session.token,
      });
    }
  },
};
</script>

<style scoped>
.content {
  display: flex;
  flex-direction: column;
  align-items: center;
}
.content > div {
  max-width: 90vw;
}
</style>
