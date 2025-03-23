plugins {
    id("dev.kikugie.stonecutter")
    id("dev.architectury.loom") version "1.9-SNAPSHOT" apply false
    id("architectury-plugin") version "3.4-SNAPSHOT" apply false
    id("com.github.johnrengelman.shadow") version "8.1.1" apply false
    id("me.modmuss50.mod-publish-plugin") version "0.8.1" apply false
}
stonecutter active "1.20.1" /* [SC] DO NOT EDIT */
stonecutter.automaticPlatformConstants = true

// Builds every version into `build/libs/{mod.version}/{loader}`
stonecutter registerChiseled tasks.register("chiseledBuild", stonecutter.chiseled) {
    group = "project"
    ofTask("buildAndCollect")
}

// Builds loader-specific versions into `build/libs/{mod.version}/{loader}`
for (it in stonecutter.tree.branches) {
    if (it.id.isEmpty()) continue
    val loader = it.id.upperCaseFirst()
    stonecutter registerChiseled tasks.register("chiseledBuild$loader", stonecutter.chiseled) {
        group = "project"
        versions { branch, _ -> branch == it.id }
        ofTask("buildAndCollect")
    }
}

stonecutter registerChiseled tasks.register("chiseledPublishToMavenLocal", stonecutter.chiseled) {
    group = "project"
    ofTask("publishToMavenLocal")
}

stonecutter registerChiseled tasks.register("chiseledPublish", stonecutter.chiseled) {
    group = "project"
    ofTask("publishMods")
}

// Runs active versions for each loader
for (it in stonecutter.tree.nodes) {
    if (it.metadata != stonecutter.current || it.branch.id.isEmpty()) continue
    val types = listOf("Client", "Server")
    val loader = it.branch.id.upperCaseFirst()
    for (type in types) it.tasks.register("runActive$type$loader") {
        group = "project"
        dependsOn("run$type")
    }
}

allprojects {
    repositories {
        mavenLocal()
        maven("https://maven.isxander.dev/releases")
        maven("https://maven.terraformersmc.com/")
        maven {
            name = "Fuzs Mod Resources"
            setUrl("https://raw.githubusercontent.com/Fuzss/modresources/main/maven/")
        }

        // AppleSkin
        maven("https://maven.ryanliptak.com/")

        exclusiveContent {
            forRepository {
                maven {
                    name = "Modrinth"
                    setUrl("https://api.modrinth.com/maven")
                }
            }
            filter {
                includeGroup("maven.modrinth")
            }
        }

        exclusiveContent {
            forRepository {
                maven {
                    setUrl("https://cursemaven.com")
                }
            }
            filter {
                includeGroup("curse.maven")
            }
        }

    }
}

stonecutter parameters {
    listOf(
        "appleskin",
        "armorchroma",
        "coldsweat",
        "dehydration",
        "detailab",
        "environmentz",
        "farmers_delight_refabricated",
        "hotbarslotcycling",
        "legendary_survival_overhaul",
        "onebar",
        "quark",
        "raised",
        "statuseffectbars"
    ).map { modName ->
        modName to
                if (node == null)
                    // :neoforge:1.20.1 is not defined, so we would not be able to switch back to 1.20.1 without
                    // defining any constants used in the neoforge source set. Just set them all to unsupported.
                    "[UNSUPPORTED]"
                else if (node!!.sibling("") == null || node!!.prop("loom.platform") == null)
                    node!!.mod.dep(modName)
                else
                    // For e.g. :fabric:1.20.1, use the property of :1.20.1
                    node!!.sibling("")!!.mod.dep(node!!.prop("loom.platform")!!, modName)
    }.forEach { (mod, version) ->
        val modIsPresent = !version.startsWith("[");
        const(mod, modIsPresent)
        dependency(mod, if (modIsPresent) version else "0")
    }
}
