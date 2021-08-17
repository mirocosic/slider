(ns app.events
  (:require
   ["@react-native-async-storage/async-storage" :refer [default] :rename {default AsyncStorage}]
   [re-frame.core :refer [reg-event-db reg-event-fx reg-fx inject-cofx trim-v after path dispatch] :as rf]
   [day8.re-frame.http-fx]
   [day8.re-frame.async-flow-fx :as async-flow-fx]
   [ajax.core :refer [json-request-format json-response-format]]
   [promesa.core :as p]
   [applied-science.js-interop :as j]
   [cljs-bean.core :refer [->js]]))

(reg-event-fx
 :initialize
 (fn [_ _]
   {:db {:name "Miro"
         :count 1
         :loading false}
    :dispatch [:app-initialized]}))

(reg-event-fx
  :app-initialized
  (fn [{:keys [db]} _]
      {:db  (assoc db :app-initialized true)
       :fx [[:dispatch [:load-posts]]
            [:dispatch [:load-user]]]}))

(reg-event-db
  :click
  (fn [db _]
    (assoc db :count (inc (:count db)))))

(reg-event-db
  :clear
  (fn [db _]
    (assoc db :count 0)))

(reg-event-fx
  :load-posts
  (fn [{:keys [db]} _]
    {:db (assoc db :loading true)
     :http-xhrio {:method :get
                  :uri             "https://dummyapi.io/data/v1/post?limit=20"
                  :headers         {:app-id "61158dedde3039a61367e7ee"}
                  :timeout         8000                                           ;; optional see API docs
                  :response-format (json-response-format {:keywords? true})  ;; IMPORTANT!: You must provide this.
                  :on-success      [:success-load-posts]
                  :on-failure      [:failure-load-posts]}}))

(reg-event-db
  :success-load-posts
  (fn [db [_ result]]
    (println "success")
    (assoc db :posts (:data result) :loading false)))

(reg-event-db
  :failure-load-posts
  (fn [db [_ result]]
    (assoc db :loading false)))

(reg-event-db
  :toggle-like-post
  (fn [db [_ post-id]]
    (assoc db :posts (map (fn [post] 
                            (if (= (:id post) post-id) 
                                (assoc post :liked (not (:liked post)))
                                post))
                          (:posts db)))))





;;; Load user from API and save to Async storage

(defn load-user-flow
  []
  {:first-dispatch [:do-load-user]
   :rules [{:when :seen? :events :success-load-user :dispatch [:do-save-user]}
           {:when :seen? :events :done-save-user :dispatch [:done-flow]}]})

(reg-event-fx
  :load-user
  (fn [{:keys [db]} _]
    {:db db
     :async-flow (load-user-flow)}))


(reg-event-fx
  :do-load-user
  (fn [{:keys [db]} _]
    {:db (assoc db :loading true)
     :http-xhrio {:method :get
                  :uri             "https://dummyapi.io/data/v1/user/60d0fe4f5311236168a109ca"
                  :headers         {:app-id "61158dedde3039a61367e7ee"}
                  :timeout         8000                                           ;; optional see API docs
                  :response-format (json-response-format {:keywords? true})  ;; IMPORTANT!: You must provide this.
                  :on-success      [:success-load-user]
                  :on-failure      [:failure-load-user]}}))

(reg-event-db
  :success-load-user
  (fn [db [_ result]]
    (assoc db :user result)))

(reg-event-db
  :failure-load-user
  (fn [db [_ result]]
    (assoc db :loading false)))

(reg-event-fx
  :do-save-user
  (fn [{:keys [db]} [_ result]]
    (->
      (p/create (fn [resolve reject]
                  (resolve (j/call AsyncStorage :setItem "user" (.stringify js/JSON (->js (:user db)))))))
      (p/then (fn [_] (dispatch [:done-save-user]))))
    {:db db}))


(reg-event-db
  :done-flow
  (fn [db [_ result]]
    (-> (p/create (fn [resolve reject]
                    (resolve (j/call AsyncStorage :getItem "user"))))
        (p/then (fn [res] (js/console.log res))))
    db))


