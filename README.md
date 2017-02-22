# LottieDome  Lottie库的练习

###1.编写过程中  遇到的问题
>Error:Execution failed for task ':app:processDebugManifest'.
>Manifest merger failed : uses-sdk:minSdkVersion 11 cannot be smaller than version 14 declared in library [com.airbnb.android:lottie:1.5.0] F:\CodeManagement\LottieDome\app\build\intermediates\exploded-aar\com.airbnb.android\lottie\1.5.0\AndroidManifest.xml
>Suggestion: use tools:overrideLibrary="com.airbnb.lottie" to force usage

  解决    minSdkVersion 给为14   最小支持14

    defaultConfig {
            applicationId "com.example.zhangshubin.lottiedome"
            minSdkVersion 14
            targetSdkVersion 25
            versionCode 1
            versionName "1.0"
            testInstrumentationRunner "android.support.test.runner.AndroidJUnitRunner"
      }