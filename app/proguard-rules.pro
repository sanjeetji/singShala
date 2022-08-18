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

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

-repackageclasses 'com.sensibol.lucidmusic.singstr'
-allowaccessmodification

-keep class com.sensibol.lucidmusic.singstr.domain.model.Exercise
-keep class com.sensibol.lucidmusic.singstr.domain.model.ExerciseScore
-keep class com.sensibol.lucidmusic.singstr.domain.model.Genre
-keep class com.sensibol.lucidmusic.singstr.domain.model.Lesson
-keep class com.sensibol.lucidmusic.singstr.domain.model.SingMode
-keep class com.sensibol.lucidmusic.singstr.domain.model.SongMini
-keep class com.sensibol.lucidmusic.singstr.domain.model.SingScore
-keep class com.sensibol.lucidmusic.singstr.gui.app.feed.FeedType
-keep class com.sensibol.lucidmusic.singstr.gui.app.profile.self.CoverView
-keep class com.sensibol.lucidmusic.singstr.gui.app.profile.self.ProfileView

#keep json classes
 -keepclassmembernames class * extends com.applozic.mobicommons.json.JsonMarker {
     !static !transient <fields>;
 }

 -keepclassmembernames class * extends com.applozic.mobicommons.json.JsonParcelableMarker {
     !static !transient <fields>;
 }
-keep class com.applozic.** {
    !static !transient <fields>;
}
 #GSON Config
-keepattributes Signature
-keep class sun.misc.Unsafe { *; }
-keep class com.google.gson.examples.android.model.** { *; }
-keep class org.eclipse.paho.client.mqttv3.logging.JSR47Logger { *; }
-keep class android.support.** { *; }
-keep interface android.support.** { *; }
-dontwarn android.support.v4.**
-keep public class com.google.android.gms.* { public *; }
-dontwarn com.google.android.gms.**
-keep class com.google.gson.** { *; }

