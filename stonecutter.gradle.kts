plugins {
    id("dev.kikugie.stonecutter")
    id("dev.architectury.loom") version "1.10-SNAPSHOT" apply false
    id("com.github.johnrengelman.shadow") version "8.1.1" apply false
    id("me.modmuss50.mod-publish-plugin") version "0.8.1" apply false
}

stonecutter active file("active.stonecutter")

stonecutter parameters {
    constants {
        match(node.metadata.project.substringAfterLast("-"), "fabric", "neoforge", "forge")
    }
}

stonecutter tasks {
    /*order("publishModrinth")
    order("publishCurseforge")
     */
}

/*
plugins {
    id("dev.kikugie.stonecutter")
    id("dev.architectury.loom") version "1.10-SNAPSHOT" apply false
    id("architectury-plugin") version "3.4-SNAPSHOT" apply false
    id("com.github.johnrengelman.shadow") version "8.1.1" apply false
    id("me.modmuss50.mod-publish-plugin") version "0.8.1" apply false
}
stonecutter active file("active.stonecutter")

// Runs active versions for each loader
for (it in stonecutter.tree.nodes) {
    if (!it.metadata.isActive ||  it.branch.id.isEmpty()) continue
    val types = listOf("Client", "Server")
    val loader = it.branch.id.upperCaseFirst()
    for (type in types) tasks.register("runActive$type$loader") {
        group = "project"
        dependsOn("${it.hierarchy}:run$type")
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
    constants {
        match(branch.id, "fabric", "neoforge", "forge")
    }
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
                if (node.sibling("") == null || node.project.prop("loom.platform") == null)
                    node.project.mod.dep(modName)
                else
                    // For e.g. :fabric:1.20.1, use the property of :1.20.1
                    node.sibling("")!!.project.mod.dep(node.project.prop("loom.platform")!!, modName)
    }.forEach { (mod, version) ->
        val modIsPresent = !version.startsWith("[");
        constants[mod] = modIsPresent
        dependencies[mod] = parse(if (modIsPresent) version.split("+")[0] else "0")
    }
}
*/
