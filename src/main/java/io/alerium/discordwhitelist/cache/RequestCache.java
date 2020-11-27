package io.alerium.discordwhitelist.cache;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import io.alerium.discordwhitelist.WhitelistPlugin;

import java.util.UUID;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;

public final class RequestCache {

    private final Cache<String, UUID> cache;

    public RequestCache(final WhitelistPlugin plugin) {
        this.cache = CacheBuilder.newBuilder()
                .expireAfterWrite(plugin.getConfig().getInt("settings.cacheExpiry"), TimeUnit.MINUTES)
                .build();
    }

    /**
     * Adds a cache for a specific player to the cache
     *
     * @param code              The player specific code
     * @param playerIdentifier  Specified player's {@link UUID}
     */
    public void addCodeToCache(final String code, final UUID playerIdentifier) {
        this.cache.put(code, playerIdentifier);
    }

    /**
     * Returns the {@link UUID} associated to the given code,
     * or null if there is none
     *
     * @param code  Specified code in interest
     * @return  Returns a {@link UUID} associated to specified code or null
     */
    public UUID getUUIDAssociatedTo(final String code) {
        return this.cache.getIfPresent(code);
    }

    /**
     * Invalidates desired code if it exists within the Cache
     *
     * @param code  Code specified to be invalidated
     */
    public void invalidateCode(final String code) {
        this.cache.invalidate(code);
    }

    /**
     * Invalidates all residual codes for a specified user
     * Ensures cleanup of unused codes to prevent any mess-ups
     *
     * @param playerIdentifier Specified player's {@link UUID}
     */
    public void invalidateUserCodes(final UUID playerIdentifier) {
        final ConcurrentMap<String, UUID> cacheAsMap = this.cache.asMap();

        for (final String code : cacheAsMap.keySet()) {
            final UUID identifier = cacheAsMap.get(code);

            if (identifier == null) continue;
            if (identifier.equals(playerIdentifier)) {
                this.cache.invalidate(code);
            }
        }
    }

}
