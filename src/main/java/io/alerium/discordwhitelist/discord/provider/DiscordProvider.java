package io.alerium.discordwhitelist.discord.provider;

import io.alerium.discordwhitelist.WhitelistPlugin;
import io.alerium.discordwhitelist.discord.command.DiscordWhitelistCommand;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;
import org.bukkit.configuration.file.FileConfiguration;

import javax.security.auth.login.LoginException;
import java.util.logging.Level;

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
        final FileConfiguration config = plugin.getConfig();
        JDA jdaProvider = null;

        try {
            jdaProvider = JDABuilder.createDefault(config.getString("settings.token"))
                    .setStatus(OnlineStatus.ONLINE)
                    .build().awaitReady();
        } catch (final LoginException | InterruptedException ex) {
            plugin.getLogger().log(Level.WARNING, "Discord bot was unable to start! Please verify the bot token is correct.");
            plugin.getPluginLoader().disablePlugin(plugin);
        }

        if (jdaProvider == null)
            throw new RuntimeException("The JDA was null, failed to continue initialization! (Please ensure valid Bot Token)");

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
