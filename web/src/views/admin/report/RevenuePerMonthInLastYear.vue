<template>
  <div class="revenue-per-month-in-last-year">
    <Chart type="bar" :data="data" />
  </div>
</template>

<script>
import { mapState } from "vuex";

import Chart from "primevue/chart";

export default {
  name: "RevenuePerMonthInLastYear",
  components: {
    Chart,
  },
  computed: {
    ...mapState("session", { session: (state) => state.session }),
    ...mapState("report", {
      rawdata: (state) => state.revenuePerMonthInLastYear,
    }),
    data() {
      return {
        labels: this.labels,
        datasets: [
          {
            label: "Revenue per Month in Last Year",
            backgroundColor: "#ffa726",
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
      for (let { revenue } of this.rawdata) {
        dataset.push(revenue);
      }
      return dataset.reverse();
    },
  },
  mounted() {
    if (this.session) {
      this.$store.dispatch("report/sendMessage", {
        action: "REVENUE_PER_MONTH_IN_LAST_YEAR",
        token: this.$store.state.session.session.token,
      });
    }
  },
};
</script>

<style scoped>
.revenue-per-month-in-last-year {
  padding: 0 15vw;
}
</style>
