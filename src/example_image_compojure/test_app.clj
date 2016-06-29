(ns example-image-compojure.test-app
  (:require [image-compojure.core :as image-compojure])
  (:import (java.awt.geom RoundRectangle2D RoundRectangle2D$Double Rectangle2D$Double)
           (java.awt Polygon Color)))



(def second-transformation (image-compojure/transform
                             (image-compojure/translate 100 100)
                             (image-compojure/rotate 5)))

(def shapes-vec [(Rectangle2D$Double. 0 0 100 100)
                 (Polygon. (int-array [150 250 325 375 450 275 100])
                           (int-array [150 100 125 225 250 375 300]) 7)])

(def shapes-vec1 [(Polygon. (int-array [150 250 325 375 450 275 100])
                            (int-array [150 100 125 225 250 375 300]) 7)])

(def shapes-vec2 [(Rectangle2D$Double. 0 0 400 400)
                  ])

(def shapes-vec3 [(Rectangle2D$Double. 200 200 400 400)])

(defn example-one []
  (image-compojure/compose
    800 600
    {:antialiasing         :off
     :aplpha-interpolation :default
     :color-rendering      :quality
     :dithering            :disable
     :fractional-metrics   :on
     :interpolatioin       :bicubic
     :rendering            :default
     :stroke-control       :default
     :text-antialiasing    :default
     }

    (image-compojure/with-attributes
      {:width       1.0
       :join        :miter
       :miter-limit 10.0
       :cap         :square
       :dash        nil
       :dash-phase  0
       :composite   :src_over
       :alpha       1.0
       :paint       (image-compojure/color 125 125 125 255)
       :xor-mode    nil
       }

      (image-compojure/with-transform (image-compojure/transform
                                        (image-compojure/translate 100 100)
                                        (image-compojure/rotate 10)
                                        (image-compojure/shear 100 100)
                                        (image-compojure/scale 100 100))

                                      (image-compojure/line 1 1 2 2 {:paint     :red
                                                                     :linewidth 0.5
                                                                     :joinstyle :dash
                                                                     :composite :src})
                                      (image-compojure/with-transform second-transformation
                                                                      (image-compojure/rectangle 100 100 100 100)
                                                                      (image-compojure/rectangle 50 50 100 100 {:paint     :green
                                                                                                                :linewidth 0.5
                                                                                                                :joinstyle :dash
                                                                                                                :composite :source
                                                                                                                :fill      true})))
      (image-compojure/image 0 0 (image-compojure/load-image "src/test.png"))

      (image-compojure/with-attributes {:paint :black}
                                       (image-compojure/oval 0 0 500 500 {:paint :blue :fill true})
                                       (image-compojure/oval 0 0 500 500))

      (image-compojure/shapes [(Rectangle2D$Double. 0 0 100 100)
                               (Polygon. (int-array [150 250 325 375 450 275 100])
                                         (int-array [150 100 125 225 250 375 300]) 7)]
                              {:fill  true
                               :paint :yellow})
      )
    ))

;normal code in dsl
(defn example []
  (image-compojure/render
    (image-compojure/compose 800 2000
                             {:antialiasing :on}
                             (image-compojure/with-attributes
                               {:paint :red}
                               (dotimes [n 10]
                                 (doall (map
                                          (fn [val] (if (= 0 (mod n 2))
                                                      (image-compojure/line 400 50 (* n val) 1000)
                                                      (image-compojure/line 400 50 (* n val) 1000 {:paint :blue})))
                                          (range 100 106))))))))

(defn image-example []
  (let [image (image-compojure/load-image "res/test.png")]
    (image-compojure/image 0 0 image)
    (image-compojure/image 0 0 (/ (.getWidth image) 2) (/ (.getHeight image) 2) 0 0 (.getWidth image) (.getHeight image) image)
    ))



(defn test-comp []
  (let [img (image-compojure/load-image "res/bg-1.JPG")
        w (.getWidth img)
        h (.getHeight img)
        logo #(image-compojure/image 0 0 (image-compojure/load-image "res/Logo-white.png"))
        anyup-red (image-compojure/color 233 80 65 255)
        headline (image-compojure/create-styled-text "Headline" :sans-serif :italic 50
                                                     {:foreground (image-compojure/color :white)})
        wod (image-compojure/create-styled-text "wod-description" :sans-serif :bold 25
                                                {:foreground (image-compojure/color :white)})
        name (image-compojure/create-styled-text "score" :sans-serif :italic 30
                                                 {:foreground (image-compojure/color :white)})
        date (image-compojure/create-styled-text "score" :sans-serif :plain 20
                                                 {:foreground (image-compojure/color :white)})

        black (image-compojure/color 0 0 0 200)
        polygon (Polygon. (int-array [0, 0, w, w, (/ w 3), (/ w 3)])
                          (int-array [0, h, h, (* h 0.65), (* h 0.85), 0])
                          6)]
    (image-compojure/render
      (image-compojure/compose
        (.getWidth img) (.getHeight img) {:antialiasing :on}
        (image-compojure/image 0 0 img)
        (image-compojure/shapes [polygon] {:fill true :paint black})
        (image-compojure/shapes [polygon] {:paint anyup-red :width 8})
        (image-compojure/with-transform (image-compojure/transform
                                          (image-compojure/translate (* w 0.5) (* h 0.8))
                                          (image-compojure/rotate 0))
                                        (logo))
        (image-compojure/with-transform
          (image-compojure/transform
            (image-compojure/translate 0 200))
          ;-------------Name ------------------------------
          (image-compojure/styled-text 50 0 (merge name {:text "Nicolas Schwartau"}))
          (image-compojure/styled-text 100 30 (merge date {:text "Sonntag, 08.05.2016"}))
          (image-compojure/line 50 50 (- (/ w 3) 50) 50 {:paint anyup-red})


          ;---------------Wod Description---------------
          (image-compojure/styled-text 50 200 (merge headline {:text "Workout: DAWN"}))
          (image-compojure/styled-text 100 250 (merge wod {:text "Auf Zeit:"}))
          (image-compojure/styled-text 100 300 (merge wod {:text "50  Squats"}))
          (image-compojure/styled-text 100 335 (merge wod {:text "40  Sit-Ups"}))
          (image-compojure/styled-text 100 370 (merge wod {:text "30  Push-Ups"}))
          (image-compojure/styled-text 100 405 (merge wod {:text "10  Pull-Ups"}))
          ;-----------------Score---------------
          (image-compojure/line 50 600 (- (/ w 3) 50) 600 {:paint anyup-red})
          (image-compojure/styled-text 50 700 (merge wod {:text "Dein Ergebnis:"}))
          (image-compojure/styled-text 100 800 (merge headline {:text "5:33 Minuten"}))
          )))))





(defn color-example [shapes1 shapes2 shapes3]
  (let [colors [(image-compojure/color :black) (image-compojure/color 255 0 0 125) (image-compojure/color 0 0 255)]
        texture (image-compojure/texture-paint (image-compojure/load-image "res/txtr.JPG") 0 0 50 50)
        gradient-paint (image-compojure/gradient-paint 200 200 (first colors) 600 600 (second colors) true)]
    (image-compojure/shapes shapes2 {:fill true :paint (first colors)})
    (image-compojure/shapes shapes3 {:fill true :paint gradient-paint})
    (image-compojure/shapes shapes1 {:fill true :paint texture})))

(defn text-example []
  (image-compojure/background :black)
  (image-compojure/styled-text 50 200 (image-compojure/create-styled-text
                                        "IMAGE-COMPOJURE"
                                        :times :bold 50 {:foreground    (image-compojure/color :yellow)
                                                         :background    (image-compojure/color :red)
                                                         :kerning       false
                                                         :strikethrough false
                                                         :swap-colors   false
                                                         :underline     :low-on-pixel
                                                         :weight        :weight-demibold
                                                         :width         :width-condensed
                                                         })))

(defn render-example [image shape-obj]
  (image-compojure/render image {:as :file :path "res/output.png" :clipping shape-obj})
  (image-compojure/render image {:as :json})
  (image-compojure/render image))



(defn with-attributes-example []
  (image-compojure/with-attributes {:paint :yellow :width 50}
                                   (image-compojure/oval 50 50 500 500 {:paint :black :fill true :width 200})
                                   (image-compojure/oval 50 50 500 500)))

(defn test-me []
  (image-compojure/render (image-compojure/compose 600 600
                                                   (color-example shapes-vec1 shapes-vec2 shapes-vec3)))
  (image-compojure/render (image-compojure/compose 600 600 {:text-antialiasing :on}
                                                   (text-example)))
  (image-compojure/render (image-compojure/compose 600 600 {:antialiasing :on}
                                                   (with-attributes-example)))
  (image-compojure/render (image-compojure/compose 1000 1000 {:antialiasing :on}
                                                   (image-example)))
  (example))