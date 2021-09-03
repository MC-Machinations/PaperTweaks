import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

fun ShadowJar.configureStandard() {
    archiveClassifier.set("")
    archiveFileName.set("VanillaTweaks.jar")

    val prefix = "me.machinemaker.libs"
    listOf(
        "org.bstats",
        "cloud.commandframework",
        "io.papermc.lib",
        "net.kyori.adventure.text.minimessage",
        "me.machinemaker.lectern"
    ).forEach { pack ->
        relocate(pack, "$prefix.$pack")
    }
}