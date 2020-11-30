package io.alerium.discordwhitelist;

import io.alerium.discordwhitelist.user.provider.WhitelistProvider;
import io.alerium.discordwhitelist.user.provider.wrapper.WhitelistUser;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class Placeholders extends PlaceholderExpansion {

    private static final Map<UUID, WhitelistUser> PLACEHOLDER_USER_CACHE = new HashMap<>();
    private final WhitelistProvider whitelistProvider;

    Placeholders(final WhitelistProvider provider) {
        this.whitelistProvider = provider;
    }

    @Override
    public @NotNull String getVersion() {
        return "0.0.1-Alpha";
    }

    @Override
    public @NotNull String getAuthor() {
        return "Frcsty";
    }

    @Override
    public @NotNull String getIdentifier() {
        return "discord-whitelist";
    }

    @Override
    public String onPlaceholderRequest(final Player player, @NotNull final String params) {
        final UUID uuid = player.getUniqueId();

        WhitelistUser user;
        if (PLACEHOLDER_USER_CACHE.get(uuid) != null) {
            user = PLACEHOLDER_USER_CACHE.get(uuid);
        } else {
            user = whitelistProvider.getWhitelistUserByUUID(uuid);

            PLACEHOLDER_USER_CACHE.put(uuid, user);
        }

        if (user == null) {
            return null;
        }

        switch (params.toLowerCase()) {
            case "whitelist-status":
                return formatNullable(formatBoolean(user.isWhitelisted()));
            case "whitelist-time":
                return formatNullable(new Date(user.getTimeWhitelisted()));
            case "discord-id":
                return formatNullable(user.getDiscordID());
            case "discord-name":
                return formatNullable(user.getDiscordName());
            case "discord-discrim":
                return formatNullable(user.getDiscordDiscriminator());
        }

        return null;
    }

    private String formatNullable(final Object object) {
        return object == null ? "N/A" : object.toString();
    }

    private String formatBoolean(final boolean bool) {
        return bool ? "True" : "False";
    }

    public static void updateUser(final WhitelistUser user) {
        PLACEHOLDER_USER_CACHE.put(user.getMinecraftUUID(), user);
    }

}
