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
   [social-wallet-admin-console.core :refer :all :reload :true])
  (:use [gorilla-repl core table html]))
;; @@
;; =>
;;; {"type":"html","content":"<span class='yclj-nil'>nil</span>","value":"nil"}
;; <=
;; @@

;; View transactions for tag
(list-transactions-plain)

;; @@
