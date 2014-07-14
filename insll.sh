#!/bin/bash
echo "adb install com.hybrid.app.facebook"
adb install app/build/outputs/apk/app-facebook-debug.apk
echo "adb install com.hybrid.app.twitter"
adb install app/build/outputs/apk/app-twitter-debug.apk
echo "adb install com.hybrid.app.linkedIn"
adb install app/build/outputs/apk/app-linkedIn-debug.apk
echo "adb install com.hybrid.app.xing"
adb install app/build/outputs/apk/app-xing-debug.apk
echo "adb install com.hybrid.app.sina.weibo"
adb install app/build/outputs/apk/app-sinaWeiBo-debug.apk
echo "adb install com.hybrid.app.sohu.weibo"
adb install app/build/outputs/apk/app-sohuWeiBo-debug.apk
