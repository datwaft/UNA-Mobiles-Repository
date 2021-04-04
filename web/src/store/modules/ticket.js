export default {
  namespaced: true,
  state: () => ({
    connected: false,
    viewPerFlight: {},
    viewPerPurchase: {},
  }),
  mutations: {
    setConnection(state, value) {
      state.connected = value;
    },
    setViewPerFlight(state, { flight, view }) {
      state.viewPerFlight[flight] = view;
    },
    setViewPerPurchase(state, { purchase, view }) {
      state.viewPerPurchase[purchase] = view;
    },
  },
  actions: {
    sendMessage() {},
    connectionOpened({ commit }) {
      commit("setConnection", true);
    },
    connectionClosed({ commit }) {
      commit("setConnection", false);
    },
    connectionError({ commit }) {
      commit(
        "setMessage",
        {
          severity: "error",
          summary: "Server connection error",
          detail: "The connection to the server couldn't be made",
        },
        { root: true }
      );
    },
    emitCreated({ commit }) {
      commit(
        "setMessage",
        {
          severity: "success",
          summary: "Success",
          detail: "Your reservation was registered successfully",
          life: 3000,
        },
        { root: true }
      );
    },
  },
};
