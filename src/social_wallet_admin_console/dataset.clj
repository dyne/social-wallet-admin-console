(ns social-wallet-admin-console.dataset)

(defn- get-type      [data] (get-in data [:meta :type]))

(defn participants? [attr] (= (get-type attr) :participants))
(defn transactions? [attr] (= (get-type attr) :transactions))
(defn tags?         [attr] (= (get-type attr) :tags))


