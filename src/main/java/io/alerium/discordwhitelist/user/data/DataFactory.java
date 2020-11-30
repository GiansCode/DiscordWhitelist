package io.alerium.discordwhitelist.user.data;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import io.alerium.discordwhitelist.user.provider.wrapper.WhitelistUser;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.UUID;

public interface DataFactory {

    WhitelistUser getUserByMinecraftUUID(final UUID uuid);

    WhitelistUser getUserByDiscordID(final long discordID);

    default HikariDataSource configureDataSource(final JavaPlugin plugin) {
        final HikariConfig config = new HikariConfig(
                plugin.getDataFolder() + "/hikari.properties"
        );

        return new HikariDataSource(config);
    }

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
