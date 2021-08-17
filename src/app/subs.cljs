(ns app.subs
  (:require [re-frame.core :refer [reg-sub]]))

(reg-sub
 :name
 (fn [db _]
   (:name db)))

(reg-sub
 :count
 (fn [db _]
   (:count db)))
 
(reg-sub
  :posts
  (fn [db _]
     (:posts db)))

(reg-sub
  :user
  (fn [db _]
     (:user db)))

(reg-sub 
  :loading
  (fn [db _]
     (:loading db)))
