
// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.androidApplication) apply false
    alias(libs.plugins.jetbrainsKotlinAndroid) apply false
    kotlin("jvm") version "1.8.0"
    id("org.jetbrains.kotlin.plugin.serialization") version "1.9.0"
    id("com.google.dagger.hilt.android") version "2.44" apply false
    kotlin("kapt") version "1.9.0"

}
buildscript {
    dependencies {
        classpath ("com.google.dagger:hilt-android-gradle-plugin:2.44")
    }
}