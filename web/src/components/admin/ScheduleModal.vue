<template>
  <div class="schedule-modal">
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
              :disabled="!isRouteDifferent"
              @click="route = selected.route"
              v-if="mode == 'update'"
            />
            <span class="p-float-label">
              <Dropdown
                id="route"
                v-model="route"
                :class="{ 'p-invalid': !isRouteValid }"
                :options="routes"
                optionLabel="name"
                optionValue="identifier"
                autofocus
              />
              <label for="route">Route</label>
            </span>
          </div>
          <small id="route-help" class="p-error" v-if="!isRouteValid">
            <template v-for="help of routeHelp">
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
              :disabled="!isDepartureTimeDifferent"
              @click="departureTime = selected.departureTime"
              v-if="mode == 'update'"
            />
            <span class="p-float-label">
              <Calendar
                id="departureTime"
                v-model="rawDepartureTime"
                :showTime="true"
                :timeOnly="true"
                :showSeconds="true"
                :stepMinute="15"
                :stepSecond="15"
                :manualInput="false"
              />
              <label for="departureTime">Departure Time</label>
            </span>
          </div>
        </div>
        <br />
        <div class="p-field">
          <div class="p-inputgroup">
            <Button
              icon="pi pi-replay"
              class="p-button-warning"
              :disabled="!isWeekdayDifferent"
              @click="weekday = selected.weekday"
              v-if="mode == 'update'"
            />
            <span class="p-float-label">
              <Dropdown
                id="weekday"
                v-model="weekday"
                :class="{ 'p-invalid': !isWeekdayValid }"
                :options="weekdaysCombo"
                optionLabel="name"
                optionValue="name"
              />
              <label for="weekday">Weekday</label>
            </span>
          </div>
          <small id="weekday-help" class="p-error" v-if="!isWeekdayValid">
            <template v-for="help of weekdayHelp">
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
              :disabled="!isDiscountDifferent"
              @click="discount = selected.discount"
              v-if="mode == 'update'"
            />
            <span class="p-float-label">
              <InputNumber
                id="discount"
                v-model.number="stylizedDiscount"
                :class="{ 'p-invalid': !isDiscountValid }"
                suffix="%"
              />
              <label for="discount">Discount</label>
            </span>
          </div>
          <small id="discount-help" class="p-error" v-if="!isDiscountValid">
            <template v-for="help of discountHelp">
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
import Dropdown from "primevue/dropdown";
import InputNumber from "primevue/inputnumber";
import Calendar from "primevue/calendar";

export default {
  name: "ScheduleModal",
  components: {
    Dialog,
    Button,
    Dropdown,
    InputNumber,
    Calendar,
  },
  props: {
    visible: Boolean,
    selected: Object,
    mode: String,
    routes: Array,
  },
  emits: ["update:visible"],
  data() {
    return {
      route: null,
      rawDepartureTime: new Date(Date.parse(`2020-10-10T00:00:00`)),
      weekday: null,
      discount: 0.0,
      weekdays: [
        "Monday",
        "Tuesday",
        "Wednesday",
        "Thursday",
        "Friday",
        "Saturday",
        "Sunday",
      ],
    };
  },
  computed: {
    departureTime: {
      get() {
        return new Date(
          this.rawDepartureTime.getTime() -
            this.rawDepartureTime.getTimezoneOffset() * 60 * 1000
        )
          .toISOString()
          .slice(11, 19);
      },
      set(value) {
        this.rawDepartureTime = new Date(Date.parse(`2020-10-10T${value}`));
      },
    },
    stylizedDiscount: {
      get() {
        return this.discount * 100;
      },
      set(value) {
        this.discount = value / 100;
      },
    },
    weekdaysCombo() {
      let result = [];
      for (let weekday of this.weekdays) {
        result.push({ name: weekday });
      }
      return result;
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
    isRouteValid() {
      return [this.route !== null].every((e) => e);
    },
    isWeekdayValid() {
      return [
        this.weekday !== null,
        this.weekdays.includes(this.weekday),
      ].every((e) => e);
    },
    isDiscountValid() {
      return [
        this.discount !== null,
        this.discount >= 0,
        this.discount <= 1,
      ].every((e) => e);
    },
    isValid() {
      return [
        this.isRouteValid,
        this.isWeekdayValid,
        this.isDiscountValid,
      ].every((e) => e);
    },
    routeHelp() {
      return [this.route === null ? "Route is not optional" : null].filter(
        (e) => e
      );
    },
    weekdayHelp() {
      return [this.weekday === null ? "Weekday is not optional" : null].filter(
        (e) => e
      );
    },
    discountHelp() {
      return [
        this.discount === null ? "Discount is not optional" : null,
        this.discount < 0 ? "Discount must be positive" : null,
        this.discount > 1 ? "Discount must not be greater than 100%" : null,
      ].filter((e) => e);
    },
    isRouteDifferent() {
      if (this.mode == "create") return false;
      return this.route !== this.selected?.route;
    },
    isDepartureTimeDifferent() {
      if (this.mode == "create") return false;
      return this.departureTime !== this.selected?.departureTime;
    },
    isWeekdayDifferent() {
      if (this.mode == "create") return false;
      return this.weekday !== this.selected?.weekday;
    },
    isDiscountDifferent() {
      if (this.mode == "create") return false;
      return this.discount !== this.selected?.discount;
    },
    isDifferent() {
      return [
        this.isRouteDifferent,
        this.isDepartureTimeDifferent,
        this.isWeekdayDifferent,
        this.isDiscountDifferent,
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
      this.reset();
    },
    mode(newValue, oldValue) {
      if (newValue !== oldValue) {
        this.reset();
      }
    },
    routes() {
      this.reset();
    },
  },
  methods: {
    reset() {
      if (this.mode == "create") {
        this.route = null;
        this.departureTime = "00:00:00";
        this.weekday = null;
        this.discount = 0.0;
      } else {
        this.route = this.selected?.route ?? null;
        this.departureTime = this.selected?.departure_time ?? "00:00:00";
        this.weekday = this.selected?.weekday ?? null;
        this.discount = this.selected?.discount ?? 0.0;
      }
    },
    accept() {
      if (this.mode === "create") {
        this.$store.dispatch("schedule/sendMessage", {
          action: "CREATE",
          token: this.$store.state.session.session.token,
          route: this.route,
          departure_time: this.departureTime,
          weekday: this.weekday,
          discount: this.discount,
        });
      } else if (this.mode === "update") {
        this.$store.dispatch("schedule/sendMessage", {
          action: "UPDATE",
          token: this.$store.state.session.session.token,
          identifier: this.selected.identifier,
          route: this.route,
          departure_time: this.departureTime,
          weekday: this.weekday,
          discount: this.discount,
        });
      }
    },
  },
};
</script>
