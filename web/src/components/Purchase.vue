<template>
  <Dialog
    :header="header"
    v-model:visible="isVisible"
    :style="{ 'min-width': '50vw' }"
  >
    <br />
    <div class="p-d-flex p-jc-center p-ai-center" v-if="section === 1">
      <div class="p-field">
        <span class="p-float-label">
          <InputNumber
            id="ticketNumber"
            v-model="ticketNumber"
            suffix=" tickets"
            :min="0"
            :max="buyableTickets"
            showButtons
            buttonLayout="horizontal"
            decrementButtonClass="p-button-secondary"
            incrementButtonClass="p-button-secondary"
            incrementButtonIcon="pi pi-plus"
            decrementButtonIcon="pi pi-minus"
            :class="{ 'p-invalid': !isTicketNumberValid }"
          />
          <label for="ticketNumber">Number of tickets</label>
        </span>
        <small
          id="ticket-number-help"
          class="p-error"
          v-if="!isTicketNumberValid"
        >
          <template v-for="help of ticketNumberHelp">
            {{ help }}
          </template>
        </small>
      </div>
    </div>
    <div class="p-d-flex p-jc-center p-ai-center" v-if="section > 1">
      <div class="p-field">
        <span class="p-float-label">
          <Dropdown
            id="paymentMethod"
            v-model="selectedPaymentMethod"
            :options="paymentMethods"
            optionLabel="value"
            :class="{ 'p-invalid': !isPaymentMethodValid }"
          />
          <label for="paymentMethod">Payment Method</label>
        </span>
        <small
          id="payment-method-help"
          class="p-error"
          v-if="!isPaymentMethodValid"
        >
          <template v-for="help of paymentMethodHelp">
            {{ help }}
          </template>
        </small>
      </div>
    </div>
    <br />
    <br />
    <div class="p-d-flex p-jc-center p-ai-center">
      <span class="p-float-label">
        <InputNumber
          id="totalPrice"
          v-model="totalPrice"
          mode="currency"
          currency="USD"
          locale="en-US"
          disabled
        />
        <label for="totalPrice">Price</label>
      </span>
    </div>
    <br />
    <template #footer>
      <Button
        icon="pi pi-times-circle"
        label="Cancel"
        @click="isVisible = false"
      />
      <Button
        icon="pi pi-angle-left"
        label="Go Back"
        @click="section -= 1"
        v-if="section > 1"
      />
      <Button
        icon="pi pi-angle-right"
        label="Continue"
        @click="section += 1"
        v-if="section == 1"
        :disabled="!isSectionValid"
      />
      <Button
        icon="pi pi-credit-card"
        label="Purchase"
        @click="purchase()"
        :disabled="!(section > 1) || !isValid"
      />
    </template>
  </Dialog>
</template>

<script>
import Dialog from "primevue/dialog";
import InputNumber from "primevue/inputnumber";
import Dropdown from "primevue/dropdown";
import Button from "primevue/button";

export default {
  name: "Purchase",
  components: {
    Dialog,
    InputNumber,
    Dropdown,
    Button,
  },
  props: {
    visible: Boolean,
    selectedFlight: Object,
  },
  emits: ["update:visible"],
  data() {
    return {
      section: 1,
      ticketNumber: 0,
      selectedPaymentMethod: null,
      paymentMethods: [
        { value: "Paypal" },
        { value: "American Express" },
        { value: "Visa" },
        { value: "Credomatic" },
      ],
    };
  },
  computed: {
    isVisible: {
      get() {
        return this.visible;
      },
      set(value) {
        this.$emit("update:visible", value);
      },
    },
    header() {
      switch (this.section) {
        case 1:
          return "Please, select how many seats you want";
        default:
          return "Please, select your payment method";
      }
    },
    totalPrice() {
      return this.selectedFlight.ticket_price * this.ticketNumber;
    },
    buyableTickets() {
      return (
        this.selectedFlight.passenger_total -
        this.selectedFlight.passenger_amount
      );
    },
    isTicketNumberValid() {
      return [this.ticketNumber > 0].every((e) => e);
    },
    ticketNumberHelp() {
      return [
        this.ticketNumber.length > 0 ? null : "You must buy at least 1 ticket",
      ].filter((e) => e != null);
    },
    isPaymentMethodValid() {
      return [this.selectedPaymentMethod].every((e) => e);
    },
    paymentMethodHelp() {
      return [
        this.selectedPaymentMethod ? null : "You must select a payment method",
      ].filter((e) => e != null);
    },
    isSectionValid() {
      switch (this.section) {
        case 1:
          return [this.isTicketNumberValid].every((e) => e);
        default:
          return true;
      }
    },
    isValid() {
      return [this.isTicketNumberValid, this.isPaymentMethodValid].every(
        (e) => e
      );
    },
  },
  watch: {
    selectedFlight(newValue, oldValue) {
      if (newValue?.identifier !== oldValue?.identifier) {
        this.section = 1;
        this.ticketNumber = 0;
        this.selectedPaymentMethod = null;
      }
    },
  },
  methods: {
    purchase() {
      console.log("Make purchase");
    },
  },
};
</script>

<style scoped>
.p-dropdown,
.p-inputnumber {
  min-width: 30vw;
}
</style>
