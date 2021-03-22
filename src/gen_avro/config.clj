(ns gen-avro.config
  (:require [gen-avro.random :refer [random]]
            [gen-avro.util :as util]
            [jackdaw.serdes.avro :refer [+base-schema-type-registry+]]))

(def ^:private previous-seed-atom (atom nil))

(defn previous-seed [] @previous-seed-atom)

(defn- default-opts [{:keys [seed overlay] :as opts}]
  (let [seed (cond
               (= :previous seed) (previous-seed)
               (some? seed) seed
               :else (:seed (random)))
        random (random seed)
        overlay-fn (if (some? overlay)
                     (fn [x] (util/overlay x overlay))
                     identity)]
    (merge {:type-registry +base-schema-type-registry+
            :overlay-fn overlay-fn}
           opts
           {:random random
            :seed seed})))

(defn init-opts [opts]
  (let [opts (default-opts opts)]
    (reset! previous-seed-atom (:seed opts))
    opts))

