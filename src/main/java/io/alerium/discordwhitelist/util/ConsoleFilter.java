package io.alerium.discordwhitelist.util;

import io.alerium.discordwhitelist.WhitelistPlugin;

import java.util.logging.Filter;
import java.util.logging.LogRecord;
import java.util.regex.Pattern;

public final class ConsoleFilter implements Filter {

    private static final Pattern PATTERN = Pattern.compile("name=([a-zA-Z0-9_]{3,16})");
    private final WhitelistPlugin plugin;

    public ConsoleFilter(final WhitelistPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean isLoggable(final LogRecord record) {
        if (record == null || record.getMessage() == null) {
            return true;
        }

        final String message = record.getMessage();
        System.out.println(message);
        if (message.contains(plugin.getConfig().getString("settings.consoleLogFilter"))) {
            final String user = PATTERN.matcher(message).group();

            record.setMessage("User '" + user + "' tried to join, but is not whitelisted!");
        }

        return true;
    }
}
