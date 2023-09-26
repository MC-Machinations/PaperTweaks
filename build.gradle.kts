import xyz.jpenilla.runpaper.task.RunServer

plugins {
    java
    alias(libs.plugins.shadow)
    alias(libs.plugins.indra.licenser.spotless)
    alias(libs.plugins.runPaper)
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

val paperApi: Provider<String> = libs.versions.minecraft.map { "io.papermc.paper:paper-api:$it-R0.1-SNAPSHOT" }
dependencies {
    compileOnly(paperApi)

    implementation(libs.mm.mirror) // TODO replace with MethodHandles
    implementation(libs.mm.lectern) // TODO replace with configurate
    implementation(libs.cloud.paper)
    implementation(libs.cloud.extras) {
        exclude(group = "net.kyori")
    }
    implementation(libs.bstats)
    implementation(libs.moonshine)
    implementation(libs.guice)
    implementation(libs.guice.assistedInject)
    implementation(libs.jackson.yaml)
    implementation(libs.jackson.paramNames)
    implementation(libs.jdbi.core)
    implementation(libs.jdbi.sqlobject)
    implementation(libs.commons.config)
    implementation(libs.commons.bean)

    // Native to minecraft
    compileOnly(libs.authlib)
    compileOnly(libs.slf4j)

    // soft dependencies
    compileOnly(libs.worldguard)
    compileOnly(libs.griefprevention)
    compileOnly(libs.brigadier)

    // TODO convert to libs.versions.toml
    implementation("io.github.classgraph:classgraph:4.8.157")
    implementation("io.leangen.geantyref:geantyref:1.3.14")
    implementation("com.h2database:h2:1.4.200")
    implementation("org.xerial:sqlite-jdbc:3.40.0.0")

    // tests
    testImplementation(paperApi)
    testImplementation(libs.commons.config)
    testRuntimeOnly(libs.commons.bean)
    testImplementation(libs.slf4j)
    testImplementation(libs.jackson.yaml)
    testImplementation(libs.jackson.paramNames)
    testImplementation(libs.guice)
    testImplementation(libs.junit.api)
    testImplementation(libs.mockito)

    testRuntimeOnly(libs.junit.engine)
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

    processResources {
        filteringCharset = Charsets.UTF_8.name()
        filesMatching(listOf("plugin.yml", "paper-plugin.yml")) {
            expand("version" to project.version)
        }
    }

    compileJava {
        options.encoding = Charsets.UTF_8.name()
        options.compilerArgs.apply {
            add("-parameters")
            add("-Xlint")
        }
        options.release.set(17)
    }

    test {
        useJUnitPlatform()
    }

    jar {
        manifest {
            attributes(
                "Implementation-Title" to "PaperTweaks",
                "Implementation-Version" to project.version
            )
        }
    }

    shadowJar {
        dependencies {
            exclude(dependency("org.checkerframework:checker-qual"))
            exclude(dependency("org.apache.commons:commons-lang3"))
            listOf("guava", "errorprone", "j2objc").forEach {
                exclude(dependency("com.google.$it:"))
            }
            exclude(dependency("com.google.code.findbugs:jsr305"))
            exclude(dependency("org.slf4j:slf4j-api"))
            exclude(dependency("org.yaml:snakeyaml"))
        }

        listOf(
            "cloud.commandframework", // cloud
            "com.fasterxml.jackson", // jackson
            "com.github.benmanes", // caffeine
            "com.google.inject", // guice
            "io.github.classgraph", // classgraph
            "nonapi.io.github.classgraph", // classgraph
            "io.leangen.geantyref", // geantyref
            "javax.inject", // javax
            "me.machinemaker.mirror", // mirror
            "me.machinemaker.lectern", // lectern
            "net.kyori.moonshine", // moonshine
            "org.antlr", // antlr (jdbi)
            "org.aopalliance", // aopalliance (guice)
            "org.bstats", // bStats
            "org.h2", // h2 db
            "org.jdbi.v3", // jdbi v3
            "org.sqlite" // sqlite db
        ).forEach {
            relocate(it, "me.machinemaker.papertweaks.libs.$it")
        }

        listOf(
            "beanutils",
            "collections",
            "configuration2",
            "logging",
            "text",
        ).map { "org.apache.commons.$it" }.forEach {
            relocate(it, "me.machinemaker.papertweaks.libs.$it")
        }
    }

    withType<RunServer> { // set for both runServer and runMojangMappedServer
        systemProperty("com.mojang.eula.agree", "true")
    }

    runServer {
        minecraftVersion(libs.versions.minecraft.get())
    }
}
