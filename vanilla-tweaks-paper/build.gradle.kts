dependencies {
    implementation(project(":vanilla-tweaks-bukkit")) {
        exclude(group="io.papermc", module="paperlib")
        exclude(group="me.lucko", module="commodore")
    }

    compileOnly("io.papermc.paper:paper-api:1.17.1-R0.1-SNAPSHOT")

    // tests
    testImplementation("io.papermc.paper:paper-api:1.17.1-R0.1-SNAPSHOT")
}

tasks {
    shadowJar {
        configureStandard()

    }
}
