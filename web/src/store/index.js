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

import purchase from "./modules/purchase";
import purchaseWebSocket from "./plugins/purchaseWebSocket";

import ticket from "./modules/ticket";
import ticketWebSocket from "./plugins/ticketWebSocket";

import planeType from "./modules/plane-type";
import planeTypeWebSocket from "./plugins/planeTypeWebSocket";

import route from "./modules/route";
import routeWebSocket from "./plugins/routeWebSocket";

import plane from "./modules/plane";
import planeWebSocket from "./plugins/planeWebSocket";

import report from "./modules/report";
import reportWebSocket from "./plugins/reportWebSocket";

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
    purchase,
    ticket,
    planeType,
    route,
    plane,
    report,
  },
  plugins: [
    createLogger(),
    sessionWebSocket(),
    userWebSocket(),
    flightWebSocket(),
    scheduleWebSocket(),
    purchaseWebSocket(),
    ticketWebSocket(),
    planeTypeWebSocket(),
    routeWebSocket(),
    planeWebSocket(),
    reportWebSocket(),
  ],
};

export default createStore(store);
