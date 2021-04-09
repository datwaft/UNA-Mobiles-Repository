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
              :disabled="!isYearDifferent"
              @click="year = selected.year"
              v-if="mode == 'update'"
            />
            <span class="p-float-label">
              <InputNumber
                id="year"
                v-model.number="year"
                :class="{ 'p-invalid': !isYearValid }"
                :min="1901"
                :max="2099"
                :useGrouping="false"
                autofocus
              />
              <label for="year">Year</label>
            </span>
          </div>
          <small id="year-help" class="p-error" v-if="!isYearValid">
            <template v-for="help of yearHelp">
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
              :disabled="!isModelDifferent"
              @click="model = selected.model"
              v-if="mode == 'update'"
            />
            <span class="p-float-label">
              <InputText
                id="model"
                v-model.trim="model"
                :class="{ 'p-invalid': !isModelValid }"
              />
              <label for="model">Model</label>
            </span>
          </div>
          <small id="model-help" class="p-error" v-if="!isModelValid">
            <template v-for="help of modelHelp">
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
              :disabled="!isBrandDifferent"
              @click="brand = selected.brand"
              v-if="mode == 'update'"
            />
            <span class="p-float-label">
              <InputText
                id="brand"
                v-model.trim="brand"
                :class="{ 'p-invalid': !isBrandValid }"
              />
              <label for="brand">Brand</label>
            </span>
          </div>
          <small id="brand-help" class="p-error" v-if="!isBrandValid">
            <template v-for="help of brandHelp">
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
              :disabled="!isRowsDifferent"
              @click="rows = selected.rows"
              v-if="mode == 'update'"
            />
            <span class="p-float-label">
              <InputNumber
                id="rows"
                v-model.number="rows"
                :class="{ 'p-invalid': !isRowsValid }"
                :min="25"
                :max="30"
                :useGrouping="false"
                showButtons
              />
              <label for="rows">Rows</label>
            </span>
          </div>
          <small id="rows-help" class="p-error" v-if="!isRowsValid">
            <template v-for="help of rowsHelp">
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
              :disabled="!isColumnsDifferent"
              @click="columns = selected.columns"
              v-if="mode == 'update'"
            />
            <span class="p-float-label">
              <InputNumber
                id="columns"
                v-model.number="columns"
                :class="{ 'p-invalid': !isColumnsValid }"
                :min="6"
                :step="3"
                :max="9"
                :useGrouping="false"
                showButtons
              />
              <label for="columns">Columns</label>
            </span>
          </div>
          <small id="columns-help" class="p-error" v-if="!isColumnsValid">
            <template v-for="help of columnsHelp">
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

export default {
  name: "PlaneTypeModal",
  components: {
    Dialog,
    Button,
    InputNumber,
    InputText,
  },
  props: {
    visible: Boolean,
    selected: Object,
    mode: String,
  },
  emits: ["update:visible"],
  data() {
    return {
      year: 1990,
      model: "",
      brand: "",
      rows: 25,
      columns: 6,
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
    isYearValid() {
      return [this.year > 1900, this.year < 2100].every((e) => e);
    },
    isModelValid() {
      return [this.model.length > 0].every((e) => e);
    },
    isBrandValid() {
      return [this.brand.length > 0].every((e) => e);
    },
    isRowsValid() {
      return [this.rows >= 25, this.rows <= 30].every((e) => e);
    },
    isColumnsValid() {
      return [this.columns == 6 || this.columns == 9].every((e) => e);
    },
    isValid() {
      return [
        this.isYearValid,
        this.isModelValid,
        this.isBrandValid,
        this.isRowsValid,
        this.isColumnsValid,
      ].every((e) => e);
    },
    yearHelp() {
      return [
        this.year <= 1900 ? "Year must be greater than 1900" : null,
        this.year >= 2100 ? "Year must be less than 2100" : null,
      ].filter((e) => !!e);
    },
    modelHelp() {
      return [
        this.model.length <= 0
          ? "Model must have at least one non-black character"
          : null,
      ].filter((e) => !!e);
    },
    brandHelp() {
      return [
        this.brand.length <= 0
          ? "Brand must have at least one non-black character"
          : null,
      ].filter((e) => !!e);
    },
    rowsHelp() {
      return [
        this.rows < 25 ? "Rows must be at least 25" : null,
        this.rows > 30 ? "Rows must be at maximum 30" : null,
      ].filter((e) => !!e);
    },
    columnsHelp() {
      return [
        this.columns != 6 && this.columns != 9
          ? "Columns must be 6 or 9"
          : null,
      ].filter((e) => !!e);
    },
    isYearDifferent() {
      if (this.mode == "create") return false;
      return this.year !== this.selected?.year;
    },
    isModelDifferent() {
      if (this.mode == "create") return false;
      return this.model !== this.selected?.model;
    },
    isBrandDifferent() {
      if (this.mode == "create") return false;
      return this.brand !== this.selected?.brand;
    },
    isRowsDifferent() {
      if (this.mode == "create") return false;
      return this.rows !== this.selected?.rows;
    },
    isColumnsDifferent() {
      if (this.mode == "create") return false;
      return this.columns !== this.selected?.columns;
    },
    isDifferent() {
      return [
        this.isYearDifferent,
        this.isModelDifferent,
        this.isBrandDifferent,
        this.isRowsDifferent,
        this.isColumnsDifferent,
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
  },
  methods: {
    reset() {
      if (this.mode == "create") {
        this.year = 1990;
        this.model = "";
        this.brand = "";
        this.rows = 25;
        this.columns = 6;
      } else {
        this.year = this.selected?.year ?? 1990;
        this.model = this.selected?.model ?? "";
        this.brand = this.selected?.brand ?? "";
        this.rows = this.selected?.rows ?? 25;
        this.columns = this.selected?.columns ?? 6;
      }
    },
    accept() {
      if (this.mode === "create") {
        this.$store.dispatch("planeType/sendMessage", {
          action: "CREATE",
          token: this.$store.state.session.session.token,
          year: this.year,
          model: this.model,
          brand: this.brand,
          rows: this.rows,
          columns: this.columns,
        });
      } else if (this.mode === "update") {
        this.$store.dispatch("planeType/sendMessage", {
          action: "UPDATE",
          token: this.$store.state.session.session.token,
          identifier: this.selected.identifier,
          year: this.year,
          model: this.model,
          brand: this.brand,
          rows: this.rows,
          columns: this.columns,
        });
      }
    },
  },
};
</script>
