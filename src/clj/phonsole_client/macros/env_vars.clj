(ns phonsole-client.macros.env-vars
  (require [environ.core :refer [env]]))


(defmacro env-var [var-name]
  (env var-name))
