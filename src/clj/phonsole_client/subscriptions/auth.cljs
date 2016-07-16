(ns phonsole-client.subscriptions.auth
  (:require [re-frame.core :refer [register-handler register-sub]]
            [phonsole-client.auth :refer [log-out! show-lock!]]
            [phonsole-client.push-events :refer [stop-listening!]])
  (:require-macros [reagent.ratom :refer [reaction]]))

(defn profile-retrieved [db [_ profile]]
   (assoc db :user-details profile))

(defn log-out [db _]
   (log-out!)
   (stop-listening!)
   (show-lock!)
   (dissoc db :user-details))

(defn user-details [db]
  (reaction (:user-details @db)))
