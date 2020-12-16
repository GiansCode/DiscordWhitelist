package io.alerium.discordwhitelist.listener;

import io.alerium.discordwhitelist.WhitelistPlugin;
import io.alerium.discordwhitelist.cache.CodeBuilder;
import io.alerium.discordwhitelist.user.provider.WhitelistProvider;
import io.alerium.discordwhitelist.util.ColorUtils;
import io.alerium.discordwhitelist.util.ReplaceUtils;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;

import java.util.UUID;

public final class PlayerListener implements Listener {

    private final WhitelistPlugin plugin;

    public PlayerListener(final WhitelistPlugin plugin) {
        this.plugin = plugin;
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

        String code;
        if (plugin.getRequestCache().getCodeAssociatedToUUID(uuid) != null) {
            code = plugin.getRequestCache().getCodeAssociatedToUUID(uuid);
        } else {
            code = CodeBuilder.getRandomCode();
        }

        event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, "");
        event.setKickMessage(ReplaceUtils.replaceString(
                ColorUtils.colorize(plugin.getConfig().getString("messages.kickMessage")),
                "{code}", code
        ));

        plugin.getRequestCache().addCodeToCache(code, uuid);
    }

}
