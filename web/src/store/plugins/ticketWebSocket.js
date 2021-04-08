import connection from "@/assets/connection";

export default function () {
  return (store) => {
    const socket = new WebSocket(`ws://${connection.url}/ticket`);
    socket.onopen = () => {
      store.dispatch("ticket/connectionOpened");
    };
    socket.onclose = () => {
      store.dispatch("ticket/connectionClosed");
    };
    socket.onerror = (event) => {
      store.dispatch("ticket/connectionError", event);
    };
    // Message handler
    socket.onmessage = (event) => {
      let data = JSON.parse(event.data);
      switch (data.action) {
        case "CREATE":
          store.dispatch("ticket/emitCreated", data);
          // Update purchase view
          store.dispatch("purchase/sendMessage", {
            action: "VIEW_ALL",
            token: store.state.session.session.token,
          });
          break;
        case "VIEW_ALL_PER_FLIGHT":
          store.commit("ticket/setViewPerFlight", data);
          break;
        case "VIEW_ALL_PER_PURCHASE":
          store.commit("ticket/setViewPerPurchase", data);
          break;
        case "ERROR":
          store.dispatch("processError", data);
          break;
      }
    };
    // Message listener
    store.subscribeAction((action, state) => {
      if (!state.session.connected) return;
      if (action.type === "ticket/sendMessage") {
        socket.send(JSON.stringify(action.payload));
      }
    });
  };
}
