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
    implementation("me.lucko:commodore:1.10") {
        exclude("com.mojang", "brigadier")
    }

    compileOnly("org.spigotmc:spigot-api:1.17.1-R0.1-SNAPSHOT")

    // tests
    testImplementation("org.spigotmc:spigot-api:1.17.1-R0.1-SNAPSHOT")
}

tasks {
    shadowJar {
        configureStandard()
        relocate("me.lucko.commodore", "me.machinemaker.libs.me.lucko.commodore")
    }
}
