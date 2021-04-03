import connection from "@/assets/connection";

export default function purchaseWebSocket() {
  return (store) => {
    const socket = new WebSocket(`ws://${connection.url}/purchase`);
    socket.onopen = () => {
      store.dispatch("purchase/connectionOpened");
    };
    socket.onclose = () => {
      store.dispatch("purchase/connectionClosed");
    };
    socket.onerror = (event) => {
      store.dispatch("purchase/connectionError", event);
    };
    // Message handler
    socket.onmessage = (event) => {
      let data = JSON.parse(event.data);
      switch (data.action) {
        case "CREATE":
          store.dispatch("purchase/emitCreated");
          break;
        case "VIEW_ALL":
          store.commit("purchase/setView", data.view);
          break;
        case "ERROR":
          store.dispatch("processError", data);
          break;
      }
    };
    // Message listener
    store.subscribeAction((action, state) => {
      if (!state.session.connected) return;
      if (action.type === "purchase/sendMessage") {
        socket.send(JSON.stringify(action.payload));
      }
    });
  };
}
