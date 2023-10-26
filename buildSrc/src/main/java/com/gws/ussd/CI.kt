package com.gws.ussd

object CI {
    private const val versionMajor = 1
    private const val versionMinor = 0
    private const val versionPatch = 0
    private const val jvmTarget = "1.8"
    private const val jdkDesugar = "com.android.tools:desugar_jdk_libs:1.1.5"

    private val githubBuildNumber: Int
        get() = System.getenv("GITHUB_RUN_NUMBER")?.toInt() ?: 0

    val versionCode = versionMajor * 10000 + versionMinor * 1000 +
            versionPatch * 100 + githubBuildNumber

    private const val snapshotBase = "$versionMajor.$versionMinor.$versionPatch"

    private val snapshotVersion: String
        get() = when (val n = githubBuildNumber) {
            0 -> "$snapshotBase"
            else -> "$snapshotBase.$n"
        }

    private val releaseVersion
        get() = System.getenv("RELEASE_VERSION")

    fun isRelease() = releaseVersion != null

    val publishVersion = releaseVersion ?: snapshotVersion
}
