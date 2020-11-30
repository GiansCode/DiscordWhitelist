package io.alerium.discordwhitelist.user;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import io.alerium.discordwhitelist.user.data.DataFactory;
import io.alerium.discordwhitelist.user.provider.KeyProvider;
import io.alerium.discordwhitelist.user.provider.wrapper.WhitelistUser;

import java.util.concurrent.TimeUnit;

public final class WhitelistCache {

    private final LoadingCache<KeyProvider, WhitelistUser> cache;

    public WhitelistCache(final DataFactory dataFactory) {
        this.cache = CacheBuilder.newBuilder()
                .expireAfterAccess(3, TimeUnit.HOURS)
                .build(
                        new CacheLoader<KeyProvider, WhitelistUser>() {
                            @Override
                            public WhitelistUser load(final KeyProvider keyProvider) throws Exception {
                                WhitelistUser user = null;

                                if (keyProvider.getMinecraftUUID() != null) {
                                    user = dataFactory.getUserByMinecraftUUID(keyProvider.getMinecraftUUID());
                                }

                                if (keyProvider.getDiscordID() != 0 && user == null) {
                                    user = dataFactory.getUserByDiscordID(keyProvider.getDiscordID());
                                }

                                // Assign uuid upon retrieval
                                return user != null ? user : new WhitelistUser(null);
                            }
                        }
                );
    }

    public LoadingCache<KeyProvider, WhitelistUser> getCache() {
        return this.cache;
    }

}
