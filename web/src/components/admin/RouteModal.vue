<template>
  <div class="plane-type-modal">
    <Dialog
      :header="header"
      v-model:visible="isVisible"
      :style="{ 'min-width': '50vw' }"
    >
      <br />
      <div class="p-fluid">
        <div class="p-field">
          <div class="p-inputgroup">
            <Button
              icon="pi pi-replay"
              class="p-button-warning"
              :disabled="!isOriginDifferent"
              @click="origin = selected.origin"
              v-if="mode == 'update'"
            />
            <span class="p-float-label">
              <InputText
                id="origin"
                v-model.trim="origin"
                :class="{ 'p-invalid': !isOriginValid }"
                autofocus
              />
              <label for="origin">Origin</label>
            </span>
          </div>
          <small id="origin-help" class="p-error" v-if="!isOriginValid">
            <template v-for="help of originHelp">
              {{ help }}
            </template>
          </small>
        </div>
        <br />
        <div class="p-field">
          <div class="p-inputgroup">
            <Button
              icon="pi pi-replay"
              class="p-button-warning"
              :disabled="!isDestinationDifferent"
              @click="destination = selected.destination"
              v-if="mode == 'update'"
            />
            <span class="p-float-label">
              <InputText
                id="destination"
                v-model.trim="destination"
                :class="{ 'p-invalid': !isDestinationValid }"
              />
              <label for="destination">Destination</label>
            </span>
          </div>
          <small
            id="destination-help"
            class="p-error"
            v-if="!isDestinationValid"
          >
            <template v-for="help of destinationHelp">
              {{ help }}
            </template>
          </small>
        </div>
        <br />
        <div class="p-field">
          <div class="p-inputgroup">
            <Button
              icon="pi pi-replay"
              class="p-button-warning"
              :disabled="!isDurationDifferent"
              @click="duration = selected.duration"
              v-if="mode == 'update'"
            />
            <span class="p-float-label">
              <Calendar
                id="duration"
                v-model="rawduration"
                :showTime="true"
                :timeOnly="true"
                :showSeconds="true"
                :class="{ 'p-invalid': !isDurationValid }"
                :stepMinute="15"
                :stepSecond="15"
                :manualInput="false"
              />
              <label for="duration">Duration</label>
            </span>
          </div>
          <small id="duration-help" class="p-error" v-if="!isDurationValid">
            <template v-for="help of durationHelp">
              {{ help }}
            </template>
          </small>
        </div>
        <br />
        <div class="p-field">
          <div class="p-inputgroup">
            <Button
              icon="pi pi-replay"
              class="p-button-warning"
              :disabled="!isPriceDifferent"
              @click="price = selected.price"
              v-if="mode == 'update'"
            />
            <span class="p-float-label">
              <InputNumber
                id="price"
                v-model.number="price"
                :class="{ 'p-invalid': !isPriceValid }"
                prefix="$"
                autofocus
              />
              <label for="price">Price</label>
            </span>
          </div>
          <small id="price-help" class="p-error" v-if="!isPriceValid">
            <template v-for="help of priceHelp">
              {{ help }}
            </template>
          </small>
        </div>
      </div>
      <template #footer>
        <Button
          icon="pi pi-times-circle"
          label="Cancel"
          @click="isVisible = false"
        />
        <Button
          :icon="icon"
          :label="acceptLabel"
          :disabled="!isAcceptable"
          @click="accept()"
        />
      </template>
    </Dialog>
  </div>
</template>

<script>
import Dialog from "primevue/dialog";
import Button from "primevue/button";
import InputNumber from "primevue/inputnumber";
import InputText from "primevue/inputtext";
import Calendar from "primevue/calendar";

export default {
  name: "RouteModal",
  components: {
    Dialog,
    Button,
    InputNumber,
    InputText,
    Calendar,
  },
  props: {
    visible: Boolean,
    selected: Object,
    mode: String,
  },
  emits: ["update:visible"],
  data() {
    return {
      origin: "",
      destination: "",
      rawduration: new Date(Date.parse(`2020-10-10T00:00:00`)),
      price: 0,
    };
  },
  computed: {
    duration: {
      get() {
        return new Date(
          this.rawduration.getTime() -
            this.rawduration.getTimezoneOffset() * 60 * 1000
        )
          .toISOString()
          .slice(11, 19);
      },
      set(value) {
        this.rawduration = new Date(Date.parse(`2020-10-10T${value}`));
      },
    },
    isVisible: {
      get() {
        return this.visible;
      },
      set(value) {
        this.$emit("update:visible", value);
      },
    },
    header() {
      return this.mode === "create" ? "Create" : "Update";
    },
    acceptLabel() {
      return this.mode === "create" ? "Create" : "Update";
    },
    icon() {
      if (this.mode === "create") {
        return "pi pi-plus";
      } else {
        return "pi pi-pencil";
      }
    },
    isOriginValid() {
      return [this.origin.length > 0].every((e) => e);
    },
    isDestinationValid() {
      return [this.destination.length > 0].every((e) => e);
    },
    isDurationValid() {
      return [this.duration !== "00:00:00"].every((e) => e);
    },
    isPriceValid() {
      return [this.price != 0].every((e) => e);
    },
    isValid() {
      return [
        this.isOriginValid,
        this.isDestinationValid,
        this.isDurationValid,
        this.isPriceValid,
      ].every((e) => e);
    },
    originHelp() {
      return [
        this.origin.length <= 0
          ? "Origin must have at least one non-black character"
          : null,
      ].filter((e) => !!e);
    },
    destinationHelp() {
      return [
        this.destination.length <= 0
          ? "Destination must have at least one non-black character"
          : null,
      ].filter((e) => !!e);
    },
    durationHelp() {
      return [
        this.duration === "00:00:00"
          ? "Duration must be greater than 00:00:00"
          : null,
      ].filter((e) => !!e);
    },
    priceHelp() {
      return [
        this.price == 0 ? "Price must be greater than zero" : null,
      ].filter((e) => !!e);
    },
    isOriginDifferent() {
      if (this.mode == "create") return false;
      return this.origin !== this.selected?.origin;
    },
    isDestinationDifferent() {
      if (this.mode == "create") return false;
      return this.destination !== this.selected?.destination;
    },
    isDurationDifferent() {
      if (this.mode == "create") return false;
      return this.duration !== this.selected?.duration;
    },
    isPriceDifferent() {
      if (this.mode == "create") return false;
      return this.price !== this.selected?.price;
    },
    isDifferent() {
      return [
        this.isOriginDifferent,
        this.isDestinationDifferent,
        this.isDurationDifferent,
        this.isPriceDifferent,
      ].some((e) => e);
    },
    isAcceptable() {
      if (this.mode == "create") {
        return this.isValid;
      } else {
        return this.isValid && this.isDifferent;
      }
    },
  },
  watch: {
    selected() {
      if (this.mode == "update") {
        this.reset();
      }
    },
    mode(newValue, oldValue) {
      if (newValue !== oldValue) {
        this.reset();
      }
    },
  },
  methods: {
    reset() {
      if (this.mode == "create") {
        this.origin = "";
        this.destination = "";
        this.duration = "00:00:00";
        this.price = 0;
      } else {
        this.origin = this.selected?.origin ?? "";
        this.destination = this.selected?.destination ?? "";
        this.duration = this.selected?.duration ?? "00:00:00";
        this.price = this.selected?.price ?? 0;
      }
    },
    accept() {
      if (this.mode === "create") {
        this.$store.dispatch("route/sendMessage", {
          action: "CREATE",
          token: this.$store.state.session.session.token,
          origin: this.origin,
          destination: this.destination,
          duration: this.duration,
          price: this.price,
        });
      } else if (this.mode === "update") {
        this.$store.dispatch("route/sendMessage", {
          action: "UPDATE",
          token: this.$store.state.session.session.token,
          identifier: this.selected.identifier,
          origin: this.origin,
          destination: this.destination,
          duration: this.duration,
          price: this.price,
        });
      }
    },
  },
};
</script>
