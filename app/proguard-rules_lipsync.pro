-optimizationpasses 4
#-allowaccessmodification
-dontusemixedcaseclassnames
-dontskipnonpubliclibraryclasses
-dontskipnonpubliclibraryclassmembers
-dontpreverify
-verbose

#your package path where your gson models are stored
-keep class com.mediamonks.comcast.lipsync.data.vo.** { *; }
#your package path where your customviews
-keep class com.mediamonks.comcast.lipsync.ui.** {*;}

# The -optimizations option disables some arithmetic simplifications that Dalvik 1.0 and 1.5 can't handle.
#-optimizations !code/simplification/arithmetic,!field/*,!class/merging/*,!method/propagation/parameter
-keepattributes SourceFile,LineNumberTable,InnerClasses

#Google play services proguard rules:
-keep class * extends java.util.ListResourceBundle {
    protected Object[][] getContents();
}

-keep public class com.google.android.gms.common.internal.safeparcel.SafeParcelable {
    public static final *** NULL;
}

-keepnames @com.google.android.gms.common.annotation.KeepName class *
-keepclassmembernames class * {
    @com.google.android.gms.common.annotation.KeepName *;
}

-keepnames class * implements android.os.Parcelable {
    public static final ** CREATOR;
}
## because google play service gets stripped of unnessecary methods and code dont warn
-dontwarn com.google.android.gms.**

# Ignore overridden Android classes
-dontwarn android.**
-keep class android.**

# Ignore imported library references
-dontwarn android.support.v4.**
-keep class android.support.v4.** { *; }

-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.preference.Preference

-keepclasseswithmembernames class * {
    native <methods>;
}

# Keep all public constructors of all public classes, but still obfuscate+optimize their content.
# This is necessary because optimization removes constructors which are called through XML.
-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet, int);
    public <init>(android.content.Context, android.util.AttributeSet);
    public <init>(android.content.Context);
}
-keepclassmembers class * extends android.app.Activity {
    public void *(android.view.View);
}
-keepclassmembers class **.R$* {
    public static <fields>;
}

# Keep serializable objects
-keepclassmembers class * implements java.io.Serializable {
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}

-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

#-keep class * implements android.os.Parcelable {
#    public static final android.os.Parcelable$Creator *;
#}

-keep class * extends java.util.ListResourceBundle {
    protected Object[][] getContents();
}

##########################
## SECURITY             ##
##########################

# Remove VERBOSE and DEBUG level log statements
#-assumenosideeffects class android.util.Log {
#    public static *** i(...);
#    public static *** d(...);
#    public static *** v(...);
#    public static *** e(...);
#    public static *** w(...);
#    public static *** wtf(...);
#}

##########################
## SUPPORT LIBRARIES    ##
##########################

# support-v4
-dontwarn android.support.v4.**
-keep class android.support.v4.app.** { *; }
-keep interface android.support.v4.app.** { *; }

# support-v7
-dontwarn android.support.v7.**
-keep class android.support.v7.internal.** { *; }
-keep interface android.support.v7.internal.** { *; }
-keep class android.support.v7.** { *; }
-keep interface android.support.v7.** { *; }

##########################
## LIBRARIES            ##
##########################

## LIBRARY: Retrofit
-keep class org.apache.http.** { *; }
-keep class org.apache.james.mime4j.** { *; }
-keep class javax.inject.** { *; }
-keep class retrofit.** { *; }
-keepclasseswithmembers class * {
    @retrofit.http.* <methods>;
}
-dontwarn rx.**
-dontwarn retrofit.**
-dontwarn com.google.appengine.**

## LIBRARY: GSON
-keep class com.google.gson.** { *; }
-keep class com.google.inject.** { *; }
-dontwarn com.google.gson.**

# Gson uses generic type information stored in a class file when working with
# fields. Proguard removes such information by default, so configure it to keep
# all of it. Facebook SDK Also requires this.
-keepattributes Signature

-keepclassmembers enum * {
     public static **[] values();
     public static ** valueOf(java.lang.String);
}

# For using GSON @Expose annotation
-keepattributes *Annotation*

# Gson specific classes
-keep class sun.misc.Unsafe { *; }

# PagerSlidingTabStrip classes
-keep class com.astuetz.** {*;}

## LIBRARY: OkHttp
-keep class com.squareup.okhttp.** { *; }
-keep interface com.squareup.okhttp.** { *; }
-dontwarn com.squareup.okhttp.**
-dontwarn okio.**

## ButterKnife
-keep class butterknife.** { *; }
-dontwarn butterknife.internal.**
-keep class **$$ViewBinder { *; }

-keepclasseswithmembernames class * {
    @butterknife.* <fields>;
}

-keepclasseswithmembernames class * {
    @butterknife.* <methods>;
}

## Crashlytics
-keep class com.crashlytics.** { *; }

## StickyListHeader
-keep class com.emilsjolander.** { *; }
-dontwarn com.emilsjolander.**

## Joda time
-keep public class org.joda.time.** {public private protected *;}
-dontwarn org.joda.time.**

## Urban Airship
## Required even for GCM only apps
-dontwarn com.amazon.device.messaging.**
-dontwarn com.urbanairship.amazon.ADMWrapper.**
-dontwarn com.urbanairship.push.ADMRegistrar.**
-dontwarn com.urbanairship.push.iam.view.**

-keep public class * extends com.urbanairship.Autopilot

## Required for the Javascript Interface
-keepclassmembers class com.urbanairship.js.UAJavascriptInterface {
   public *;
}
## SQLCipher
-optimizations !code/simplification/arithmetic,!field/*,!class/merging/*,!code/allocation/variable

-keep class net.sqlcipher.** { *; }

-keep class net.sqlcipher.database.** { *; }
-dontwarn net.sqlcipher.**
-keepattributes EnclosingMethod

## Spongycastle
-keep class org.spongycastle.** { *; }
-dontwarn org.spongycastle.jce.provider.X509LDAPCertStoreSpi
-dontwarn org.spongycastle.x509.util.LDAPStoreHelper

## Cardslib
-dontwarn it.gmariotti.cardslib.library.**

## Cupboard
-dontwarn nl.qbusict.cupboard.**

## Mobfox ads SDK
-keep class com.adsdk.** { *; }
-keep class com.adsdk.sdk.** { *;}
-keep class com.adsdk.sdk.video.** { *; }

-keep class com.mobfox.** { *; }
-keep class com.mobfox.adapter.** {*;}
-keep class com.mobfox.sdk.** {*;}
-keep class com.mobfox.sdk.data.** {*;}
-keep class com.mobfox.video.** {*;}

-keep class org.simpleframework.** { *; }
-dontwarn javax.xml.stream.**

-dontwarn com.adsdk.sdk.customevents.**

## mediation libraries for Mobfox
-dontwarn com.vungle.**
-dontwarn com.millennialmedia.**
-dontwarn com.mopub.**
-dontwarn com.inmobi.monetization.**
-dontwarn com.inmobi.commons.**
-dontwarn com.flurry.**
-dontwarn com.facebook.**
-dontwarn com.chartboost.**
-dontwarn com.amazon.**
-dontwarn com.unity3d.**
-dontwarn com.jirbo.adcolony.**

# Widespace
-keep class com.widespace.internal.interfaces.** {
   *;
}
-dontwarn com.widespace.adspace.helpers.AttributesParseHelper

## JAVACV
-keep @org.bytedeco.javacpp.annotation interface * {
    *;
}
-keep @org.bytedeco.javacpp.annotation.Platform public class *
-keepclasseswithmembernames class * {
    @org.bytedeco.* <fields>;
}
-keepclasseswithmembernames class * {
    @org.bytedeco.* <methods>;
}
-keepattributes EnclosingMethod
-keep @interface org.bytedeco.javacpp.annotation.*,javax.inject.*
-keepattributes *Annotation*, Exceptions, Signature, Deprecated, SourceFile, SourceDir, LineNumberTable, LocalVariableTable, LocalVariableTypeTable, Synthetic, EnclosingMethod, RuntimeVisibleAnnotations, RuntimeInvisibleAnnotations, RuntimeVisibleParameterAnnotations, RuntimeInvisibleParameterAnnotations, AnnotationDefault, InnerClasses
-keep class org.bytedeco.javacpp.** {*;}
-dontwarn java.awt.**
-dontwarn org.bytedeco.javacv.**
-dontwarn org.bytedeco.javacpp.**


#Twitter SDK
#Proguard Config for when AppCompat is not a dependency
-dontwarn  com.digits.sdk.android.*ActionBarActivity

# retrofit specific
-dontwarn com.squareup.okhttp.**
-dontwarn com.google.appengine.api.urlfetch.**
-dontwarn rx.**
-dontwarn retrofit.**
-keepattributes Signature
-keepattributes *Annotation*
-keep class com.squareup.okhttp.** { *; }
-keep interface com.squareup.okhttp.** { *; }
-keep class retrofit.** { *; }
-keepclasseswithmembers class * {
    @retrofit.http.* <methods>;
}

## mp4parser
-dontwarn com.googlecode.mp4parser.authoring.tracks.**
-keep class com.coremedia.iso.** { *; }
-keep interface com.coremedia.iso.** { *; }

-keep class com.mp4parser.** { *; }
-keep interface com.mp4parser.** { *; }

-keep class com.googlecode.mp4parser.** { *; }
-keep interface com.googlecode.mp4parser.** { *; }