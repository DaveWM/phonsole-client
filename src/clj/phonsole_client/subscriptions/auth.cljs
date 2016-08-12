(ns phonsole-client.subscriptions.auth
  (:require [re-frame.core :refer [register-handler register-sub]]
            [phonsole-client.auth :refer [log-out! show-lock!]]
            [phonsole-client.push-events :refer [stop-listening!]]
            [phonsole-client.auth :refer [profile-key]]
            [phonsole-client.local-storage :as ls])
  (:require-macros [reagent.ratom :refer [reaction]]))

(defn profile-retrieved [db [_ profile]]
  (ls/set! profile-key profile)
  (assoc db profile-key profile))

(defn log-out [db _]
   (log-out!)
   (stop-listening!)
   (show-lock!)
   (dissoc db :user-details))

(defn user-details [db]
  (reaction (:user-details @db)))
