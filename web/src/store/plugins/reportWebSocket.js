import connection from "@/assets/connection";

export default function () {
  return (store) => {
    const socket = new WebSocket(`ws://${connection.url}/report`);
    socket.onopen = () => {
      store.dispatch("report/connectionOpened");
    };
    socket.onclose = () => {
      store.dispatch("report/connectionClosed");
    };
    socket.onerror = (event) => {
      store.dispatch("report/connectionError", event);
    };
    // Message handler
    socket.onmessage = (event) => {
      let data = JSON.parse(event.data);
      switch (data.action) {
        case "PURCHASE_COUNT_PER_MONTH_IN_LAST_YEAR":
          store.commit(
            "report/setPurchaseCountPerMonthInLastYear",
            data.report
          );
          break;
        case "REVENUE_PER_MONTH_IN_LAST_YEAR":
          store.commit("report/setRevenuePerMonthInLastYear", data.report);
          break;
        case "CLIENTS_PER_PLANE":
          store.commit("report/setClientsPerPlane", data.report);
          break;
        case "TOP_5_ROUTES_PER_TICKET_NUMBER":
          store.commit("report/setTop5RoutesPerTicketNumber", data.report);
          break;
        case "ERROR":
          store.dispatch("processError", data);
          break;
      }
    };
    // Message listener
    store.subscribeAction((action, state) => {
      if (!state.session.connected) return;
      if (action.type === "report/sendMessage") {
        socket.send(JSON.stringify(action.payload));
      }
    });
  };
}
