plugins {
    java
    alias(libs.plugins.shadow)
    alias(libs.plugins.indra.licenser.spotless)
}

group = "me.machinemaker"
version = "0.3.0-SNAPSHOT"
description = "A replacement for the VanillaTweaks datapack"

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://maven.enginehub.org/repo/")
    maven("https://jitpack.io") {
        mavenContent {
            includeGroup("com.github.TechFortress")
        }
    }
}

dependencies {
    compileOnly(libs.paper.api)

    implementation(libs.mm.mirror)
    implementation(libs.mm.lectern) // TODO replace with configurate
    implementation(libs.cloud.paper)
    implementation(libs.cloud.extras)
    implementation(libs.bstats)
    implementation(libs.moonshine)
    implementation(libs.guice)
    implementation(libs.guice.assistedInject)
    implementation(libs.jackson.yaml)
    implementation(libs.jackson.paramNames)
    implementation(libs.jdbi.core)
    implementation(libs.jdbi.sqlobject)

    // Native to minecraft
    compileOnly(libs.authlib)
    compileOnly(libs.slf4j)

    // soft dependencies
    compileOnly(libs.worldguard)
    compileOnly(libs.griefprevention)
    compileOnly(libs.brigadier)

    // TODO convert to libs.versions.toml
    implementation("io.github.classgraph:classgraph:4.8.157")
    implementation("org.apache.commons:commons-configuration2:2.8.0")
    implementation("commons-beanutils:commons-beanutils:1.9.4")
    implementation("io.leangen.geantyref:geantyref:1.3.14")
    implementation("com.h2database:h2:1.4.200")
    implementation("org.xerial:sqlite-jdbc:3.40.0.0")

    // tests
    testImplementation(libs.paper.api)
    testImplementation("org.apache.commons:commons-configuration2:2.8.0")
    testRuntimeOnly("commons-beanutils:commons-beanutils:1.9.4")
    testImplementation(libs.slf4j)
    testImplementation("com.fasterxml.jackson.dataformat:jackson-dataformat-yaml:2.14.2")
    testImplementation(libs.guice)
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.9.0")
    testImplementation("org.mockito:mockito-core:4.8.0")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.9.0")
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(17))
}

spotless {
    format("javaMisc") {
        target("src/**/package-info.java")
        licenseHeaderFile(rootProject.file(".spotless/HEADER_misc"), "(\\/\\*\\*|@DefaultQualifier)")
            .updateYearWithLatest(true)
    }
}

indraSpotlessLicenser {
    headerFormat {
        starSlash()
        property("name", "Machine_Maker")
    }
    licenseHeaderFile(rootProject.file(".spotless/HEADER"))
    extraConfig {
        spotless {
            updateYearWithLatest(true)
        }
    }
}

tasks {
    assemble {
        dependsOn(shadowJar)
    }

    shadowJar {
        relocate("org.bstats", "me.machinemaker.libs.org.bstats") // bstats requires being relocated
    }

    processResources {
        filteringCharset = Charsets.UTF_8.name()
        filesMatching(listOf("plugin.yml", "paper-plugin.yml")) {
            expand("version" to project.version)
        }
    }

    compileJava {
        options.encoding = Charsets.UTF_8.name()
        options.compilerArgs.add("-parameters")
        options.release.set(17)
    }

    test {
        useJUnitPlatform()
    }

    jar {
        manifest {
            attributes(
                "Implementation-Title" to "VanillaTweaks",
                "Implementation-Version" to project.version
            )
        }
    }
}
