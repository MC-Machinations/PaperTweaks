import net.kyori.indra.repository.SonatypeRepositories
import net.kyori.indra.repository.sonatypeSnapshots

plugins {
    id("vanilla-tweaks.parent-build-logic")
    id("net.kyori.indra") version "2.1.1"
    id("net.kyori.indra.license-header") version "2.0.6"
    id("com.github.johnrengelman.shadow") version "7.0.0" apply false
}

description = "A replacement for the VanillaTweaks datapack"

gradle.buildFinished {
    rootProject.buildDir.deleteRecursively()
}

allprojects {
    apply(plugin="net.kyori.indra")
    apply(plugin="net.kyori.indra.license-header")

    group = "me.machinemaker"
    version = "0.2.0-SNAPSHOT"

    configure<org.cadixdev.gradle.licenser.LicenseExtension>() {
        header(rootProject.file("HEADER"))
    }

    tasks {
        build {
            dependsOn(named("checkLicenses"))
        }
    }
}

subprojects {
    apply(plugin="net.kyori.indra")
    apply(plugin="net.kyori.indra.license-header")
    apply(plugin="com.github.johnrengelman.shadow")

    repositories {
        mavenCentral()
        sonatype.s01Snapshots().mavenContent {
            includeGroup("net.kyori")
        }
        sonatype.ossSnapshots().mavenContent {
            includeGroup("cloud.commandframework")
        }
        maven("https://libraries.minecraft.net/")
        maven("https://papermc.io/repo/repository/maven-public/")
        maven("https://maven.enginehub.org/repo/")
        maven("https://jitpack.io") {
            mavenContent {
                includeGroup("com.github.TechFortress")
            }
        }
    }

    dependencies {
        compileOnly("io.leangen.geantyref:geantyref:1.3.11")
        implementation("me.machinemaker.mirror:mirror-paper:0.1.1")
        implementation("me.machinemaker.lectern:lectern-yaml:0.2.1")
        implementation("net.kyori:adventure-platform-bukkit:4.1.2-SNAPSHOT") // s01.oss
        implementation("net.kyori:adventure-text-minimessage:4.11.0")
        implementation(platform("cloud.commandframework:cloud-bom:1.8.0-SNAPSHOT")) // oss
        implementation("cloud.commandframework:cloud-paper")
        implementation("cloud.commandframework:cloud-minecraft-extras")
        implementation("org.bstats:bstats-bukkit:2.2.1")
        implementation("io.papermc:paperlib:1.0.6")
        implementation("net.kyori.moonshine:moonshine-standard:2.0.4")

        // Loaded via plugin.yml libraries
        compileOnly("io.github.classgraph:classgraph:4.8.114")
        compileOnly("com.google.inject:guice:5.0.1")
        compileOnly("com.google.inject.extensions:guice-assistedinject:5.0.1")
        compileOnly("org.apache.commons:commons-configuration2:2.7")
        compileOnly("com.fasterxml.jackson.dataformat:jackson-dataformat-yaml:2.12.5")
        compileOnly("com.fasterxml.jackson.module:jackson-module-parameter-names:2.12.5")
        compileOnly(platform("org.jdbi:jdbi3-bom:3.22.0"))
        compileOnly("org.jdbi:jdbi3-core")
        compileOnly("org.jdbi:jdbi3-sqlobject")
        compileOnly("com.h2database:h2:1.4.200")
        compileOnly("org.xerial:sqlite-jdbc:3.36.0.3")

        // Native to minecraft
        compileOnly("com.mojang:authlib:3.11.49")
        compileOnly("org.slf4j:slf4j-api:1.7.30")
        compileOnly("com.mojang:brigadier:1.0.18")

        // soft dependencies
        compileOnly("com.sk89q.worldguard:worldguard-bukkit:7.0.6")
        compileOnly("com.github.TechFortress:GriefPrevention:16.17.1")

        testImplementation("org.apache.commons:commons-configuration2:2.7")
        testRuntimeOnly("commons-beanutils:commons-beanutils:1.9.4")
        testImplementation("org.slf4j:slf4j-api:1.7.30")
        testImplementation("com.fasterxml.jackson.dataformat:jackson-dataformat-yaml:2.12.5")
        testImplementation("com.google.inject:guice:5.0.1")
        testImplementation("org.junit.jupiter:junit-jupiter-api:5.7.2")
        testImplementation("org.mockito:mockito-core:3.12.4")
        testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.7.2")
    }

    indra {
        javaVersions {
            testWith(17)
            target(17)
        }

        github("MC-Machinations", "VanillaTweaks")

        gpl3OnlyLicense()
    }

    tasks {
        compileJava {
            options.compilerArgs.add("-parameters")
            options.release.set(17)
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

        jar {
            manifest {
                attributes(
                    "Implementation-Title" to "VanillaTweaks",
                    "Implementation-Version" to rootProject.version
                )
            }
        }
    }
}
