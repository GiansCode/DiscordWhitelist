package io.alerium.discordwhitelist;

import io.alerium.discordwhitelist.bukkit.command.WhitelistRemoveCommand;
import io.alerium.discordwhitelist.cache.RequestCache;
import io.alerium.discordwhitelist.discord.provider.DiscordProvider;
import io.alerium.discordwhitelist.listener.PlayerListener;
import io.alerium.discordwhitelist.user.provider.WhitelistProvider;
import me.mattstudios.mf.base.CommandManager;
import org.bukkit.Bukkit;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;
import rest.faucet.task.Tasks;

import java.util.logging.Level;

public final class WhitelistPlugin extends JavaPlugin {

    private final DiscordProvider discordProvider = new DiscordProvider(this);
    private final WhitelistProvider whitelistProvider = new WhitelistProvider(this, discordProvider);

    private final RequestCache requestCache = new RequestCache(this);

    @Override
    public void onEnable() {
        Tasks.init(this);

        this.discordProvider.initiateBot();
        Bukkit.getServer().getPluginManager().registerEvents(
                new PlayerListener(this), this
        );
        Bukkit.getServicesManager().register(WhitelistProvider.class, whitelistProvider, this, ServicePriority.Normal);

        final CommandManager manager = new CommandManager(this);
        manager.register(
                new WhitelistRemoveCommand(this)
        );

        new Placeholders(whitelistProvider).register();
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
