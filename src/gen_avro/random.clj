(ns gen-avro.random
  (:import [java.util Random]))

(defprotocol RandomSupplier
  (random-double [this] [this max] [this min max]))

(defrecord JavaRandomSupplier [seed ^Random r]
  RandomSupplier
  (random-double [_] (.nextDouble r))
  (random-double [this max] (* (random-double this) max))
  (random-double [this min max] (+' min (random-double this (-' max min)))))

(defn random-long
  ([random max] (long (random-double random max)))
  ([random min max] (long (+' min (random-double random (-' max min)))))
  ([random] (long (random-double random Long/MIN_VALUE (inc' Long/MAX_VALUE)))))

(defn random-float
  ([random max] (float (random-double random max)))
  ([random min max] (float (+' min (random-double random (-' max min)))))
  ([random] (float (random-double random Float/MIN_VALUE (inc' Float/MAX_VALUE)))))

(defn random-int
  ([random max] (int (random-long random max)))
  ([random min max] (int (random-long random min max)))
  ([random] (int (random-long random Integer/MIN_VALUE (inc' Integer/MAX_VALUE)))))

(defn random-choice [random choices]
  (nth choices (random-long random (count choices))))

(defn random-bool [random]
  (random-choice random [true false]))

(defn random-byte [random]
  (byte (random-long random Byte/MIN_VALUE (inc' Byte/MAX_VALUE))))

(defn random-char [random]
  (char (random-int random (int Character/MIN_VALUE) (inc' 100))))

(defn random-string [random length]
  (apply str (repeatedly length #(random-char random))))

(defn random
  ([seed]
   (->JavaRandomSupplier seed (Random. seed)))
  ([]
   (let [r (Random.)
         seed (.nextInt r)]
     (->JavaRandomSupplier seed (Random. seed)))))
