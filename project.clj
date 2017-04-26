(defproject phonsole-client "0.1.0"
  :description "The client for the phonsole application"
  :url "https://www.phonsole.co.uk"
  :license {:name "GNU Public License V3"
            :url "https://www.gnu.org/licenses/gpl-3.0.en.html"}

  :min-lein-version "2.6.1"
  
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [org.clojure/clojurescript "1.8.51"]
                 [org.clojure/core.async "0.2.374" :exclusions [org.clojure/tools.reader]]
                 [re-frame "0.7.0"]
                 [reagent "0.6.0-alpha2"]
                 [cljsjs/auth0-lock "8.3.2-2"]
                 [secretary "1.2.3"]
                 [kibu/pushy "0.3.6"]
                 [com.taoensso/sente "1.10.0"]
                 [environ "1.0.3"]
                 [com.cognitect/transit-cljs "0.8.239"]
                 [cljs-http "0.1.41"]]
  :npm {:dependencies [[materialize-css "0.97.6"]
                       [node-sass "3.8.0"]]}
  
  :plugins [[lein-figwheel "0.5.3-2"]
            [lein-cljsbuild "1.1.3" :exclusions [[org.clojure/clojure]]]
            [lein-npm "0.6.2"]
            [lein-resource "15.10.2"]]

  :source-paths ["src"]

  :clean-targets ^{:protect false} ["resources/public/js/compiled" "target"]

  :cljsbuild {:builds
              [{:id "dev"
                :source-paths ["src"]

                ;; If no code is to be run, set :figwheel true for continued automagical reloading
                :figwheel {:on-jsload "phonsole-client.core/on-js-reload"}

                :compiler {:main phonsole-client.core
                           :asset-path "js/compiled/out"
                           :output-to "resources/public/js/app.js"
                           :output-dir "resources/public/js/compiled/out"
                           :source-map-timestamp true}}
               ;; This next build is an compressed minified build for
               ;; production. You can build this with:
               ;; lein cljsbuild once min
               {:id "prod"
                :source-paths ["src"]
                :compiler {:output-to "resources/public/js/app.js"
                           :main phonsole-client.core
                           :optimizations :advanced
                           :pretty-print false}}]}

  :figwheel {;; :http-server-root "public" ;; default and assumes "resources"
             ;; :server-port 3449 ;; default
             ;; :server-ip "127.0.0.1"

             :css-dirs ["resources/public/css"] ;; watch and update CSS

             ;; Start an nREPL server into the running figwheel process
             ;; :nrepl-port 7888

             ;; Server Ring Handler (optional)
             ;; if you want to embed a ring handler into the figwheel http-kit
             ;; server, this is for simple ring servers, if this
             ;; doesn't work for you just run your own server :) (see lien-ring)
             
             ;; :ring-handler hello_world.server/handler

             ;; To be able to open files in your editor from the heads up display
             ;; you will need to put a script on your path.
             ;; that script will have to take a file path and a line number
             ;; ie. in  ~/bin/myfile-opener
             ;; #! /bin/sh
             ;; emacsclient -n +$2 $1
             ;;
             ;; :open-file-command "myfile-opener"
             
             ;; if you are using emacsclient you can just use
             ;; :open-file-command "emacsclient"

             ;; if you want to disable the REPL
             ;; :repl false

             ;; to configure a different figwheel logfile path
             ;; :server-logfile "tmp/logs/figwheel-logfile.log"
             }

  :resource {:resource-paths ["src/js"]
             :target-path "resources/public"}
 
  ;; setting up nREPL for Figwheel and ClojureScript dev
  ;; Please see:
  ;; https://github.com/bhauman/lein-figwheel/wiki/Using-the-Figwheel-REPL-within-NRepl
  
  :profiles {:dev {:dependencies [[figwheel-sidecar "0.5.3-2"]
                                  [com.cemerick/piggieback "0.2.1"]]
                   ;; need to add dev source path here to get user.clj loaded
                   :source-paths ["src/clj" "dev"]
                   ;; for CIDER
                   ;; :plugins [[cider/cider-nrepl "0.12.0"]]
                   :repl-options {; for nREPL dev you really need to limit output
                                  :init (set! *print-length* 50)
                                  :nrepl-middleware [cemerick.piggieback/wrap-cljs-repl]}}}

)
