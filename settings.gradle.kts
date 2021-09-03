pluginManagement {
    repositories {
        gradlePluginPortal()
    }
    includeBuild("build-logic")
}

rootProject.name = "vanilla-tweaks"
include("vanilla-tweaks-bukkit")
include("vanilla-tweaks-paper")
