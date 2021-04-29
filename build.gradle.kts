plugins {
    id("java")
    id("com.github.johnrengelman.shadow").version("6.1.0")
}

repositories {
    mavenLocal()
    maven {
        url = uri("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
    }

    maven {
        url = uri("https://libraries.minecraft.net/")
    }

    maven {
        url = uri("https://repo.aikar.co/content/groups/aikar/")
    }

    maven {
        url = uri("https://repo.codemc.org/repository/maven-public")
    }

    maven {
        url = uri("https://oss.sonatype.org/content/repositories/snapshots")
    }

    maven {
        url = uri("https://repo.maven.apache.org/maven2/")
    }
}

dependencies {
    implementation("me.machinemaker:config-manager:1.0-SNAPSHOT")
    implementation("com.google.inject:guice:4.0")
    implementation("co.aikar:acf-bukkit:0.5.0-SNAPSHOT")
    implementation("org.bstats:bstats-bukkit:1.7")
    implementation("io.papermc:paperlib:1.0.4")

    compileOnly("org.spigotmc:spigot-api:1.16.5-R0.1-SNAPSHOT")
    compileOnly("com.mojang:authlib:1.5.25")

    testImplementation("org.junit.jupiter:junit-jupiter-api:5.7.0")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.7.0")
}

group = "me.machinemaker"
version = "1.0.0-SNAPSHOT"
description = "A replacement for the VanillaTweaks datapack"

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}


tasks {

    jar {
        enabled = false
    }

    test {
        useJUnitPlatform()
    }

    compileJava {
        options.encoding = "UTF-8"
        options.compilerArgs.add("-parameters")
    }

    processResources {
        filesMatching("plugin.yml") {
            expand("version" to project.version)
        }
    }

    assemble {
        dependsOn(shadowJar)
    }

    shadowJar {
        archiveClassifier.set("")
        archiveFileName.set(rootProject.name + ".jar")

        dependencies {
            exclude(dependency("org.jetbrains:annotations")) // TODO remove after removing config-manager dependency
        }

        relocate("io.papermc.lib", "me.machinemaker.libs.paperlib")
        relocate("org.bstats", "me.machinemaker.libs.bstats")

        // Guice
        relocate("javax.inject", "me.machinemaker.libs.jsr330")
        relocate("org.aopalliance", "me.machinemaker.libs.aopalliance")


        // TODO TO BE REMOVED
        relocate("co.aikar.commands", "me.machinemaker.libs.commands")
        relocate("co.aikar.locales", "me.machinemaker.libs.locales")
        relocate("org.apache.commons.lang3", "me.machinemaker.libs.commons.lang3")
    }
}
