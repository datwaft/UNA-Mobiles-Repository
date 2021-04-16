import connection from "@/assets/connection";

export default function () {
  return (store) => {
    const socket = new WebSocket(`ws://${connection.url}/route`);
    socket.onopen = () => {
      store.dispatch("route/connectionOpened");
    };
    socket.onclose = () => {
      store.dispatch("route/connectionClosed");
    };
    socket.onerror = (event) => {
      store.dispatch("route/connectionError", event);
    };
    // Message handler
    socket.onmessage = (event) => {
      let data = JSON.parse(event.data);
      switch (data.action) {
        case "GET_ALL":
          store.commit("route/setData", data.data);
          break;
        case "CREATE":
          store.dispatch("route/emitCreated", data);
          break;
        case "UPDATE":
          store.dispatch("route/emitUpdated", data);
          break;
        case "ERROR":
          store.dispatch("processError", data);
          break;
      }
    };
    // Message listener
    store.subscribeAction((action, state) => {
      if (!state.route.connected) return;
      if (action.type === "route/sendMessage") {
        socket.send(JSON.stringify(action.payload));
      }
    });
  };
}
