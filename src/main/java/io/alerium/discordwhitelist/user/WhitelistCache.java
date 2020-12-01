package io.alerium.discordwhitelist.user;

import io.alerium.discordwhitelist.user.data.DatabaseProvider;
import io.alerium.discordwhitelist.user.provider.KeyProvider;
import io.alerium.discordwhitelist.user.provider.wrapper.WhitelistUser;

import java.util.HashMap;
import java.util.Map;

public final class WhitelistCache {

    private final Map<KeyProvider, WhitelistUser> cache = new HashMap<>();
    private final DatabaseProvider provider;

    public WhitelistCache(final DatabaseProvider provider) {
        this.provider = provider;

    }

    public WhitelistUser getWhitelistUser(final KeyProvider key) {
        WhitelistUser user = null;

        for (final KeyProvider userKey : this.cache.keySet()) {
            if (key.getMinecraftUUID() != null && userKey.getMinecraftUUID() != null) {
                if (key.getMinecraftUUID() == userKey.getMinecraftUUID()) {
                    user = this.cache.get(userKey);
                    break;
                }
            }

            if (key.getDiscordID() != 0 && userKey.getDiscordID() != 0) {
                if (key.getDiscordID() == userKey.getDiscordID()) {
                    user = this.cache.get(userKey);
                    break;
                }
            }

        }

        if (user == null) {
            if (key.getMinecraftUUID() != null) {
                user = provider.getUserByMinecraftUUID(key.getMinecraftUUID());
            } else if (key.getDiscordID() != 0) {
                user = provider.getUserByDiscordID(key.getDiscordID());
            }
        }

        return user != null ? user : new WhitelistUser(null);
    }

    public void updateWhitelistUser(final WhitelistUser user) {
        KeyProvider key = new KeyProvider().of(user.getMinecraftUUID());

        if (user.getDiscordID() != 0) {
            key.of(user.getDiscordID());
        }

        this.cache.put(key, user);
        this.provider.setUser(user);
    }

}
