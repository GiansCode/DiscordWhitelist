package io.alerium.discordwhitelist.user.provider;

import java.util.UUID;

public final class KeyProvider {

    private UUID minecraftUUID;
    private long discordID;

    KeyProvider of(final UUID minecraftUUID) {
        this.minecraftUUID = minecraftUUID;

        return this;
    }

    KeyProvider of(final long discordID) {
        this.discordID = discordID;

        return this;
    }

}