plugins { id("com.android.application") }
android {
    namespace = "br.com.ipit.professor"
    compileSdk = 36
    buildFeatures { buildConfig = true }
    defaultConfig {
        applicationId = "br.com.ipit.professor"
        minSdk = 26
        targetSdk = 36
        versionCode = 3
        versionName = "0.2.1"
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    buildTypes { release { isMinifyEnabled = false } }
}

dependencies { implementation("androidx.core:core:1.13.1") }
