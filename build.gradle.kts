plugins {
    id("dev.isxander.modstitch.base") version "0.5.12"
}

fun prop(name: String, consumer: (prop: String) -> Unit) {
    (findProperty(name) as? String?)?.let(consumer)
}

val minecraft = property("deps.minecraft") as String
val modIdConst = "mirrordim"

// 1.20.1 运行时是 Java 17；Modstitch 要求构建 JVM 至少为 21
val javaTargetVersion = when (minecraft) {
    "1.19.2", "1.20.1" -> 17
    else -> throw IllegalArgumentException("请先为 Minecraft $minecraft 登记 Java 版本")
}

val resourcePackFormat = when (minecraft) {
    "1.19.2" -> 9
    "1.20.1" -> 15
    else -> throw IllegalArgumentException("请先为 Minecraft $minecraft 登记资源包格式")
}

val dataPackFormat = when (minecraft) {
    "1.19.2" -> 9
    "1.20.1" -> 15
    else -> throw IllegalArgumentException("请先为 Minecraft $minecraft 登记数据包格式")
}

data class LoaderManifest(
    val modId: String,
    val versionRange: String,
    val languageVersion: String,
    val minecraftVersionRange: String,
)

val loaderManifest = when (minecraft) {
    "1.19.2" -> LoaderManifest(
        modId = "forge",
        versionRange = "[43,)",
        languageVersion = "[43,)",
        minecraftVersionRange = "[1.19.2,1.20)",
    )

    "1.20.1" -> LoaderManifest(
        modId = "forge",
        versionRange = "[47,)",
        languageVersion = "[47,)",
        minecraftVersionRange = "[1.20.1,1.21)",
    )

    else -> throw IllegalArgumentException("请先为 Minecraft $minecraft 登记加载器清单")
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
        }
    }

    // 1.20.1 Forge 走 ModDevGradle 的 legacyforge 平台
    moddevgradle {
        enable {
            prop("deps.forge") { forgeVersion = it }
        }
        defaultRuns()
        configureNeoforge {
            // Forge 47.2.20 自带的 AT 在 MDG 的严格校验下会报“record 需要同时放宽构造器”等警告；
            // 这里关闭校验，避免上游 AT 阻止玩家侧开发环境构建。
            validateAccessTransformers = false
            runs.all { disableIdeRun() }
        }
    }

    mixin {
        addMixinsToModManifest = true
        configs.register(modIdConst)
    }
}

// Stonecutter 常量，后续给加载器差异代码使用
val constraint: String = name.split("-")[1]
stonecutter {
    consts(
        "fabric" to constraint.equals("fabric"),
        "neoforge" to constraint.equals("neoforge"),
        "forge" to constraint.equals("forge"),
    )
}

dependencies {
    // Create 是可选兼容：编译期需要，运行期不强制安装
    when (minecraft) {
        "1.19.2" -> modCompileOnly("maven.modrinth:create:tJVykywJ")
        "1.20.1" -> modCompileOnly("curse.maven:create-328085:4835191")
    }

    annotationProcessor("org.spongepowered:mixin:0.8.5:processor")
}

// Modstitch 在 legacyforge 平台会无条件要求 refmap；
// 如果 Mixin AP 没有产出（例如当前源集没有可处理 Mixin），补一份空 refmap。
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
