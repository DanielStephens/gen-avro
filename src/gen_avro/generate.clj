(ns gen-avro.generate
  (:require [abracad.avro :as avro]
            [gen-avro.random :refer :all]
            [jackdaw.serdes.avro :as jsa])
  (:import [jackdaw.serdes.avro SchemaCoercion ArrayType MapType RecordType UnionType BooleanType BytesType DoubleType FloatType IntType LongType StringType NullType SchemalessType EnumType]
           [org.apache.avro Schema$Field]
           [clojure.lang IObj]))

(defprotocol Generator
  (generate [this random]))

(defn random-array [random element-coercion]
  (vec (repeatedly (random-int random 16)
                   #(generate element-coercion random))))

(defn random-map [random value-coercion]
  (let [size (random-int random 16)
        keys (doall (repeatedly size #(random-string random (random-int random 32))))
        vals (doall (repeatedly size #(generate value-coercion random)))]
    (->> (map vector keys vals)
         (into {}))))

(defn random-record [random field->schema+coercion]
  (->> field->schema+coercion
       (map (fn [[field-key [^Schema$Field _field field-coercion]]]
              [field-key (generate field-coercion random)]))
       (into {})))

(defn random-union [random coercion-types]
  (generate (random-choice random coercion-types)
            random))

(extend-protocol Generator
  SchemaCoercion
  (generate [schema-type random]
    (condp instance? schema-type
      BooleanType (random-bool random)
      BytesType (byte-array (repeatedly (random-int random 2048) #(random-byte random)))
      DoubleType (random-double random)
      FloatType (random-float random)
      IntType (random-int random)
      LongType (random-long random)
      StringType (random-string random (random-int random 1024))
      NullType nil
      SchemalessType (throw (ex-info "Can't generate a value for schemaless type." {}))))

  ArrayType
  (generate [this random]
    (random-array random (:element-coercion this)))

  MapType
  (generate [this random]
    (random-map random (:value-coercion this)))

  RecordType
  (generate [this random]
    (random-record random (:field->schema+coercion this)))

  UnionType
  (generate [this random]
    (random-union random (:coercion-types this)))

  EnumType
  (generate [this random]
    (->> this
         :schema
         (.getEnumSymbols)
         distinct
         (random-choice random)
         (jsa/avro->clj this))))

(defn generate-from-schema
  [schema random type-registry]
  (let [schema-coercion ((jsa/make-coercion-stack type-registry) (avro/parse-schema schema))]
    (generate schema-coercion random)))

(defn generate-from-schema-with-opts
  [schemas {:keys [seed random overlay-fn type-registry]}]
  (let [schema (random-choice random schemas)
        gen-val (generate-from-schema schema random type-registry)
        gen-val (overlay-fn gen-val)
        gen-val (if (instance? IObj gen-val)
                  (vary-meta gen-val assoc :seed seed)
                  gen-val)]
    gen-val))
