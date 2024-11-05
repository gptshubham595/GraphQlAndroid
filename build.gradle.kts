// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.apollo4.plugin)
    alias(libs.plugins.hilt.android.plugin) apply false
    alias(libs.plugins.ktlint.plugin) apply false
}