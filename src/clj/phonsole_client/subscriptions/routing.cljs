(ns phonsole-client.subscriptions.routing
  (:require [re-frame.core :refer [register-handler register-sub]])
  (:require-macros [reagent.ratom :refer [reaction]]))

(defn set-page [db [_ page]]
  (assoc db :page page))

(defn page [db]
  (reaction (:page @db)))
