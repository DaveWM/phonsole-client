(ns phonsole-client.components.console
  (:require [reagent.core :as r]
            [re-frame.core :refer [dispatch]]
            [phonsole-client.components.connection-status :refer [connection-status]]))

(defn console-output [output]
  [:div {:class "output"}
   (-> (vec output)
       (conj ">_")
       (->>
        (map #(identity [:p {:class "line"} %]))))])

(defn console [client]
  (let [tailing (atom false)]
    (r/create-class
     {:reagent-render (fn [client]
                        [:div {:class "console"}
                         [:div {:class "card"}
                          [:div {:class "output-container"}
                           (console-output (:output client))]
                          [:div {:class "card-content"}
                           [:p {:class "card-title"}
                            [:span {:class "margin-horizontal"} (:client-id client)]
                            (connection-status (not (:disconnected? client)))]]
                          [:div {:class "card-action"}
                           (when (:disconnected? client)
                             [:button {:class "btn btn-flat"
                                       :on-click #(dispatch [:remove-console (:client-id client)])}
                              "Close"])
                           [:span {:class "fill"}]
                           [:i {:class "material-icons icon-action"
                                :on-click #(-> %
                                               .-target
                                               .-parentElement
                                               .-parentElement
                                               .webkitRequestFullscreen)}
                            "open_with"]]]
                         ])
      :component-will-update (fn [this]
                               (let [elem (-> (r/dom-node this)
                                              (.querySelector ".output-container"))]
                                 (reset! tailing (<= (.-scrollHeight elem) (+ (.-scrollTop elem) (.-offsetHeight elem))))))
      :component-did-update (fn [this]
                              (let [elem (-> (r/dom-node this)
                                             (.querySelector ".output-container"))]
                                (when @tailing
                                  (set! (.-scrollTop elem) (.-scrollHeight elem)))))})))
