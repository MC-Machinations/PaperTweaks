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
    implementation("me.lucko:commodore:2.2") {
        exclude("com.mojang", "brigadier")
    }

    val adventureVersion = "4.12.0"
    implementation("net.kyori:adventure-text-serializer-plain:$adventureVersion")
    implementation("net.kyori:adventure-api:$adventureVersion")

    val mcVersion = project.providers.gradleProperty("mcVersion").get()
    compileOnly("org.spigotmc:spigot-api:$mcVersion")

    // tests
    testImplementation("org.spigotmc:spigot-api:$mcVersion")
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
