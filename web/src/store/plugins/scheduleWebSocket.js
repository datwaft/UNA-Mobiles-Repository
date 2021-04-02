import connection from "@/assets/connection";

export default function sessionWebSocket() {
  return (store) => {
    const socket = new WebSocket(`ws://${connection.url}/schedule`);
    socket.onopen = () => {
      store.dispatch("schedule/connectionOpened");

      // Request data
      store.dispatch("schedule/sendMessage", {
        action: "GET_ALL_WITH_DISCOUNT",
      });
    };
    socket.onclose = () => {
      store.dispatch("schedule/connectionClosed");
    };
    socket.onerror = (event) => {
      store.dispatch("schedule/connectionError", event);
    };
    // Message handler
    socket.onmessage = (event) => {
      let data = JSON.parse(event.data);
      switch (data.action) {
        case "GET_ALL_WITH_DISCOUNT":
          store.commit("schedule/setData", data.value);
          break;
        case "ERROR":
          store.dispatch("processError", data);
          break;
      }
    };
    // Message listener
    store.subscribeAction((action, state) => {
      if (!state.session.connected) return;
      if (action.type === "schedule/sendMessage") {
        socket.send(JSON.stringify(action.payload));
      }
    });
  };
}
