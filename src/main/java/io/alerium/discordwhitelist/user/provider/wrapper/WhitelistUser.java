package io.alerium.discordwhitelist.user.provider.wrapper;

import java.sql.Time;
import java.util.UUID;

public final class WhitelistUser {

    private final UUID minecraftUUID;

    private long discordID;

    private boolean whitelistedStatus;
    private long whitelistedTime;


    public WhitelistUser(final UUID uuid) {
        this.minecraftUUID = uuid;
    }

    public boolean isWhitelisted() {
        return this.whitelistedStatus;
    }

    public void setWhitelistedStatus(final boolean status) {
        this.whitelistedStatus = status;
    }

    public UUID getMinecraftUUID() {
        return this.minecraftUUID;
    }

    public long getDiscordID() {
        return this.discordID;
    }

    public long getTimeWhitelisted() {
        return this.whitelistedTime;
    }

}
