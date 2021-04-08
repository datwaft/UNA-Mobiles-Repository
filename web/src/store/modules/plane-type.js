export default {
  namespaced: true,
  state: () => ({
    connected: false,
    data: null,
  }),
  mutations: {
    setConnection(state, value) {
      state.connected = value;
    },
    setData(state, value) {
      state.data = value;
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
          detail: "The plane type was created successfully",
          life: 3000,
        },
        { root: true }
      );
    },
    emitUpdated({ commit }) {
      commit(
        "setMessage",
        {
          severity: "success",
          summary: "Success",
          detail: "The plane type was updated successfully",
          life: 3000,
        },
        { root: true }
      );
    },
  },
};
