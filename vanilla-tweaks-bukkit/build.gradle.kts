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
    implementation("me.lucko:commodore:2.0") {
        exclude("com.mojang", "brigadier")
    }

    val adventureVersion = "4.11.0"
    implementation("net.kyori:adventure-text-serializer-plain:$adventureVersion")
    implementation("net.kyori:adventure-api:$adventureVersion")
    compileOnly("org.spigotmc:spigot-api:1.19-R0.1-SNAPSHOT")

    // tests
    testImplementation("org.spigotmc:spigot-api:1.19-R0.1-SNAPSHOT")
}

tasks {
    shadowJar {
        configureStandard()
        archiveFileName.set("PaperTweaks-Bukkit.jar")

        val prefix = "me.machinemaker.libs"
        listOf(
            "me.lucko.commodore",
            "net.kyori",
        ).forEach { pack ->
            relocate(pack, "$prefix.$pack")
        }
    }
}
