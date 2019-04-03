(ns hello-compojure.handler
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [clojure.data.json :as json]
            [clojure.string :as string]
            [ring.middleware.defaults :refer [wrap-defaults site-defaults]]))

(def users (json/read-str (slurp "users.json") :key-fn keyword))

(defn full-name
  [user]
  (str (string/capitalize (:first (:name user))) " " (:last (:name user))))

(defn user-link
  [user]
  (str "<a href=\"/" (:username user) "\">" (full-name user) "</a>"))

(defroutes app-routes
  (GET "/" [] (apply str (string/join "<br />" (map user-link users))))
  (GET "/:username" [username] (str username))
  (route/not-found "Not Found"))

(def app
  (wrap-defaults app-routes site-defaults))
