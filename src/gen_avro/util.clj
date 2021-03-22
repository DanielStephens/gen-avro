(ns gen-avro.util)

(defn- deep-merge
  "Shamelessly ripped from medley https://github.com/weavejester/medley/blob/master/src/medley/core.cljc#L213
  Recursively merges maps together. If all the maps supplied have nested maps
  under the same keys, these nested maps are merged. Otherwise the value is
  overwritten, as in `clojure.core/merge`."
  {:arglists '([& maps])
   :added    "1.1.0"}
  ([])
  ([a] a)
  ([a b]
   (when (or a b)
     (letfn [(merge-entry [m e]
               (let [k  (key e)
                     v' (val e)]
                 (if (contains? m k)
                   (assoc m k (let [v (get m k)]
                                (if (and (map? v) (map? v'))
                                  (deep-merge v v')
                                  v')))
                   (assoc m k v'))))]
       (reduce merge-entry (or a {}) (seq b)))))
  ([a b & more]
   (reduce deep-merge (or a {}) (cons b more))))

(defn overlay
  [value overlay]
  {:pre [(or (nil? value) (map? value))
         (or (nil? overlay) (map? overlay))]}
  (deep-merge value overlay))
