import org.jlleitschuh.gradle.ktlint.reporter.ReporterType

plugins {
    alias(libs.plugins.android.application)
    kotlin("android")
    kotlin("kapt")
    alias(libs.plugins.apollo4.plugin)
    alias(libs.plugins.hilt.android.plugin)
    alias(libs.plugins.ktlint.plugin)
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
        dataBinding = true
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

    implementation(libs.hilt.android)
    kapt(libs.hilt.android.compiler)

    implementation(libs.apollo.runtime)
}

tasks.register<Exec>("startGraphQLServer") {
    println("Starting GraphQL")
    description = "starts the graphql."
    group = "graph-ql"
    workingDir = project.file("$rootDir")
    commandLine("sh","startGraphQLServer.sh")
    doLast {
//        commandLine(":app:downloadSampleGraphQLApolloSchemaFromIntrospection") // RUN FIRST TIMER
        logger.info("graphQL started successfully.")
    }
}

afterEvaluate {
    tasks.findByName("preBuild")?.apply {
        dependsOn("startGraphQLServer")
//        dependsOn(":app:generateApolloSources") // RUN FIRST TIMER
    }
}

ktlint {
    version.set("0.48.2")
    debug.set(true)
    verbose.set(true)
    android.set(true)
    outputToConsole.set(true)
    outputColorName.set("RED")
    ignoreFailures.set(true)
    enableExperimentalRules.set(true)
    additionalEditorconfig.set( // not supported until ktlint 0.49
        mapOf(
            "max_line_length" to "20"
        )
    )
    // disabledRules.set(setOf("final-newline")) // not supported with ktlint 0.48+
    baseline.set(file("$projectDir/config/ktlint-baseline.xml"))
    reporters {
        reporter(ReporterType.PLAIN)
        reporter(ReporterType.CHECKSTYLE)
        reporter(ReporterType.SARIF)
    }
    kotlinScriptAdditionalPaths {
        include(fileTree("scripts/"))
    }
    filter {
        exclude("**/generated/**")
        include("**/kotlin/**")
    }
}
