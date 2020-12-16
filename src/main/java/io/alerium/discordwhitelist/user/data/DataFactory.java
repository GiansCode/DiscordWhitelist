package io.alerium.discordwhitelist.user.data;

import io.alerium.discordwhitelist.user.provider.wrapper.WhitelistUser;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.UUID;

public interface DataFactory {

    WhitelistUser getUserByMinecraftUUID(final UUID uuid);

    WhitelistUser getUserByDiscordID(final long discordID);

    default Properties readPropertiesFile(final String fileName) {
        FileInputStream inputStream = null;
        final Properties properties = new Properties();

        try {
            try {
                inputStream = new FileInputStream(fileName);
                properties.load(inputStream);
            } catch (final IOException ex) {
                ex.printStackTrace();
            }
        } finally {
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
            } catch (final IOException ex) {
                ex.printStackTrace();
            }
        }

        return properties;
    }

}
