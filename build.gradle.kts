plugins {
    idea
    `java-library`
    `maven-publish`
    alias(libs.plugins.moddevgradle)
    alias(libs.plugins.fletching.table)
    alias(libs.plugins.mod.publish)
}

version = "${property("mod_version")}+${libs.versions.minecraft.get()}"
group = property("maven_group")!!

val javaVersion = 21

neoForge.version = libs.versions.neoforge.get()

configurations {
    val localRuntime = create("localRuntime")
    runtimeClasspath.configure {
        extendsFrom(localRuntime)
    }
}

repositories {
    maven {
        url = uri("https://maven.enjarai.dev/releases")
    }
    maven {
        url = uri("https://maven.enjarai.dev/mirrors")
    }

    maven {
        url = uri("https://maven.su5ed.dev/releases")
    }

    // YACL
    maven {
        url = uri("https://maven.isxander.dev/releases")
    }

    // Permissions API, which is a dep of DABR
    maven {
        url = uri("https://oss.sonatype.org/content/repositories/snapshots")
    }
}

dependencies {
    implementation(libs.bundles.barrel.roll)
    implementation(libs.yacl)
}

neoForge {
    parchment {
        minecraftVersion = libs.versions.parchment.minecraft
        mappingsVersion = libs.versions.parchment.mappings
    }

    runs {
        with(maybeCreate("client")) {
            client()

            // Comma-separated list of namespaces to load gametests from. Empty = all namespaces.
            systemProperty("neoforge.enabledGameTestNamespaces", property("mod_id").toString())
        }

        with(maybeCreate("server")) {
            server()
            programArgument("--nogui")
            systemProperty("neoforge.enabledGameTestNamespaces", property("mod_id").toString())
        }

        // This run config launches GameTestServer and runs all registered gametests, then exits.
        // By default, the server will crash when no gametests are provided.
        // The gametest system is also enabled by default for other run configs under the /test command.
        with(maybeCreate("gameTestServer")) {
            type = "gameTestServer"
            systemProperty("neoforge.enabledGameTestNamespaces", property("mod_id").toString())
        }

        with(maybeCreate("data")) {
            data()

            // example of overriding the workingDirectory set in configureEach above, uncomment if you want to use it
            // gameDirectory = project.file('run-data')

            // Specify the modid for data generation, where to output the resulting resource, and where to look for existing resources.
            programArguments.addAll("--mod", property("mod_id").toString(), "--all", "--output", file("src/generated/resources").absolutePath, "--existing", file("src/main/resources").absolutePath)
        }

        // applies to all the run configs above
        configureEach {
            // Recommended logging data for a userdev environment
            // The markers can be added/remove as needed separated by commas.
            // "SCAN": For mods scan.
            // "REGISTRIES": For firing of registry events.
            // "REGISTRYDUMP": For getting the contents of all registries.
            systemProperty("forge.logging.markers", "REGISTRIES")

            // Recommended logging level for the console
            // You can set various levels here.
            // Please read: https://stackoverflow.com/questions/2031163/when-to-use-the-different-log-levels
            logLevel = org.slf4j.event.Level.DEBUG
        }
    }

    mods {
        // define mod <-> source bindings
        // these are used to tell the game which sources are for which mod
        // multi mod projects should define one per mod
        create(property("mod_id").toString()) {
            sourceSet(sourceSets.main.get())
        }
    }
}

sourceSets.main.configure {
    resources.srcDir("src/generated/resources")
}

val generateModMetadata = tasks.register<ProcessResources>("generateModMetadata") {
    val replaceProperties = mapOf(
        "minecraft_version" to libs.versions.minecraft.get(),
        "minecraft_version_range" to "[${libs.versions.minecraft.get()}]",
        "neo_version" to libs.versions.neoforge.get(),
        "mod_license" to "MIT",
        "mod_id" to project.property("mod_id"),
        "mod_version" to project.version
    )

    inputs.properties(replaceProperties)
    expand(replaceProperties)

    val inputFiles = layout.projectDirectory.dir("src/main/templates")
    inputs.dir(inputFiles)
    from(inputFiles)

    val outputDir = layout.buildDirectory.dir("generated/sources/modMetadata")
    into(outputDir)
    outputs.dir(outputDir)
}
// Include the output of "generateModMetadata" as an input directory for the build
// this works with both building through Gradle and the IDE.
sourceSets.main.configure {
    resources.srcDir(generateModMetadata)
}
// To avoid having to run "generateModMetadata" manually, make it run on every project reload
neoForge.ideSyncTask(generateModMetadata)

tasks.withType<JavaCompile> {
    options.release.set(javaVersion)
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(javaVersion))
        vendor = JvmVendorSpec.MICROSOFT
    }
    withSourcesJar()
}

tasks.named<Jar>("jar") {
    from("LICENSE") {
        rename("LICENSE", "LICENSE_${archiveBaseName.get()}")
    }
}

//fletchingTable {
//    lang.create("main") {
//        patterns.add("assets/modid/lang/**")
//    }
//}


// configure the maven publication
publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"])
        }
    }

    // See https://docs.gradle.org/current/userguide/publishing_maven.html for information on how to set up publishing.
    repositories {
        // Add repositories to publish to here.
        // Notice: This block does NOT have the same function as the block in the top level.
        // The repositories here will be used for publishing your artifact, not for
        // retrieving dependencies.
    }
}

//publishMods {
//    file = tasks.named<Jar>("jar").map { it.archiveFile.get() }
//    displayName = "${property("mod_version")} for ${libs.versions.minecraft.get()}"
//    version = property("mod_version").toString()
//    changelog = rootProject.file("CHANGELOG.md").readText()
//    type = BETA
//    modLoaders.add("neoforge")
//
//    val min = property("publish_target_min").toString()
//    val max = property("publish_target_max").toString()
//
//    val modrinthToken = providers.gradleProperty("enjaraiModrinthToken")
//    if (modrinthToken.isPresent) {
//        modrinth {
//            projectId = property("mod_modrinth").toString()
//            accessToken = modrinthToken.get()
//
//            if (min == max) {
//                minecraftVersions.add(min)
//            } else {
//                minecraftVersionRange {
//                    start = min
//                    end = max
//                }
//            }
//
//            requires {
//                slug = "do-a-barrel-roll"
//            }
//        }
//    }
//
//    val curseforgeToken = providers.gradleProperty("enjaraiCurseforgeToken")
//    if (curseforgeToken.isPresent) {
//        curseforge {
//            projectId = property("mod_curseforge").toString()
//            accessToken = curseforgeToken.get()
//
//            if (min == max) {
//                minecraftVersions.add(min)
//            } else {
//                minecraftVersionRange {
//                    start = min
//                    end = max
//                }
//            }
//
//            requires {
//                slug = "do-a-barrel-roll"
//            }
//        }
//
//        val githubToken = providers.gradleProperty("enjaraiGithubToken")
//        if (githubToken.isPresent) {
//            github {
//                repository = property("mod_github").toString()
//                accessToken = githubToken.get()
//
//                commitish = property("git_branch").toString()
//                tagName = project.version.toString()
//            }
//        }
//    }
//}

// IDEA no longer automatically downloads sources/javadoc jars for dependencies, so we need to explicitly enable the behavior.
idea {
    module {
        isDownloadSources = true
        isDownloadJavadoc = true
    }
}
