(ns clj-web-purchase.core
  (:require [clojure.string :as str]
            [clojure.walk :as walk]
            [compojure.core :as c]
            [ring.adapter.jetty :as j]
            [hiccup.core :as h]))


(defonce server (atom nil))


(defn get-purchases []
  (let
    [purchases (slurp "purchases.csv")
     purchases (str/split-lines purchases)
     purchases (map (fn [line]
                      (str/split line #",")) purchases)
     headers (first purchases)
     purchases (rest purchases)
     purchases (map (fn [line]
                      (zipmap headers line)) purchases) ]
    purchases))

(defn purchase-html [category]
  (let [purchases (get-purchases)
        purchases (if (= 0 (count category))
                    purchases
                    (filter (fn [purchase]
                              (= (get purchase "category") category))
                            purchases))]
    [:table {:cellpadding 20}
     [:tr
      [:th "Customer ID"]
      [:th "Date"]
      [:th "Credit Card"]
      [:th "CVV"]
      [:th "Category"]]
     (map (fn [purchase]
            [:tr
             [:td (str (get purchase "customer_id"))]
             [:td (str (get purchase "date"))]
             [:td (str (get purchase "credit_card"))]
             [:td (str (get purchase "cvv"))]
             [:td (str (get purchase "category"))]])
          purchases)]))


(c/defroutes app
             (c/GET "/:category{.*}" [category]
                    (h/html [:html
                             [:body
                              [:a {:href "/"} "View All"][:br]
                              [:a {:href "/Furniture"} "Furniture"][:br]
                              [:a {:href "/Alcohol"} "Alcohol"][:br]
                              [:a {:href "/Toiletries"} "Toiletries"][:br]
                              [:a {:href "/Shoes"} "Shoes"][:br]
                              [:a {:href "Food"} "Food"][:br]
                              [:a {:href "Jewelry"} "Jewelry"][:br]

                              (purchase-html category)]])))

(defn -main []
  (when @server
    (.stop @server))
  (reset! server (j/run-jetty app {:port 3000 :join? false})))
