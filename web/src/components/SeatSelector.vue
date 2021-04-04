<template>
  <div class="seat-selector">
    <table>
      <tr>
        <td />
        <td class="number" v-for="(_, r) in matrix[1]" :key="r">
          {{ r + 1 }}
        </td>
      </tr>
      <tr v-for="(column, c) in matrix" :key="c">
        <td :class="column.length === 0 ? 'empty' : 'number'">
          <template v-if="column.length !== 0">
            {{ calculateColumnNumber(c) + 1 }}
          </template>
        </td>
        <td v-for="(seat, r) in column" :key="r">
          <button
            @click="selectSeat(r, calculateColumnNumber(c))"
            class="seat"
            :class="getSeatClass(seat)"
            :disabled="readonly || getSeatClass(seat) === 'occupied'"
          />
        </td>
      </tr>
    </table>
  </div>
</template>

<script>
export default {
  name: "SeatSelector",
  props: {
    ticketsToPurchase: Number,
    tickets: Array,
    ticketsPerFlight: Array,
    ticketsPerPurchase: Array,
    columnNumber: Number,
    rowNumber: Number,
    readonly: Boolean,
  },
  emits: ["update:tickets"],
  computed: {
    selected: {
      get() {
        return this.tickets;
      },
      set(value) {
        this.$emit("update:tickets", value);
      },
    },
    selectedTickets() {
      return this.readonly ? this.ticketsPerPurchase : this.selected;
    },
    matrix() {
      let matrix = [];
      for (let c = 1; c <= this.columnNumber; ++c) {
        let row = [];
        for (let r = 1; r <= this.rowNumber; ++r) {
          let value = null;
          if (
            this.ticketsPerFlight.filter((o) => o.row === r && o.column === c)
              .length > 0
          ) {
            value = false;
          }
          if (
            this.selectedTickets.filter((o) => o.row === r && o.column === c)
              .length > 0
          ) {
            value = true;
          }
          row.push(value);
        }
        if (c == 4 && this.columnNumber == 6) {
          matrix.push([]);
        }
        if ((c == 4 || c == 7) && this.columnNumber == 9) {
          matrix.push([]);
        }
        matrix.push(row);
      }
      return matrix;
    },
  },
  methods: {
    getSeatClass(seat) {
      if (seat === true) {
        return "selected";
      } else if (seat === false) {
        return "occupied";
      } else {
        return "not-selected";
      }
    },
    calculateColumnNumber(number) {
      if (this.columnNumber == 6) {
        if (number >= 4) {
          return number - 1;
        } else {
          return number;
        }
      } else {
        if (number < 4) {
          return number;
        } else if (number < 7) {
          return number - 1;
        } else {
          return number - 2;
        }
      }
    },
    selectSeat(row, column) {
      row += 1;
      column += 1;
      if (
        this.selected.filter((v) => v.row === row && v.column === column)
          .length > 0
      ) {
        this.selected = this.selected.filter(
          (v) => !(v.row === row && v.column === column)
        );
      }
      if (this.ticketsToPurchase > this.selected.length) {
        this.selected.push({ row: row, column: column });
      }
    },
  },
};
</script>

<style scoped>
.number {
  --side: 2rem;
  width: var(--side);
  height: var(--side);
  text-align: center;
  vertical-align: middle;
}

.empty {
  --side: 2rem;
  width: var(--side);
  height: var(--side);
}

.seat {
  --side: 2rem;
  width: var(--side);
  height: var(--side);
}

.selected {
  background-color: var(--green-300);
}
.not-selected {
  background-color: var(--surface-0);
}
.occupied {
  background-color: var(--pink-300);
}
</style>
