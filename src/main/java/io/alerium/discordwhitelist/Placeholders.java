package io.alerium.discordwhitelist;

import io.alerium.discordwhitelist.user.provider.KeyProvider;
import io.alerium.discordwhitelist.user.provider.WhitelistProvider;
import io.alerium.discordwhitelist.user.provider.wrapper.WhitelistUser;
import io.alerium.discordwhitelist.util.ReplaceUtils;
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
    private final String timeFormat;

    Placeholders(final WhitelistPlugin plugin, final WhitelistProvider provider) {
        this.whitelistProvider = provider;

        this.timeFormat = plugin.getConfig().getString("settings.timeFormat");
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
            user = whitelistProvider.getWhitelistUserByKeyProvider(new KeyProvider().of(uuid));

            updateUser(user);
        }

        if (user == null) return null;
        if (user.getDiscordName() == null) {

            user.getDiscordName();
            user.getDiscordDiscriminator();

            updateUser(user);
        }

        switch (params.toLowerCase()) {
            case "whitelist-status":
                return formatNullable(formatBoolean(user.isWhitelisted()));
            case "whitelist-time":
                return formatNullable(getFormattedTime(new Date(user.getTimeWhitelisted())));
            case "discord-id":
                return formatNullable(user.getDiscordID() == 0 ? "Invalid Account" : user.getDiscordID());
            case "discord-name":
                return formatNullable(user.getDiscordName());
            case "discord-discrim":
                return formatNullable(user.getDiscordDiscriminator());
        }

        return null;
    }

    private String getFormattedTime(final Date date) {
        final String dateString = date.toString();
        final String[] dateComponents = dateString.split(" ");

        return ReplaceUtils.replaceString(
                timeFormat,
                "{week-day}", dateComponents[0],
                "{month}", dateComponents[1],
                "{day}", dateComponents[2],
                "{time}", dateComponents[3],
                "{timezone}", dateComponents[4],
                "{year}", dateComponents[5]
        );
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
