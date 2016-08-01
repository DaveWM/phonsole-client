(ns phonsole-client.local-storage
  (:require [cognitect.transit :as t]))

(def reader (t/reader :json))
(def writer (t/writer :json))

(defn set! [key value]
  (.setItem js/localStorage (name key) (t/write writer value)))

(defn get [key]
  (->> (.getItem js/localStorage (name key))
       (t/read reader)))

(defn delete [key]
  (.removeItem js/localStorage (name key)))
