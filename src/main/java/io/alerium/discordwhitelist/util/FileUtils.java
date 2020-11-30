package io.alerium.discordwhitelist.util;

import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.Arrays;

public final class FileUtils {

    public static void saveResources(@NotNull final JavaPlugin plugin, @NotNull final String... resources) {
        Arrays.stream(resources).forEach(resource -> {
            if (!new File(plugin.getDataFolder(), resource).exists()) plugin.saveResource(resource, false);
        });
    }

}
