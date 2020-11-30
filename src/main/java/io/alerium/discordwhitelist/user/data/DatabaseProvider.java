package io.alerium.discordwhitelist.user.data;

import com.zaxxer.hikari.HikariDataSource;
import io.alerium.discordwhitelist.WhitelistPlugin;
import io.alerium.discordwhitelist.discord.provider.DiscordProvider;
import io.alerium.discordwhitelist.user.provider.wrapper.WhitelistUser;
import io.alerium.discordwhitelist.util.FileUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;
import java.util.UUID;
import java.util.logging.Level;

public final class DatabaseProvider implements DataFactory {

    private final DiscordProvider discordProvider;
    private final WhitelistPlugin plugin;
    private final HikariDataSource dataSource;

    public DatabaseProvider(final WhitelistPlugin plugin, final DiscordProvider discordProvider) {
        this.discordProvider = discordProvider;
        FileUtils.saveResources(plugin,
                "hikari.properties"
        );

        this.dataSource = configureDataSource(plugin);
        this.plugin = plugin;

        setupDatabase();
    }

    private Connection getConnection() {
        java.sql.Connection connection = null;

        try {
            connection = dataSource.getConnection();
        } catch (final SQLException ex) {
            plugin.getLogger().log(Level.WARNING, "Failed to establish the database connection!");
        }

        return connection;
    }

    private void setupDatabase() {
        final Connection connection = getConnection();

        if (connection == null) {
            plugin.getLogger().log(Level.WARNING, "Failed to setup Database Table, as the connection could not be acquired!");
            plugin.getServer().getPluginManager().disablePlugin(plugin);
            return;
        }

        try {
            final PreparedStatement tableCreationStatement = connection.prepareStatement(
                    "CREATE TABLE IF NOT EXISTS `" + getDatabaseName() + "`.`users` (" +
                            "`uuid` CHAR(36) NOT NULL, " +
                            "`discordID` LONG NULL, " +
                            "`whitelistStatus` BOOLEAN NOT NULL, " +
                            "`whitelistTime` LONG NULL, " +
                            "PRIMARY KEY(`uuid`));"
            );

            tableCreationStatement.execute();
            connection.close();
        } catch (final SQLException ex) {
            plugin.getLogger().log(Level.WARNING, "Failed to create 'users' table. Please Check your database details!", ex.getMessage());
            plugin.getPluginLoader().disablePlugin(plugin);
        }
    }

    private String getDatabaseName() {
        final Properties properties = readPropertiesFile(plugin.getDataFolder() + "/hikari.properties");
        return properties.getProperty("dataSource.databaseName");
    }

    public void setUser(final WhitelistUser user) {
        final Connection connection = getConnection();

        if (connection == null) return;

        try {
            final PreparedStatement statement = connection.prepareStatement(
                    "INSERT INTO `" + getDatabaseName() + "`.`users` (uuid, discordID, whitelistStatus, whitelistTime) VALUES (?, ?, ?, ?) ON DUPLICATE KEY UPDATE discordID=?, whitelistStatus=?, whitelistTime=?;"
            );

            statement.setString(1, user.getMinecraftUUID().toString());
            statement.setLong(2, user.getDiscordID());
            statement.setBoolean(3, user.isWhitelisted());
            statement.setLong(4, user.getTimeWhitelisted());

            statement.setLong(5, user.getDiscordID());
            statement.setBoolean(6, user.isWhitelisted());
            statement.setLong(7, user.getTimeWhitelisted());

            statement.execute();
            connection.close();
        } catch (final SQLException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public WhitelistUser getUserByMinecraftUUID(final UUID uuid) {
        final Connection connection = getConnection();

        if (connection == null) return null;

        try {
            final PreparedStatement statement = connection.prepareStatement(
                    "SELECT * FROM `" + getDatabaseName() + "`.`users` WHERE uuid=?;"
            );

            statement.setString(1, uuid.toString());
            final ResultSet result = statement.executeQuery();

            if (result != null && result.next()) {
                final WhitelistUser user = new WhitelistUser(UUID.fromString(result.getString("uuid")));

                user.setDiscordID(result.getLong("discordID"), this.discordProvider.getLinkedGuild());
                user.setWhitelistedStatus(result.getBoolean("whitelistStatus"));
                user.setWhitelistedTime(result.getLong("whitelistTime"));

                return user;
            }

            connection.close();
        } catch (final SQLException ex) {
            ex.printStackTrace();
        }

        return null;
    }

    @Override
    public WhitelistUser getUserByDiscordID(final long discordID) {
        final Connection connection = getConnection();

        if (connection == null) return null;

        try {
            final PreparedStatement statement = connection.prepareStatement(
                    "SELECT * FROM `" + getDatabaseName() + "`.`users` WHERE discordID=?;"
            );

            statement.setLong(1, discordID);
            final ResultSet result = statement.executeQuery();

            if (result != null && result.next()) {
                final WhitelistUser user = new WhitelistUser(UUID.fromString(result.getString("uuid")));

                user.setDiscordID(result.getLong("discordID"), this.discordProvider.getLinkedGuild());
                user.setWhitelistedStatus(result.getBoolean("whitelistStatus"));
                user.setWhitelistedTime(result.getLong("whitelistTime"));

                return user;
            }

            connection.close();
        } catch (final SQLException ex) {
            ex.printStackTrace();
        }
        return null;
    }

}
