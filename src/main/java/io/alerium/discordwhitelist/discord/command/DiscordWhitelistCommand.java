package io.alerium.discordwhitelist.discord.command;

import io.alerium.discordwhitelist.WhitelistPlugin;
import io.alerium.discordwhitelist.discord.provider.DiscordProvider;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Collections;
import java.util.UUID;

public final class DiscordWhitelistCommand extends ListenerAdapter {

    private final DiscordProvider discordProvider;
    private final WhitelistPlugin plugin;

    private final long deletionTimer;

    public DiscordWhitelistCommand(final WhitelistPlugin plugin, final DiscordProvider discordProvider) {
        this.discordProvider = discordProvider;
        this.plugin = plugin;

        this.deletionTimer = plugin.getConfig().getLong("settings.messageDeletion") * 20;
    }

    @Override
    public void onGuildMessageReceived(final GuildMessageReceivedEvent event) {
        final TextChannel channel = event.getChannel();
        final Guild guild = event.getGuild();
        final String content = event.getMessage().getContentRaw();
        if (!content.startsWith(discordProvider.getCommandPrefix() + "whitelist") && !content.startsWith(guild.getSelfMember().getAsMention())) {
            return;
        }

        final User author = event.getAuthor();
        if (author.isBot()) return;

        final Member member = event.getMember();
        if (member == null) {
            return;
        }
        final String[] arguments = content.split(" ");

        if (arguments.length < 2) {
            final Message msg = new MessageBuilder(plugin.getConfig().getString("messages.invalidArguments")).build();

            sendAndDeleteAfter(msg, channel);
            return;
        }

        final String enteredCode = arguments[1];
        final UUID minecraftUserIdentifier = this.plugin.getRequestCache().getUUIDAssociatedTo(enteredCode);

        if (minecraftUserIdentifier == null) {
            final Message msg = new MessageBuilder(plugin.getConfig().getString("messages.invalidCode")).build();

            sendAndDeleteAfter(msg, channel);
            return;
        }


    }

    private void sendAndDeleteAfter(final Message message, final TextChannel channel) {
        channel.sendMessage(message).queue();

        new BukkitRunnable() {
            @Override
            public void run() {
                channel.deleteMessages(Collections.singletonList(message)).queue();
            }
        }.runTaskLater(plugin, deletionTimer);
    }

}