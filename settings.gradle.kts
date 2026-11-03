pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
        // Modstitch
        maven("https://maven.isxander.dev/releases/")
        // Fabric Loom
        maven("https://maven.fabricmc.net/")
        // ModDevGradle / NeoForge
        maven("https://maven.neoforged.net/releases/")
        // Stonecutter
        maven("https://maven.kikugie.dev/releases")
        maven("https://maven.kikugie.dev/snapshots")
    }
}

plugins {
    id("dev.kikugie.stonecutter") version "0.6+"
}

stonecutter {
    kotlinController = true
    centralScript = "build.gradle.kts"

    create(rootProject) {
        // 先以 1.20.1 Forge 作为基准变体，后续在此追加 1.21.1 / Fabric 等目标。
        vers("1.19.2-forge", "1.19.2")
        vers("1.20.1-forge", "1.20.1")

        // src/ 以该变体的代码形态提交，其它变体由 Stonecutter 预处理生成
        vcsVersion = "1.20.1-forge"
    }
}

rootProject.name = "MirrorDimension"
