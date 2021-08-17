## Description

This app is currently IOS only. 
It is a simple app that tests a few features.

Loads some dummy data from `https://dummyapi.io`

Built using:
  - reagent
  - re-frame, re-frame https fx and re-frame async fx libs
  - shadow-cljs
  - react navigation
  - react native gesture handler
  - react native reanimated 2
  - react native shared element

Some features that it implements:
 - react native shared element /w react navigation 
 - reanimated entering animations
 - pinch to zoom gesture handler
 - swipeable rows 

### How to run dev

1. `yarn install`
2. `npx pod-install`
3. `shadow-cljs watch app`
4. `yarn start` in a separate tab
5. `npx react-native run-ios`
