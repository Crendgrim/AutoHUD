@file:Suppress("UnstableApiUsage")

plugins {
    kotlin("jvm") version "2.1.21"
    id("com.google.devtools.ksp") version "2.1.21-2.0.2"
    id("dev.kikugie.fletching-table.fabric") version "0.1.0-alpha.7"
    id("dev.kikugie.fletching-table.neoforge") version "0.1.0-alpha.7"
    id("dev.kikugie.stonecutter")
    id("dev.architectury.loom")
    id("com.github.johnrengelman.shadow")
    id("me.modmuss50.mod-publish-plugin")
}

val minecraft = stonecutter.current.version
val loader = Loader.of(stonecutter.current.project)
class ModDependencies {
    operator fun get(name: String) = property("deps.$name").toString()
}
val deps = ModDependencies()
enum class DependencyLevel {
    Include,
    Implementation,
    CompileOnly
}
fun modDependency(modId: String, url: String, level: DependencyLevel) {
    val isPresent = !deps[modId].startsWith("[")
    stonecutter {
        constants {
            put(modId, isPresent)
        }
        dependencies {
            put(modId, if (isPresent) deps[modId] else "0")
        }
    }

    if (isPresent) {
        val artifact = findProperty("deps.${modId}_artifact")?.toString() ?: deps[modId]
        val resolvedUrl = url.replace("{}", artifact)
        dependencies {
            when (level) {
                DependencyLevel.Include -> {
                    modImplementation(resolvedUrl)
                    include(resolvedUrl)
                }

                DependencyLevel.Implementation -> modImplementation(resolvedUrl)
                DependencyLevel.CompileOnly -> modCompileOnly(resolvedUrl)
            }
        }
    }
}

version = "${mod.version}+$minecraft"
group = "${mod.group}.$loader"
base {
    archivesName.set(mod.id)
}

val shadowBundle: Configuration by configurations.creating {
    isCanBeConsumed = false
    isCanBeResolved = true
}

repositories {
    mavenLocal()
    maven("https://repo.spongepowered.org/maven")

    strictMaven("https://api.modrinth.com/maven", "Modrinth", "maven.modrinth")
    strictMaven("https://www.cursemaven.com", "CurseForge", "curse.maven")

    when (loader) {
        Loader.Forge -> {
            maven("https://maven.minecraftforge.net")
            maven("https://thedarkcolour.github.io/KotlinForForge/")
        }

        Loader.NeoForge -> {
            maven("https://maven.neoforged.net/releases/")
            maven("https://thedarkcolour.github.io/KotlinForForge/")
        }

        Loader.Fabric -> {
        }
    }
    // load this after KotlinForForge
    maven("https://maven.isxander.dev/releases")

    maven("https://maven.shedaniel.me/")
    maven("https://maven.terraformersmc.com/")

    maven {
        name = "Fuzs Mod Resources"
        setUrl("https://raw.githubusercontent.com/Fuzss/modresources/main/maven/")
    }

    // AppleSkin
    maven("https://maven.ryanliptak.com/")
}

dependencies {
    minecraft("com.mojang:minecraft:$minecraft")
    mappings(loom.layered {
        mappings("net.fabricmc:yarn:$minecraft+build.${mod.dep("yarn_build")}:v2")
        mod.dep("neoforge_patch").takeUnless { it.startsWith('[') }?.let {
            mappings("dev.architectury:yarn-mappings-patch-neoforge:$it")
        }
    })

    when (loader) {
        Loader.Fabric -> {
            modImplementation("net.fabricmc:fabric-loader:${mod.dep("fabric_loader")}")
        }
        Loader.Forge -> {
            "forge"("net.minecraftforge:forge:$minecraft-${mod.dep("forge_loader")}")
            "io.github.llamalad7:mixinextras-common:${mod.dep("mixin_extras")}".let {
                annotationProcessor(it)
                compileOnly(it)
            }
            "io.github.llamalad7:mixinextras-forge:${mod.dep("mixin_extras")}".let {
                implementation(it)
                include(it)
            }
            if (stonecutter.eval(minecraft, ">=1.21.6")) {
                annotationProcessor("net.minecraftforge:eventbus-validator:7.0-beta.7")
            }
        }
        Loader.NeoForge -> {
            "neoForge"("net.neoforged:neoforge:${mod.dep("neoforge_loader")}")
        }
    }

    modDependency("fabric_api", "net.fabricmc.fabric-api:fabric-api:{}", DependencyLevel.Implementation)
    modDependency("modmenu", "com.terraformersmc:modmenu:{}", DependencyLevel.Implementation)
    modDependency("libbamboo", "mod.crend:libbamboo:{}", DependencyLevel.Include)
    modDependency("yacl", "dev.isxander:yet-another-config-lib:{}", DependencyLevel.CompileOnly)

    modDependency("appleskin", "squeek.appleskin:appleskin-${loader}:{}", DependencyLevel.CompileOnly)
    modDependency("armorchroma", "maven.modrinth:armor-chroma-for-fabric:{}", DependencyLevel.CompileOnly)
    modDependency("coldsweat", "maven.modrinth:cold-sweat:{}", DependencyLevel.CompileOnly)
    modDependency("detailab", "maven.modrinth:detail-armor-bar:{}", DependencyLevel.CompileOnly)
    modDependency("dehydration", "maven.modrinth:dehydration:{}", DependencyLevel.CompileOnly)
    modDependency("drg_flares", "curse.maven:drg-flares-{}", DependencyLevel.CompileOnly)
    modDependency("environmentz", "maven.modrinth:environmentz:{}", DependencyLevel.CompileOnly)
    modDependency("farmers_delight_refabricated", "maven.modrinth:farmers-delight-refabricated:{}", DependencyLevel.CompileOnly)
    modDependency("feathers", "maven.modrinth:feathers:{}", DependencyLevel.CompileOnly)
    modDependency("hotbarslotcycling", "fuzs.hotbarslotcycling:hotbarslotcycling-${loader}:{}", DependencyLevel.CompileOnly)
    modDependency("inventorio", "maven.modrinth:inventorio:{}", DependencyLevel.CompileOnly)
    modDependency("legendary_survival_overhaul", "curse.maven:legendary-survival-overhaul-840254:{}", DependencyLevel.CompileOnly)
    modDependency("onebar", "maven.modrinth:onebar:{}", DependencyLevel.CompileOnly)
    modDependency("overflowing_bars", "maven.modrinth:overflowing-bars:{}", DependencyLevel.CompileOnly)
    modDependency("quark", "maven.modrinth:quark:{}", DependencyLevel.CompileOnly)
    modDependency("raised", "maven.modrinth:raised:{}", DependencyLevel.CompileOnly)
    modDependency("stamina", "maven.modrinth:insane-stamina:{}", DependencyLevel.CompileOnly)
    modDependency("simplyskills", "maven.modrinth:simply-skills:{}", DependencyLevel.CompileOnly)
    modDependency("statuseffectbars", "maven.modrinth:status-effect-bars:{}", DependencyLevel.CompileOnly)
    modDependency("thirstwastaken", "maven.modrinth:thirst-was-taken:{}", DependencyLevel.CompileOnly)
}

loom {
    accessWidenerPath = rootProject.file("src/main/resources/autohud.accesswidener")

    if (loader.isForge()) {
        forge.convertAccessWideners = true
        forge.mixinConfigs(
            "autohud.mixins.json",
            "autohud-compat.mixins.json"
        )
    }

    decompilers {
        get("vineflower").apply { // Adds names to lambdas - useful for mixins
            options.put("mark-corresponding-synthetics", "1")
        }
    }
}

java {
    withSourcesJar()
    val java = if (stonecutter.eval(minecraft, ">=1.20.5"))
        JavaVersion.VERSION_21 else JavaVersion.VERSION_17
    targetCompatibility = java
    sourceCompatibility = java
}

tasks.remapJar {
    injectAccessWidener = true
    inputFile = tasks.shadowJar.get().archiveFile
    archiveClassifier = loader.toString()
    dependsOn(tasks.shadowJar)
}

tasks.shadowJar {
    configurations = listOf(shadowBundle)
    archiveClassifier = "dev-shadow"
    when (loader) {
        Loader.Fabric -> exclude("META-INF", "pack.mcmeta", "architectury.common.json")
        Loader.Forge -> exclude("fabric.mod.json", "META-INF/neoforge.mods.toml", "architectury.common.json")
        Loader.NeoForge -> exclude( "fabric.mod.json", "META-INF/mods.toml", "architectury.common.json")
    }
}

tasks.processResources {
    properties(
        listOf("fabric.mod.json", "META-INF/mods.toml", "META-INF/neoforge.mods.toml", "pack.mcmeta"),
        "id" to mod.id,
        "name" to mod.name,
        "version" to mod.version,
        "minecraft" to mod.prop("mc_dep")
    )
}

fletchingTable {
    mixins.create("main") {
        default = "${mod.id}.mixins.json"
        configs.put("compat", "${mod.id}-compat.mixins.json")
    }
    fabric {
        applyMixinConfig = false
        entrypointMappings.put("modmenu", "com.terraformersmc.modmenu.api.ModMenuApi")
        entrypointMappings.put("autohud", "mod.crend.autohud.api.AutoHudApi")
    }
    neoforge {
        applyMixinConfig = false
    }
}

tasks.register<Copy>("buildAndCollect") {
    group = "build"
    description = "Builds all versions and copies them to build/libs/<version>"
    from(tasks.remapJar.get().archiveFile, tasks.remapSourcesJar.get().archiveFile)
    into(rootProject.layout.buildDirectory.file("libs/${mod.version}/$loader"))
    dependsOn("build")
}

if (mod.publish("current")) {
    publishMods {
        displayName = "[${loader.name} ${mod.prop("mc_title")}] ${mod.name} ${mod.version}"

        val modrinthToken = providers.gradleProperty("MODRINTH_TOKEN").orNull
        val curseforgeToken = providers.gradleProperty("CURSEFORGE_TOKEN").orNull
        dryRun = mod.publish("dryrun") || modrinthToken == null || curseforgeToken == null

        file = tasks.remapJar.get().archiveFile
        version = "${mod.version}+$minecraft-$loader"
        changelog = mod.prop("changelog")
        type = STABLE
        modLoaders.add(loader.toString())

        val supportedVersions = mod.prop("mc_targets").split(" ")

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
}
