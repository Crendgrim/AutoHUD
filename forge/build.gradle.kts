@file:Suppress("UnstableApiUsage")

plugins {
    id("dev.architectury.loom")
    id("architectury-plugin")
    id("com.github.johnrengelman.shadow")
    id("maven-publish")
    id("me.modmuss50.mod-publish-plugin")
}

val loader = prop("loom.platform")!!
val minecraft: String = stonecutter.current.version
val common: Project = requireNotNull(stonecutter.node.sibling("")) {
    "No common project for $project"
}

version = "${mod.version}+$minecraft"
group = "${mod.group}.$loader"
base {
    archivesName.set(mod.id)
}
architectury {
    platformSetupLoomIde()
    forge()
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
    get("developmentForge").extendsFrom(commonBundle)
}

repositories {
    maven("https://maven.minecraftforge.net")
}

dependencies {
    minecraft("com.mojang:minecraft:$minecraft")
    mappings("net.fabricmc:yarn:$minecraft+build.${common.mod.dep("yarn_build")}:v2")
    "forge"("net.minecraftforge:forge:$minecraft-${common.mod.dep("forge_loader")}")
    "io.github.llamalad7:mixinextras-common:${mod.dep("mixin_extras")}".let {
        annotationProcessor(it)
        implementation(it)
    }
    "io.github.llamalad7:mixinextras-forge:${mod.dep("mixin_extras")}".let {
        implementation(it)
        include(it)
    }

    mapOf(
        "coldsweat" to "maven.modrinth:cold-sweat:${common.mod.dep("coldsweat_artifact")}",
        "hotbarslotcycling" to "fuzs.hotbarslotcycling:hotbarslotcycling-forge:{}",
        "legendary_survival_overhaul" to "curse.maven:legendary-survival-overhaul-840254:{}",
        "quark" to "maven.modrinth:quark:{}",
        "raised" to "maven.modrinth:raised:Forge-${common.mod.dep("raised_artifact")}-{}",
    ).map { (modName, url) ->
        common.mod.dep("forge", modName) to url.replace("{}", common.mod.dep("forge", modName))
    }.filterNot { (version, _) ->
        version.startsWith("[")
    }.forEach { (_, url) ->
        modCompileOnly(url)
    }

    modImplementation(name="libbamboo", group="mod.crend", version="${common.mod.dep("libbamboo")}-forge")
    include(name="libbamboo", group="mod.crend", version="${common.mod.dep("libbamboo")}-forge")

    commonBundle(project(common.path, "namedElements")) { isTransitive = false }
    shadowBundle(project(common.path, "transformProductionForge")) { isTransitive = false }
}

loom {
    decompilers {
        get("vineflower").apply { // Adds names to lambdas - useful for mixins
            options.put("mark-corresponding-synthetics", "1")
        }
    }

    forge.convertAccessWideners = true
    forge.mixinConfigs(
        "autohud-common.mixins.json",
        "autohud-common-compat.mixins.json",
        "autohud-forge.mixins.json",
        "autohud-forge-compat.mixins.json",
    )

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
    archiveClassifier = loader
    dependsOn(tasks.shadowJar)
}

tasks.shadowJar {
    configurations = listOf(shadowBundle)
    archiveClassifier = "dev-shadow"
    exclude("fabric.mod.json", "architectury.common.json")
}

tasks.processResources {
    properties(listOf("META-INF/mods.toml", "pack.mcmeta"),
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

            artifact(tasks.remapJar.get().archiveFile)
            artifact(tasks.remapSourcesJar.get().archiveFile) {
                classifier = "sources"
            }
        }
    }
}

publishMods {
    displayName = "[Forge ${common.mod.prop("mc_title")}] ${mod.name} ${mod.version}"

    val modrinthToken = providers.gradleProperty("MODRINTH_TOKEN").orNull
    val curseforgeToken = providers.gradleProperty("CURSEFORGE_TOKEN").orNull
    dryRun = modrinthToken == null || curseforgeToken == null

    file = tasks.remapJar.get().archiveFile
    version = "${mod.version}+$minecraft-$loader"
    changelog = mod.prop("changelog")
    type = STABLE
    modLoaders.add(loader)

    val supportedVersions = common.mod.prop("mc_targets").split(" ")

    modrinth {
        projectId = property("publish.modrinth").toString()
        accessToken = modrinthToken
        minecraftVersions.addAll(supportedVersions)

        optional("yacl")
    }
    curseforge {
        projectId = property("publish.curseforge").toString()
        projectSlug = property("publish.curseforge_slug").toString()
        accessToken = curseforgeToken
        minecraftVersions.addAll(supportedVersions)
        clientRequired = true
        serverRequired = false

        optional("yacl")
    }
}
