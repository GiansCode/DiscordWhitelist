package io.alerium.discordwhitelist;

import io.alerium.discordwhitelist.cache.RequestCache;
import io.alerium.discordwhitelist.discord.provider.DiscordProvider;
import io.alerium.discordwhitelist.listener.PlayerListener;
import io.alerium.discordwhitelist.user.provider.WhitelistProvider;
import me.bristermitten.pdm.PDMBuilder;
import me.bristermitten.pdm.PluginDependencyManager;
import org.bukkit.Bukkit;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;

public final class WhitelistPlugin extends JavaPlugin {

    private final RequestCache requestCache = new RequestCache();

    private DiscordProvider discordProvider;
    private WhitelistProvider whitelistProvider;

    @Override
    public void onLoad() {

    }

    @Override
    public void onEnable() {
        final PluginDependencyManager dependencyManager = new PDMBuilder(this).build();

        final CompletableFuture<Void> load = dependencyManager.loadAllDependencies();
        load.thenRunAsync(() -> {
            getLogger().log(Level.INFO, "Loaded All Runtime Dependencies.");

            this.discordProvider = new DiscordProvider(this);
            this.whitelistProvider = new WhitelistProvider(discordProvider);
            this.discordProvider.initiateBot();
            Bukkit.getServer().getPluginManager().registerEvents(
                    new PlayerListener(this), this
            );
            Bukkit.getServicesManager().register(WhitelistProvider.class, whitelistProvider, this, ServicePriority.Normal);
            getLogger().log(Level.INFO, "Initialized Plugin Successfully.");
        });
    }

    @Override
    public void onDisable() {
        reloadConfig();
    }

    public WhitelistProvider getWhitelistProvider() {
        return this.whitelistProvider;
    }

    public RequestCache getRequestCache() {
        return this.requestCache;
    }

}
