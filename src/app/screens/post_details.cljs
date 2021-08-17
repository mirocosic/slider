(ns app.screens.post-details
    (:require ["react-native" :as rn]
              ["react-navigation-shared-element" :refer [SharedElement]]
              ["react-native-gesture-handler" :refer [PinchGestureHandler State]]
              ["react-native-reanimated" :refer [default AnimatedLayout FadeIn] :rename {default Reanimated}]
              [reagent.core :as r]
              [cljs-bean.core :refer [->js ->clj]]
              [applied-science.js-interop :as j]
              [re-frame.core :as rf]))

(def width (-> rn/Dimensions
               (j/call :get "window")
               (j/get :width)))

(def height (-> rn/Dimensions
               (j/call :get "window")
               (j/get :height)))

(def Value (j/get rn/Animated :Value))

(def styles
  ^js (-> {:container
           {:flex 1}
           :title
           {:fontWeight "bold"
            :fontSize 24
            :color "darkred"}}
          (->js)
          (rn/StyleSheet.create)))

(defn ImageCmp [post details-visible]
  (let [pinch-scale (atom (Value. 1))
        animation (j/call rn/Animated :timing @pinch-scale #js{:toValue 1 :duration 250 :useNativeDriver true})]
    (fn []
      [:> SharedElement {:id (:id post)}
        [:> PinchGestureHandler
          {:onGestureEvent (j/call rn/Animated :event (->js [{:nativeEvent {:scale @pinch-scale}}]) (->js {:useNativeDriver true}))
            :onHandlerStateChange (fn [event]
                                    (when (= (j/get State :BEGAN) (j/get-in event [:nativeEvent :state]))
                                          (reset! details-visible false))
                                    (when (= (j/get State :END) (j/get-in event [:nativeEvent :state]))
                                          (reset! details-visible true)
                                          (j/call animation :start)))}
          [:> (j/get rn/Animated :Image)
            {:source {:uri (:image post)}
              :resizeMode "cover"
              :style [{:width width :height width :zIndex 100 :position "absolute" :top 0 :left 0}
                      {:transform (->js [{:scale (j/call @pinch-scale :interpolate (->js {:inputRange [1 2] 
                                                                                          :outputRange [1 2]
                                                                                          :extrapolateLeft "clamp"}))}])}]}]]])))


(defn PostDetailsCmp [{:keys [navigation route]}]
 (let [details-visible (r/atom true)]
  
   (fn []
    (let [post (get-in (->clj route) [:params :post])
          owner (:owner post)]

      [:> rn/View {:style [(.-container styles)]}

        [ImageCmp post details-visible]

        [:> rn/TouchableOpacity {:style {:position "absolute" :top 30 :right 20 :backgroundColor "gray" :borderRadius 20 :width 30 :height 30 :opacity 0.7 :alignItems "center" :justifyContent "center"}
                                  :onPress #(j/call navigation :pop)
                                  :hitSlop #js{:top 20 :right 20 :bottom 20 :left 20}}
          [:> rn/Text {:style {:fontSize 20 :color "white"}} "X"]]

        [:> AnimatedLayout {:style {:margin-top width :opacity (if @details-visible 1 0)}}
         [:> (j/get Reanimated :View) {:entering (j/call FadeIn :delay 300)}
          [:> rn/View {:style {:flex-direction "row" :align-items "center" :justifyContent "space-between" :padding 10}}
            [:> rn/View {:style {:flex-direction "row" :align-items "center"}}
              [:> rn/Image {:source {:uri (:picture owner)}
                            :style {:width 50 :height 50 :borderRadius 25 :marginRight 20}}]
              [:> rn/View
                [:> rn/Text {:style {:fontSize 16 :color "gray"}} 
                  (str (:firstName owner) " " (:lastName owner))]
                [:> rn/Text {:style {:fontSize 12 :color "gray"}} 
                  (.toDateString (new js/Date (:publishDate post)))]]]
            [:> rn/View {:style {:paddingRight 10}}
              [:> rn/Text {:style {:fontSize 16 :color "gray"}} (str "❤️" (:likes post))]]]


          [:> rn/View {:style {:padding 10}}
            [:> rn/Text {:style {:fontSize 20 :color "gray" :textAlign "center"}} (:text post)]]
          
          [:> rn/View {:style {:padding 10 :flexDirection "row" :justifyContent "center"}}
            (map-indexed
              (fn [idx tag]
                  [:> rn/View {:key idx :style {:backgroundColor "teal" :borderRadius 5 :margin 5 :padding 5}}
                    [:> rn/Text {:style {:fontSize 20 :color "white" :textAlign "center"}} tag " "]])
              (:tags post))]]]]))))

          


;; move the mapping fn to stack config as per docs
(defn mappingFn [route]
  (->js [{:id (j/get-in route [:params :post :id])
          :animation "move"
          :resize "clip"}])) 

(def PostDetails
  (j/assoc! (r/reactify-component PostDetailsCmp) :sharedElements mappingFn))
