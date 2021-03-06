package io.alerium.discordwhitelist.user.provider;

import java.util.UUID;

public final class KeyProvider {

    private UUID minecraftUUID;
    private long discordID;

    public KeyProvider of(final UUID minecraftUUID) {
        this.minecraftUUID = minecraftUUID;

        return this;
    }

    public KeyProvider of(final long discordID) {
        this.discordID = discordID;

        return this;
    }

    public UUID getMinecraftUUID() {
        return this.minecraftUUID;
    }

    public long getDiscordID() {
        return this.discordID;
    }
}