import net.kyori.indra.repository.sonatypeSnapshots
import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    id("vanilla-tweaks.parent-build-logic")
    id("net.kyori.indra") version "2.0.6"
    id("net.kyori.indra.license-header") version "2.0.6" apply false
    id("com.github.johnrengelman.shadow") version "7.0.0" apply false
}

group = "me.machinemaker"
version = "0.2.0-SNAPSHOT"
description = "A replacement for the VanillaTweaks datapack"

subprojects {
    apply(plugin="net.kyori.indra")
    apply(plugin="net.kyori.indra.license-header")
    apply(plugin="com.github.johnrengelman.shadow")

    group = rootProject.group;
    version = rootProject.version;

    repositories {
        mavenLocal() // TODO remove
        mavenCentral()
        sonatypeSnapshots()
        maven("https://repo.incendo.org/content/repositories/snapshots") // For cloud snapshot builds
        maven("https://libraries.minecraft.net/")
        maven("https://papermc.io/repo/repository/maven-public/")
    }

    dependencies {
        compileOnly("io.leangen.geantyref:geantyref:1.3.11")
        implementation("me.machinemaker:lectern:0.1.1-SNAPSHOT") {
            exclude("com.fasterxml.jackson.core", "jackson-databind")
        }
        implementation("cloud.commandframework:cloud-paper:1.5.0") {
            exclude("io.leangen.geantyref", "geantyref")
        }
        implementation("cloud.commandframework:cloud-minecraft-extras:1.5.0") {
            exclude("net.kyori")
            exclude("io.leangen.geantyref", "geantyref")
        }
        implementation("net.kyori:adventure-text-minimessage:4.1.0-SNAPSHOT") {
            exclude("net.kyori", "adventure-api")
        }
        implementation("org.bstats:bstats-bukkit:2.2.1")
        implementation("io.papermc:paperlib:1.0.6")

        // Loaded via plugin.yml libraries
        compileOnly("io.github.classgraph:classgraph:4.8.114")
        compileOnly("com.google.inject:guice:5.0.1")
        compileOnly("me.lucko:adventure-platform-bukkit:4.8.1")

        // Native to minecraft
        compileOnly("com.mojang:authlib:1.5.25")
        compileOnly("org.slf4j:slf4j-api:1.7.30")

        testImplementation("me.lucko:adventure-platform-bukkit:4.8.1")
        testImplementation("com.google.inject:guice:5.0.1")
        testImplementation("org.junit.jupiter:junit-jupiter-api:5.7.2")
        testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.7.2")
    }

    configure<net.kyori.indra.IndraExtension> {
        javaVersions {
            testWith(16)
            target(16)
        }

        github("MC-Machinations", "VanillaTweaks")

        gpl3OnlyLicense()
    }

    tasks {
        compileJava {
            options.compilerArgs.add("-parameters")
        }

        processResources {
            filesMatching("plugin.yml") {
                expand("version" to project.version)
            }
        }

        assemble {
            dependsOn("shadowJar")
        }

        test {
            useJUnitPlatform()
        }
    }
}
