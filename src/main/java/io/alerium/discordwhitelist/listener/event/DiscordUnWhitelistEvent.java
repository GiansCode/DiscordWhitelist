package io.alerium.discordwhitelist.listener.event;

import io.alerium.discordwhitelist.user.provider.wrapper.WhitelistUser;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public final class DiscordUnWhitelistEvent extends Event {

    private final WhitelistUser user;

    public DiscordUnWhitelistEvent(final WhitelistUser user) {
        this.user = user;
    }

    public WhitelistUser getWhitelistUser() {
        return this.user;
    }

    //

    private static final HandlerList HANDLER_LIST = new HandlerList();

    public static HandlerList getHandlerList() { return HANDLER_LIST; }

    @Override public @NotNull HandlerList getHandlers() {
        return HANDLER_LIST;
    }

}
