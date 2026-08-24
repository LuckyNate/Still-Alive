plugins {
    id("com.android.application")
}

android {
    namespace = "com.prankdom.stillalive"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.prankdom.stillalive"
        minSdk = 33
        targetSdk = 36
        versionCode = 2
        versionName = "0.2.0"
    }
}

dependencies {
    implementation("androidx.webkit:webkit:1.17.0")
    implementation("androidx.core:core-ktx:1.17.0")
}
