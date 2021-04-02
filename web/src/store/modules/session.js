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
    signIn(state, { username, token, authorization }) {
      state.data = {
        username,
        token,
        authorization,
      };
    },
    signOut(state) {
      state.data = null;
    },
  },
  actions: {
    sendMessage() {},
    connectionOpened({ commit }) {
      commit("setConnection", true);
    },
    connectionClosed({ commit }) {
      commit("setConnection", false);
      commit("signOut");
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
    signIn({ commit }, data) {
      commit("signIn", data);
      commit(
        "setMessage",
        {
          severity: "success",
          summary: "Success",
          detail: `Successfully logged in as "${data.username}"`,
          life: 3000,
        },
        { root: true }
      );
    },
    signOut({ commit }, data) {
      commit("signOut", data);
    },
  },
};
