(ns app.main
  (:require
    [shadow.react-native :refer (render-root)]
    ["react-native" :as rn]
    ["@react-navigation/native" :refer [NavigationContainer]]
    ["@react-navigation/stack" :refer [createStackNavigator]]
    ["react-navigation-shared-element" :refer [createSharedElementStackNavigator]]
    [cljs-bean.core :refer [->js]]
    [applied-science.js-interop :as j]
    [reagent.core :as r]
    [re-frame.core :as rf]
    [app.screens.home :refer [Home]]
    [app.screens.profile :refer [Profile]]
    [app.screens.posts :refer [Posts]]
    [app.screens.post-details :refer [PostDetails]]
    [app.events]
    [app.subs]))



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

(def main-stack (createSharedElementStackNavigator))

(defn root []
  [:> NavigationContainer
    [:> (.-Navigator main-stack) 
        {:mode "modal"}

      [:> (.-Screen main-stack) 
        {:name "Posts" 
         :component (r/reactify-component Posts)
         :options (fn [props] 
                    (->js {:cardStyleInterpolator (fn [props] (->js {:cardStyle {:opacity (j/get-in props [:current :progress])}}))
                           :headerRight (fn []
                                         (r/as-element
                                           [:> rn/TouchableOpacity {:onPress #(j/call (j/get props :navigation) :navigate "Profile")
                                                                    :style {:paddingRight 20}}
                                              [:> rn/Text "⚙️"]]))}))}]
      [:> (.-Screen main-stack) 
        {:name "Details" 
         :component PostDetails
         :options (->js {:cardStyleInterpolator (fn [props] (->js {:cardStyle {:opacity (j/get-in props [:current :progress])}}))
                         :headerShown false})}]

      [:> (.-Screen main-stack) {:name "Profile" :component (r/reactify-component Profile)}]]])


(defn start
  {:dev/after-load true}
  []
  (rf/dispatch-sync [:initialize])
  (render-root "slider" (r/as-element [root])))

(defn init []
  (start))