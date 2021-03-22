# gen-avro
Generate Clojure data from avro schemas using abracad and jackdaw.
Useful for generatively testing code against an avro schema contract.

[![Clojars Project](https://img.shields.io/clojars/v/djs/gen-avro.svg)](https://clojars.org/djs/gen-avro)

```clojure
(gen {:name "RecordName"
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
       {:name "nestedRecordField" :type {:name "NestedRecordName"
                                         :namespace "NestedRecordNamespace"
                                         :type :record
                                         :fields
                                         [{:name "nestedBoolField" :type :boolean}]}}
       {:name "unionField" :type [:int :string :null]}
       {:name "enumField" :type {:name "EnumName"
                                 :type :enum
                                 :symbols [:choiceA :choiceB]}}]}
     ;; options are, well, optional,
     ;; seed is useful to specify to recreate a failing test
     ;; overlay is useful if your test relies on some specific values to pass
     {:seed 1
      :overlay {:bytesField :something-else}})
      
=>
{:boolField true,
 :bytesField :something-else, ; this comes from the overlay, clearly not bytes so don't need to match the schema, dealers choice.
 :doubleField 0.7783069863694084,
 :floatField 2.9038145E38,
 :intField -1126701618,
 :longField -1090730480785029120,
 :stringField "a a^_^...",
 :nilField nil,
 :arrayField [-1472521433
              1898308205
              ...],
 :mapField {"J?A\f6M" 1526821303,
            "\rD;\f" -1922757881,
            ...},
 :nestedRecordField {:nestedBoolField true},
 :unionField 459451483,
 :enumField :choiceB}
```
