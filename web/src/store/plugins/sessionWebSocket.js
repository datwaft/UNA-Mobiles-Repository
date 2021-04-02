import connection from "@/assets/connection";

export default function sessionWebSocket() {
  return (store) => {
    const socket = new WebSocket(`ws://${connection.url}/session`);
    socket.onopen = () => {
      store.dispatch("session/connectionOpened");
    };
    socket.onclose = () => {
      store.dispatch("session/connectionClosed");
    };
    socket.onerror = (event) => {
      store.dispatch("session/connectionError", event);
    };
    // Message handler
    socket.onmessage = (event) => {
      let data = JSON.parse(event.data);
      switch (data.action) {
        case "LOGIN":
          store.dispatch("session/signIn", data);
          break;
        case "LOGOUT":
          store.dispatch("session/signOut");
          break;
        case "ERROR":
          store.dispatch("processError", data);
          break;
      }
    };
    // Message listener
    store.subscribeAction((action, state) => {
      if (!state.session.connected) return;
      if (action.type === "session/sendMessage") {
        socket.send(JSON.stringify(action.payload));
      }
    });
  };
}
