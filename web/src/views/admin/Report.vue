<template>
  <div class="report">
    <TabView>
      <TabPanel header="Purchase count per month in last year">
        <PurchaseCountPerMonthInLastYear />
      </TabPanel>
      <TabPanel header="Revenue per month in last year">
        <RevenuePerMonthInLastYear />
      </TabPanel>
      <TabPanel header="Clients per plane">
        <ClientsPerPlane />
      </TabPanel>
      <TabPanel header="Top 5 routes per ticket number">
        <Top5RoutesPerTicketNumber />
      </TabPanel>
    </TabView>
  </div>
</template>

<script>
import { mapState } from "vuex";

import TabView from "primevue/tabview";
import TabPanel from "primevue/tabpanel";

import PurchaseCountPerMonthInLastYear from "@/views/admin/report/PurchaseCountPerMonthInLastYear";
import RevenuePerMonthInLastYear from "@/views/admin/report/RevenuePerMonthInLastYear";
import ClientsPerPlane from "@/views/admin/report/ClientsPerPlane";
import Top5RoutesPerTicketNumber from "@/views/admin/report/Top5RoutesPerTicketNumber";

export default {
  name: "Report",
  components: {
    TabView,
    TabPanel,
    PurchaseCountPerMonthInLastYear,
    RevenuePerMonthInLastYear,
    ClientsPerPlane,
    Top5RoutesPerTicketNumber,
  },
  computed: {
    ...mapState("session", { session: (state) => state.session }),
  },
  watch: {
    session() {
      if (!this.session || this.session.authorization !== "admin") {
        this.$router.push({ name: "Home" });
      }
    },
  },
  created() {
    if (!this.session || this.session.authorization !== "admin") {
      this.$router.push({ name: "Home" });
    }
  },
};
</script>
