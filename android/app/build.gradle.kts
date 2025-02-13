plugins {
    alias(libs.plugins.android.application)

    // Add the Google services Gradle plugin
    id("com.google.gms.google-services")
}

android {
    namespace = "it.unimib.devtrinity.moneymind"
    compileSdk = 34

    defaultConfig {
        applicationId = "it.unimib.devtrinity.moneymind"
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    implementation(libs.material.v1110)
    implementation(libs.google.material.v190)

    //android worker
    implementation(libs.work.runtime.v291)
    implementation(libs.guava)

    //firebase
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.auth)

    //firestore
    implementation(libs.firebase.firestore)

    //room
    implementation(libs.room.runtime)
    annotationProcessor(libs.room.compiler)

    //Retrofit
    implementation(libs.retrofit2.retrofit)
    implementation(libs.retrofit2.converter.gson)

}