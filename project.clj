(defproject clj-mandelbrot-javafx "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.9.0-alpha13"]
                 [halgari/fn-fx "0.4.0"]]
  :plugins [[refactor-nrepl "2.3.1"]
            [cider/cider-nrepl "0.14.0"]]
  :main clj-mandelbrot-javafx.javafx-init
  :aot [clj-mandelbrot-javafx.javafx-init]
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}})
