<template>
  <div class="tickets">
    <Dialog
      :header="header"
      v-model:visible="isVisible"
      :style="{ 'min-width': '50vw' }"
    >
      <ProgressBar :value="progressBar" v-if="!hasBeenReserved" />
      <SeatSelector
        :ticketsToPurchase="seatsAmount"
        v-model:tickets="selectedTickets"
        :ticketsPerFlight="flightTickets"
        :ticketsPerPurchase="purchaseTickets"
        :columnNumber="columnNumber"
        :rowNumber="rowNumber"
        :readonly="hasBeenReserved"
      />
      <template #footer>
        <template v-if="!hasBeenReserved">
          <Button
            icon="pi pi-times-circle"
            label="Cancel"
            @click="isVisible = false"
          />
          <Button
            icon="pi pi-credit-card"
            label="Reserve"
            @click="reserve()"
            :disabled="!isValid"
          />
        </template>
        <template v-else>
          <Button
            icon="pi pi-angle-left"
            label="Go back"
            @click="isVisible = false"
          />
        </template>
      </template>
    </Dialog>
  </div>
</template>

<script>
import { mapState } from "vuex";

import Dialog from "primevue/dialog";
import Button from "primevue/button";
import ProgressBar from "primevue/progressbar";

import SeatSelector from "@/components/SeatSelector";

export default {
  name: "Tickets",
  components: {
    Dialog,
    Button,
    ProgressBar,
    SeatSelector,
  },
  props: {
    visible: Boolean,
    selectedPurchase: Object,
  },
  emits: ["update:visible"],
  data() {
    return {
      selectedTickets: [],
    };
  },
  computed: {
    ...mapState("ticket", {
      flightTicketsView: (state) => state.viewPerFlight,
    }),
    ...mapState("ticket", {
      purchaseTicketsView: (state) => state.viewPerPurchase,
    }),
    flightTickets() {
      return this.flightTicketsView[this.selectedPurchase?.flight] ?? [];
    },
    purchaseTickets() {
      return this.purchaseTicketsView[this.selectedPurchase?.identifier] ?? [];
    },
    isVisible: {
      get() {
        return this.visible;
      },
      set(value) {
        this.$emit("update:visible", value);
      },
    },
    hasBeenReserved() {
      return this.selectedPurchase?.has_been_reserved === true;
    },
    header() {
      return this.hasBeenReserved
        ? "View reservation information"
        : "Reserve your tickets";
    },
    seatsAmount() {
      return this.selectedPurchase?.ticket_amount;
    },
    columnNumber() {
      return this.selectedPurchase?.plane_columns;
    },
    rowNumber() {
      return this.selectedPurchase?.plane_rows;
    },
    progressBar() {
      return (100 * this.selectedTickets.length) / this.seatsAmount;
    },
    isValid() {
      return this.selectedTickets.length === this.seatsAmount;
    },
  },
  watch: {
    selectedPurchase(value) {
      if (!value) return;
      this.$store.dispatch("ticket/sendMessage", {
        action: "VIEW_ALL_PER_FLIGHT",
        flight: this.selectedPurchase.flight,
      });
      this.$store.dispatch("ticket/sendMessage", {
        action: "VIEW_ALL_PER_PURCHASE",
        purchase: this.selectedPurchase.identifier,
      });
    },
    isVisible(value) {
      if (!value) {
        this.selectedTickets = [];
      }
    },
  },
  methods: {
    reserve() {
      this.$store.dispatch("ticket/sendMessage", {
        action: "CREATE",
        flight: this.selectedPurchase.flight,
        purchase: this.selectedPurchase.identifier,
        tickets: this.selectedTickets,
      });
    },
  },
};
</script>
