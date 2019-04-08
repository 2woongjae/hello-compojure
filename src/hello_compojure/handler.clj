(ns hello-compojure.handler
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [clojure.data.json :as json]
            [clojure.string :as string]
            [hiccup.core :as hiccup]
            [selmer.parser :as selmer]
            [ring.middleware.defaults :refer [wrap-defaults site-defaults]]))

(def users (json/read-str (slurp "users.json") :key-fn keyword))

(defn full-name
  [user]
  (str (string/capitalize (:first (:name user))) " " (:last (:name user))))

(defn user-link
  [user]
  (str "<a href=\"/" (:username user) "\">" (full-name user) "</a>"))

(defn get-user
  [user]
  {:full-name (full-name user) :username (:username user)})

(defroutes app-routes
  (GET "/" [] (str (string/join "<br />" (map user-link users))))
  (GET "/hiccup" []
    (hiccup/html
      [:html {:lang "en"}
       [:head
        [:meta {:charset "UTF-8"}]
        [:title "User Index"]]
       [:body
        [:h1 "hiccup"]
        [:ul (for [user (map get-user users)] [:li [:a {:href (str "/" (:username user))} (:full-name user)]])]]]
    )
  )
  (GET "/selmer" [] (selmer/render-file "index.html" {:users (map get-user users)}))
  (GET "/:username" [username] (str username))
  (route/not-found "Not Found"))

(def app
  (wrap-defaults app-routes site-defaults))
