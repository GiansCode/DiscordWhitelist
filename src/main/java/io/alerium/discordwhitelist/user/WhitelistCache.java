package io.alerium.discordwhitelist.user;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import io.alerium.discordwhitelist.user.provider.KeyProvider;
import io.alerium.discordwhitelist.user.provider.wrapper.WhitelistUser;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

public final class WhitelistCache {

    private final LoadingCache<KeyProvider, WhitelistUser> cache;

    public WhitelistCache() {
        this.cache = CacheBuilder.newBuilder()
                .expireAfterAccess(5, TimeUnit.MINUTES)
                .build(
                        new CacheLoader<KeyProvider, WhitelistUser>() {
                            @Override
                            public WhitelistUser load(final KeyProvider key) throws Exception {
                                return null;
                            }
                        }
                );
    }

    public LoadingCache<KeyProvider, WhitelistUser> getCache() { return this.cache; }

}
