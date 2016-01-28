(ns re-typerooni.core
  (:require [reagent.core :as reagent :refer [atom]]
            [reagent.session :as session]
            [secretary.core :as secretary :include-macros true]
            [accountant.core :as accountant]
            [re-frame.core :refer [dispatch-sync]]
            [re-typerooni.views :refer [re-typerooni-app]]
            [re-typerooni.handlers]
            [re-typerooni.subs]))

;; -------------------------
;; Views

(defn home-page []
  [:div [:h2 "Welcome to re-typerooni"]
   [:div [:a {:href "/about"} "go to about page"]]])

(defn about-page []
  [:div [:h2 "About re-typerooni"]
   [:div [:a {:href "/"} "go to the home page"]]])

(defn current-page []
  [:div [(session/get :current-page)]])

;; -------------------------
;; Routes

(secretary/defroute "/" []
  (session/put! :current-page #'re-typerooni-app))

(secretary/defroute "/about" []
  (session/put! :current-page #'about-page))

;; -------------------------
;; Initialize app

(defn mount-root []
  (dispatch-sync [:initialise-wpm])
  (reagent/render [current-page] (.getElementById js/document "app")))

(defn init! []
  (accountant/configure-navigation!)
  (accountant/dispatch-current!)
  (mount-root))
