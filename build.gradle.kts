// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.compose) apply false

    //room
    alias(libs.plugins.ksp) apply false

    //room
    id("com.android.library") version "8.2.0" apply false

    //dagger-hilt
    alias(libs.plugins.hilt) apply false
}