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

// الأكواد التالية هي المسؤولة عن بناء الإضافة وإنشاء ملف plugins.json
tasks.register("make") {
    dependsOn(":ArabicProvider:assembleRelease")
    doLast {
        copy {
            from("ArabicProvider/build/outputs/apk/release")
            into("$buildDir/plugins")
        }
    }
}

tasks.register("makePluginsJson") {
    dependsOn("make")
    doLast {
        val pluginsDir = file("$buildDir/plugins")
        val plugins = pluginsDir.listFiles()?.filter { it.extension == "apk" }?.map {
            mapOf(
                "url" to it.name,
                "version" to 1,
                "description" to "Arabic Provider",
                "name" to "عربي",
                "internalName" to "ArabicProvider",
                "authors" to listOf("Marouane"),
                "repositoryUrl" to "https://github.com/marouane1990star/re-3arabi",
                "language" to "ar"
            )
        } ?: emptyList()

        val json = mapOf(
            "name" to "Re-3arabi CloudStream Repo",
            "description" to "مستودع إضافات عربية لتطبيق CloudStream",
            "manifestVersion" to 1,
            "plugins" to plugins
        )

        val jsonFile = file("$buildDir/plugins/plugins.json")
        jsonFile.writeText(com.fasterxml.jackson.databind.ObjectMapper().writeValueAsString(json))
    }
}
