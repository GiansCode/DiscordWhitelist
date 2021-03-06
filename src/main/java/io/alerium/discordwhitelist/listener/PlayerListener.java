package io.alerium.discordwhitelist.listener;

import io.alerium.discordwhitelist.WhitelistPlugin;
import io.alerium.discordwhitelist.cache.CodeBuilder;
import io.alerium.discordwhitelist.user.provider.WhitelistProvider;
import io.alerium.discordwhitelist.util.ColorUtils;
import io.alerium.discordwhitelist.util.ReplaceUtils;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;

import java.util.UUID;

public final class PlayerListener implements Listener {

    private final WhitelistPlugin plugin;
    private final FileConfiguration config;

    public PlayerListener(final WhitelistPlugin plugin) {
        this.plugin = plugin;
        this.config = plugin.getConfig();
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerPreLogin(final AsyncPlayerPreLoginEvent event) {
        final WhitelistProvider whitelistProvider = plugin.getWhitelistProvider();
        final UUID uuid = event.getUniqueId();

        if (!whitelistProvider.isValidUser(uuid)) {
            whitelistProvider.setWhitelisted(uuid, false);
        }

        if (whitelistProvider.isWhitelisted(uuid)) {
            return;
        }

        String code = plugin.getRequestCache().getCodeAssociatedToUUID(uuid);

        if (code == null) {
            code = CodeBuilder.getRandomCode(
                    config.getInt("settings.codeLength"),
                    config.getString("settings.codeType")
            );
        }

        event.setLoginResult(AsyncPlayerPreLoginEvent.Result.KICK_OTHER);
        event.setKickMessage(ReplaceUtils.replaceString(
                ColorUtils.colorize(plugin.getConfig().getString("messages.kickMessage")),
                "{code}", code
        ));

        plugin.getRequestCache().addCodeToCache(code, uuid);
    }

}
