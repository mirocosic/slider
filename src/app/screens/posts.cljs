(ns app.screens.posts
    (:require ["react-native" :as rn]
              ["react-native-reanimated" :refer [default AnimatedLayout FadeIn] :rename {default Reanimated}]
              ["react-navigation-shared-element" :refer [SharedElement]]
              ["react-native-gesture-handler" :refer [TouchableOpacity]]
              ["react-native-gesture-handler/Swipeable" :refer [default ] :rename {default Swipeable}]
              [reagent.core :as r]
              [applied-science.js-interop :as j]
              [cljs-bean.core :refer [->js ->clj]]
              [re-frame.core :as rf]))

(def styles
  ^js (-> {:container
           {:flex 1}
           :title
           {:fontWeight "bold"
            :fontSize 24
            :color "darkred"}}
          (->js)
          (rn/StyleSheet.create)))

(defn row [post navigation idx]
  (let [swipe-ref (atom nil)]
    (fn [post navigation idx]
      [:> AnimatedLayout
       [:> (j/get Reanimated :View) {:entering (j/call FadeIn :delay (+ 200 (* 100 idx)))}
        [:> Swipeable 
             {:key (:id post)
              :ref (fn [el] (reset! swipe-ref el))
              :containerStyle (->js {:backgroundColor "white" :borderBottomWidth 1 :borderColor "gray"})
              :renderRightActions (fn [] (r/as-element
                                          [:> rn/TouchableOpacity {:style {:alignItems "center" :justifyContent "center" :padding 20 :backgroundColor "teal"}
                                                                   :onPress (fn []
                                                                             (rf/dispatch [:toggle-like-post (:id post)])
                                                                             (j/call @swipe-ref :close))}
                                            [:> rn/Text {:style {:color "white"}} (if (:liked post) "Unlike" "Like")]]))}
          [:> TouchableOpacity
            {:style {:padding 20  :backgroundColor "white"}
             :onPress #(.navigate navigation "Details" (->js {:post post}))}

            [:> rn/View {:style {:flexDirection "row"}}
              [:> SharedElement {:id (:id post)}
                  [:> rn/Image {:source {:uri (:image post)} :style {:width 100 :height 100}}]]
              [:> rn/View {:style {:flexShrink 1 :flexDirection "row" :paddingLeft 10 :alignItems "center"}}
                [:> rn/Text {:style {:fontSize 16 :flexWrap "wrap"}} (:text post)]]]
            (when (:liked post)
              [:> rn/Text {:style {:fontSize 20 :position "absolute" :top 10 :left 10}} "❤️"])]]]])))


(defn Posts [{:keys [navigation]}]
  (let [posts @(rf/subscribe [:posts])
        loading @(rf/subscribe [:loading])]

    [:> rn/View {:style (.-container styles)}
      (cond
        (and loading (not (seq posts)))
        [:> rn/ActivityIndicator {:style {:margin 20} :size "large"}]

        (seq posts)
        [:> rn/ScrollView
          {:showsVerticalScrollIndicator false
            :refreshControl (r/as-element
                                [:> rn/RefreshControl {:onRefresh #(rf/dispatch [:load-posts]) 
                                                       :refreshing loading 
                                                        :tintColor "#008080"}])}
          (map-indexed 
              (fn [idx post]
                ^{:key (:id post)}
                [row post navigation idx])
              posts)])]))
