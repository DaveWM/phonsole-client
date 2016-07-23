(ns phonsole-client.pages.home
  (:require [reagent.core :as r]
            [re-frame.core :refer [subscribe register-sub dispatch register-handler]]
            [phonsole-client.components.console :refer [console]]))

(defn home-page []
  (let [clients (subscribe [:connected-consoles])]
    (fn []
      [:div
       [:h2 "All Connected Consoles"]
       [:div {:class "flex align-space-around wrap"}
        (if (empty? @clients)
          [:p "No Consoles connected"]
          (map (fn [[client-id client]]
                      (console client))
                    @clients))]])))
