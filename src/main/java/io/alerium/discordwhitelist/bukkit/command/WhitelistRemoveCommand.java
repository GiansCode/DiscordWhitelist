package io.alerium.discordwhitelist.bukkit.command;

import io.alerium.discordwhitelist.WhitelistPlugin;
import io.alerium.discordwhitelist.listener.event.DiscordUnWhitelistEvent;
import io.alerium.discordwhitelist.user.provider.WhitelistProvider;
import io.alerium.discordwhitelist.util.ColorUtils;
import me.clip.placeholderapi.libs.JSONMessage;
import me.mattstudios.mf.annotations.Command;
import me.mattstudios.mf.annotations.Default;
import me.mattstudios.mf.annotations.SubCommand;
import me.mattstudios.mf.base.CommandBase;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import rest.faucet.task.Tasks;

import java.util.UUID;

@Command("whitelist")
public final class WhitelistRemoveCommand extends CommandBase {

    private final WhitelistProvider provider;

    private final FileConfiguration config;

    public WhitelistRemoveCommand(final WhitelistPlugin plugin) {
        this.provider = plugin.getWhitelistProvider();

        this.config = plugin.getConfig();
    }

    @Default
    public void onDefaultCommand(final Player player) {
        Tasks.async(task -> {
            final JSONMessage message = JSONMessage.create(ColorUtils.colorize(config.getString("messages.whitelistRemove")));

            message.runCommand("/whitelist remove");
            message.send(player);
        });
    }

    @SubCommand("remove")
    public void onCommand(final Player player) {
        final UUID uuid = player.getUniqueId();
        Tasks.async(task -> {
            if (!provider.isWhitelisted(uuid)) {
                player.sendMessage(
                        ColorUtils.colorize(config.getString("messages.notWhitelisted"))
                );
                return;
            }

            provider.setWhitelisted(uuid, false);
        });

        player.kickPlayer(ColorUtils.colorize(config.getString("messages.kickSelfMessage")));
        Bukkit.getServer().getPluginManager().callEvent(new DiscordUnWhitelistEvent(
                provider.getWhitelistUserByUUID(uuid)
        ));
    }

}
