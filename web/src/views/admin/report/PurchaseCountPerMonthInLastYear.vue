<template>
  <div class="purchase-count-per-month-in-last-year">
    <Chart type="bar" :data="data" />
  </div>
</template>

<script>
import { mapState } from "vuex";

import Chart from "primevue/chart";

export default {
  name: "PurchaseCountPerMonthInLastYear",
  components: {
    Chart,
  },
  computed: {
    ...mapState("session", { session: (state) => state.session }),
    ...mapState("report", {
      rawdata: (state) => state.purchaseCountPerMonthInLastYear,
    }),
    data() {
      return {
        labels: this.labels,
        datasets: [
          {
            label: "Purchase Count per Month in Last Year",
            backgroundColor: "#42a5f5",
            data: this.dataset,
          },
        ],
      };
    },
    labels() {
      let labels = [];
      if (!this.rawdata) return [];
      for (let { year, month } of this.rawdata) {
        labels.push(`${month} ${year}`);
      }
      return labels.reverse();
    },
    dataset() {
      let dataset = [];
      if (!this.rawdata) return [];
      for (let { count } of this.rawdata) {
        dataset.push(count);
      }
      return dataset.reverse();
    },
  },
  mounted() {
    if (this.session) {
      this.$store.dispatch("report/sendMessage", {
        action: "PURCHASE_COUNT_PER_MONTH_IN_LAST_YEAR",
        token: this.$store.state.session.session.token,
      });
    }
  },
};
</script>

<style scoped>
.purchase-count-per-month-in-last-year {
  padding: 0 15vw;
}
</style>
