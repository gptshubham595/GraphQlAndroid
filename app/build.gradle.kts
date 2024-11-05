plugins {
    alias(libs.plugins.android.application)
    kotlin("android")
    id("com.apollographql.apollo")
}

android {
    namespace = "com.oops.graphqlapollo"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.oops.graphqlapollo"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    sourceSets {
        getByName("main") {
            java {
                srcDirs("src/main/graphql")
            }
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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.14"
    }

    buildFeatures {
        compose = true
    }

    apollo {
        service("SampleGraphQL") {
            packageName = "com.oops.graphqlapollo"
            introspection {
                endpointUrl = "http://localhost:4000/"
                schemaFile = File("src/main/graphql/com/oops/graphqlapollo/schema.sdl")
            }
        }
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    //compose
    implementation(platform(libs.compose.bom))
    implementation(libs.compose.foundation)
    implementation(libs.ui.tooling.preview)
    debugImplementation(libs.ui.tooling)
    debugImplementation(libs.compose.material)
    implementation(libs.activity.compose)
    implementation(libs.viewmodel.compose)


    implementation(libs.apollo.runtime)
}

tasks.register<Exec>("startGraphQLServer") {
    println("Starting GraphQL")
    description = "starts the graphql."
    group = "graph-ql"
    workingDir = project.file("$rootDir")
    commandLine("sh","startGraphQLServer.sh")
    doLast {
        logger.info("graphQL started successfully.")
    }
}

afterEvaluate {
    tasks.findByName("preBuild")?.apply {
        dependsOn("startGraphQLServer")
    }
}