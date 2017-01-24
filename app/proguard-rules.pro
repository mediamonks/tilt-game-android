# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in C:\Users\stephan\AppData\Local\Android\sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

-optimizationpasses 4
-dontobfuscate
-dontusemixedcaseclassnames
-dontskipnonpubliclibraryclasses
-dontskipnonpubliclibraryclassmembers
-dontpreverify
-verbose

-keep class com.squareup.okhttp.** { *; }
-keep interface com.squareup.okhttp.** { *; }
-keep class **$$ViewInjector { *; }
-keep class org.andengine.extension.physics.box2d.** { *; }
-keep class com.badlogic.gdx.physics.box2d.** { *; }
-keep class com.mediamonks.googleflip.pages.game.physics.levels.** { *; }
-keep class com.mediamonks.googleflip.pages.game.physics.control.** { *; }
-keep class com.mediamonks.googleflip.pages.game.management.gamemessages.** { *; }
-keep class com.mediamonks.googleflip.pages.game.management.gamemessages.c2s.** { *; }
-keep class com.mediamonks.googleflip.pages.game.management.gamemessages.s2c.** { *; }
-keep class com.mediamonks.googleflip.data.vo.** { *; }
-keepclassmembers class com.mediamonks.googleflip.pages.game.management.gamemessages.** {
    <fields>;
}
-keepclassmembers class com.mediamonks.googleflip.data.constants.** {
    <fields>;
}

-keepattributes SourceFile,LineNumberTable,InnerClasses

-keep class com.google.gson.** { *; }
-keep class com.google.inject.** { *; }
-dontwarn com.google.gson.**

#ButterKnife
-keep class butterknife.** { *; }
-dontwarn butterknife.internal.**
-keep class **$$ViewBinder { *; }

-keepclasseswithmembernames class * {
@butterknife.* <fields>;
}

-keepclasseswithmembernames class * {
@butterknife.* <methods>;
}

-dontwarn butterknife.internal.**
-dontwarn retrofit.**
-dontwarn okio.**
-dontwarn com.squareup.okhttp.**
-dontwarn org.andengine.util.**

# Remove Android logging code (in this case, including errors).
-assumenosideeffects class android.util.Log {
    public static boolean isLoggable(java.lang.String, int);
    public static int v(...);
    public static int i(...);
    public static int w(...);
    public static int d(...);
    public static int e(...);
    public static java.lang.String getStackTraceString(java.lang.Throwable);
}
