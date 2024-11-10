plugins {
    id("dev.architectury.loom")
    id("architectury-plugin")
}

val minecraft = stonecutter.current.version

version = "${mod.version}+$minecraft"
group = "${mod.group}.common"
base {
    archivesName.set("${mod.id}-common")
}

architectury.common(stonecutter.tree.branches.mapNotNull {
    if (stonecutter.current.project !in it) null
    else it.prop("loom.platform")
})

repositories {
}

dependencies {
    minecraft("com.mojang:minecraft:$minecraft")
    mappings("net.fabricmc:yarn:$minecraft+build.${mod.dep("yarn_build")}:v2")
    modImplementation("net.fabricmc:fabric-loader:${mod.dep("fabric_loader")}")

    modImplementation(name="libbamboo", group="mod.crend.libbamboo", version="fabric-${mod.dep("libbamboo")}")
    modImplementation("dev.isxander:yet-another-config-lib:${mod.dep("yacl")}-fabric")

    mapOf(
        "appleskin" to "squeek.appleskin:appleskin-fabric:${mod.dep("appleskin_artifact")}-{}",
        "armorchroma" to "maven.modrinth:armor-chroma-for-fabric:{}",
        "detailab" to "maven.modrinth:detail-armor-bar:{}",
        "dehydration" to "maven.modrinth:dehydration:{}",
        "environmentz" to "maven.modrinth:environmentz:{}",
        "farmers_delight_refabricated" to "maven.modrinth:farmers-delight-refabricated:{}",
        "hotbarslotcycling" to "fuzs.hotbarslotcycling:hotbarslotcycling-fabric:{}",
        "onebar" to "maven.modrinth:onebar:{}",
        "raised" to "maven.modrinth:raised:Fabric-${mod.dep("raised_artifact")}-{}",
        "statuseffectbars" to "maven.modrinth:status-effect-bars:{}"
    ).map { (modName, url) ->
        mod.dep(modName) to url.replace("{}", mod.dep(modName))
    }.filterNot { (version, _) ->
        version.startsWith("[")
    }.forEach { (_, url) ->
        modCompileOnly(url)
    }
}

loom {
    accessWidenerPath = rootProject.file("src/main/resources/autohud.accesswidener")

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

tasks.build {
    group = "versioned"
    description = "Must run through 'chiseledBuild'"
}