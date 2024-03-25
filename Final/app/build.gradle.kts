plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.chatapplication"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.chatapplication"
        minSdk = 24
        targetSdk = 33
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
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
        dataBinding = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.4 .3"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }

}


dependencies {

    implementation("androidx.core:core-ktx:1.9.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.2")
    implementation("androidx.activity:activity-compose:1.8.2")
    implementation(platform("androidx.compose:compose-bom:2023.03.00"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.annotation:annotation:1.7.1")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.6.2")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.6.2")
    implementation("androidx.core:core-ktx:+")
    implementation("com.google.firebase:firebase-firestore-ktx:24.10.3")
    implementation("com.google.firebase:firebase-storage-ktx:20.3.0")
    implementation("com.google.firebase:firebase-storage:20.3.0")
    implementation("com.android.volley:volley:1.2.1")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation(platform("androidx.compose:compose-bom:2023.03.00"))
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")

    implementation("com.github.getActivity:EasyHttp:12.6")
    implementation("com.squareup.okhttp3:okhttp:3.12.13")
    implementation("com.google.code.gson:gson:2.9.0")
    implementation("com.github.getActivity:GsonFactory:6.5")
    implementation("com.jakewharton.timber:timber:4.7.1")

    implementation("org.greenrobot:eventbus:3.3.1")

    // https://github.com/getActivity/XXPermissions
    implementation("com.github.getActivity:XXPermissions:18.5")

    // Shape：https://github.com/getActivity/ShapeView
//    api("com.github.getActivity:ShapeView:6.0")
    // https://github.com/getActivity/ToastUtils
    implementation("com.github.getActivity:ToastUtils:10.5")
    // https://github.com/getActivity/TitleBar
    implementation("com.github.getActivity:TitleBar:9.2")
    // https://github.com/gyf-dev/ImmersionBar
    implementation("com.geyifeng.immersionbar:immersionbar:3.2.2")
    // https://github.com/bumptech/glide
    api("com.github.bumptech.glide:glide:4.14.2")
    // MMKV：https://github.com/Tencent/MMKV
    api("com.tencent:mmkv-static:1.2.14")
    //https://github.com/socketio/socket.io-client-java
    api("io.socket:socket.io-client:2.0.1")
    //https://github.com/gotev/android-upload-service
    api("net.gotev:uploadservice:4.7.0")

    api("com.github.chuckerteam.chucker:library:3.5.2")
    //https://github.com/square/retrofit
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    //https://github.com/MarkoMilos/Paginate
    api("com.github.markomilos:paginate:1.0.0")
    //https://github.com/PierfrancescoSoffritti/android-youtube-player
    api("com.pierfrancescosoffritti.androidyoutubeplayer:core:11.1.0")

    implementation("com.makeramen:roundedimageview:2.3.0")

    //link:https://github.com/facebook/shimmer-android
    api("com.facebook.shimmer:shimmer:0.5.0@aar")

    implementation("com.github.getActivity:ShapeView:9.0")
    // ShapeDrawable：https://github.com/getActivity/ShapeDrawable
    implementation("com.github.getActivity:ShapeDrawable:3.0")
    implementation("com.github.ybq:Android-SpinKit:1.4.0")
    implementation("com.cloudinary:cloudinary-android:2.0.0")
//    implementation("com.linkedin.android.litr:litr:1.1.0")
    implementation("com.google.firebase:firebase-appcheck")
    implementation("com.google.firebase:firebase-auth:22.0.0") // Use the latest version available
    implementation("com.google.firebase:firebase-appcheck-safetynet:16.1.2")
    api("com.google.android.exoplayer:exoplayer:2.17.1")
//Kéo thả màn hình: https://github.com/r0adkll/Slidr
    api("com.r0adkll:slidableactivity:2.1.0")
    // thu vien theo mo hinh
    api("com.google.android.flexbox:flexbox:3.0.0")
}