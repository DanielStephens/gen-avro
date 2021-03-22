(ns gen-avro.core
  (:require [gen-avro.config :as conf]
            [gen-avro.generate :refer [generate-from-schema-with-opts]]
            [gen-avro.util :refer [overlay]]))

(def previous-seed conf/previous-seed)

(defn gen
  ([schema]
   (gen schema nil))
  ([schema opts]
   (generate-from-schema-with-opts schema (conf/init-opts opts))))
