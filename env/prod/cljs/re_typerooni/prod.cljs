(ns re-typerooni.prod
  (:require [re-typerooni.core :as core]))

;;ignore println statements in prod
(set! *print-fn* (fn [& _]))

(core/init!)
