dependencies {
    implementation(project(":vanilla-tweaks-bukkit", "shadow")) {
        exclude("org.spigotmc:spigot-api:1.17.1-R0.1-SNAPSHOT")
    }

    compileOnly("io.papermc.paper:paper-api:1.17.1-R0.1-SNAPSHOT")

    // tests
    testImplementation("io.papermc.paper:paper-api:1.17.1-R0.1-SNAPSHOT")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.7.2")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.7.2")
}

tasks {
    shadowJar {
        archiveClassifier.set("")
        archiveFileName.set("VanillaTweaks.jar")
    }
}
