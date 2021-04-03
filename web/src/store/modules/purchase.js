export default {
  namespaced: true,
  state: () => ({
    connected: false,
    view: null,
  }),
  mutations: {
    setConnection(state, value) {
      state.connected = value;
    },
    setView(state, value) {
      state.view = value;
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
          detail: "Your purchase was registered successfully",
          life: 3000,
        },
        { root: true }
      );
    },
  },
};
