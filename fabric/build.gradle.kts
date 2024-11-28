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
    fabric()
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
    get("developmentFabric").extendsFrom(commonBundle)
}

repositories {
}

dependencies {
    minecraft("com.mojang:minecraft:$minecraft")
    mappings("net.fabricmc:yarn:$minecraft+build.${common.mod.dep("yarn_build")}:v2")
    modImplementation("net.fabricmc:fabric-loader:${mod.dep("fabric_loader")}")

    listOf(
        "fabric-key-binding-api-v1",
        "fabric-lifecycle-events-v1",
        "fabric-rendering-v1"
    ).forEach {
        modImplementation(fabricApi.module(it, common.mod.dep("fabric_api")))
    }

    modImplementation(name="libbamboo", group="mod.crend", version="${common.mod.dep("libbamboo")}-fabric")
    include(name="libbamboo", group="mod.crend", version="${common.mod.dep("libbamboo")}-fabric")

    modCompileOnly("com.terraformersmc:modmenu:${common.mod.dep("modmenu")}")

    mapOf(
        "appleskin" to "squeek.appleskin:appleskin-fabric:${common.mod.dep("appleskin_artifact")}-{}",
        "armorchroma" to "maven.modrinth:armor-chroma-for-fabric:{}",
        "detailab" to "maven.modrinth:detail-armor-bar:{}",
        "dehydration" to "maven.modrinth:dehydration:{}",
        "environmentz" to "maven.modrinth:environmentz:{}",
        "farmers_delight_refabricated" to "maven.modrinth:farmers-delight-refabricated:{}",
        "hotbarslotcycling" to "fuzs.hotbarslotcycling:hotbarslotcycling-fabric:{}",
        "onebar" to "maven.modrinth:onebar:{}",
        "raised" to "maven.modrinth:raised:Fabric-${common.mod.dep("raised_artifact")}-{}",
        "statuseffectbars" to "maven.modrinth:status-effect-bars:{}"
    ).map { (modName, url) ->
        common.mod.dep(modName) to url.replace("{}", common.mod.dep("fabric", modName))
    }.filterNot { (version, _) ->
        version.startsWith("[")
    }.forEach { (_, url) ->
        modCompileOnly(url)
    }

    commonBundle(project(common.path, "namedElements")) { isTransitive = false }
    shadowBundle(project(common.path, "transformProductionFabric")) { isTransitive = false }
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

tasks.shadowJar {
    configurations = listOf(shadowBundle)
    archiveClassifier = "dev-shadow"
}

tasks.remapJar {
    injectAccessWidener = true
    input = tasks.shadowJar.get().archiveFile
    archiveClassifier = loader
    dependsOn(tasks.shadowJar)
}

tasks.jar {
    archiveClassifier = "dev"
}

tasks.processResources {
    properties(listOf("fabric.mod.json"),
        "id" to mod.id,
        "name" to mod.name,
        "version" to mod.version,
        "minecraft" to common.mod.prop("mc_dep_fabric")
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
    displayName = "[Fabric ${common.mod.prop("mc_title")}] ${mod.name} ${mod.version}"

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

        requires("fabric-api")
        optional("yacl")
        optional("modmenu")
    }
    curseforge {
        projectId = property("publish.curseforge").toString()
        projectSlug = property("publish.curseforge_slug").toString()
        accessToken = curseforgeToken
        minecraftVersions.addAll(supportedVersions)
        clientRequired = true
        serverRequired = false

        requires("fabric-api")
        optional("yacl")
        optional("modmenu")
    }
}
