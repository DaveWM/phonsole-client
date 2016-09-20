(ns phonsole-client.macros.config
  (:require [environ.core :refer [env]]))

(def defaults
  {:server-url "localhost:8080"})


(defmacro config [key]
  (let [value (or (env key)
                  (defaults key))]
    (if (nil? value)
      (throw (Exception. (str "No config var found for key" (name key))))
      value)))
