(ns com.wsscode.pathom.viz.transit
  (:refer-clojure :exclude [read write])
  (:require [cognitect.transit :as t]
            #?(:cljs [goog.object :as gobj]))
  #?(:clj (:import (java.io ByteArrayOutputStream ByteArrayInputStream)
                   (com.cognitect.transit WriteHandler))))

#?(:clj
   (deftype DefaultHandler []
     WriteHandler
     (tag [_this _v] "unknown")
     (rep [_this v] (pr-str v)))
   :cljs
   (deftype DefaultHandler []
     Object
     (tag [_this _v] "unknown")
     (rep [_this v] (pr-str v))))

(defn read [s]
  #?(:clj
     (let [in     (ByteArrayInputStream. (.getBytes s))
           reader (t/reader in :json)]
       (t/read reader))

     :cljs
     (let [reader (t/reader :json)]
       (t/read reader s))))

#?(:cljs
   (def cljs-write-handlers
     {"default" (DefaultHandler.)}))

(defn ^String write [x]
  #?(:clj
     (let [out    (ByteArrayOutputStream. 4096)
           writer (t/writer out :json {:default-handler (DefaultHandler.)
                                       :transform       t/write-meta})]
       (t/write writer x)
       (.toString out))

     :cljs
     (let [writer (t/writer :json {:handlers  cljs-write-handlers
                                   :transform t/write-meta})]
       (t/write writer x))))

#?(:cljs
   (defn envelope-json [msg]
     #js {:transit-message (write msg)}))

#?(:cljs
   (defn unpack-json [msg]
     (some-> (gobj/get msg "transit-message") read)))
