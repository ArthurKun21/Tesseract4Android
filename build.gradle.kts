plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
}


val tesseract4AndroidVersion by extra("4.8.0")