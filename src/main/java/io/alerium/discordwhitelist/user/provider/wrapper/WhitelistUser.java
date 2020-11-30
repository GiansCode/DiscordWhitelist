package io.alerium.discordwhitelist.user.provider.wrapper;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;

import java.util.UUID;

public final class WhitelistUser {

    private UUID minecraftUUID;

    private long discordID;
    private String discordDiscrim;
    private String discordName;

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

        if (status)
            this.whitelistedTime = System.currentTimeMillis();
    }

    public UUID getMinecraftUUID() {
        return this.minecraftUUID;
    }

    public void setWhitelistedTime(final long time) {
        this.whitelistedTime = time;
    }

    public void setMinecraftUUID(final UUID uuid) {
        this.minecraftUUID = uuid;
    }

    public long getDiscordID() {
        return this.discordID;
    }

    public void setDiscordID(final long id, final Guild guild) {
        this.discordID = id;

        final Member member = guild.getMemberById(id);

        if (member == null) return;
        this.discordName = member.getUser().getName();
        this.discordDiscrim = member.getUser().getAsTag();
    }

    public String getDiscordDiscriminator() {
        return this.discordDiscrim;
    }

    public String getDiscordName() {
        return this.discordName;
    }

    public long getTimeWhitelisted() {
        return this.whitelistedTime;
    }

}
