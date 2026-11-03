plugins {
    id("dev.kikugie.stonecutter")
}

stonecutter active "1.20.1-forge"

stonecutter registerChiseled tasks.register("chiseledBuild", stonecutter.chiseled) {
    group = "project"
    ofTask("build")
}

allprojects {
    repositories {
        mavenCentral()
        maven("https://maven.neoforged.net/releases")
        maven("https://maven.minecraftforge.net")
        maven("https://maven.fabricmc.net/")
        maven("https://maven.parchmentmc.org")
        maven("https://cursemaven.com")
        maven("https://maven.createmod.net")
        maven("https://maven.ithundxr.dev/snapshots")
        maven("https://api.modrinth.com/maven")
    }
}
