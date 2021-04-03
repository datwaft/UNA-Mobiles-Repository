import connection from "@/assets/connection";

export default function sessionWebSocket() {
  return (store) => {
    const socket = new WebSocket(`ws://${connection.url}/flight`);
    socket.onopen = () => {
      store.dispatch("flight/connectionOpened");

      // Request data
      store.dispatch("flight/sendMessage", {
        action: "VIEW_ALL",
      });
    };
    socket.onclose = () => {
      store.dispatch("flight/connectionClosed");
    };
    socket.onerror = (event) => {
      store.dispatch("flight/connectionError", event);
    };
    // Message handler
    socket.onmessage = (event) => {
      let data = JSON.parse(event.data);
      switch (data.action) {
        case "VIEW_ALL":
          store.commit("flight/setView", data.view);
          break;
        case "ERROR":
          store.dispatch("processError", data);
          break;
      }
    };
    // Message listener
    store.subscribeAction((action, state) => {
      if (!state.session.connected) return;
      if (action.type === "flight/sendMessage") {
        socket.send(JSON.stringify(action.payload));
      }
    });
  };
}
