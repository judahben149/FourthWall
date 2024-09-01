plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.jetbrainsKotlinAndroid)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt)
}

android {
    namespace = "com.judahben149.fourthwall"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.judahben149.fourthwall"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
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
        viewBinding = true
    }
    packagingOptions {
        resources {
            excludes += setOf("META-INF/DEPENDENCIES")
        }
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.navigation.fragment)

    ksp(libs.hilt.android.compiler)
    implementation(libs.hilt.android)
    implementation(libs.glide)

    /** required to make tbdex and key management work */
    implementation(libs.androidx.security.crypto)

    implementation("xyz.block:tbdex-httpclient:2.0.1") {
        exclude(group = "com.google.protobuf", module = "protobuf-java")
        exclude(group = "org.bouncycastle", module = "bcprov-jdk18on")
        exclude(group = "com.github.stephenc.jcip", module = "jcip-annotations")
        exclude(group = "com.google.crypto.tink", module="tink")
    }

    implementation("xyz.block:tbdex-protocol:2.0.1") {
        exclude(group = "com.google.protobuf", module = "protobuf-java")
        exclude(group = "org.bouncycastle", module = "bcprov-jdk18on")
        exclude(group = "com.github.stephenc.jcip", module = "jcip-annotations")
        exclude(group = "com.google.crypto.tink", module="tink")
    }



    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}