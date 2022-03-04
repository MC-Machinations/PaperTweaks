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
    implementation("me.lucko:commodore:1.13") {
        exclude("com.mojang", "brigadier")
    }

    val adventureVersion = "4.9.3"
    implementation("net.kyori:adventure-text-serializer-plain:$adventureVersion")
    implementation("net.kyori:adventure-api:$adventureVersion")
    compileOnly("org.spigotmc:spigot-api:1.17.1-R0.1-SNAPSHOT")

    // tests
    testImplementation("org.spigotmc:spigot-api:1.17.1-R0.1-SNAPSHOT")
}

tasks {
    shadowJar {
        configureStandard("Bukkit")

        val prefix = "me.machinemaker.libs"
        listOf(
            "me.lucko.commodore",
            "net.kyori",
        ).forEach { pack ->
            relocate(pack, "$prefix.$pack")
        }
    }
}
