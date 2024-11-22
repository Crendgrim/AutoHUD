@file:Suppress("UnstableApiUsage")

plugins {
    id("dev.architectury.loom")
    id("architectury-plugin")
    id("com.github.johnrengelman.shadow")
    id("maven-publish")
}

val loader = prop("loom.platform")!!
val minecraft: String = stonecutter.current.version
val common: Project = requireNotNull(stonecutter.node.sibling("")) {
    "No common project for $project"
}

version = "${mod.version}+$minecraft"
group = "${mod.group}.$loader"
base {
    archivesName.set("${mod.id}-$loader")
}
architectury {
    platformSetupLoomIde()
    neoForge()
}

val commonBundle: Configuration by configurations.creating {
    isCanBeConsumed = false
    isCanBeResolved = true
}

val shadowBundle: Configuration by configurations.creating {
    isCanBeConsumed = false
    isCanBeResolved = true
}

configurations {
    compileClasspath.get().extendsFrom(commonBundle)
    runtimeClasspath.get().extendsFrom(commonBundle)
    get("developmentNeoForge").extendsFrom(commonBundle)
}

repositories {
    maven("https://maven.neoforged.net/releases/")
}

dependencies {
    minecraft("com.mojang:minecraft:$minecraft")
    mappings(loom.layered {
        mappings("net.fabricmc:yarn:$minecraft+build.${common.mod.dep("yarn_build")}:v2")
        common.mod.dep("neoforge_patch").takeUnless { it.startsWith('[') }?.let {
            mappings("dev.architectury:yarn-mappings-patch-neoforge:$it")
        }
    })
    "neoForge"("net.neoforged:neoforge:${common.mod.dep("neoforge_loader")}")

    mapOf(
        "coldsweat" to "maven.modrinth:cold-sweat:${common.mod.dep("coldsweat_artifact")}",
        "hotbarslotcycling" to "fuzs.hotbarslotcycling:hotbarslotcycling-neoforge:{}",
        "raised" to "maven.modrinth:raised:NeoForge-${common.mod.dep("raised_artifact")}-{}",
    ).map { (modName, url) ->
        common.mod.dep("neoforge", modName) to url.replace("{}", common.mod.dep("neoforge", modName))
    }.filterNot { (version, _) ->
        version.startsWith("[")
    }.forEach { (_, url) ->
        modCompileOnly(url)
    }

    modImplementation(name="libbamboo", group="mod.crend", version="${common.mod.dep("libbamboo")}-neoforge")
    include(name="libbamboo", group="mod.crend", version="${common.mod.dep("libbamboo")}-neoforge")

    commonBundle(project(common.path, "namedElements")) { isTransitive = false }
    shadowBundle(project(common.path, "transformProductionNeoForge")) { isTransitive = false }
}

loom {
    decompilers {
        get("vineflower").apply { // Adds names to lambdas - useful for mixins
            options.put("mark-corresponding-synthetics", "1")
        }
    }

    runConfigs.all {
        isIdeConfigGenerated = true
        runDir = "../../../run"
        vmArgs("-Dmixin.debug.export=true")
    }
}

java {
    withSourcesJar()
    val java = if (stonecutter.eval(minecraft, ">=1.20.5"))
        JavaVersion.VERSION_21 else JavaVersion.VERSION_17
    targetCompatibility = java
    sourceCompatibility = java
}

tasks.jar {
    archiveClassifier = "dev"
}

tasks.remapJar {
    injectAccessWidener = true
    input = tasks.shadowJar.get().archiveFile
    archiveClassifier = null
    dependsOn(tasks.shadowJar)
}

tasks.shadowJar {
    configurations = listOf(shadowBundle)
    archiveClassifier = "dev-shadow"
    exclude(
        "fabric.mod.json",
        "architectury.common.json",
        if (stonecutter.eval(minecraft, "<=1.20.4")) "META-INF/neoforge.mods.toml" else "META-INF/mods.toml"
    )
}

tasks.processResources {
    properties(
        listOf(
            if (stonecutter.eval(minecraft, "<=1.20.4")) "META-INF/mods.toml" else "META-INF/neoforge.mods.toml",
            "pack.mcmeta"
        ),
        "id" to mod.id,
        "name" to mod.name,
        "version" to mod.version,
        "minecraft" to common.mod.prop("mc_dep_forgelike")
    )
}

tasks.build {
    group = "versioned"
    description = "Must run through 'chiseledBuild'"
}

tasks.register<Copy>("buildAndCollect") {
    group = "versioned"
    description = "Must run through 'chiseledBuild'"
    from(tasks.remapJar.get().archiveFile, tasks.remapSourcesJar.get().archiveFile)
    into(rootProject.layout.buildDirectory.file("libs/${mod.version}/$loader"))
    dependsOn("build")
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = mod.prop("group")
            artifactId = mod.prop("id")
            version = "${mod.version}+${minecraft}-${loader}"

            from(components["java"])
        }
    }
}
