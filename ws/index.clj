;; gorilla-repl.fileformat = 1

;; **
;;; # Social Wallet :: admin console
;;;
;;; Interactive terminal for Social Wallet admins, built with [Freecoin](https://freecoin.dyne.org).
;;;
;; **

;; @@
(ns social-wallet-admin-console.term
  (:require
   [clojure.repl :refer :all]
   [incanter.core :refer :all]
   [auxiliary [core docs maps :refer :all]]
   [social-wallet-admin-console.core :refer :all :reload :true]
   [social-wallet-admin-console.vis.graph2d :refer :all :reload :true])
  (:use [gorilla-repl core table html]))
;; @@
;; =>
;;; {"type":"html","content":"<span class='clj-nil'>nil</span>","value":"nil"}
;; <=
