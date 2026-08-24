plugins {
    id("com.android.application")
}

val ciBuildNumber = System.getenv("GITHUB_RUN_NUMBER")?.toIntOrNull()

android {
    namespace = "com.prankdom.stillalive"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.prankdom.stillalive"
        minSdk = 33
        targetSdk = 36
        versionCode = ciBuildNumber ?: 3
        versionName = if (ciBuildNumber != null) "0.3.$ciBuildNumber" else "0.3.0"
    }
}

dependencies {
    implementation("androidx.webkit:webkit:1.17.0")
    implementation("androidx.core:core-ktx:1.17.0")
}
