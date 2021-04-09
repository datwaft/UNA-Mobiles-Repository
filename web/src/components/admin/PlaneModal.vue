<template>
  <div class="plane-modal">
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
              :disabled="!isNameDifferent"
              @click="name = selected.name"
              v-if="mode == 'update'"
            />
            <span class="p-float-label">
              <InputText
                id="name"
                v-model.trim="name"
                :class="{ 'p-invalid': !isNameValid }"
              />
              <label for="name">Name</label>
            </span>
          </div>
          <small id="name-help" class="p-error" v-if="!isNameValid">
            <template v-for="help of nameHelp">
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
              :disabled="!isTypeDifferent"
              @click="type = selected.type"
              v-if="mode == 'update'"
            />
            <span class="p-float-label">
              <Dropdown
                v-model="type"
                :options="types"
                optionLabel="name"
                optionValue="identifier"
              />
              <label for="type">Type</label>
            </span>
          </div>
          <small id="type-help" class="p-error" v-if="!isTypeValid">
            <template v-for="help of typeHelp">
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
import InputText from "primevue/inputtext";
import Dropdown from "primevue/dropdown";

export default {
  name: "PlaneModal",
  components: {
    Dialog,
    Button,
    InputText,
    Dropdown,
  },
  props: {
    visible: Boolean,
    selected: Object,
    mode: String,
    types: Array,
  },
  emits: ["update:visible"],
  data() {
    return {
      name: "",
      type: null,
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
    isNameValid() {
      return [this.name.length > 0].every((e) => e);
    },
    isTypeValid() {
      return [this.type !== null].every((e) => e);
    },
    isValid() {
      return [this.isNameValid, this.isTypeValid].every((e) => e);
    },
    nameHelp() {
      return [
        this.name.length <= 0
          ? "Name must have at least one non-black character"
          : null,
      ].filter((e) => !!e);
    },
    typeHelp() {
      return [this.type === null ? "Type is not optional" : null].filter(
        (e) => e
      );
    },
    isNameDifferent() {
      if (this.mode == "create") return false;
      return this.name !== this.selected?.name;
    },
    isTypeDifferent() {
      if (this.mode == "create") return false;
      return this.type !== this.selected?.type;
    },
    isDifferent() {
      return [this.isNameDifferent, this.isTypeDifferent].some((e) => e);
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
    types() {
      this.reset();
    },
  },
  methods: {
    reset() {
      if (this.mode == "create") {
        this.name = "";
        this.type = null;
      } else {
        this.name = this.selected?.name ?? "";
        this.type = this.selected?.type ?? null;
      }
    },
    accept() {
      if (this.mode === "create") {
        this.$store.dispatch("plane/sendMessage", {
          action: "CREATE",
          token: this.$store.state.session.session.token,
          name: this.name,
          type: this.type,
        });
      } else if (this.mode === "update") {
        this.$store.dispatch("plane/sendMessage", {
          action: "UPDATE",
          token: this.$store.state.session.session.token,
          identifier: this.selected.identifier,
          name: this.name,
          type: this.type,
        });
      }
    },
  },
};
</script>
