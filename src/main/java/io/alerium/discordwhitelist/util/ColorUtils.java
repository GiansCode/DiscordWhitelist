package io.alerium.discordwhitelist.util;

import me.mattstudios.mfmsg.bukkit.BukkitMessage;

public final class ColorUtils {

    private static final BukkitMessage MESSAGE = BukkitMessage.create();

    public static String colorize(final String msg) {
        return MESSAGE.parse(msg).toString();
    }

}
