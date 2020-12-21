package io.alerium.discordwhitelist.discord.provider;

import io.alerium.discordwhitelist.WhitelistPlugin;
import io.alerium.discordwhitelist.discord.command.DiscordWhitelistCommand;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;
import org.bukkit.configuration.file.FileConfiguration;

import javax.security.auth.login.LoginException;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class DiscordProvider {

    private final WhitelistPlugin plugin;

    private static Guild guild;
    private TextChannel channel;

    private String commandPrefix;

    public DiscordProvider(final WhitelistPlugin plugin) {
        this.plugin = plugin;
        plugin.saveDefaultConfig();
    }

    @SuppressWarnings("ConstantConditions")
    public void initiateBot() {
        final Logger logger = plugin.getLogger();
        final FileConfiguration config = plugin.getConfig();
        JDA jdaProvider = null;

        try {
            jdaProvider = JDABuilder.createDefault(config.getString("settings.token"))
                    .setStatus(OnlineStatus.valueOf(config.getString("settings.onlineStatus", "ONLINE").toUpperCase()))
                    .setActivity(Activity.of(Activity.ActivityType.valueOf(config.getString("settings.activity")), config.getString("settings.activityMessage")))
                    .build().awaitReady();
        } catch (final LoginException | InterruptedException ex) {
            logger.log(Level.SEVERE, "Discord bot was unable to start! Please verify the bot token is correct.");
            plugin.getPluginLoader().disablePlugin(plugin);
        }

        if (jdaProvider == null) {
            logger.log(Level.SEVERE, "The plugin failed to initialize due to: JDA Provider was null!");
            logger.log(Level.SEVERE, "(Ensure that the entered Bot Token is valid!)");
            plugin.getPluginLoader().disablePlugin(plugin);
            return;
        }

        guild = jdaProvider.getGuildById(config.getString("settings.guildId"));
        this.channel = jdaProvider.getTextChannelById(config.getString("settings.textChannelId"));

        this.commandPrefix = config.getString("settings.commandPrefix");

        jdaProvider.addEventListener(
                new DiscordWhitelistCommand(plugin, this)
        );
    }

    public static Guild getLinkedGuild() {
        return guild;
    }

    public TextChannel getLinkedChannel() {
        return this.channel;
    }

    public String getCommandPrefix() {
        return this.commandPrefix;
    }

}
