(defproject warp "0.7.0"
  :main warp.main
  :profiles {:uberjar {:aot :all}}
  :plugins [[lein-cljsbuild "1.1.3"]]
  :dependencies [[org.clojure/clojure           "1.8.0"]
                 [org.clojure/tools.logging     "0.3.1"]
                 [org.clojure/tools.cli         "0.3.3"]
                 [org.clojure/core.async        "0.2.374"]
                 [com.stuartsierra/component    "0.3.1"]
                 [spootnik/unilog               "0.7.13"]
                 [spootnik/uncaught             "0.5.3"]
                 [spootnik/signal               "0.2.0"]
                 [spootnik/watchman             "0.3.5"]
                 [spootnik/net                  "0.2.11"]
                 [bidi                          "1.25.0"]
                 [cheshire                      "5.5.0"]
                 [org.javassist/javassist       "3.20.0-GA"]

                 [org.clojure/clojurescript     "1.8.34"]
                 [reagent                       "0.6.0-alpha"]
                 [cljs-http                     "0.1.39"]]
  :clean-targets ^{:protect false} [:target-path "resources/public/warp"]
;;  :prep-tasks ["compile" ["cljsbuild" "once"]]
  :cljsbuild {:builds [{:id "warp"
                        :source-paths ["src/warp/client"]
                        :compiler {:output-to     "resources/public/warp/app.js"
                                   :output-dir    "resources/public/warp"
                                   :asset-path    "/warp"
                                   :optimizations :whitespace
                                   :pretty-print  false}
                        :main     warp.client.app}]})
