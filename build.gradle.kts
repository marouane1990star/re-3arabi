buildscript {
    repositories {
        google()
        mavenCentral()
        maven("https://jitpack.io")
    }
    dependencies {
        classpath("com.android.tools.build:gradle:8.1.4")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.9.10")
    }
}

allprojects {
    repositories {
        google()
        mavenCentral()
        maven("https://jitpack.io")
    }
}

tasks.register("make") {
    dependsOn(":ArabicProvider:assembleRelease")
}

tasks.register("makePluginsJson") {
    dependsOn("make")
    doLast {
        val json = """
        {
            "name": "Re-3arabi CloudStream Repo",
            "description": "مستودع إضافات عربية",
            "manifestVersion": 1,
            "plugins": [
                {
                    "url": "ArabicProvider-release.apk",
                    "version": 1,
                    "description": "Arabic Provider",
                    "name": "عربي",
                    "internalName": "ArabicProvider",
                    "authors": ["Marouane"],
                    "repositoryUrl": "https://github.com/marouane1990star/re-3arabi",
                    "language": "ar"
                }
            ]
        }
        """.trimIndent()
        file("plugins.json").writeText(json)
    }
}
