(ns gen-avro.core
  (:require [gen-avro.config :as conf]
            [gen-avro.generate :refer [generate-from-schema-with-opts]]
            [gen-avro.util :refer [overlay]]))

(def previous-seed conf/previous-seed)

(defn gen-one-of
  "Generates random clojure data that conforms to one of the schemas provided.

  INPUT
  schemas - a collection of schemas, or maps that represent schemas.
  opts - an optional map, containing generation configuration:
    {
      :seed < integer | :previous > - The seed to use for random generation, with the same seed you will also receive identical results.
      :type-registry - defaults to jackdaw.serdes.avro/+base-schema-type-registry+, see jackdaw for more details on how the type registry needs to work.
      :overlay - if present and a clojure map is randomly generated, deeply merges the result.
      :overlay-fn - a function that will always be called on the generated result, if specified :overlay does nothing.
    }

  OUTPUT
  Returns clojure data generated against one of the schemas.
  If possible, the seed used is merged into the metadata of the returned object."
  ([schemas]
   (gen-one-of schemas nil))
  ([schemas opts]
   (generate-from-schema-with-opts schemas (conf/init-opts opts))))

(defn gen
  "See `gen-one-of`
  This variant accepts one schema rather than multiple."
  ([schema]
   (gen-one-of [schema] nil))
  ([schema opts]
   (gen-one-of [schema] opts)))
