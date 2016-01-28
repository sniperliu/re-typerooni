(ns re-typerooni.utils)

(defn generate-words [dict num]
  (map (fn [w] {:word w
                :correctness nil
                :backspace-used nil })
       (take num (repeatedly #(rand-nth dict)))))
