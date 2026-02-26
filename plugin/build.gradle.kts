import org.cthing.gradle.plugins.buildconstants.SourceAccess
import xyz.jpenilla.resourcefactory.paper.PaperPluginYaml.Load

plugins {
    java
    id("com.gradleup.shadow")
    id("org.cthing.build-constants")
    id("xyz.jpenilla.run-paper")
    id("xyz.jpenilla.resource-factory")
}

repositories {
    maven("https://repo.codemc.io/repository/maven-releases/")
    maven("https://repo.codemc.io/repository/maven-snapshots/")
    maven("https://repo.extendedclip.com/releases/")
    maven("https://repo.opencollab.dev/main/")
}

dependencies {
    implementation(project(":api"))

    compileOnly("com.fasterxml.jackson.core:jackson-databind:2.21.0")

    compileOnly(libs.mongo)
    compileOnly(libs.hazelcast)
    compileOnly(libs.configlib)
    compileOnly(libs.guice)

    compileOnly(libs.paperApi)
    compileOnly(libs.packetevents)
    compileOnly(libs.geyser)
    compileOnly(libs.floodgate)

    compileOnly(libs.lombok)
    annotationProcessor(libs.lombok)
    testCompileOnly(libs.lombok)
    testAnnotationProcessor(libs.lombok)
}

sourceSets.main {
    resourceFactory {
        paperPluginYaml {
            name = "minestack"
            description = project.description.toString()
            version = project.version.toString()

            main = "minestack.plugin.MinestackPlugin"
            apiVersion = "1.21"

            foliaSupported = true

            loader = "minestack.plugin.MinestackPluginLoader"
            bootstrapper = "minestack.plugin.MinestackPluginBootstrapper"

            dependencies {
                server("packetevents", load = Load.BEFORE)
                server("Geyser-Spigot", load = Load.BEFORE, required = false)
                server("floodgate", load = Load.BEFORE, required = false)
            }
        }
    }
}

tasks {
    runServer {
        minecraftVersion("1.21.11")

        downloadPlugins {
            modrinth("packetevents", libs.packetevents.get().version.orEmpty() + "+spigot")
            hangar("Geyser", "Geyser")
            hangar("Floodgate", "Floodgate")
        }
    }

    generateBuildConstants {
        classname = "minestack.plugin.Constants"
        sourceAccess = SourceAccess.PACKAGE

        val pluginLibs = listOf(
            libs.mongo,
            libs.hazelcast,
            libs.configlib,
            libs.guice
        )

        additionalConstants.put("PLUGIN_LIBRARIES", pluginLibs.joinToString(",") { optLib ->
            val lib = optLib.get()
            "${lib.module}:${lib.version}"
        })
    }

    shadowJar {
        dependsOn(generateBuildConstants)
    }
}
