(ns phonsole-client.subscriptions.register
  (:require [re-frame.core]
            [phonsole-client.subscriptions.auth :as auth]
            [phonsole-client.subscriptions.consoles :as consoles]
            [phonsole-client.subscriptions.initialise :as init]
            [phonsole-client.subscriptions.routing :as routing])
  (:require-macros [phonsole-client.macros.register-sub-functions :refer [register-subs register-handlers]]))

(defn register []
  (register-handlers auth/profile-retrieved
                     auth/log-out
                     [:clients consoles/connected]
                     [:sender consoles/output]
                     consoles/remove-console
                     init/initialise
                     routing/set-page)
  (register-subs auth/user-details
                 consoles/connected-consoles
                 routing/page))
