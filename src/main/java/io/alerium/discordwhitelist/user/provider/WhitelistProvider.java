package io.alerium.discordwhitelist.user.provider;

import io.alerium.discordwhitelist.discord.provider.DiscordProvider;
import io.alerium.discordwhitelist.user.WhitelistCache;
import io.alerium.discordwhitelist.user.provider.wrapper.WhitelistUser;
import net.dv8tion.jda.api.entities.Member;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.util.UUID;
import java.util.concurrent.ExecutionException;

/**
 * Exposes API Methods, and Provides them to our plugin
 */
public final class WhitelistProvider {

    private final WhitelistCache whitelistCache = new WhitelistCache();
    private final DiscordProvider discordProvider;

    public WhitelistProvider(final DiscordProvider discordProvider) {
        this.discordProvider = discordProvider;
    }

    /**
     * Returns the User's Whitelist Status
     *
     * @param uuid User's Minecraft UUID
     * @return boolean (true/false) of the user's status, or false if invalid user
     * @throws ExecutionException if the user could not be retrieved
     */
    public boolean isWhitelisted(final UUID uuid) throws ExecutionException {
        final WhitelistUser user = this.whitelistCache.getCache().get(new KeyProvider().of(uuid));
        if (user != null)
            return user.isWhitelisted();

        return false;
    }

    /**
     * Set's the User's Whitelist Status to the given value
     *
     * @param uuid   User's Minecraft UUID
     * @param status new status to be set
     * @throws ExecutionException if the user could not be retrieved
     */
    public void setWhitelisted(final UUID uuid, final boolean status) throws ExecutionException {
        final WhitelistUser user = this.whitelistCache.getCache().get(new KeyProvider().of(uuid));
        if (user == null)
            return;

        user.setWhitelistedStatus(status);
        // Update DB User
    }

    /**
     * Returns the player associated to the discord ID
     *
     * @param discordID User's Discord ID
     * @return The OfflinePlayer object associated to the ID or null
     * @throws ExecutionException if the user could not be retrieved
     */
    public OfflinePlayer getMinecraftAccount(final long discordID) throws ExecutionException {
        final WhitelistUser user = this.whitelistCache.getCache().get(new KeyProvider().of(discordID));
        if (user == null)
            return null;

        return Bukkit.getOfflinePlayer(user.getMinecraftUUID());
    }

    /**
     * Returns the user's associated discord id
     *
     * @param minecraftUUID User's Minecraft UUID
     * @return The associated Discord ID or 0 if not present
     * @throws ExecutionException if the user could not be retrieved
     */
    public long getDiscordID(final UUID minecraftUUID) throws ExecutionException {
        final WhitelistUser user = this.whitelistCache.getCache().get(new KeyProvider().of(minecraftUUID));
        if (user == null)
            return 0;

        return user.getDiscordID();
    }

    /**
     * Returns the associated Discord Account's Name
     *
     * @param minecraftUUID User's Minecraft UUID
     * @return The associated Discord account's name or null if not present
     * @throws ExecutionException if the user could not be retrieved
     */
    public String getDiscordName(final UUID minecraftUUID) throws ExecutionException {
        final WhitelistUser user = this.whitelistCache.getCache().get(new KeyProvider().of(minecraftUUID));
        if (user == null)
            return null;

        final long userID = user.getDiscordID();
        if (userID == 0)
            return null;

        final Member discordMember = this.discordProvider.getLinkedGuild().getMemberById(userID);
        if (discordMember == null)
            return null;

        return discordMember.getUser().getName();
    }

    /**
     * Returns the associated Discord Account's Discriminator
     *
     * @param minecraftUUID User's Minecraft UUID
     * @return The associated Discord account's discriminator or null if not present
     * @throws ExecutionException if the user could not be retrieved
     */
    public String getDiscordDiscriminator(final UUID minecraftUUID) throws ExecutionException {
        final WhitelistUser user = this.whitelistCache.getCache().get(new KeyProvider().of(minecraftUUID));
        if (user == null)
            return null;

        final long userID = user.getDiscordID();
        if (userID == 0)
            return null;

        final Member discordMember = this.discordProvider.getLinkedGuild().getMemberById(userID);
        if (discordMember == null)
            return null;

        return discordMember.getUser().getAsTag();
    }

    /**
     * Returns the time (as long) when the user got whitelisted
     *
     * @param minecraftUUID User's Minecraft UUID
     * @return The time the user was whitelisted or 0 if not present
     * @throws ExecutionException if the user could not be retrieved
     */
    public long getTimeWhitelisted(final UUID minecraftUUID) throws ExecutionException {
        final WhitelistUser user = this.whitelistCache.getCache().get(new KeyProvider().of(minecraftUUID));
        if (user == null)
            return 0;

        return user.getTimeWhitelisted();
    }

}
