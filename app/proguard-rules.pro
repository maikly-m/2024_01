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

-keepparameternames
-repackageclasses 'repack.uu.ss'

# 保留行数
-keepattributes SourceFile,LineNumberTable
#------------------------------------------------------------------
# activity都要保持其公共方法和属性
-keep class * extends androidx.appcompat.app.AppCompatActivity {
    public *;
}
-keep class * extends android.app.Activity {
    public *;
}

#Fragment不需要在AndroidManifest.xml中注册，需要额外保护下
-keep public class * extends android.app.Fragment
-keep public class * extends androidx.fragment.app.Fragment

# 保持androix库不被混淆
-keep class androidx.** {
    *;
}

# 保留特定的字段名，防止被混淆
-keepclassmembers class * {
    public <fields>;
}

# 防止混淆 Firebase 和其他 SDK（比如 Firebase Analytics）
-keep class com.google.firebase.** { *; }

# 禁用 R8 的某些优化（如对 Java 字符串常量的内联）
-dontoptimize

# 不对未使用的代码做去除
-dontwarn okhttp3.**

# 防止混淆在反射中使用的类和方法
-keep class * extends java.lang.reflect.Constructor { *; }
-keepclassmembers class * {
    public static <methods>;
}

# 禁止混淆日志类
-keep class android.util.Log { *; }

#------------------------------------------------------------------
# Gson 需要保留类型和字段名
-keep class com.google.gson.** { *; }

# 保留基于反射的类
-keepclassmembers class * {
    public <methods>;
    public <fields>;
}

# 禁用 R8 的某些优化（比如内联字符串常量）
-dontoptimize
# 将映射文件输出到指定路径
-printmapping build/outputs/mapping/release/mapping.txt

# 保持 Parcelable 不被混淆
-keepclassmembers class * implements android.os.Parcelable {
  public static final android.os.Parcelable$Creator CREATOR;
}

# 保存序列化类和非private成员不被混淆
-keepnames class * implements java.io.Serializable

# 网络相关
# okhttp--------------------------------------------------------------------------------- start
# JSR 305 annotations are for embedding nullability information.
-dontwarn javax.annotation.**

# A resource is loaded with a relative path so the package of this class must be preserved.
-keepnames class okhttp3.internal.publicsuffix.PublicSuffixDatabase

# Animal Sniffer compileOnly dependency to ensure APIs are compatible with older versions of Java.
-dontwarn org.codehaus.mojo.animal_sniffer.*

# OkHttp platform used only on JVM and when Conscrypt dependency is available.
-dontwarn okhttp3.internal.platform.ConscryptPlatform
# okhttp--------------------------------------------------------------------------------- end

#retrofit -------------------------------------------------------------------------------- start
# Retrofit does reflection on generic parameters. InnerClasses is required to use Signature and
# EnclosingMethod is required to use InnerClasses.
-keepattributes Signature, InnerClasses, EnclosingMethod

# Retrofit does reflection on method and parameter annotations.
-keepattributes RuntimeVisibleAnnotations, RuntimeVisibleParameterAnnotations

# Retain service method parameters when optimizing.
-keepclassmembers,allowshrinking,allowobfuscation interface * {
    @retrofit2.http.* <methods>;
}

# Ignore annotation used for build tooling.
-dontwarn org.codehaus.mojo.animal_sniffer.IgnoreJRERequirement

# Ignore JSR 305 annotations for embedding nullability information.
-dontwarn javax.annotation.**

# Guarded by a NoClassDefFoundError try/catch and only used when on the classpath.
-dontwarn kotlin.Unit

# Top-level functions that can only be used by Kotlin.
-dontwarn retrofit2.KotlinExtensions
-dontwarn retrofit2.KotlinExtensions$*

# With R8 full mode, it sees no subtypes of Retrofit interfaces since they are created with a Proxy
# and replaces all potential values with null. Explicitly keeping the interfaces prevents this.
-if interface * { @retrofit2.http.* <methods>; }
-keep,allowobfuscation interface <1>
#retrofit --------------------------------------------------------------------------------- end

#keep注解
#http://tools.android.com/tech-docs/support-annotations
-dontskipnonpubliclibraryclassmembers
-printconfiguration
-keep, allowobfuscation @interface androidx.annotation.Keep

-keep @androidx.annotation.Keep class *
-keepclassmembers class * {
    @androidx.annotation.Keep *;
}

-keep class retrofit2.** { *; }
-keepattributes *Annotation*
-keep class com.squareup.okhttp.** { *; }
-keep interface com.squareup.okhttp.** { *; }
-keep class okhttp3.** { *; }
-keep interface okhttp3.** { *; }

-keep class timber.log.Timber {
    *;
}

#----------------------app内部的 bean interface annotation 自定义view等---------


