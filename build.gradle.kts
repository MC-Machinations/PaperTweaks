import net.kyori.indra.repository.sonatypeSnapshots

plugins {
    id("net.kyori.indra") version "2.0.1"
    id("net.kyori.indra.license-header") version "2.0.1" apply false
    id("com.github.johnrengelman.shadow") version "6.1.0" apply false
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
        jar {
            enabled = false;
        }

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
