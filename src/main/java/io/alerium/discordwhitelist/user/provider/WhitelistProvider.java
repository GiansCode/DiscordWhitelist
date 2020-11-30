package io.alerium.discordwhitelist.user.provider;

import io.alerium.discordwhitelist.Placeholders;
import io.alerium.discordwhitelist.WhitelistPlugin;
import io.alerium.discordwhitelist.discord.provider.DiscordProvider;
import io.alerium.discordwhitelist.user.WhitelistCache;
import io.alerium.discordwhitelist.user.data.DatabaseProvider;
import io.alerium.discordwhitelist.user.provider.wrapper.WhitelistUser;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.util.UUID;
import java.util.concurrent.ExecutionException;

/**
 * Exposes API Methods, and Provides them to our plugin
 */
public final class WhitelistProvider {

    private final DatabaseProvider databaseProvider;
    private final WhitelistCache whitelistCache;

    public WhitelistProvider(final WhitelistPlugin plugin, final DiscordProvider discordProvider) {
        this.databaseProvider = new DatabaseProvider(plugin, discordProvider);
        this.whitelistCache = new WhitelistCache(databaseProvider);
    }

    /**
     * Updates the user to database
     *
     * @param user Desired user to be updated
     */
    public void updateDatabaseUser(final WhitelistUser user) {
        this.databaseProvider.setUser(user);
        this.whitelistCache.getCache().put(new KeyProvider().of(user.getMinecraftUUID()), user);
        updateUserInPlaceholderCache(user);
    }

    /**
     * Returns the User's Whitelist Status
     *
     * @param uuid User's Minecraft UUID
     * @return boolean (true/false) of the user's status, or false if invalid user
     */
    public boolean isWhitelisted(final UUID uuid) {
        try {
            final WhitelistUser user = this.whitelistCache.getCache().get(new KeyProvider().of(uuid));
            if (user != null)
                return user.isWhitelisted();
        } catch (final ExecutionException ex) {
            ex.printStackTrace();
        }

        return false;
    }

    /**
     * Set's the User's Whitelist Status to the given value
     *
     * @param uuid   User's Minecraft UUID
     * @param status new status to be set
     */
    public void setWhitelisted(final UUID uuid, final boolean status) {
        try {
            final KeyProvider key = new KeyProvider().of(uuid);
            final WhitelistUser user = this.whitelistCache.getCache().get(key);
            if (user == null)
                return;

            user.setMinecraftUUID(uuid);
            user.setWhitelistedStatus(status);

            databaseProvider.setUser(user);
            this.whitelistCache.getCache().put(key, user);
            updateUserInPlaceholderCache(user);
        } catch (final ExecutionException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Returns the player associated to the discord ID
     *
     * @param discordID User's Discord ID
     * @return The OfflinePlayer object associated to the ID or null
     */
    public OfflinePlayer getMinecraftAccount(final long discordID) {
        try {
            final WhitelistUser user = this.whitelistCache.getCache().get(new KeyProvider().of(discordID));
            if (user != null)
                return Bukkit.getOfflinePlayer(user.getMinecraftUUID());
        } catch (final ExecutionException ex) {
            ex.printStackTrace();
        }

        return null;
    }

    /**
     * Returns the user's associated discord id
     *
     * @param minecraftUUID User's Minecraft UUID
     * @return The associated Discord ID or 0 if not present
     */
    public long getDiscordID(final UUID minecraftUUID) {
        try {
            final WhitelistUser user = this.whitelistCache.getCache().get(new KeyProvider().of(minecraftUUID));
            if (user != null)
                return user.getDiscordID();
        } catch (final ExecutionException ex) {
            ex.printStackTrace();
        }

        return 0;
    }

    /**
     * Returns the associated Discord Account's Name
     *
     * @param minecraftUUID User's Minecraft UUID
     * @return The associated Discord account's name or null if not present
     */
    public String getDiscordName(final UUID minecraftUUID) {
        try {
            final WhitelistUser user = this.whitelistCache.getCache().get(new KeyProvider().of(minecraftUUID));
            if (user == null)
                return null;

            final long userID = user.getDiscordID();
            if (userID == 0)
                return null;

            return user.getDiscordName();
        } catch (final ExecutionException ex) {
            ex.printStackTrace();
        }

        return null;
    }

    /**
     * Returns the associated Discord Account's Discriminator
     *
     * @param minecraftUUID User's Minecraft UUID
     * @return The associated Discord account's discriminator or null if not present
     */
    public String getDiscordDiscriminator(final UUID minecraftUUID) {
        try {
            final WhitelistUser user = this.whitelistCache.getCache().get(new KeyProvider().of(minecraftUUID));
            if (user == null)
                return null;

            final long userID = user.getDiscordID();
            if (userID == 0)
                return null;

            return user.getDiscordDiscriminator();
        } catch (final ExecutionException ex) {
            ex.printStackTrace();
        }

        return null;
    }

    /**
     * Returns the time (as long) when the user got whitelisted
     *
     * @param minecraftUUID User's Minecraft UUID
     * @return The time the user was whitelisted or 0 if not present
     */
    public long getTimeWhitelisted(final UUID minecraftUUID) {
        try {
            final WhitelistUser user = this.whitelistCache.getCache().get(new KeyProvider().of(minecraftUUID));
            if (user != null)
                return user.getTimeWhitelisted();
        } catch (final ExecutionException ex) {
            ex.printStackTrace();
        }

        return 0;
    }

    /**
     * Returns the WhitelistUser object for given user, or null
     *
     * @param minecraftUUID User's Minecraft UUID
     * @return The WhitelistUser object for specified user or null
     */
    public WhitelistUser getWhitelistUserByUUID(final UUID minecraftUUID) {
        try {
            final WhitelistUser user = this.whitelistCache.getCache().get(new KeyProvider().of(minecraftUUID));
            if (user != null)
                return user;
        } catch (final ExecutionException ex) {
            ex.printStackTrace();
        }

        return null;
    }

    private void updateUserInPlaceholderCache(final WhitelistUser user) {
        Placeholders.updateUser(user);
    }

}
