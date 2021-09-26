import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

fun ShadowJar.configureStandard(platform: String) {
    archiveClassifier.set("")
    archiveFileName.set("VanillaTweaks-$platform.jar")

    dependencies {
        dependencyFilter.exclude {
            it.moduleGroup.startsWith("com.fasterxml.jackson")
        }
        dependencyFilter.exclude(dependencyFilter.dependency("org.yaml:snakeyaml:"))
        dependencyFilter.exclude(dependencyFilter.dependency("io.leangen.geantyref:geantyref:"))
    }

    val prefix = "me.machinemaker.libs"
    listOf(
        "org.bstats",
        "cloud.commandframework",
        "io.papermc.lib",
        "me.machinemaker.lectern",
    ).forEach { pack ->
        relocate(pack, "$prefix.$pack")
    }
}
