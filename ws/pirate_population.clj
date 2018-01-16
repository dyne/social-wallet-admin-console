;; gorilla-repl.fileformat = 1

;; **
;;; # Social Wallet :: admin console
;;;
;;; Script to generate a pirate population using the wallet
;;;
;; **

;; @@
(ns social-wallet-admin-console.term
  (:require
   [clojure.repl :refer :all]
   [clojure.java.io :as io]
   [clojure.string :refer [split]]
   [fxc.random :as rand]
   [incanter.core :refer :all]
   [auxiliary [core docs maps :refer :all]]
   [social-wallet-admin-console.core :refer :all :reload :true]
   [social-wallet-admin-console.vis.graph2d :refer :all :reload :true])
  (:use [gorilla-repl core table html]))
;; @@
;; =>
;;; {"type":"html","content":"<span class='clj-nil'>nil</span>","value":"nil"}
;; <=
;; @@

(create-participant {:name "ClearingHouse"
                     :email "clearing@pirates.net"
                     :password "harr"})
(def tags [ "ship" "treasure" "plunging" "island" "harbor" "sailing" ])
(def pirates (-> "social-wallet-admin-console/pirates.txt" io/resource slurp (split #"\n")))
;; @@
;; =>
;;; {"type":"html","content":"<span class='clj-nil'>nil</span>","value":"nil"}
;; <=
;; @@

(map #(create-participant {:name %
                           :email (str % "@pirates.net")
                           :password "harr"})
     (take 100 pirates))
;; @@
;; =>
;;; {"type":"html","content":"<span class='clj-nil'>nil</span>","value":"nil"}
;; <=
;; @@

(map #(create-transaction {:from "clearing@pirates.net"
                           :amount 100
                           :to (str % "@pirates.net")})
     (take 100 pirates))
;; @@
;; =>
;;; {"type":"html","content":"<span class='clj-nil'>nil</span>","value":"nil"}
;; <=
;; @@

(for [c (-> 5 rand range)]  
  (map #(create-transaction
         {:from (str % "@pirates.net")
          :amount (-> 2 rand/create :integer)
          :to (str (nth pirates (rand 100)) "@pirates.net")
          :tags [(nth tags (rand (count tags)))]})
       (take 100 pirates)))
;; @@
