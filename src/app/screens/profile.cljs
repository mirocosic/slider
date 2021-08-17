(ns app.screens.profile
  (:require ["react-native" :as rn]
            [reagent.core :as r]
            [re-frame.core :as rf]))

(def styles
  ^js (-> {:container
           {:flex 1
            :alignItems "center"}
           :title
           {:fontWeight "bold"
            :fontSize 16}}
          (clj->js)
          (rn/StyleSheet.create)))


(defn Profile [{:keys [navigation]}]
 (let [user @(rf/subscribe [:user])]

  [:> rn/View {:style (.-container styles)}
    [:> rn/Text {:style (.-title styles)} "First name: " (:firstName user)]
    [:> rn/Text {:style (.-title styles)} "First name: " (:lastName user)]]))
