(ns phonsole-client.components.console
  (:require [reagent.core :as r]
            [re-frame.core :refer [dispatch]]
            [phonsole-client.components.connection-status :refer [connection-status]]
            [clojure.string :as str]
            [goog.string :as gstring]))

(defn colour [colour]
  {:style {:color colour}})

(defn background [colour]
  {:style {:background-color colour}})

(defn convert-index-map [to-convert transform starting-index]
  (->> to-convert
       (map
        (fn [[offset item-vec]]
          (map-indexed (fn [i item] [(+ i starting-index offset) (transform item)]) item-vec)))
       (into {})))

(def ansi-codes
  (let [colours {0 ["black" "red" "green" "yellow" "blue" "darkmagenta" "cyan" "lightgrey"]
                 60 ["darkgrey" "lightpink" "lightgreen" "lightyellow" "lightblue" "magenta" "lightcyan" "white"]}]
    (merge {
            1 {:style {:font-weight "bold"}}
            2 {:style {:opacity 0.5}}
            4 {:style {:text-decoration "underline"}}
            8 {:style {:visibility "hidden"}}
            0 {:style {:font-weight "normal" :opacity 1 :text-decoration "none" :visibility "visible"}
               :class "default-colour default-background"}
            21 {:style {:font-weight "normal"}}
            22 {:style {:opacity 1}}
            24 {:style {:text-decoration "none"}}
            28 {:style {:visibility "visible"}}

            39 {:class "default-colour"}
            49 {:class "default-background"}
            }
           (convert-index-map colours colour 30)
           (convert-index-map colours background 40))))

(defn line-parts-to-hiccup [[first-part & other-parts :as parts]]
  (if (empty? parts)
    nil
    (->> (if-let [[match group] (re-matches #"\[(\d*)m" first-part)]
           (let [number (if (empty? group)
                          0
                          (js/parseInt group))]
             [:span (or (get ansi-codes number) {}) (line-parts-to-hiccup other-parts)])
           [:span first-part (line-parts-to-hiccup other-parts)])
         (remove nil?)
         vec)))

(defn line-to-hiccup [line]
  (line-parts-to-hiccup (-> line
                            (str/replace "\t" "&nbsp;&nbsp;")
                            (str/replace " " "&nbsp;")
                            (gstring/unescapeEntities)
                            (str/split #"\u001B(\[\d*m)")
                            (->> (remove empty?)))))

(defn console-output [output]
  [:div {:class "output"}
   (-> (vec output)
       (conj ">_")
       (->>
        (map #(identity [:p {:class "line"} (line-to-hiccup %)]))))])

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
