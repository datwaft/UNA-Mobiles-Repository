import connection from "@/assets/connection";

export default function () {
  return (store) => {
    const socket = new WebSocket(`ws://${connection.url}/plane`);
    socket.onopen = () => {
      store.dispatch("plane/connectionOpened");
    };
    socket.onclose = () => {
      store.dispatch("plane/connectionClosed");
    };
    socket.onerror = (event) => {
      store.dispatch("plane/connectionError", event);
    };
    // Message handler
    socket.onmessage = (event) => {
      let data = JSON.parse(event.data);
      switch (data.action) {
        case "GET_ALL":
          store.commit("plane/setData", data.data);
          break;
        case "CREATE":
          store.dispatch("plane/emitCreated", data);
          break;
        case "UPDATE":
          store.dispatch("plane/emitUpdated", data);
          break;
        case "ERROR":
          store.dispatch("processError", data);
          break;
      }
    };
    // Message listener
    store.subscribeAction((action, state) => {
      if (!state.session.connected) return;
      if (action.type === "plane/sendMessage") {
        socket.send(JSON.stringify(action.payload));
      }
    });
  };
}
