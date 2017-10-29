(ns clj-mandelbrot-javafx.core
  (:require [fn-fx.fx-dom :as dom]
            [fn-fx.controls :as controls]
            [fn-fx.diff :refer [component defui render should-update?]]
            [fn-fx.util :as util]
            [clojure.java.io :as io])
  (:import (javax.imageio.ImageIO)
           (java.awt.Color)))

(def width 900)
(def height 600)
(def depth 30)

(defn mandelbrotbean
  "mandelbrotbean \n
  x means x-position, y means y-position, \n
  opacity means javafx.scene.paint.Color instance \n
  Return: javafx.scene.paint.Color instance"
  [pos color]
  (let [cx (first pos)
        cy (second pos)]
    (loop [x 0 y 0 times 1]
      (cond
        (== times depth) (.deriveColor color 0.0 1.0 0.0 1.0)
        (> (+ (* x x) (* y y)) 4) (.deriveColor color 0.0 1.0 (* 4 (/ 1 times)) 1.0)
        :else (recur (+ (* x x) (* -1.0 y y) cx)
                     (+ (* 2.0 x y) cy)
                     (inc times))))))
(def elem (mandelbrotbean [2 -1] (javafx.scene.paint.Color. 0 0 1.0 1.0)))

(defn write-image [int_list]
  (let [img (java.awt.image.BufferedImage. width height (java.awt.image.BufferedImage/TYPE_INT_ARGB))]
    (.setRGB img 0 0 width height  (int-array int_list) 0 width)
    (javax.imageio.ImageIO/write img "png" (java.io.File. (.getFile (io/resource "image.png")))))
  (str "Draw finish"))

(defn mandelbrot
  "get mandelbrot list \n
  pos means {:x-min :x-max :y-min :y-max}, \n
  color means javafx.scene.paint.Color inst(def ex (atom nil))
  Return: image file as 'image.png'
  Notice : clojure cannot save file as jpeg "
  [pos color]
  (let [x-min (:x-min pos)
        x-max (:x-max pos)
        y-min (:y-min pos)
        y-max (:y-max pos)
        pos-data (for [k (range y-min y-max (/ (- y-max y-min) height))
                       i (range x-min x-max (/ (- x-max x-min ) width))] [i k])]
    (write-image (doall (for [pos-list pos-data]
                          (let [precolor (mandelbrotbean pos-list color)
                                recolor (java.awt.Color. (float (.getRed precolor))
                                                         (float (.getGreen precolor))
                                                         (float (.getBlue precolor))
                                                         (float (.getOpacity precolor)))]
                            (.getRGB recolor))))))
  (str "Finish"))

(mandelbrot {:x-min -2 :x-max 1
             :y-min -1 :y-max 1}
            (javafx.scene.paint.Color/BLUE))

(def initial-state
  {:root-stage? true
   :data (mandelbrot {:x-min -2 :x-max 1 :y-min -1 :y-max 1} (javafx.scene.paint.Color/BLUE))
   :pos {:x-min -2 :x-max 1
         :y-min -1 :y-max 1}
   :color (javafx.scene.paint.Color/BLUE)})

(defonce data-state (atom initial-state))

(defn force-exit [root-stage?]
  (reify javafx.event.EventHandler
    (handle [this event]
      (when-not root-stage?
        (println "Closing application")
        (javafx.application.Platform/exit)))))

(defmulti handle-event (fn [_ {:keys [event]}]
                         event))

(defmethod handle-event :reset
  [_ {:keys [root-stage?]}]
  (assoc initial-state :root-stage? root-stage?))

(defmethod handle-event :paint
  [{:keys [data] :as datas} {:keys [fn-fx/includes]}]
  (let [writer (.getGraphicsContext2D (:target (:fn-fx/event includes)))]
    (.setFill writer (javafx.scene.paint.Color/BLUE))
    (.fillRect writer 50 50 150 150)
    ))

(defui Stage
  (render [this {:keys [root-stage? data pos] :as state}]
          (controls/stage
           :title "Fractals: Mandelbrot"
           :on-close-request (force-exit root-stage?)
           :shown true
           :scene (controls/scene
                   :root (controls/border-pane
                          :top (controls/h-box
                                :padding (javafx.geometry.Insets. 15 12 15 12)
                                :spacing 80
                                :alignment (javafx.geometry.Pos/CENTER)
                                :children [(controls/grid-pane
                                            :alignment (javafx.geometry.Pos/CENTER)
                                            :hgap 10
                                            :vgap 10
                                            :padding (javafx.geometry.Insets. 0 10 0 10)
                                            :children [(controls/text
                                                        :text (str "x-min: " (:x-min pos))
                                                        :font (controls/font
                                                               :family "Verdana"
                                                               :weight :normal
                                                               :size 15)
                                                        :grid-pane/column-index 0
                                                        :grid-pane/row-index 0)
                                                       (controls/text
                                                        :text (str "x-max: " (:x-max pos))
                                                        :font (controls/font
                                                               :family "Verdana"
                                                               :weight :normal
                                                               :size 15)
                                                        :grid-pane/column-index 0
                                                        :grid-pane/row-index 1)
                                                       (controls/text
                                                        :text (str "y-min: " (:y-min pos))
                                                        :font (controls/font
                                                               :family "Verdana"
                                                               :weight :normal
                                                               :size 15)
                                                        :grid-pane/column-index 1
                                                        :grid-pane/row-index 0)
                                                       (controls/text
                                                        :text (str "y-max: " (:y-max pos))
                                                        :font (controls/font
                                                               :family "Verdana"
                                                               :weight :normal
                                                               :size 15)
                                                        :grid-pane/column-index 1
                                                        :grid-pane/row-index 1)])
                                           (controls/color-picker
                                            :value (javafx.scene.paint.Color/CORAL))
                                           (controls/button 
                                            :text (str " + "))
                                           (controls/button
                                            :text (str " - "))
                                           (controls/button
                                            :text (str " < "))
                                           (controls/v-box
                                            :alignment (javafx.geometry.Pos/CENTER)
                                            :padding (javafx.geometry.Insets. 0 10 0 10)
                                            :children [(controls/button
                                                        :text (str " ^ "))
                                                       (controls/button
                                                        :text (str " v "))])
                                           (controls/button
                                            :text (str " >"))
                                           ])
                          :center (controls/image-view
                                   :id :canvas
                                   :fit-width width
                                   :smooth true
                                   :image (javafx.scene.image.Image. "image.png")
                                   )
                          :bottom (controls/border-pane
                                   :center (controls/text
                                              :text "development"
                                              :text-alignment (javafx.scene.text.TextAlignment/CENTER))))))))

(defn start
  ([] (start {:root-stage? true}))
  ([{:keys [root-stage?]}]
   (swap! data-state assoc :root-stage? root-stage?)
   (let [handler-fn (fn [event]
                      (println event)
                      (try
                        (swap! data-state handle-event event)
                        (catch Throwable exception
                          (println exception))))
         ui-state (agent (dom/app (stage @data-state )
                                  handler-fn))]
     (add-watch
      data-state :ui
      (fn [_ _ _ _]
        (send ui-state
              (fn [old-ui]
                (println "-- State Updated --")
                (println @data-state)
                (dom/update-app old-ui
                                (stage @data-state)))))))))

