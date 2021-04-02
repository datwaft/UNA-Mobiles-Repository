import { createStore } from "vuex";
import { createLogger } from "vuex";

import session from "./modules/session";
import sessionWebSocket from "./plugins/sessionWebSocket";

import user from "./modules/user";
import userWebSocket from "./plugins/userWebSocket";

import flight from "./modules/flight";
import flightWebSocket from "./plugins/flightWebSocket";

import schedule from "./modules/schedule";
import scheduleWebSocket from "./plugins/scheduleWebSocket";

let store = {
  strict: process.env.NODE_ENV !== "production",
  state: () => ({
    message: null,
  }),
  mutations: {
    setMessage(state, value) {
      state.message = value;
    },
  },
  actions: {
    processError({ commit }, message) {
      if ("type" in message) {
        let summary = "";
        let detail = "";
        switch (message.type) {
          case "DUPLICATE":
            summary = "Registration error";
            detail = "Username already exists";
            break;
          case "CREDENTIALS":
            summary = "Credentials error";
            detail = "Invalid credentials";
            break;
        }
        commit("setMessage", {
          severity: "error",
          summary: summary,
          detail: detail,
          life: 3000,
        });
      } else if ("message" in message) {
        commit("setMessage", {
          severity: "error",
          summary: "Error",
          detail: message.message,
          life: 3000,
        });
      } else {
        commit("setMessage", {
          severity: "error",
          summary: "Error",
          detail: "Unknown error",
          life: 3000,
        });
      }
    },
  },
  modules: {
    session,
    user,
    flight,
    schedule,
  },
  plugins: [
    createLogger(),
    sessionWebSocket(),
    userWebSocket(),
    flightWebSocket(),
    scheduleWebSocket(),
  ],
};

export default createStore(store);
