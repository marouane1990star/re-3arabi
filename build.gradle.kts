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

// هذا الجزء هو الذي يصنع ملف plugins.json
tasks.register("makePluginsJson") {
    doLast {
        val plugins = listOf(
            mapOf(
                "url" to "ArabicProvider.apk",
                "version" to 1,
                "description" to "Arabic Provider",
                "name" to "عربي",
                "internalName" to "ArabicProvider",
                "authors" to listOf("Marouane"),
                "repositoryUrl" to "https://github.com/marouane1990star/re-3arabi",
                "language" to "ar"
            )
        )
        val json = mapOf(
            "name" to "Re-3arabi CloudStream Repo",
            "description" to "مستودع إضافات عربية",
            "manifestVersion" to 1,
            "plugins" to plugins
        )
        val mapper = com.fasterxml.jackson.databind.ObjectMapper()
        file("plugins.json").writeText(mapper.writeValueAsString(json))
    }
}
