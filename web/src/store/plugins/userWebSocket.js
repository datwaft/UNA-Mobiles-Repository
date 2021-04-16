import connection from "@/assets/connection";

export default function () {
  return (store) => {
    const socket = new WebSocket(`ws://${connection.url}/user`);
    socket.onopen = () => {
      store.dispatch("user/connectionOpened");
    };
    socket.onclose = () => {
      store.dispatch("user/connectionClosed");
    };
    socket.onerror = (event) => {
      store.dispatch("user/connectionError", event);
    };
    // Message handler
    socket.onmessage = (event) => {
      let data = JSON.parse(event.data);
      switch (data.action) {
        case "REGISTER":
          store.dispatch("user/emitRegistered");
          break;
        case "GET":
          store.commit("user/setGet", data.get);
          break;
        case "UPDATE":
          store.dispatch("user/emitUpdated");
          break;
        case "ERROR":
          store.dispatch("processError", data);
          break;
      }
    };
    // Message listener
    store.subscribeAction((action, state) => {
      if (!state.user.connected) return;
      if (action.type === "user/sendMessage") {
        socket.send(JSON.stringify(action.payload));
      }
    });
  };
}
