repositories {
    maven {
        url = uri("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
        content {
            includeGroup("org.spigotmc")
        }
    }
    maven("https://libraries.minecraft.net/")
    mavenCentral()
}

dependencies {
    implementation("me.machinemaker:lectern:0.1.1-SNAPSHOT")
    implementation("io.papermc:paperlib:1.0.6")
    implementation("me.lucko:commodore:1.10")
    implementation("cloud.commandframework:cloud-paper:1.5.0")
    implementation("cloud.commandframework:cloud-minecraft-extras:1.5.0")
    implementation("com.google.inject:guice:5.0.1")
    implementation("net.kyori:adventure-platform-bukkit:4.0.0-SNAPSHOT")
    implementation("net.kyori:adventure-text-minimessage:4.1.0-SNAPSHOT")
    implementation("org.bstats:bstats-bukkit:2.2.1")
    implementation("io.github.classgraph:classgraph:4.8.114")

    compileOnly("org.spigotmc:spigot-api:1.17.1-R0.1-SNAPSHOT")
    compileOnly("com.mojang:authlib:1.5.25")
    compileOnly("org.slf4j:slf4j-api:1.7.30")

    // tests
    testImplementation("org.spigotmc:spigot-api:1.17.1-R0.1-SNAPSHOT")
}

tasks {
    shadowJar {
        archiveClassifier.set("")
        archiveFileName.set("VanillaTweaks.jar")

        dependencies {
            exclude(dependency("org.checkerframework:checker-qual:"))
            exclude(dependency("com.mojang:brigadier:"))
            exclude(dependency("org.codehaus.mojo:animal-sniffer-annotations:"))
            exclude(dependency("com.google.errorprone:error_prone_annotations:"))
            exclude(dependency("com.google.j2objc:j2objc-annotations:"))
        }

        relocate("io.papermc.lib", "me.machinemaker.libs.paperlib")
        relocate("io.github.classgraph", "me.machinemaker.libs.classgraph")
        relocate("nonapi.io.github.classgraph", "me.machinemaker.libs.classgraph.nonapi")
        relocate("io.leangen.geantyref", "me.machinemaker.libs.geantyref")

        relocate("org.bstats", "me.machinemaker.libs.bstats")
        relocate("org.aopalliance", "me.machinemaker.libs.aopalliance")

        relocate("net.kyori", "me.machinemaker.libs.kyori")

        relocate("cloud.commandframework", "me.machinemaker.libs.cloud")

        relocate("com.fasterxml.jackson", "me.machinemaker.libs.jackson")

        relocate("com.google.common", "me.machinemaker.libs.google.common")
        relocate("com.google.inject", "me.machinemaker.libs.google.inject")
        relocate("com.google.thirdparty.publicsuffix", "me.machinemaker.libs.google.thirdparty.publicsuffix")

        relocate("javax", "me.machinemaker.libs.javax")

        relocate("me.machinemaker.lectern", "me.machinemaker.libs.lectern")
        relocate("me.lucko.commodore", "me.machinemaker.libs.commodore")
    }
}
