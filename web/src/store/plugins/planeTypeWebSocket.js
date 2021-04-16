import connection from "@/assets/connection";

export default function () {
  return (store) => {
    const socket = new WebSocket(`ws://${connection.url}/plane-type`);
    socket.onopen = () => {
      store.dispatch("planeType/connectionOpened");
    };
    socket.onclose = () => {
      store.dispatch("planeType/connectionClosed");
    };
    socket.onerror = (event) => {
      store.dispatch("planeType/connectionError", event);
    };
    // Message handler
    socket.onmessage = (event) => {
      let data = JSON.parse(event.data);
      switch (data.action) {
        case "GET_ALL":
          store.commit("planeType/setData", data.data);
          break;
        case "CREATE":
          store.dispatch("planeType/emitCreated", data);
          break;
        case "UPDATE":
          store.dispatch("planeType/emitUpdated", data);
          break;
        case "ERROR":
          store.dispatch("processError", data);
          break;
      }
    };
    // Message listener
    store.subscribeAction((action, state) => {
      if (!state.planeType.connected) return;
      if (action.type === "planeType/sendMessage") {
        socket.send(JSON.stringify(action.payload));
      }
    });
  };
}
