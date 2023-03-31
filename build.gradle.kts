plugins {
    id("vanilla-tweaks.parent")
    id("net.kyori.indra") version "3.0.1"
    id("net.kyori.indra.licenser.spotless") version "3.0.1"
}

description = "A replacement for the VanillaTweaks datapack"

allprojects {
    apply(plugin="net.kyori.indra")
    apply(plugin="net.kyori.indra.licenser.spotless")

    group = "me.machinemaker"
    version = "0.2.0-SNAPSHOT"

    indraSpotlessLicenser {
        headerFormat {
            starSlash()
            property("name", "Machine_Maker")
        }
        licenseHeaderFile(rootProject.file("HEADER"))
        extraConfig {
            spotless {
                updateYearWithLatest(true)
            }
        }
    }

    tasks {
        build {
            dependsOn(named("checkLicenses"))
        }
    }
}

subprojects {
    apply(plugin="net.kyori.indra")
    apply(plugin="com.github.johnrengelman.shadow")

    repositories {
        mavenCentral()
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
        implementation("me.machinemaker.mirror:mirror-paper:0.1.1")
        implementation("me.machinemaker.lectern:lectern-yaml:0.2.1")
        implementation("net.kyori:adventure-platform-bukkit:4.2.0")
        implementation("net.kyori:adventure-text-minimessage:4.13.0")
        implementation(platform("cloud.commandframework:cloud-bom:1.8.3"))
        implementation("cloud.commandframework:cloud-paper")
        implementation("cloud.commandframework:cloud-minecraft-extras")
        implementation("org.bstats:bstats-bukkit:3.0.1")
        implementation("io.papermc:paperlib:1.0.6")
        implementation("net.kyori.moonshine:moonshine-standard:2.0.4")

        // Loaded via plugin.yml libraries
        compileOnly("io.github.classgraph:classgraph:4.8.157")
        compileOnly("com.google.inject:guice:5.1.0")
        compileOnly("com.google.inject.extensions:guice-assistedinject:5.1.0")
        compileOnly("org.apache.commons:commons-configuration2:2.8.0")
        compileOnly("com.fasterxml.jackson.dataformat:jackson-dataformat-yaml:2.14.2")
        compileOnly("com.fasterxml.jackson.module:jackson-module-parameter-names:2.14.2")
        compileOnly("io.leangen.geantyref:geantyref:1.3.14")
        compileOnly(platform("org.jdbi:jdbi3-bom:3.22.0"))
        compileOnly("org.jdbi:jdbi3-core")
        compileOnly("org.jdbi:jdbi3-sqlobject")
        compileOnly("com.h2database:h2:1.4.200")
        compileOnly("org.xerial:sqlite-jdbc:3.40.0.0")

        // Native to minecraft
        compileOnly("com.mojang:authlib:3.11.49")
        compileOnly("org.slf4j:slf4j-api:2.0.5")
        compileOnly("com.mojang:brigadier:1.0.18")

        // soft dependencies
        compileOnly("com.sk89q.worldguard:worldguard-bukkit:7.0.6")
        compileOnly("com.github.TechFortress:GriefPrevention:16.17.1")

        testImplementation("org.apache.commons:commons-configuration2:2.8.0")
        testRuntimeOnly("commons-beanutils:commons-beanutils:1.9.4")
        testImplementation("org.slf4j:slf4j-api:2.0.5")
        testImplementation("com.fasterxml.jackson.dataformat:jackson-dataformat-yaml:2.14.2")
        testImplementation("com.google.inject:guice:5.1.0")
        testImplementation("org.junit.jupiter:junit-jupiter-api:5.9.0")
        testImplementation("org.mockito:mockito-core:4.8.0")
        testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.9.0")
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
            options.encoding = Charsets.UTF_8.name()
            options.compilerArgs.add("-parameters")
            options.release.set(17)
        }

        processResources {
            filteringCharset = Charsets.UTF_8.name()
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
