(ns phonsole-client.subscriptions.connection
  (:require-macros [reagent.ratom :refer [reaction]]))

(defn connection-state-changed [db [_ state]]
  (assoc db :connection state))

(defn connection-state [db [_]]
  (reaction (:connection @db)))
