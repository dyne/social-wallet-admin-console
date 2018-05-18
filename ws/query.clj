;; gorilla-repl.fileformat = 1

;; **
;;; # Social Wallet :: admin console
;;;
;;; Script to query the DB for tags, transactions etc.
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
;;; {"type":"html","content":"<span class='clj-nil'>nil</span>","value":"nil"}
;; <=
;; @@

(-> (list-tags) view-table)

;; @@
;; =>
;;; {"type":"html","content":"<span class='clj-nil'>nil</span>","value":"nil"}
;; <=
;; @@

;; View transactions for brenningham@pirates.net
(-> (list-transactions {:account-id "brenningham@pirates.net"}) view-table)

;; @@
