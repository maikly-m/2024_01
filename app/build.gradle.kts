
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
                    val newFileName = "app-${android.defaultConfig.versionName}-${android.defaultConfig.versionCode}-${time}-${variant.name}.apk"
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

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}