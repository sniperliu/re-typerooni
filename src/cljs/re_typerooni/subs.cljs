(ns re-typerooni.subs
  (:require-macros [reagent.ratom :refer [reaction]])
  (:require [re-frame.core :refer [register-sub dispatch]]))

;; -- subscriptions
(register-sub
 :wpm
 (fn [db _]
   (reaction @db)))

(register-sub
 :test-words
 (fn [db _]
   (reaction (:test-words @db))))

(register-sub
 :timer
 (fn [db _]
   (reaction (:timer @db))))

(register-sub
 :status
 (fn [db _]
   (reaction (:status @db))))

(register-sub
 :current-input
 (fn [db _]
   (reaction (:current-input @db))))

(register-sub
 :display-control
 (fn [db _]
   (reaction (select-keys @db
                          [:num-row-in-display :row-offset
                           :current-word]))))

(register-sub
 :stats
 (fn [db _]
   (reaction (:stats @db))))
