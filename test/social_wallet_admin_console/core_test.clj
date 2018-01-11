(ns social-wallet-admin-console.core-test
  (:require [clojure.test :refer :all]
            [social-wallet-admin-console.core :refer :all]
            [gorilla-repl.core :as gc]))

(deftest a-test
  (testing "FIXME, I fail."
    (is (= 1 1))))

(deftest run-gorilla-server
  (gc/run-gorilla-server {:port 8990}))


