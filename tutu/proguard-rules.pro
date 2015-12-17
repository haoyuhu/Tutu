# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in /Applications/adt-bundle-mac-x86_64-20140702/sdk/tools/proguard/proguard-android.txt
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

# This is a configuration file for ProGuard.
# http://proguard.sourceforge.net/index.html#manual/usage.html

-dontusemixedcaseclassnames
-dontskipnonpubliclibraryclasses
-verbose

# Optimization is turned off by default. Dex does not like code run
# through the ProGuard optimize and preverify steps (and performs some
# of these optimizations on its own).
-dontoptimize
-dontpreverify
# Note that if you want to enable optimization, you cannot just
# include optimization flags in your own project configuration file;
# instead you will need to point to the
# "proguard-android-optimize.txt" file instead of this one from your
# project.properties file.

-keepattributes *Annotation*
-keep public class com.google.vending.licensing.ILicensingService
-keep public class com.android.vending.licensing.ILicensingService

# For native methods, see http://proguard.sourceforge.net/manual/examples.html#native
-keepclasseswithmembernames class * {
    native <methods>;
}

# keep setters in Views so that animations can still work.
# see http://proguard.sourceforge.net/manual/examples.html#beans
-keepclassmembers public class * extends android.view.View {
   void set*(***);
   *** get*();
}

# We want to keep methods in Activity that could be used in the XML attribute onClick
-keepclassmembers class * extends android.app.Activity {
   public void *(android.view.View);
}

# For enumeration classes, see http://proguard.sourceforge.net/manual/examples.html#enumerations
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

-keep class * implements android.os.Parcelable {
  public static final android.os.Parcelable$Creator *;
}

-keepclassmembers class **.R$* {
    public static <fields>;
}

# The support library contains references to newer platform versions.
# Don't warn about those in case this app is linking against an older
# platform version.  We know about them, and they are safe.
-dontwarn android.support.**

# $ANDROID_HOME/tools/proguard/proguard-android.txt will also be used
# option in that file are not mentioned here

#apk 包内所有 class 的内部结构
#-dump class_files.txt
#未混淆的类和成员
#-printseeds seeds.txt
#列出从 apk 中删除的代码
-printusage unused.txt
#混淆前后的映射
-printmapping mapping.txt

# http://proguard.sourceforge.net/manual/retrace/examples.html#with
# show
-renamesourcefileattribute TUNow
-keepattributes SourceFile,LineNumberTable

## for umeng analytics
# http://bbs.umeng.com/thread-5446-1-1.html
# note: last part of the post has been set in proguard-android.txt

-keepclassmembers class * {
    public <init>(org.json.JSONObject);
}

-keep public class com.huhaoyu.tutu.R$*{
    public static final int *;
}

## for umeng message
# http://dev.umeng.com/push/android/170%E9%9B%86%E6%88%90%E6%96%87%E6%A1%A3#1_1
# see 1.8.2
-keep class org.android.agoo.** {
    public <fields>;
    public <methods>;
}

-keep class com.umeng.message.* {
    public <fields>;
    public <methods>;
}

# for jsoup
-keep public class org.jsoup.** {
    public *;
}

## For Gson
# https://github.com/krschultz/android-proguard-snippets/blob/master/libraries/proguard-gson.pro
## GSON 2.2.4 specific rules ##

# Gson uses generic type information stored in a class file when working with fields. Proguard
# removes such information by default, so configure it to keep all of it.
-keepattributes Signature

# For using GSON @Expose annotation
-keepattributes *Annotation*

-keepattributes EnclosingMethod

# Gson specific classes
-keep class sun.misc.Unsafe { *; }
-keep class com.google.gson.** { *; }

## support-v7-appcompat
# https://github.com/krschultz/android-proguard-snippets/blob/master/libraries/proguard-support-v7-appcompat.pro
-keep public class android.support.v7.widget.** { *; }
-keep public class android.support.v7.internal.widget.** { *; }
-keep public class android.support.v7.internal.view.menu.** { *; }

-keep public class * extends android.support.v4.view.ActionProvider {
    public <init>(android.content.Context);
}

## support-v7-cardview
# https://github.com/krschultz/android-proguard-snippets/blob/master/libraries/proguard-support-v7-cardview.pro
# http://stackoverflow.com/questions/29679177/cardview-shadow-not-appearing-in-lollipop-after-obfuscate-with-proguard/29698051
-keep class android.support.v7.widget.RoundRectDrawable { *; }

### http://coolshell.info/blog/2015/03/android-studio-prefrence.html
-keep public class * extends android.app.Fragment
-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.preference.PreferenceActivity
-keep public class * extends android.support.v4.app.FragmentActivity
-keep public class * extends android.support.v7.app.ActionBarActivity
-keep public class * extends android.app.Service
-keep class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.app.backup.BackupAgentHelper
-keep public class * extends android.preference.Preference
-keep public class com.android.vending.licensing.ILicensingService
-keep public class * extends android.support.v4.app.Fragment

# for okhttp used by umeng push
-keep class com.squareup.okhttp.** { *; }
-keep interface com.squareup.okhttp.** { *; }
# for umeng addAlias
-keep class com.google.** { *; }

# for No implementation found for long org.android.spdy.SpdyAgent.submitRequest
-keep,includedescriptorclasses class org.android.** {
    *;
}

# temporarily disable obfuscation for all 3rd party libs
-keep class !mu.lab.** { *; }
-keep class !com.huhaoyu.tutu.** { *; }
-keepclasseswithmembernames class !mu.lab.** { *; }
-keepclasseswithmembernames class !com.huhaoyu.tutu.** { *; }

-keep class butterknife.** { *; }
-dontwarn butterknife.internal.**
-keep class **$$ViewBinder { *; }

-keepclasseswithmembernames class * {
    @butterknife.* <fields>;
}

-keepclasseswithmembernames class * {
    @butterknife.* <methods>;
}

# http://square.github.io/otto/
-keepattributes *Annotation*
-keepclassmembers class ** {
    @com.squareup.otto.Subscribe public *;
    @com.squareup.otto.Produce public *;
}

# rx
-keep class io.realm.annotations.RealmModule
-keep @io.realm.annotations.RealmModule class *
-dontwarn javax.**
-dontwarn io.realm.**
-dontwarn rx.internal.**

# okhttp
-dontwarn okio.**

# for org.apache and so on.
-ignorewarnings
