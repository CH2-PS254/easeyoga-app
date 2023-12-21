# Ease Yoga App

<img src="https://github.com/CH2-PS254/easeyoga-app/blob/master/Banner%20App.png?raw=true" align="center">


## Introduction 
Ease Yoga App is an application Powered by Kotlin, Android Jetpack Compose, and Koin as Dependencies Injection. This Application developed
by Team Capstone CH2-PS254 which is based on the habits of teenagers today who rarely do sports activities.
With this application, everyone can easily do sports activities, one of which is Yoga, this application
can also be used by lay people to professionals without using an instructor, where this application 
will help users by detecting good and correct yoga poses.


## Table Of Content 
- [Introduction](#introduction)
- [Features](#features)
- [Screenshots](#screenshots)
- [Libraries](#libraries)
- [Project Structure](#project-structure)

## Features 
- Feature Authentication user for registration and login.
- Feature Realtime detection of correct yoga poses.
- Uses 2 Tensor Flow models for better accuracy.
- Get live data for list pose from API.

## Libraries 
- [Retrofit](https://square.github.io/retrofit/)
- [Okhttp 3](https://square.github.io/okhttp/)
- [AndroidX Lifecycle LiveData KTX](https://developer.android.com/jetpack/androidx/releases/lifecycle#2.6.2)
- [TensorFlow Lite Support Library](https://bintray.com/google/tensorflow/org.tensorflow.lite.support)
- [Volley](https://developer.android.com/training/volley)
- [Glide](https://github.com/bumptech/glide)
- [Shimmer](https://github.com/facebook/shimmer-android)
- [Retrofit](https://square.github.io/retrofit/)
- [Kotlin Coroutines for Android](https://github.com/Kotlin/kotlinx.coroutines)
- [Dagger Hilt for Android](https://dagger.dev/hilt/)
- [AndroidX Room](https://developer.android.com/topic/libraries/architecture/room)

## Project Structure 
- base
- camera
- customview
- data
  - local
  - model
  - remote
    - authpack
    - pose
  - repository
  - resource
- di
- login
- pose
- register
- splashscreen
- user
- utils
  - extension
