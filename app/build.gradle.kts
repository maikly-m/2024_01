
import com.android.build.api.variant.impl.VariantOutputImpl
import java.time.Instant
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
}

android {
    namespace = "com.example.u"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.u"
        minSdk = 26
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        // 配置 NDK 支持的架构
        ndk {
            abiFilters.add("armeabi-v7a")
            abiFilters.add("arm64-v8a")
        }
    }
    lint {
        baseline = file("lint-baseline.xml")
    }

    buildTypes {
        getByName("debug") {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro"
            )
        }
        getByName("release") {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro"
            )
        }
    }
    flavorDimensions("default")
    productFlavors {
        create("default") {
            dimension = "default"
            buildConfigField("String", "productFlavors", "\"default\"")
            manifestPlaceholders["PRODUCT_FLAVOR"] = "default"
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        dataBinding = true
        viewBinding = true
        buildConfig = true
    }

//    // 配置 NDK 架构过滤器，指定只打包 arm64 和 armeabi-v7a 架构
//    splits {
//        abi {
//            isEnable = true
//            reset()  // 清除默认值，确保只有需要的架构被打包
//            include("armeabi-v7a", "arm64-v8a")  // 只打包这两种架构
//            isUniversalApk = false  // 禁用生成包含所有架构的通用 APK
//        }
//    }

    packagingOptions {
        exclude("META-INF/gradle/incremental.annotation.processors")
    }

    // 获取 UTC 时间并格式化为 "yyyyMMdd" 格式
    val time =
        DateTimeFormatter.ofPattern("yyyyMMdd").withZone(ZoneOffset.UTC).format(Instant.now())
    androidComponents {
        onVariants { variant ->
            println("onVariants name: ${variant.name}")
            variant.outputs.forEach { output ->
                if (output is VariantOutputImpl){
                    println("outputFileName = ${output.outputFileName.get()}")
                    val substringAfterLast = output.outputFileName.get().substringAfterLast('.')
                    println("substringAfterLast = $substringAfterLast")
                    val newFileName = "app-${android.defaultConfig.versionName}-${android.defaultConfig.versionCode}-${time}-${variant.name}.${substringAfterLast}"
                    output.outputFileName = newFileName
                }
            }
        }
    }
}

dependencies {
    implementation(files("../libs/mid-sdk-2.10.jar"))
    implementation(files("../libs/mta-sdk-2.0.0.jar"))
    implementation(files("../libs/open_sdk_lite.jar"))
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.lifecycle.livedata.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)

    implementation(libs.guolindev.permissionx)
    implementation(libs.squareup.retrofit)
    implementation(libs.squareup.retrofit.converter.gson)
    implementation(libs.google.gson)
    implementation(libs.loggingInterceptor)
    implementation(libs.jakewharton.timber)
    implementation(libs.tencent.mm.opensdk)
    implementation(libs.auto.value.gson) // AutoValue Gson 扩展库
    implementation(project(":SlideVerify"))
    annotationProcessor(libs.auto.value)  // AutoValue 注解处理器
    annotationProcessor(libs.auto.value.gson)   // Gson 扩展的注解处理器
    implementation(libs.zxing.core)  // ZXing 核心库
    implementation(libs.zxing.android.embedded)  // ZXing Android 嵌入式库
    implementation(libs.jsoup)
    implementation(libs.glide)  // Glide 的核心库
    annotationProcessor(libs.compiler)  // Glide 的注解处理器，用于生成 Glide API
    implementation(libs.androidx.media3.exoplayer)
    implementation(libs.androidx.media3.ui)
    implementation(libs.compressor)
    implementation(libs.material.dialogs.core)
    implementation(libs.androidpicker.common)
    implementation(libs.androidpicker.wheelpicker)
    implementation(libs.mlkit.barcode.scanning)
    implementation(libs.androidx.camera.core)
    implementation(libs.androidx.camera.view)
    implementation(libs.androidx.camera.lifecycle)
    implementation(libs.androidx.camera.camera2)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}