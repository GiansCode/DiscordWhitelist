package io.alerium.discordwhitelist.listener;

import io.alerium.discordwhitelist.WhitelistPlugin;
import io.alerium.discordwhitelist.cache.CodeBuilder;
import io.alerium.discordwhitelist.util.Replace;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;

import java.util.UUID;
import java.util.concurrent.ExecutionException;

public final class PlayerListener implements Listener {

    private final WhitelistPlugin plugin;

    public PlayerListener(final WhitelistPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerPreLogin(final AsyncPlayerPreLoginEvent event) {
        final UUID uuid = event.getUniqueId();

        try {
            if (plugin.getWhitelistProvider().isWhitelisted(uuid)) {
                return;
            }
        } catch (final ExecutionException ex) {
            ex.printStackTrace();
        }

        final String code = CodeBuilder.getRandomCode("8");
        event.setKickMessage(
                String.join(" ", Replace.replaceList(
                        plugin.getConfig().getStringList("messages.kickMessage"),
                        "{code}", code)
                )
        );
        event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_WHITELIST, "");

        plugin.getRequestCache().addCodeToCache(code, uuid);
    }

}
