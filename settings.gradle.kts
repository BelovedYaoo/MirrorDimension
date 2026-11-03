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
        // 当前覆盖 Create 支持的 Minecraft 版本：1.19.2 / 1.20.1 / 1.21.1。
        vers("1.19.2-forge", "1.19.2")
        vers("1.20.1-forge", "1.20.1")
        vers("1.19.2-fabric", "1.19.2")
        vers("1.20.1-fabric", "1.20.1")
        vers("1.21.1-fabric", "1.21.1")
        vers("1.21.1-neoforge", "1.21.1")

        // src/ 以该变体的代码形态提交，其它变体由 Stonecutter 预处理生成
        vcsVersion = "1.20.1-forge"
    }
}

rootProject.name = "MirrorDimension"
