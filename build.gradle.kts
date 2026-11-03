plugins {
    id("dev.isxander.modstitch.base") version "0.5.12"
}

fun prop(name: String, consumer: (prop: String) -> Unit) {
    (findProperty(name) as? String?)?.let(consumer)
}

val minecraft = property("deps.minecraft") as String
val constraint: String = name.split("-")[1]
val modIdConst = "mirrordim"

val fabricLoaderVersion = findProperty("deps.fabricLoader") as? String ?: ""
val fabricApiVersion = findProperty("deps.fabricApi") as? String ?: ""

// 构建 JVM 由 Modstitch 要求为 21+；产物字节码按 Minecraft 版本选择
val javaTargetVersion = when (minecraft) {
    "1.19.2", "1.20.1" -> 17
    "1.21.1" -> 21
    else -> throw IllegalArgumentException("请先为 Minecraft $minecraft 登记 Java 版本")
}

val resourcePackFormat = when (minecraft) {
    "1.19.2" -> 9
    "1.20.1" -> 15
    "1.21.1" -> 34
    else -> throw IllegalArgumentException("请先为 Minecraft $minecraft 登记资源包格式")
}

val dataPackFormat = when (minecraft) {
    "1.19.2" -> 9
    "1.20.1" -> 15
    "1.21.1" -> 34
    else -> throw IllegalArgumentException("请先为 Minecraft $minecraft 登记数据包格式")
}

data class LoaderManifest(
    val modId: String,
    val versionRange: String,
    val languageVersion: String,
    val minecraftVersionRange: String,
)

val loaderManifest = when {
    constraint == "fabric" -> LoaderManifest(
        modId = "fabricloader",
        versionRange = ">=$fabricLoaderVersion",
        languageVersion = "",
        minecraftVersionRange = "~$minecraft",
    )

    constraint == "neoforge" && minecraft == "1.21.1" -> LoaderManifest(
        modId = "neoforge",
        versionRange = "[21.1,)",
        languageVersion = "[4,)",
        minecraftVersionRange = "[1.21,1.21.2)",
    )

    constraint == "forge" && minecraft == "1.19.2" -> LoaderManifest(
        modId = "forge",
        versionRange = "[43,)",
        languageVersion = "[43,)",
        minecraftVersionRange = "[1.19.2,1.20)",
    )

    constraint == "forge" && minecraft == "1.20.1" -> LoaderManifest(
        modId = "forge",
        versionRange = "[47,)",
        languageVersion = "[47,)",
        minecraftVersionRange = "[1.20.1,1.21)",
    )

    else -> throw IllegalArgumentException("请先为 $minecraft + $constraint 登记加载器清单")
}

modstitch {
    minecraftVersion = minecraft
    javaTarget = javaTargetVersion

    parchment {
        prop("deps.parchment") { mappingsVersion = it }
    }

    metadata {
        modId = modIdConst
        modName = "MirrorDimension"
        modVersion = property("mod_version") as String
        modGroup = "top.prefersmin.mirrordim"
        modAuthor = "PrefersMin"

        // Modstitch 未内建的占位符需要自行登记
        fun <K : Any, V : Any> MapProperty<K, V>.populate(block: MapProperty<K, V>.() -> Unit) {
            block()
        }

        replacementProperties.populate {
            put("mod_license", property("mod_license") as String)
            put("mod_description", property("mod_description") as String)
            put("pack_format", resourcePackFormat.toString())
            put("loader_mod_id", loaderManifest.modId)
            put("loader_version_range", loaderManifest.versionRange)
            put("loader_language_version", loaderManifest.languageVersion)
            put("minecraft_version_range", loaderManifest.minecraftVersionRange)
            put("fabric_api_version_range", ">=$fabricApiVersion")
        }
    }

    if (constraint == "fabric") {
        // Fabric：Loom + Fabric API；源码使用 Mojang 官方映射 + Parchment
        loom {
            prop("deps.fabricLoader") { fabricLoaderVersion = it }
        }
    } else {
        // Forge / NeoForge：ModDevGradle
        moddevgradle {
            enable {
                prop("deps.forge") { forgeVersion = it }
                prop("deps.neoforge") { neoForgeVersion = it }
            }
            defaultRuns()
            configureNeoforge {
                // 上游 AT 的严格校验会阻止开发环境构建；这里关闭校验
                validateAccessTransformers = false
                runs.all { disableIdeRun() }
            }
        }
    }

    mixin {
        addMixinsToModManifest = true
        configs.register(modIdConst)
    }
}

// 只保留当前变体对应的加载器清单
val otherLoaderManifests =
    listOf("META-INF/mods.toml", "META-INF/neoforge.mods.toml", "fabric.mod.json") - modstitch.modLoaderManifest
tasks.withType<ProcessResources>().configureEach {
    exclude(otherLoaderManifests)
}

// Stonecutter 常量，供注释守卫使用
stonecutter {
    consts(
        "fabric" to constraint.equals("fabric"),
        "neoforge" to constraint.equals("neoforge"),
        "forge" to constraint.equals("forge"),
    )
}

dependencies {
    // Create 是可选兼容：编译期需要，运行期不强制安装
    if (constraint == "forge") {
        when (minecraft) {
            "1.19.2" -> add("modCompileOnly", "maven.modrinth:create:tJVykywJ")
            "1.20.1" -> add("modCompileOnly", "curse.maven:create-328085:4835191")
        }
    }
    if (constraint == "fabric") {
        add("modImplementation", "net.fabricmc.fabric-api:fabric-api:$fabricApiVersion")
    }

    compileOnly("com.google.code.findbugs:jsr305:3.0.2")
    annotationProcessor("org.spongepowered:mixin:0.8.5:processor")
}

// 按平台裁剪只属于某个加载器的实现类
if (constraint != "forge") {
    sourceSets.main {
        java.exclude("top/prefersmin/mirrordim/compat/**")
        java.exclude("top/prefersmin/mirrordim/world/ChordPortalForcer.java")
        java.exclude("top/prefersmin/mirrordim/world/PortalTracksSupport.java")
    }
}
if (constraint == "fabric") {
    sourceSets.main {
        // Fabric 侧暂不实现 Forge 式的 PortalSpawnEvent / SleepFinishedTimeEvent
        java.exclude("top/prefersmin/mirrordim/event/PMEvents.java")
    }
}
if (constraint == "fabric" && minecraft != "1.21.1") {
    sourceSets.main {
        java.exclude("**/NeoForgeChordPortalForcer.java")
    }
}
if (constraint != "fabric" || minecraft == "1.21.1") {
    sourceSets.main {
        java.exclude("**/FabricChordPortalForcer.java")
    }
}

// Modstitch 在 legacyforge 平台会无条件要求 refmap；如果 AP 没有产出，补空文件
val refmapModId = modIdConst
val provideEmptyRefmap = tasks.register("provideEmptyRefmap") {
    val mixinDir = layout.buildDirectory.dir("mixin")
    outputs.dir(mixinDir)
    doLast {
        val dir = mixinDir.get().asFile
        dir.mkdirs()
        val refmap = dir.resolve("$refmapModId.refmap.json")
        if (!refmap.exists()) refmap.writeText("{\n  \"mappings\": {}\n}\n")
        val mappings = dir.resolve("$refmapModId.refmap.json.mappings.tsrg")
        if (!mappings.exists()) mappings.writeText("")
    }
}

listOf("jar", "jarJar", "reobfJar", "reobfJarJar").forEach { taskName ->
    tasks.matching { it.name == taskName }.configureEach {
        dependsOn(provideEmptyRefmap)
    }
}

// 某些 Modstitch/Stonecutter 版本下 sourceSets 的 exclude 不会传递到 chiseledSrc 的编译任务，
// 这里再对 compileJava 做一次显式裁剪。
tasks.withType<org.gradle.api.tasks.compile.JavaCompile>().configureEach {
    if (constraint != "forge") {
        exclude("top/prefersmin/mirrordim/compat/**")
        exclude("top/prefersmin/mirrordim/world/ChordPortalForcer.java")
        exclude("top/prefersmin/mirrordim/world/PortalTracksSupport.java")
    }
    if (constraint == "fabric") {
        exclude("top/prefersmin/mirrordim/event/PMEvents.java")
    }
    if (constraint == "fabric" && minecraft != "1.21.1") {
        exclude("**/NeoForgeChordPortalForcer.java")
    }
    if (constraint != "fabric" || minecraft == "1.21.1") {
        exclude("**/FabricChordPortalForcer.java")
    }
}

tasks.named<ProcessResources>("processResources") {
    filesMatching("mirrordim.mixins.json") {
        expand(mapOf("java_compatibility" to "JAVA_$javaTargetVersion"))
    }
}
