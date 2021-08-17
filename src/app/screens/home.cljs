(ns app.screens.home
  (:require ["react-native" :as rn]
            [reagent.core :as r]
            [re-frame.core :as rf]))

(def styles
  ^js (-> {:container
           {:flex 1
            :alignItems "center"
            :justifyContent "center"}
           :title
           {:fontWeight "bold"
            :fontSize 24
            :color "darkred"}}
          (clj->js)
          (rn/StyleSheet.create)))


(defn Home [{:keys [navigation]}]
 (let [name @(rf/subscribe [:name])
       count @(rf/subscribe [:count])]

  [:> rn/View {:style (.-container styles)}
    [:> rn/Text {:style (.-title styles)} "Hello from reagent!"]
    [:> rn/Text {:style (.-title styles)} (str "Count is: " count)]
    [:> rn/TouchableOpacity {:onPress #(rf/dispatch [:click])}
      [:> rn/Text "Click me!!"]]
    [:> rn/TouchableOpacity {:onPress #(rf/dispatch [:clear])}
      [:> rn/Text "Clear..."]]

    [:> rn/TouchableOpacity {:onPress #(.navigate navigation "List") :style {:paddingTop 20}}
      [:> rn/Text {:style {:fontWeight "bold"}} "Go To List >"]]]))