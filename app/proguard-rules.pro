# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in C:\Android\sdk/tools/proguard/proguard-android.txt
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
-verbose
-ignorewarnings
-repackageclasses 'o'
-printmapping mapping.txt
-dontskipnonpubliclibraryclassmembers

-keepattributes *Annotation*,Signature,InnerClasses,SourceFile,LineNumberTable,Exceptions,EnclosingMethod

# Preserve annotated Javascript interface methods
-dontnote android.webkit.JavascriptInterface
-keepclassmembers class * {
    @android.webkit.JavascriptInterface <methods>;
}

# Preserve all View implementations, their special context constructors, and their setters
-keep public class * extends android.view.View {
    public <init>(android.content.Context);
    public <init>(android.content.Context, android.util.AttributeSet);
}

# Ignore classes in descriptors of support classes
-dontnote android.support.*.view.**
-dontnote android.support.*.widget.**

# Ignore references from compatibility support classes to missing classes
-dontwarn android.support.**Compat*
-dontwarn android.support.**Honeycomb*
-dontwarn android.support.**IceCreamSandwich*
-dontwarn android.support.**JellyBean*
-dontwarn android.support.**JB*
-dontwarn android.support.**KitKat*
-dontwarn android.support.**19
-dontwarn android.support.**20
-dontwarn android.support.**21
-dontwarn android.support.**22
-dontwarn android.support.**23
-dontwarn android.support.design.**
-dontwarn android.support.v7.internal.**
-dontwarn android.support.v7.widget.Toolbar
-dontwarn android.app.Notification$Builder

# Avoid merging and inlining compatibility classes
-keep,allowshrinking class android.support.**Compat* { *; }
-keep,allowshrinking class android.support.**Honeycomb* { *; }
-keep,allowshrinking class android.support.**IceCreamSandwich* { *; }
-keep,allowshrinking class android.support.**JellyBean* { *; }
-keep,allowshrinking class android.support.**JB* { *; }
-keep,allowshrinking class android.support.**KitKat* { *; }
-keep,allowshrinking class android.support.**19 { *; }
-keep,allowshrinking class android.support.**20 { *; }
-keep,allowshrinking class android.support.**21 { *; }
-keep,allowshrinking class android.support.**22 { *; }
-keep,allowshrinking class android.support.**23 { *; }

# AppCompat v4
-keep class android.support.v4.** { *; }
-keep interface android.support.v4.** { *; }

-dontnote android.support.v4.app.Fragment
-keepclassmembers class android.support.v4.app.Fragment {
    public android.view.View getView();
}

-dontnote android.support.v4.app.FragmentManager
-keepclassmembers class android.support.v4.app.FragmentManager {
    public android.support.v4.app.Fragment findFragmentById(int);
    public android.support.v4.app.Fragment findFragmentByTag(java.lang.String);
}

# AppCompat v7
-keep public class android.support.v7.widget.** { *; }
-keep public class android.support.v7.internal.widget.** { *; }
-keep class android.support.v7.widget.LinearLayoutManager { *; }
-keep public class android.support.v7.internal.view.menu.** { *; }

-keep public class * extends android.support.v4.view.ActionProvider {
    public <init>(android.content.Context);
}

# Design support library
-dontwarn android.support.design.**
-keep class android.support.design.** { *; }
-keep interface android.support.design.** { *; }
-keep public class android.support.design.R$* { *; }
-keepnames class android.support.design.widget.CoordinatorLayout
-keep !abstract class android.support.design.widget.* implements android.support.design.widget.CoordinatorLayout$Behavior {
    <init>(android.content.Context, android.util.AttributeSet);
}

# Android annotations
-dontwarn org.androidannotations.api.rest.**

# Signature optimized with class from API level 19 or higher
-keep,allowshrinking class android.support.v4.app.FragmentState$InstantiationException {
    <init>(...);
}

-keep,allowshrinking class android.support.v4.app.Fragment$InstantiationException {
    <init>(...);
}

# Retrofit
-dontwarn retrofit2.**
-keep class retrofit2.** { *; }

-keepclasseswithmembers class * {
    @retrofit2.http.* <methods>;
}

# OkHttp3
-dontwarn okhttp3.**
-keep class okhttp3.** { *; }
-keep interface okhttp3.** { *; }

# Okio
-dontwarn okio.**
-dontwarn org.codehaus.mojo.animal_sniffer.IgnoreJRERequirement

# Gson
-keep,allowobfuscation class com.google.gson.annotations.*

-dontnote com.google.gson.annotations.Expose
-keepclassmembers class * {
    @com.google.gson.annotations.Expose <fields>;
}

-dontnote com.google.gson.annotations.SerializedName
-keepclassmembers,allowobfuscation class * {
    @com.google.gson.annotations.SerializedName <fields>;
}

-keepclassmembers enum * {
    @com.google.gson.annotations.SerializedName <fields>;
}

-keep class sun.misc.Unsafe { *; }
-keep class com.google.gson.stream.** { *; }

# EventBus
-keepclassmembers class ** {
    @org.greenrobot.eventbus.Subscribe <methods>;
}

-keep enum org.greenrobot.eventbus.ThreadMode { *; }

-keepclassmembers class ** {
    public void onEvent*(***);
    public void handleResponse*(***);
}

-keepclassmembers class ** {
	@org.greenrobot.eventbus.Subscribe <methods>;
}

-keepclassmembers class * extends org.greenrobot.eventbus.util.ThrowableFailureEvent {
    <init>(java.lang.Throwable);
}

# Timber
-dontwarn org.jetbrains.annotations.**

# Remove logs
-assumenosideeffects class android.util.Log {
    public static boolean isLoggable(java.lang.String, int);
    public static int v(...);
    public static int i(...);
    public static int w(...);
    public static int d(...);
    public static int e(...);
    public static java.lang.String getStackTraceString(java.lang.Throwable);
}

-assumenosideeffects class java.lang.Exception {
    public void printStackTrace();
}

# Glide
-keep class com.bumptech.glide.integration.okhttp3.OkHttpGlideModule