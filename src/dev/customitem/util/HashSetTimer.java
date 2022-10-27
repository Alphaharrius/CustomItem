package dev.customitem.util;

import dev.customitem.core.CurrentPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.Set;

public class HashSetTimer {

    private final Set<String> usageFlagSet;

    public HashSetTimer() {

        usageFlagSet = new HashSet<>();
    }

    @Nullable
    public BukkitTask startCountdown(@Nonnull String key, long ticks, @Nullable Runnable callbackRunnable) {

        synchronized (usageFlagSet) {

            if (usageFlagSet.contains(key)) { return null; }
            usageFlagSet.add(key);
        }

        return new BukkitRunnable() {

            @Override
            public void run() {

                synchronized (usageFlagSet) { usageFlagSet.remove(key); }
                if (callbackRunnable == null) { return; }
                callbackRunnable.run();
            }

        }.runTaskLater(CurrentPlugin.getCurrentPlugin(), ticks);
    }

}
