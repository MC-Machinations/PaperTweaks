dependencies {
    implementation(project(":vanilla-tweaks-bukkit")) {
        exclude(group="io.papermc", module="paperlib")
        exclude(group="me.lucko", module="commodore")
    }

    val mcVersion = providers.gradleProperty("mcVersion").get()
    compileOnly("io.papermc.paper:paper-api:$mcVersion")

    // tests
    testImplementation("io.papermc.paper:paper-api:$mcVersion")
}

tasks {
    shadowJar {
        archiveFileName.set("PaperTweaks.jar")
        dependencies {
            exclude(dependency("net.kyori:examination-api:"))
            exclude(dependency("net.kyori:examination-string:"))
            exclude(dependency("net.kyori:adventure-key:"))
            exclude(dependency("net.kyori:adventure-text-serializer-plain:"))
            exclude(dependency("net.kyori:adventure-text-serializer-gson:"))
            exclude(dependency("net.kyori:adventure-text-serializer-legacy:"))
            exclude(dependency("net.kyori:adventure-api:"))
            exclude(dependency("net.kyori:adventure-text-minimessage:"))
        }

        configureStandard()

        val prefix = "me.machinemaker.libs"
        listOf(
            "net.kyori.adventure.platform",
            "net.kyori.adventure.nbt",
            "net.kyori.adventure.text.serializer.bungeecord",
            "net.kyori.adventure.text.serializer.craftbukkit",
            "net.kyori.adventure.text.serializer.gson.legacyimpl"
        ).forEach { pack ->
            relocate(pack, "$prefix.$pack")
        }
    }
}
