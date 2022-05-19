(ns gen-avro.core-test
  (:require [clojure.test :refer :all]
            [gen-avro.core :as sut]
            [gen-avro.random :refer :all]
            [jackdaw.serdes.avro :refer :all]
            [abracad.avro :as avro])
  (:import
    [java.time LocalDate]
    [gen_avro.generate Generator]))

(def complex-schema
  {:name "RecordName"
   :namespace "RecordNamespace"
   :type :record
   :fields
   [{:name "boolField" :type :boolean}
    {:name "bytesField" :type :bytes}
    {:name "doubleField" :type :double}
    {:name "floatField" :type :float}
    {:name "intField" :type :int}
    {:name "longField" :type :long}
    {:name "stringField" :type :string}
    {:name "nilField" :type :null}

    {:name "arrayField" :type {:type :array :items :int}}
    {:name "mapField" :type {:type :map :values :int}}
    {:name "mapComplexField" :type {:type :map
                                    :values {:name "MapRecordName"
                                             :namespace "MapRecordNamespace"
                                             :type :record
                                             :fields [{:name "mapBoolField" :type :boolean}]}}}
    {:name "nestedRecordField" :type {:name "NestedRecordName"
                                      :namespace "NestedRecordNamespace"
                                      :type :record
                                      :fields
                                      [{:name "nestedBoolField" :type :boolean}]}}
    {:name "unionField" :type [:int :string :null]}
    {:name "enumField" :type {:name "EnumName"
                              :type :enum
                              :symbols [:choiceA :choiceB]}}]})

(defn- validate-against-schema
  [schema type-registry clj-data]
  (let [schema-coercion ((make-coercion-stack type-registry) (avro/parse-schema schema))]
    (is (some? (clj->avro schema-coercion clj-data [])))))

(deftest generated-value-is-valid-against-schema
  (let [gen-value (sut/gen complex-schema)]
    (validate-against-schema complex-schema
                             +base-schema-type-registry+
                             gen-value)))

(deftest generated-value-is-deterministic-on-seed
  (let [gen-value-1 (sut/gen complex-schema)
        seed (:seed (meta gen-value-1))
        gen-value-2 (sut/gen complex-schema {:seed seed})
        bytes-to-vec #(update % :bytesField vec)]
    (is (= (bytes-to-vec gen-value-1)
           (bytes-to-vec gen-value-2)))))

(defrecord LocalDateType []
  SchemaCoercion
  (match-clj? [_ x] (instance? LocalDate x))
  (match-avro? [_ x] (int? x))
  (avro->clj [_ x] (LocalDate/ofEpochDay x))
  (clj->avro [this x path]
    (validate-clj! this x path "LocalDate")
    (.toEpochDay x))
  Generator
  (generate [_ random]
    (let [epoch-day (random-long random
                                 (.toEpochDay (LocalDate/parse "1895-01-01"))
                                 (.toEpochDay (LocalDate/parse "3005-01-01")))]
      (LocalDate/ofEpochDay epoch-day))))

(deftest can-generate-for-custom-type
  (let [tr (assoc +base-schema-type-registry+
             {:type "int" :logical-type "localdate"}
             (fn [_ _] (->LocalDateType)))
        local-date-schema {:name "RecordName"
                           :namespace "RecordNamespace"
                           :type :record
                           :fields
                           [{:name "localDateField" :type {:type :int :logicalType :localdate}}]}

        gen-value (sut/gen local-date-schema {:type-registry tr})
        gen-local-date (:localDateField gen-value)]
    (validate-against-schema local-date-schema
                             tr
                             gen-value)
    (is (instance? LocalDate gen-local-date))))
