import com.gws.ussd.Deps

plugins {
    id("dagger.hilt.android.plugin")
    id("kotlin-kapt")
    id("org.jetbrains.kotlin.plugin.serialization")
}

android {
    namespace = "com.gws.ussd.networking"
}
dependencies {

    implementation(project(":common"))
    implementation(project(":local_models"))

    implementation("androidx.work:work-runtime-ktx:2.5.0")
    implementation(Deps.Androidx.CoreKtx.coreKtx)
    implementation(Deps.Androidx.AppCompat.appCompat)
    implementation(Deps.Google.Material.material)

    implementation(Deps.Hilt.hilt)
    implementation("androidx.hilt:hilt-common:1.0.0")
    kapt(Deps.Hilt.hilt_compiler)

    implementation(Deps.Networking.logging_interceptor)
    implementation(Deps.Networking.ok2Curl)

    implementation(Deps.Serialization.retrofitKotlinxSerializationConverter)
    implementation(Deps.Serialization.kotlinxSerialization)

    implementation(Deps.Coroutines.coroutines)

    implementation(Deps.Timber.timber)

    implementation(Deps.Androidx.Room.room)
    implementation(Deps.Androidx.Room.room_ktx)
    kapt(Deps.Androidx.Room.processor)

    implementation(Deps.Google.Gson.gson)

}
