package io.alerium.discordwhitelist.discord.command;

import io.alerium.discordwhitelist.WhitelistPlugin;
import io.alerium.discordwhitelist.discord.provider.DiscordProvider;
import io.alerium.discordwhitelist.listener.event.DiscordWhitelistEvent;
import io.alerium.discordwhitelist.user.provider.KeyProvider;
import io.alerium.discordwhitelist.user.provider.wrapper.WhitelistUser;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;
import rest.faucet.task.Tasks;

import java.util.Arrays;
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
        Tasks.async(task -> {
            final TextChannel channel = event.getChannel();
            if (!channel.getId().equals(discordProvider.getLinkedChannel().getId())) {
                return;
            }

            final Guild guild = event.getGuild();
            final Message eventMessage = event.getMessage();
            final String content = eventMessage.getContentRaw();
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
                sendAndDeleteAfter(eventMessage, msg, channel);
                return;
            }

            final String enteredCode = arguments[1];
            final UUID minecraftUserIdentifier = this.plugin.getRequestCache().getUUIDAssociatedTo(enteredCode);

            if (!isValidDiscordAccount(member.getIdLong(), minecraftUserIdentifier)) {
                final Message msg = new MessageBuilder(plugin.getConfig().getString("messages.accountAlreadyLinked")).build();
                sendAndDeleteAfter(eventMessage, msg, channel);
                return;
            }

            if (minecraftUserIdentifier == null) {
                final Message msg = new MessageBuilder(plugin.getConfig().getString("messages.invalidCode")).build();
                sendAndDeleteAfter(eventMessage, msg, channel);
                return;
            }

            WhitelistUser user = plugin.getWhitelistProvider().getWhitelistUserByKeyProvider(new KeyProvider().of(minecraftUserIdentifier));
            if (user == null) {
                user = new WhitelistUser(minecraftUserIdentifier);
            }

            user.setMinecraftUUID(minecraftUserIdentifier);
            user.setDiscordID(member.getIdLong());
            user.setWhitelistedStatus(true);

            plugin.getWhitelistProvider().updateCachedUser(user);
            plugin.getWhitelistProvider().setWhitelisted(minecraftUserIdentifier, true);
            plugin.getRequestCache().invalidateUserCodes(minecraftUserIdentifier);

            final WhitelistUser finalUser = user;
            Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () ->
                    Bukkit.getServer().getPluginManager().callEvent(new DiscordWhitelistEvent(finalUser))
            , 0);

            final Message msg = new MessageBuilder(plugin.getConfig().getString("messages.successfullyWhitelisted")).build();
            sendAndDeleteAfter(eventMessage, msg, channel);
        });
    }

    private boolean isValidDiscordAccount(final long id, final UUID uuid) {
        final WhitelistUser user = plugin.getWhitelistProvider().getWhitelistUserByKeyProvider(new KeyProvider().of(id));
        if (user == null)
            return true;

        final UUID minecraftUUID = user.getMinecraftUUID();
        if (minecraftUUID == null)
            return true;

        if (uuid == null)
            return false;

        return minecraftUUID.toString().equalsIgnoreCase(uuid.toString());
    }

    private void sendAndDeleteAfter(final Message eventMessage, final Message message, final TextChannel channel) {
        final Message msg = channel.sendMessage(message).complete();

        new BukkitRunnable() {
            @Override
            public void run() {
                channel.deleteMessagesByIds(Arrays.asList(eventMessage.getId(), msg.getId())).queue();
            }
        }.runTaskLater(plugin, deletionTimer);
    }

}
