package io.alerium.discordwhitelist.user.provider.wrapper;

import io.alerium.discordwhitelist.discord.provider.DiscordProvider;
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

    public void setMinecraftUUID(final UUID uuid) {
        this.minecraftUUID = uuid;
    }

    public void setWhitelistedTime(final long time) {
        this.whitelistedTime = time;
    }

    public long getDiscordID() {
        return this.discordID;
    }

    public void setDiscordID(final long id) {
        this.discordID = id;

        final Member member = DiscordProvider.getLinkedGuild().getMemberById(id);

        if (member == null) return;
        this.discordName = member.getUser().getName();
        this.discordDiscrim = member.getUser().getAsTag();
    }

    public String getDiscordDiscriminator() {
        if (discordDiscrim == null) {
            final Member member = DiscordProvider.getLinkedGuild().getMemberById(discordID);

            if (member != null) {
                this.discordDiscrim = member.getUser().getAsTag();
            }
        }

        return this.discordDiscrim;
    }

    public String getDiscordName() {
        if (discordName == null) {
            final Member member = DiscordProvider.getLinkedGuild().getMemberById(discordID);

            if (member != null) {
                this.discordName = member.getUser().getName();
            }
        }

        return this.discordName;
    }

    public long getTimeWhitelisted() {
        return this.whitelistedTime;
    }

}
