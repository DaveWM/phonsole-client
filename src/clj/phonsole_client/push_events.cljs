(ns phonsole-client.push-events
  (:require [taoensso.sente :as sente :refer (cb-success?)]
            [re-frame.core :refer [dispatch]]
            [cljs.core.async :refer [<! close!]])
  (:require-macros [cljs.core.async.macros :refer [go go-loop]]
                   [phonsole-client.macros.config :refer [config]]))

(def allowed-event-types #{:clients/connected :sender/output})

(defonce channel (atom nil))

(defn stop-listening! []
  (println @channel)
  (when-let [chsk @channel]
    (sente/chsk-disconnect! chsk)
    (reset! channel nil)))

(defn start-listening! [token]
  "Starts listening to the sente channel. Returns a function that disconnects from the channel"
  (when (nil? @channel)
    (print "start listening" (config :server-url))
    (let [{chsk :chsk ch-chsk :ch-recv chsk-send! :send-fn chsk-state :state} (sente/make-channel-socket! "/chsk" ; Note the same path as before
                                                                                                          {:type :auto
                                                                                                           :host (config :server-url)
                                                                                                           :params {:Authorization token
                                                                                                                    :is-viewer true}})
          event-loop (go-loop []
                       (let [{:keys [id ?data]}  (<! ch-chsk)]
                         (println id ?data)
                         (when (= id :chsk/recv)
                           (let [[id params] ?data]
                             (when (contains? allowed-event-types id)
                               (dispatch ?data)))))
                       (recur))]
      (add-watch chsk-state :connection #(dispatch [:connection-state-changed @chsk-state]))
      
      (reset! channel chsk)
      )))
