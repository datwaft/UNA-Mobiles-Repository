export default {
  namespaced: true,
  state: () => ({
    connected: false,
    purchaseCountPerMonthInLastYear: null,
    revenuePerMonthInLastYear: null,
    clientsPerPlane: null,
    top5RoutesPerTicketNumber: null,
  }),
  mutations: {
    setConnection(state, value) {
      state.connected = value;
    },
    setPurchaseCountPerMonthInLastYear(state, value) {
      state.purchaseCountPerMonthInLastYear = value;
    },
    setRevenuePerMonthInLastYear(state, value) {
      state.revenuePerMonthInLastYear = value;
    },
    setClientsPerPlane(state, value) {
      state.clientsPerPlane = value;
    },
    setTop5RoutesPerTicketNumber(state, value) {
      state.top5RoutesPerTicketNumber = value;
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
  },
};
