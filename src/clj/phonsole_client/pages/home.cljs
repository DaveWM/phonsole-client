(ns phonsole-client.pages.home
  (:require [reagent.core :as r]
            [re-frame.core :refer [subscribe register-sub dispatch register-handler]]
            [phonsole-client.components.console :refer [console]]))

(defn home-page []
  (let [clients (subscribe [:connected-consoles])]
    (fn []
      [:div {:class "home-page"}
       [:h4 "All Connected Consoles"]
       [:div {:class "flex justify-space-around wrap"}
        (if (empty? @clients)
          [:p {:class "no-consoles"} "You don't have any consoles connected to phonsole at the moment. For help getting started, see the instructions on the "
           [:a {:href "/help"} "Help Page."]]
          (map (fn [[client-id client]]
                 ^{:key (:client-id client)} [console client])
                    @clients))]])))
