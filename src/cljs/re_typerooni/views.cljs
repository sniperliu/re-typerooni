(ns re-typerooni.views
  (:require [reagent.core :refer [atom]]
            [re-frame.core :refer [subscribe dispatch]]
            [re-typerooni.db :as d]))

;; -- util to map key event to map
(defn map->keyevent [e]
  {:value (some #(when (> % 0) %)
                [(.-charCode e) (.-which e) (.-keyCode e)])
   :target (.-target e)
   :timestamp (.-timeStamp e)})

(defmulti map->event (fn [e] (condp instance? (.-nativeEvent e)
                               js/KeyboardEvent :keyboard-event
                               :undefined-event)))
(defmethod map->event :keyboard-event [e]
  (map->keyevent e))
(defmethod map->event :undefined-event [e]
  nil)

(defn words-row [r row-words]
  (let [{:keys [row-offset
                current-word]} @(subscribe [:display-control])
        indexed-words (map-indexed vector row-words)]
    [:div {:style {:clear "both"}}
     (for [[id {:keys [word correctness]}] indexed-words]
       ^{:key id}
       [:span.target-word {:class (str
                                   (when (and (= id current-word)
                                              (= r row-offset))
                                     "current") \space
                                   (case correctness
                                     nil ""
                                     true "correct"
                                     false "incorrect"))
                           } word])]))

(defn rect->map [rect]
  {:top (.-top rect)
   :left (.-left rect)
   :bottom (.-bottom rect)
   :right (.-right rect)
   :height (.-height rect)
   :width (.-width rect)
   :length (.-length rect)})

(def words-row-update
  (with-meta words-row
    {:component-did-mount
     #(println "did mount "
               (rect->map (.getBoundingClientRect (reagent.core/dom-node %))))
     :component-did-update
     #(println "did update "
               (rect->map
                (.getBoundingClientRect (reagent.core/dom-node %))))}))

(defn display-panel [words]
  (when-not (empty? words)
    (let [{:keys [num-row-in-display
                  row-offset
                  current-word]} @(subscribe [:display-control])
          status (subscribe [:status])
          target-words (subvec words row-offset
                               (+ row-offset num-row-in-display))]
      [:div#target-word-view {:class (if (= @status :finished) "game-over" "")}
       [:div#target-words
        (for [[i ws] target-words]
          ^{:key i} [words-row i ws])]])))

(defn format-minute-timer [seconds]
  (let [m (int (/ seconds 60))
        s (mod seconds 60)]
    (str m ":" (when (< s 10) "0") s)))

(defn timer []
  (let [timer (subscribe [:timer])]
    [:div#game-timer {:style {:float "left"}}
     (format-minute-timer @timer)]))

(defn control-panel [status]
  (let [current-input (subscribe [:current-input])]
    [:div
     [:input#typing-test-input
      {:type "text"
       :value @current-input
       :on-key-down #(dispatch [:key-down (map->keyevent %)])
       :on-key-press #(dispatch [:key-press (map->keyevent %)])
       :style {:width "400px"
               :float "left"
               :height "30px"
               :font-size "24"
               :padding-left "4px"
               :border-radius "4px"}}]
     [timer]
     [:div#reset {:on-click #(dispatch [:reset-wpm])
                  :style {:float "left"
                          :padding-left "10px"}}
      (case status
        :started "Restart"
        :finished "Start"
        "Reset")]]))

(defn stats-panel []
  (let [stats (subscribe [:stats])]
    [:div#games-analysis
     [:span#games-analysis-total-words
      (str "total words: " (:total-words @stats))]
     [:span#games-analysis-correct-words
      (str "correct words: ") (:total-correct-words @stats)]
     [:span#games-analysis-incorrect-words
      (str "incorrect words: " (:total-incorrect-words @stats))]
     [:span#games-analysis-wpm
      (str "wpm: " (:total-words-wpm @stats))]
     [:div#games-analysis-wordlets
      [:table
       [:tbody
        [:tr
         [:th "Wordlet"]
         [:th {:style {:font-size "70"}} "wpm ratio"]
         [:th "equivalent wpm"]
         [:th "average ms"]
         [:th "Timings"]]]]]]))

(defn re-typerooni-app []
  (let [test-words (subscribe [:test-words])
        status (subscribe [:status])]
    [:div
     [:section#wpm-app
      [:header
       [:h1 "Words Per Minute"]]
      [:section#main
       [:div
        [display-panel @test-words]
        [control-panel @status]]
       (when (= @status :finished)
         [stats-panel])]
      [:footer]]]))
