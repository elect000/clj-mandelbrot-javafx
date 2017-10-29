(ns clj-mandelbrot-javafx.javafx-init
  (:require [clj-mandelbrot-javafx.core :as core])
  (:import  (javafx.application Application))
  (:gen-class
   :extends javafx.application.Application)
  )

(defn -start [app stage]
  (core/start
   {:root-stage? false}))

(defn -main [& args]
  (Application/launch clj_mandelbrot_javafx.javafx_init (into-array String args)))

