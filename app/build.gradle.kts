plugins {
    //id("com.google.devtools.ksp")
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.google.gms.google.services)
    alias(libs.plugins.ksp)
}

android {
    namespace = "com.example.bloom"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.bloom"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        val apiKey: String = project.findProperty("GEMINI_API_KEY") as? String ?: ""
        buildConfigField("String", "GEMINI_API_KEY", "\"$apiKey\"")
        buildConfigField("String", "WEB_CLIENT_ID", "\"$apiKey\"")
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
    kotlinOptions {
        jvmTarget = "11"
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.runtime.livedata)

    // Jetpack Compose
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
    implementation(libs.androidx.navigation.compose)

    //ARCHITECTURE - ViewModel
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.7.0")

    // COIL (Pour charger les images par Uri/File)
    implementation("io.coil-kt:coil-compose:2.6.0")

    //BASE DE DONNÉES LOCALE (ROOM)
    implementation(libs.room.runtime)
    implementation(libs.room.ktx)
    ksp(libs.room.compiler)

    //REQUÊTES WEB (KTOR)
    implementation("io.ktor:ktor-client-core:2.3.8")
    implementation("io.ktor:ktor-client-android:2.3.8")
    implementation("io.ktor:ktor-client-content-negotiation:2.3.8")
    implementation("io.ktor:ktor-serialization-kotlinx-json:2.3.8")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.3")

    //FIREBASE AUTHENTICATION
    //BOM (Bill of Materials) pour les versions Firebase. DOIT être en platform()
    implementation(platform(libs.firebase.bom))
    //Dépendance Firebase (la version est prise du BOM)
    implementation(libs.firebase.auth.ktx)
    //implementation(libs.firebase.auth)

    implementation("com.google.ai.client.generativeai:generativeai:0.8.0")

    //Pour l'accès aux permissions (nécessaire pour la caméra/galerie)
    implementation("androidx.activity:activity-ktx:1.8.2")

    //TESTS
    androidTestImplementation(libs.androidx.espresso.core)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)


    //BOM Supabase
    implementation(platform(libs.supabaseBom))
    // Modules Supabase - Ils héritent de la version du BOM
    implementation(libs.supabasePostgrest)
    implementation(libs.supabaseStorage)

    //SERIALIZATION
    implementation(libs.kotlinx.serialization.json)

    //Google Sign-InS
    implementation(libs.google.auth)

    // Foundation (Column, Row, Box, Image, Spacer, etc.)
    implementation("androidx.compose.foundation:foundation")
    // Activité (pour les fonctions de setup)
    implementation("androidx.activity:activity-compose:1.9.0")

    // Hilt (Injection de Dépendances)
    implementation("com.google.dagger:hilt-android:2.51.1")
}