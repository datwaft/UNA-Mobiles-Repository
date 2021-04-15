<template>
  <div class="flight-modal">
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
              :disabled="!isPlaneDifferent"
              @click="plane = selected.plane"
              v-if="mode == 'update'"
            />
            <span class="p-float-label">
              <Dropdown
                id="plane"
                v-model="plane"
                :class="{ 'p-invalid': !isPlaneValid }"
                :options="planes"
                optionLabel="name"
                optionValue="identifier"
                autofocus
              />
              <label for="plane">Plane</label>
            </span>
            <Button
              icon="pi pi-times"
              class="p-button-danger"
              :disabled="plane == null"
              @click="plane = null"
            />
          </div>
          <small id="plane-help" class="p-error" v-if="!isPlaneValid">
            <template v-for="(help, i) of planeHelp" :key="i">
              {{ help }}
              <br v-if="i != planeHelp.length" />
            </template>
          </small>
        </div>
        <br />
        <div class="p-field">
          <div class="p-inputgroup">
            <Button
              icon="pi pi-replay"
              class="p-button-warning"
              :disabled="!isOutboundScheduleDifferent"
              @click="outboundSchedule = selected.outbound_schedule"
              v-if="mode == 'update'"
            />
            <span class="p-float-label">
              <Dropdown
                id="outboundSchedule"
                v-model="outboundSchedule"
                :class="{ 'p-invalid': !isOutboundScheduleValid }"
                :options="schedules"
                optionLabel="name"
                optionValue="identifier"
              />
              <label for="outboundSchedule">Outbound schedule</label>
            </span>
            <Button
              icon="pi pi-times"
              class="p-button-danger"
              :disabled="outboundSchedule == null"
              @click="outboundSchedule = null"
            />
          </div>
          <small
            id="outboundSchedule-help"
            class="p-error"
            v-if="!isOutboundScheduleValid"
          >
            <template v-for="(help, i) of outboundScheduleHelp" :key="i">
              {{ help }}
              <br v-if="i != outboundScheduleHelp.length" />
            </template>
          </small>
        </div>
        <br />
        <div class="p-field">
          <div class="p-inputgroup">
            <Button
              icon="pi pi-replay"
              class="p-button-warning"
              :disabled="!isOutboundDateDifferent"
              @click="outboundDate = selected.outbound_date"
              v-if="mode == 'update'"
            />
            <span class="p-float-label">
              <Calendar
                id="outboundDate"
                v-model="rawOutboundDate"
                :class="{ 'p-invalid': !isOutboundDateValid }"
                dateFormat="yy-mm-dd"
                :manualInput="false"
                :disabledDays="outboundDisabledDays"
                :disabled="outboundSchedule == null"
              />
              <label for="outboundDate">Outbound date</label>
            </span>
            <Button
              icon="pi pi-times"
              class="p-button-danger"
              :disabled="outboundDate == null"
              @click="outboundDate = null"
            />
          </div>
          <small
            id="outboundDate-help"
            class="p-error"
            v-if="!isOutboundDateValid"
          >
            <template v-for="(help, i) of outboundDateHelp" :key="i">
              {{ help }}
              <br v-if="i != outboundDateHelp.length" />
            </template>
          </small>
        </div>
        <br />
        <div class="p-field">
          <div class="p-inputgroup">
            <Button
              icon="pi pi-replay"
              class="p-button-warning"
              :disabled="!isInboundScheduleDifferent"
              @click="inboundSchedule = selected.inbound_schedule"
              v-if="mode == 'update'"
            />
            <span class="p-float-label">
              <Dropdown
                id="inboundSchedule"
                v-model="inboundSchedule"
                :class="{ 'p-invalid': !isInboundScheduleValid }"
                :options="inboundSchedules"
                optionLabel="name"
                optionValue="identifier"
                :disabled="outboundSchedule == null || outboundDate == null"
              />
              <label for="inboundSchedule">Inbound schedule (optional)</label>
            </span>
            <Button
              icon="pi pi-times"
              class="p-button-danger"
              :disabled="inboundSchedule == null"
              @click="inboundSchedule = null"
            />
          </div>
          <small
            id="inboundSchedule-help"
            class="p-error"
            v-if="!isInboundScheduleValid"
          >
            <template v-for="(help, i) of inboundScheduleHelp" :key="i">
              {{ help }}
              <br v-if="i != inboundScheduleHelp.length" />
            </template>
          </small>
        </div>
        <br />
        <div class="p-field">
          <div class="p-inputgroup">
            <Button
              icon="pi pi-replay"
              class="p-button-warning"
              :disabled="!isInboundDateDifferent"
              @click="inboundDate = selected.inbound_date"
              v-if="mode == 'update'"
            />
            <span class="p-float-label">
              <Calendar
                id="inboundDate"
                v-model="rawInboundDate"
                :class="{ 'p-invalid': !isInboundDateValid }"
                dateFormat="yy-mm-dd"
                :manualInput="false"
                :disabledDays="inboundDisabledDays"
                :minDate="dateOutboundDate"
                :disabled="
                  outboundSchedule == null ||
                  outboundDate == null ||
                  inboundSchedule == null
                "
              />
              <label for="inboundDate">Inbound date (optional)</label>
            </span>
            <Button
              icon="pi pi-times"
              class="p-button-danger"
              :disabled="inboundDate == null"
              @click="inboundDate = null"
            />
          </div>
          <small
            id="inboundDate-help"
            class="p-error"
            v-if="!isInboundDateValid"
          >
            <template v-for="(help, i) of inboundDateHelp" :key="i">
              {{ help }}
              <br v-if="i != inboundDateHelp.length" />
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
import Calendar from "primevue/calendar";

export default {
  name: "FlightModal",
  components: {
    Dialog,
    Button,
    Dropdown,
    Calendar,
  },
  props: {
    visible: Boolean,
    selected: Object,
    mode: String,
    planes: Array,
    schedules: Array,
  },
  emits: ["update:visible"],
  data() {
    return {
      plane: null,
      rawOutboundDate: null,
      outboundSchedule: null,
      rawInboundDate: null,
      inboundSchedule: null,
    };
  },
  computed: {
    outboundDisabledDays() {
      return [0, 1, 2, 3, 4, 5, 6].filter(
        (d) =>
          d !=
          (this.getWeekdayNumber(
            this.getScheduleByIdentifier(this.outboundSchedule)?.weekday
          ) +
            8) %
            7
      );
    },
    inboundDisabledDays() {
      return [0, 1, 2, 3, 4, 5, 6].filter(
        (d) =>
          d !=
          (this.getWeekdayNumber(
            this.getScheduleByIdentifier(this.inboundSchedule)?.weekday
          ) +
            8) %
            7
      );
    },
    inboundSchedules() {
      let schedule = this.getScheduleByIdentifier(this.outboundSchedule);
      return this.schedules?.filter(
        (e) =>
          e.origin == schedule?.destination && e.destination == schedule?.origin
      );
    },
    dateOutboundDate() {
      if (!this.rawOutboundDate) return null;
      return new Date(
        this.rawOutboundDate.getTime() -
          this.rawOutboundDate.getTimezoneOffset() * 60 * 1000
      );
    },
    outboundDate: {
      get() {
        if (!this.dateOutboundDate) return null;
        return this.dateOutboundDate.toISOString().slice(0, 10);
      },
      set(value) {
        if (!value) {
          this.rawOutboundDate = null;
        } else {
          this.rawOutboundDate = new Date(Date.parse(`${value}T00:00:00`));
        }
      },
    },
    dateInboundDate() {
      if (!this.rawInboundDate) return null;
      return new Date(
        this.rawInboundDate.getTime() -
          this.rawInboundDate.getTimezoneOffset() * 60 * 1000
      );
    },
    inboundDate: {
      get() {
        if (!this.dateInboundDate) return null;
        return this.dateInboundDate.toISOString().slice(0, 10);
      },
      set(value) {
        if (!value) {
          this.rawInboundDate = null;
        } else {
          this.rawInboundDate = new Date(Date.parse(`${value}T00:00:00`));
        }
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
    isPlaneValid() {
      return [
        this.plane !== null,
        this.planes.some((e) => e.identifier === this.plane),
      ].every((e) => e);
    },
    isOutboundDateValid() {
      if (this.outboundDate == null) {
        return false;
      } else {
        return [
          this.inboundDate != null
            ? this.dateOutboundDate < this.dateInboundDate
            : true,
          this.getWeekdayFromDate(this.dateOutboundDate) ==
            this.getScheduleByIdentifier(this.outboundSchedule)?.weekday,
        ].every((e) => e);
      }
    },
    isOutboundScheduleValid() {
      return [
        this.outboundSchedule !== null,
        this.schedules.some((e) => e.identifier === this.outboundSchedule),
      ].every((e) => e);
    },
    isInboundDateValid() {
      if (this.inboundDate != null) {
        return [
          this.dateInboundDate > this.dateOutboundDate,
          this.getWeekdayFromDate(this.dateInboundDate) ==
            this.getScheduleByIdentifier(this.inboundSchedule)?.weekday,
        ].every((e) => e);
      } else {
        return this.inboundSchedule == null;
      }
    },
    isInboundScheduleValid() {
      if (this.inboundSchedule != null) {
        return [
          this.inboundSchedules.some(
            (e) => e.identifier === this.inboundSchedule
          ),
          this.areSchedulesPairing(this.outboundSchedule, this.inboundSchedule),
        ].every((e) => e);
      } else {
        return this.inboundDate == null;
      }
    },
    isValid() {
      return [
        this.isPlaneValid,
        this.isOutboundDateValid,
        this.isOutboundScheduleValid,
        this.isInboundDateValid,
        this.isInboundScheduleValid,
      ].every((e) => e);
    },
    planeHelp() {
      return [this.plane === null ? "Plane is not optional" : null].filter(
        (e) => e
      );
    },
    outboundDateHelp() {
      let help = [];
      if (this.outboundDate == null) {
        help.push("Outbound date is not optional");
      } else if (this.inboundDate == null) {
        if (
          this.getWeekdayFromDate(this.dateOutboundDate) !=
          this.getScheduleByIdentifier(this.outboundSchedule)?.weekday
        ) {
          help.push(
            "Outbound date must be the same weekday of the outbound schedule"
          );
        }
      } else {
        if (
          this.dateInboundDate != null &&
          this.dateOutboundDate >= this.dateInboundDate
        ) {
          help.push("Outbound date must be before inbound date");
        }
      }
      return help;
    },
    outboundScheduleHelp() {
      return [
        this.outboundSchedule === null
          ? "Outbound schedule is not optional"
          : null,
        this.inboundSchedule != null
          ? !this.areSchedulesPairing(
              this.outboundSchedule,
              this.inboundSchedule
            )
            ? "Outbound schedule origin must be the same as inbound schedule destination"
            : null
          : null,
        this.inboundSchedule != null
          ? !this.areSchedulesPairing(
              this.outboundSchedule,
              this.inboundSchedule
            )
            ? "Outbound schedule destination must be the same as inbound schedule origin"
            : null
          : null,
      ].filter((e) => e);
    },
    inboundDateHelp() {
      let help = [];
      if (this.inboundDate != null) {
        if (this.dateInboundDate <= this.dateOutboundDate) {
          help.push("Inbound date must be after outbound date");
        }
        if (
          this.getWeekdayFromDate(this.dateInboundDate) !=
          this.getScheduleByIdentifier(this.inboundSchedule)?.weekday
        ) {
          help.push(
            "Inbound date must be the same weekday of the inbound schedule"
          );
        }
      } else if (this.inboundSchedule != null) {
        help.push(
          "If inbound schedule is present then inbound date must be present too"
        );
      }
      return help;
    },
    inboundScheduleHelp() {
      return [
        this.inboundSchedule != null
          ? !this.areSchedulesPairing(
              this.outboundSchedule,
              this.inboundSchedule
            )
            ? "Outbound schedule origin must be the same as inbound schedule destination"
            : null
          : null,
        this.inboundSchedule != null
          ? !this.areSchedulesPairing(
              this.outboundSchedule,
              this.inboundSchedule
            )
            ? "Outbound schedule destination must be the same as inbound schedule origin"
            : null
          : null,
        this.inboundDate != null
          ? this.inboundSchedule == null
            ? "If inbound date is present then inbound schedule must be present too"
            : null
          : null,
      ].filter((e) => e);
    },
    isPlaneDifferent() {
      if (this.mode == "create") return false;
      return this.plane !== this.selected?.plane;
    },
    isOutboundDateDifferent() {
      if (this.mode == "create") return false;
      return this.outboundDate !== this.selected?.outbound_date;
    },
    isOutboundScheduleDifferent() {
      if (this.mode == "create") return false;
      return this.outboundSchedule !== this.selected?.outbound_schedule;
    },
    isInboundDateDifferent() {
      if (this.mode == "create") return false;
      return this.inboundDate !== this.selected?.inbound_date;
    },
    isInboundScheduleDifferent() {
      if (this.mode == "create") return false;
      return this.inboundSchedule !== this.selected?.inbound_schedule;
    },
    isDifferent() {
      return [
        this.isPlaneDifferent,
        this.isOutboundDateDifferent,
        this.isOutboundScheduleDifferent,
        this.isInboundDateDifferent,
        this.isInboundScheduleDifferent,
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
    planes() {
      this.reset();
    },
    schedules() {
      this.reset();
    },
  },
  methods: {
    getWeekdayNumber(weekday) {
      switch (weekday) {
        case "Monday":
          return 0;
        case "Tuesday":
          return 1;
        case "Wednesday":
          return 2;
        case "Thursday":
          return 3;
        case "Friday":
          return 4;
        case "Saturday":
          return 5;
        case "Sunday":
          return 6;
        default:
          return -1;
      }
    },
    areSchedulesPairing(schedule1, schedule2) {
      schedule1 = this.getScheduleByIdentifier(schedule1);
      schedule2 = this.getScheduleByIdentifier(schedule2);
      return (
        schedule1?.origin == schedule2?.destination &&
        schedule1?.destination == schedule2?.origin
      );
    },
    getScheduleByIdentifier(identifier, collection) {
      collection = collection ?? this.schedules;
      return collection?.filter((e) => e.identifier == identifier)[0];
    },
    getWeekdayFromDate(date) {
      return new Intl.DateTimeFormat("en-US", { weekday: "long" }).format(
        this.addDaysToDate(date, 8)
      );
    },
    addDaysToDate(date, days) {
      let result = new Date(date);
      result.setDate(result.getDate() + days);
      return result;
    },
    reset() {
      if (this.mode == "create") {
        this.plane = null;
        this.outboundDate = null;
        this.outboundSchedule = null;
        this.inboundDate = null;
        this.inboundSchedule = null;
      } else {
        this.plane = this.selected?.plane ?? null;
        this.outboundDate = this.selected?.outbound_date ?? null;
        this.outboundSchedule = this.selected?.outbound_schedule ?? null;
        this.inboundDate = this.selected?.inbound_date ?? null;
        this.inboundSchedule = this.selected?.inbound_schedule ?? null;
      }
    },
    accept() {
      if (this.mode === "create") {
        this.$store.dispatch("flight/sendMessage", {
          action: "CREATE",
          token: this.$store.state.session.session.token,
          plane: this.plane,
          outbound_date: this.outboundDate,
          outbound_schedule: this.outboundSchedule,
          inbound_date: this.inboundDate,
          inbound_schedule: this.inboundSchedule,
        });
      } else if (this.mode === "update") {
        this.$store.dispatch("flight/sendMessage", {
          action: "UPDATE",
          token: this.$store.state.session.session.token,
          identifier: this.selected.identifier,
          plane: this.plane,
          outbound_date: this.outboundDate,
          outbound_schedule: this.outboundSchedule,
          inbound_date: this.inboundDate,
          inbound_schedule: this.inboundSchedule,
        });
      }
    },
  },
};
</script>
