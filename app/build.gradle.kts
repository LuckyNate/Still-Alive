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
        versionCode = 1
        versionName = "0.1.0"
    }
}

dependencies {
    implementation("androidx.webkit:webkit:1.17.0")
}
