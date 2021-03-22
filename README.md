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
{:enumField :choiceB,
 :boolField true,
 :nestedRecordField {:nestedBoolField true},
 :stringField "a a^_^(#3MB&FQ 4K07:>6b'+Y;B\fAc%.!,2d?[31+Ha78;OFNcQT@ ?XGI3cGS\f>0
               >;D=U#<XK*%IY.W[M+d;9B[G\"=-%.DHK];ANX?]L&OGLC+(K$M 30\r,EHNNad,\rB98\\IN,S<@^%VQ'C=GE#<Q4#=D:%_E,GBR- 74F\t`\r\b/Ra#P
               RK&
               M_K@^\t`RT@HP.\f3K8%YXAY6F#PG/Q\b0a-0
               ZWP#\rZ+\rC47\\&\r0X)5?VY=\bM",
 :bytesField :something-else, ; this comes from the overlay, clearly not bytes so don't need to match the schema, dealers choice.
 :intField -1126701618,
 :arrayField [-1472521433
              1898308205
              646000236
              -1915616819
              -67111389
              1690278088],
 :unionField 459451483,
 :mapField {"J?A\f6M" 1526821303,
            "\rD;\f" -1922757881,
            "E\n)*6MF1^]%Z[W'" 491315750,
            "1E.\f&\"08BS\n\r" 633518251,
            "O\rOQ%QC\"^GFD$7<7>" 791154575,
            ",RV\"K^W\n\\,'ZZ\r4)N7UL^" -1050952125,
            "JQ\\H9Z#!(" 1369462451,
            "MD" 175874311,
            "P;\\[" 1897513874,
            "\nW+C&!" -417765297,
            ":" 1654470054},
 :doubleField 0.7783069863694084,
 :nilField nil,
 :floatField 2.9038145E38,
 :longField -1090730480785029120}
```
