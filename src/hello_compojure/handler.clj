(ns hello-compojure.handler
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [clojure.data.json :as json]
            [clojure.string :as string]
            [hiccup.page :as hiccup]
            [selmer.parser :as selmer]
            [ring.middleware.file :refer :all]
            [ring.middleware.resource :refer :all]
            [ring.middleware.content-type :refer :all]
            [ring.middleware.not-modified :refer :all]
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
    (hiccup/html5
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
  (context "/profilepics" [] (wrap-file (route/not-found "File Not Found") "images"))
  (GET "/:username" [username] (selmer/render-file "user.html" {:username username}))
  (route/not-found "Not Found"))

(def app 
  (wrap-defaults app-routes site-defaults))

;(def app 
;  (wrap-file (wrap-defaults app-routes site-defaults) "images"))

;(def app 
;  (wrap-not-modified
;    (wrap-content-type
;      (wrap-file (wrap-defaults app-routes site-defaults) "images"))))

;(def app 
;  (wrap-resource (wrap-defaults app-routes site-defaults) "public"))

;(def app 
;  (wrap-not-modified
;    (wrap-content-type
;      (wrap-resource (wrap-defaults app-routes site-defaults) "images"))))
; content-type
; wrap-content-type 미들웨어는 파일 확장자를 보고 content-type을 골라준다.
; Last-Modified
; wrap-not-modified 미들웨어는 응답에 있는 Last-Modified 헤더를 요청의 If-Modified-Since 헤더와 비교한다.
; 이렇게 하면, 클라이언트에게 이미 캐싱된 리소스를 보내지 않으므로, 네트워크 대역폭이 절약된다.