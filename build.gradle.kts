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
    implementation("nl.enjarai:do-a-barrel-roll:${project.properties["do_a_barrel_roll_version"]}")

    implementation("dev.isxander:yet-another-config-lib:${project.properties["yacl_version"]}")
}

val generateModMetadata = tasks.register<ProcessResources>("generateModMetadata") {
    val replaceProperties = mapOf(
        "minecraft_version" to libs.versions.minecraft,
        "minecraft_version_range" to "[${libs.versions.minecraft.get()}]",
        "neo_version" to libs.versions.neoforge,
        "mod_license" to "MIT",
        "mod_id" to "rolling_down_in_the_deep",
        "mod_version" to project.version
    )

    inputs.properties(replaceProperties)
    expand(replaceProperties)

    from("src/main/templates")
    into(layout.buildDirectory.dir("generated/sources/modMetadata"))
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

fletchingTable {
    lang.create("main") {
        patterns.add("assets/modid/lang/**")
    }
}


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

publishMods {
    file = tasks.named<Jar>("jar").map { it.archiveFile.get() }
    displayName = "${property("mod_version")} for ${libs.versions.minecraft.get()}"
    version = property("mod_version").toString()
    changelog = rootProject.file("CHANGELOG.md").readText()
    type = BETA
    modLoaders.add("neoforge")

    val min = property("publish_target_min").toString()
    val max = property("publish_target_max").toString()

    val modrinthToken = providers.gradleProperty("enjaraiModrinthToken")
    if (modrinthToken.isPresent) {
        modrinth {
            projectId = property("mod_modrinth").toString()
            accessToken = modrinthToken.get()

            if (min == max) {
                minecraftVersions.add(min)
            } else {
                minecraftVersionRange {
                    start = min
                    end = max
                }
            }

            requires {
                slug = "do-a-barrel-roll"
            }
        }
    }

    val curseforgeToken = providers.gradleProperty("enjaraiCurseforgeToken")
    if (curseforgeToken.isPresent) {
        curseforge {
            projectId = property("mod_curseforge").toString()
            accessToken = curseforgeToken.get()

            if (min == max) {
                minecraftVersions.add(min)
            } else {
                minecraftVersionRange {
                    start = min
                    end = max
                }
            }

            requires {
                slug = "do-a-barrel-roll"
            }
        }

        val githubToken = providers.gradleProperty("enjaraiGithubToken")
        if (githubToken.isPresent) {
            github {
                repository = property("mod_github").toString()
                accessToken = githubToken.get()

                commitish = property("git_branch").toString()
                tagName = project.version.toString()
            }
        }
    }
}

// IDEA no longer automatically downloads sources/javadoc jars for dependencies, so we need to explicitly enable the behavior.
idea {
    module {
        isDownloadSources = true
        isDownloadJavadoc = true
    }
}
