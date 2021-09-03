package me.machinemaker.vanillatweaks.utils;

import org.bukkit.entity.Item;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

public abstract class ItemDropFinder extends BukkitRunnable {

    private final Item item;
    private final long maxRuns;
    private long counter;

    protected ItemDropFinder(@NotNull Item item, long maxRuns) {
        this.item = item;
        this.maxRuns = maxRuns;
    }

    @Override
    public final void run() {
        if (this.item.isDead()) {
            this.cancel();
            return;
        }

        if (this.counter >= maxRuns) {
            this.cancel();
            return;
        }

        if (this.failCheck(this.item)) {
            this.cancel();
            return;
        }

        if (this.successCheck(this.item)) {
            this.onSuccess(this.item);
            this.cancel();
            return;
        }

        counter++;
    }

    public boolean failCheck(@NotNull Item item) {
        return false;
    }

    public abstract boolean successCheck(@NotNull Item item);

    public void onSuccess(@NotNull Item item) {}
}
