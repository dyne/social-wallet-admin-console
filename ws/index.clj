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
   [clojure.string :as string]
   [clojure.data.json :as json]
   [clojure.contrib.humanize :refer :all]
   [freecoin-lib.app :refer :all]
   [social-wallet-admin-console.core :refer :all :reload :true])
  (:use [gorilla-repl core table latex html]))
;; @@
;; =>
;;; {"type":"html","content":"<span class='clj-nil'>nil</span>","value":"nil"}
;; <=
