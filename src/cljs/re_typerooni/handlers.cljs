(ns re-typerooni.handlers
  (:require [re-frame.core :refer [dispatch register-handler trim-v path debug]]
            [re-typerooni.db :refer [default-game words-10ff]]
            [re-typerooni.utils :refer [generate-words]]
            #_[schema.core :as s]))

;; -- middleare
(comment
  (defn check-and-throw
    "throw an exception if db doesn't match the schema."
    [a-schema db]
    (if-let [problems  (s/check a-schema db)]
      (throw (js/Error. (str "schema check failed: " problems)))))

  (def check-schema-mw (after (partial check-and-throw schema)))

  (def wpm-middleware [debug
                       check-schema-mw ;; ensure the schema is still valid
                       trim-v]))

;; -- handlers
(register-handler
 :initialise-wpm
 debug
 (fn [_ _]
   (dispatch [:reset-wpm])
   default-game))

(register-handler
 :start-wpm
 (fn [db _]
   (-> db
       (assoc :status :started)
       (assoc :clock (js/setInterval
                      #(dispatch [:ticker (js/Date.)]) 1000)))))

(register-handler
 :stop-wpm
 (fn [db _]
   (js/clearInterval (:clock db))
   (let [test-words (mapcat #(% 1) (:test-words db))
         typed-words (remove #(nil? (:correctness %)) test-words)
         correct-words (filter :correctness typed-words)
         total-correct-alphabet (->> correct-words
                                     (map #(count (:word %)))
                                     (reduce +))]
     (-> db
         (assoc :status :finished)
         (assoc :stats
                {:total-words (count typed-words)
                 :total-correct-words (count correct-words)
                 :total-incorrect-words (count (remove :correctness typed-words))
                 :total-words-wpm (/ total-correct-alphabet 5)})))))

;; -- Long Running
(defn words->rows-of-words [words]
  (into [] (map-indexed (fn [i e] [i (into [] e)]) (partition 6 words))))

(register-handler
 :reset-wpm
 debug
 (fn [db _]
   (js/clearInterval (:clock db))
   (let [test-words (words->rows-of-words (generate-words words-10ff 500))]
     (-> (merge db default-game)
         (assoc :test-words test-words)))))

(register-handler
 :ticker
 (fn [db _]
   (let [timer (dec (:timer db))]
     (when(zero? timer)
       (dispatch [:stop-wpm]))
     (assoc db :timer timer))))

;; -- controller to re-dispath the event
;; -- it will only handle non-character keyin
(register-handler
 :key-down
 trim-v
 (fn [db [key-event]]
   (when (= :started (:status db))
     (case (:value key-event)
       8 (dispatch [:process-backspace])
       116 (dispatch [:reset-wpm])
       nil))
   db))

;; -- process keyed in character
(register-handler
 :key-press
 trim-v
 (fn [db [key-event]]
   (when (= (:status db) :new)
     (dispatch [:start-wpm])
     (dispatch [:process-char key-event]))
   (when (= (:status db) :started) (dispatch [:process-char key-event]))
   db))

;; -- process key processing handler
(register-handler
 :process-char
 trim-v
 (fn [db [ch]]
   (-> (if (clojure.string/blank? (char (:value ch)))
         (do (when-not (clojure.string/blank? (:current-input db))
               (dispatch [:next-word]))
             db)
         (update db :current-input #(str % (char (:value ch)))))
       (update :current-word-timestamps #(conj % (:timestamp ch))))))

(register-handler
 :process-backspace
 (fn [db _]
   (let [current-word (:current-input db)]
     (-> db
         (assoc :current-backspace-used true)
         (assoc :current-input
                (subs current-word 0 (- (count current-word) 1)))
         (update :current-word-timestamps pop)))))

(register-handler
 :next-word
 debug
 (fn [db _]
   (let [row-offset (:row-offset db)
         current-backspace-used (:current-backspace-used db)
         count-current-row (count (get-in db [:test-words row-offset 1]))
         current-word-index (:current-word db)
         current-timestamps (:current-word-timestamps db)
         timestamps (map - (next current-timestamps) current-timestamps)
         word (get-in db [:test-words row-offset 1
                          current-word-index :word])
         new-db (-> db
                    (assoc-in [:test-words row-offset 1
                               current-word-index :current-backspace-used]
                              current-backspace-used)
                    (assoc-in [:test-words row-offset 1
                               current-word-index :timestamps]
                              timestamps)
                    (assoc-in [:test-words row-offset 1
                               current-word-index :correctness]
                              (= (:current-input db) word))
                    (assoc :current-backspace-used false)
                    (assoc :current-input "")
                    (assoc :current-word-timestamps []))]
     (if (= current-word-index (- count-current-row 1))
       (-> new-db
           (update :row-offset inc)
           (assoc :current-word 0))
       (update new-db :current-word inc)))))
