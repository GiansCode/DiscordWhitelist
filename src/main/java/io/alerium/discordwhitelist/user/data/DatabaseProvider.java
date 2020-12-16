package io.alerium.discordwhitelist.user.data;

import co.aikar.idb.*;
import io.alerium.discordwhitelist.WhitelistPlugin;
import io.alerium.discordwhitelist.user.provider.wrapper.WhitelistUser;
import io.alerium.discordwhitelist.util.FileUtils;

import java.sql.SQLException;
import java.util.Properties;
import java.util.UUID;

public final class DatabaseProvider implements DataFactory {

    private final String databaseName;

    public DatabaseProvider(final WhitelistPlugin plugin) {
        FileUtils.saveResources(plugin,
                "hikari.properties"
        );

        final Properties hikariProperties = readPropertiesFile(plugin.getDataFolder() + "/hikari.properties");
        this.databaseName = hikariProperties.getProperty("dataSource.databaseName");

        final DatabaseOptions options = DatabaseOptions.builder().mysql(
                hikariProperties.getProperty("dataSource.user"),
                hikariProperties.getProperty("dataSource.password"),
                databaseName,
                hikariProperties.getProperty("dataSource.server") + ":" + hikariProperties.getProperty("dataSource.port")
        ).build();
        final Database database = PooledDatabaseOptions.builder().options(options).createHikariDatabase();
        DB.setGlobalDatabase(database);

        setupDatabase();
    }

    private void setupDatabase() {
        DB.executeUpdateAsync(
                "CREATE TABLE IF NOT EXISTS `" + databaseName + "`.`users` (" +
                        "`uuid` CHAR(36) NOT NULL, " +
                        "`discordID` LONG NULL, " +
                        "`whitelistStatus` BOOLEAN NOT NULL, " +
                        "`whitelistTime` LONG NULL, " +
                        "PRIMARY KEY(`uuid`));"
        );
    }

    public void setUser(final WhitelistUser user) {
        DB.executeUpdateAsync(
                "INSERT INTO `" + databaseName + "`.`users` (uuid, discordID, whitelistStatus, whitelistTime) VALUES (?, ?, ?, ?) ON DUPLICATE KEY UPDATE discordID=?, whitelistStatus=?, whitelistTime=?;",
                user.getMinecraftUUID().toString(), user.getDiscordID(), user.isWhitelisted(), user.getTimeWhitelisted(),
                user.getDiscordID(), user.isWhitelisted(), user.getTimeWhitelisted()
        );
    }

    @Override
    public WhitelistUser getUserByMinecraftUUID(final UUID uuid) {
        try {
            final DbRow row = DB.getFirstRow(
                    "SELECT * FROM `" + databaseName + "`.`users` WHERE uuid=?;",
                    uuid.toString()
            );

            if (row != null && !row.isEmpty()) {
                final WhitelistUser user = new WhitelistUser(UUID.fromString(row.getString("uuid")));

                user.setDiscordID(Long.valueOf(row.getString("discordID")));
                user.setWhitelistedStatus(row.get("whitelistStatus"));
                user.setWhitelistedTime(Long.valueOf(row.getString("whitelistTime")));

                return user;
            }
        } catch (final SQLException ignored) {
        }

        return null;
    }

    @Override
    public WhitelistUser getUserByDiscordID(final long discordID) {
        try {
            final DbRow row = DB.getFirstRow(
                    "SELECT * FROM `" + databaseName + "`.`users` WHERE discordID=?;",
                    discordID
            );

            if (row != null && !row.isEmpty()) {
                final WhitelistUser user = new WhitelistUser(UUID.fromString(row.getString("uuid")));

                user.setDiscordID(discordID);
                user.setWhitelistedStatus(row.get("whitelistStatus"));
                user.setWhitelistedTime(Long.valueOf(row.getString("whitelistTime")));

                return user;
            }

        } catch (final SQLException ignored) {
        }

        return null;
    }

}
