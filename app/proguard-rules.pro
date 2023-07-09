# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

 -keep interface com.cheq.retail.api.APIServices
 -keep class com.cheq.retail.api.RestClient
 -keep class com.cheq.retail.ui.splash.model.GetDeviceIdResponse
 -keep class com.cheq.retail.ui.splash.model.GetDeviceIdPost
 -keep class com.cheq.retail.utils.Utils
 -keep class com.cheq.retail.api.EncryptionProvider