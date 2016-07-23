(ns phonsole-client.components.connection-status)

(defn connection-status [connected]
  [:span {:class (str "connection-status " (when connected "connected"))}
   [:i {:class "material-icons" :title (if connected "Connected" "Disconnected")}
    (if connected "network_wifi" "signal_wifi_off")]])
