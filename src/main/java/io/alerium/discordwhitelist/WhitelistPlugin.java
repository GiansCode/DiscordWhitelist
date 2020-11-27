package io.alerium.discordwhitelist;

import io.alerium.discordwhitelist.cache.RequestCache;
import io.alerium.discordwhitelist.discord.provider.DiscordProvider;
import io.alerium.discordwhitelist.listener.PlayerListener;
import io.alerium.discordwhitelist.user.provider.WhitelistProvider;
import org.bukkit.Bukkit;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Level;

public final class WhitelistPlugin extends JavaPlugin {

    private final DiscordProvider discordProvider = new DiscordProvider(this);
    private final WhitelistProvider whitelistProvider = new WhitelistProvider(discordProvider);

    private final RequestCache requestCache = new RequestCache(this);

    @Override
    public void onEnable() {
        this.discordProvider.initiateBot();
        Bukkit.getServer().getPluginManager().registerEvents(
                new PlayerListener(this), this
        );
        Bukkit.getServicesManager().register(WhitelistProvider.class, whitelistProvider, this, ServicePriority.Normal);
        getLogger().log(Level.INFO, "Initialized Plugin Successfully.");
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
