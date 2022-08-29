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

# 这里是混淆后还能保留的属性。
# 保留异常，内部类，注解，泛型，保留行号的属性。
-ignorewarnings
-keepattributes Signature
-keepattributes Exceptions
-keepattributes *Annotation*
-keepattributes InnerClasses
# 还有其它属性，如：LocalVariableTable 局部变量、SourceFile 源文件、Deprecated 废弃、Synthetic 合成。

# 保留序列化。
# 比如我们要向activity传递对象使用了Serializable接口的时候，这时候这个类及类里面#的所有内容都不能混淆。
# 这里如果项目有用到序列化和反序列化要加上这句。
-keepnames class * implements java.io.Serializable
-keepclassmembers class * implements java.io.Serializable { *; }

-keep class * implements android.os.Parcelable {
    public static final android.os.Parcelable$Creator *;
}

# 保留适配器
-keep public class * extends android.widget.BaseAdapter {*;}

-keep public class * extends android.app.Activity
-keepclassmembers class * extends android.app.Activity {
   public void *(android.view.View);
}
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.app.backup.BackupAgentHelper
-keep public class * extends android.preference.Preference
-keep public class * extends android.view.View {*;}
-keep public class * extends android.widget.PopupWindow {*;}
-keep public class * extends android.app.Fragment
-dontwarn android.support.**
-keep class android.support.** { *; }
#-keep public class * extends android.support.v4.**
#-keep public class * extends android.support.v7.**
-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet);
}
-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet, int);
}

# 保留点击和回调函数
-keepclasseswithmembers class * {
    void onClick*(...);
}
-keepclasseswithmembers class * {
    *** *Callback(...);
}
-keep class com.example.common.** {*;}
-keep class java.math.** {*;}
#retrofit2
-dontwarn retrofit2.**
-keep class retrofit2.** { *; }
-keepattributes Signature
-keepattributes Exceptions

# okhttp
-dontwarn com.squareup.okhttp3.**
-keep class com.squareup.okhttp3.** { *;}
-dontwarn okio.**

-keep class sun.misc.Unsafe { *; }
-keep class com.google.gson.stream.** { *; }
-keep class com.google.gson { *; }

#Glide
-keep public class * implements com.bumptech.glide.module.GlideModule
-keep public class * extends com.bumptech.glide.AppGlideModule
-keep public enum com.bumptech.glide.load.resource.bitmap.ImageHeaderParser$** {
  **[] $VALUES;
  public *;
}
# for DexGuard only
# 从glide4.0开始，GifDrawable没有提供getDecoder()方法，
# 需要通过反射获取gifDecoder字段值，所以需要保持GifFrameLoader和GifState类不被混淆
-keep class com.bumptech.glide.load.resource.gif.GifDrawable$GifState{*;}
-keep class com.bumptech.glide.load.resource.gif.GifFrameLoader {*;}
-keep class com.bumptech.glide.load.data.ParcelFileDescriptorRewinder$InternalRewinder {
  *** rewind();
}
-keep class **.entity.** {*;}
-keep class com.texeasy { *; }

# bugly
-dontwarn com.tencent.bugly.**
-keep public class com.tencent.bugly.**{*;}