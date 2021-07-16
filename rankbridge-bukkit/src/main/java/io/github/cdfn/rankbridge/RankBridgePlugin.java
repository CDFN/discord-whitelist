package io.github.cdfn.rankbridge;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import eu.okaeri.configs.ConfigManager;
import eu.okaeri.configs.hjson.HjsonConfigurer;
import io.github.cdfn.rankbridge.config.PluginConfiguration;
import io.github.cdfn.rankbridge.listener.AsyncPlayerJoinListener;
import io.github.cdfn.rankbridge.service.UserService;
import java.io.File;
import java.sql.SQLException;
import org.bukkit.plugin.java.JavaPlugin;

public class RankBridgePlugin extends JavaPlugin {

  public static final String SCHEMA_SQL =
      """
                create table if not exists rank_bridge
                (
                  `discord_user_id` varchar(20),
                  `ingame_nickname` varchar(30),
                  primary key (`discord_user_id`)
                );
          """;

  private PluginConfiguration pluginConfiguration;
  private HikariDataSource hikariDataSource;

  @Override
  public void onEnable() {
    this.pluginConfiguration = ConfigManager.create(PluginConfiguration.class, (it) -> {
      it.withBindFile(new File(this.getDataFolder(), "config.hjson"));
      it.withConfigurer(new HjsonConfigurer());
      it.saveDefaults();
      it.load(true);
    });
    this.hikariDataSource = this.setupDatasource();
    var userService = new UserService(this.hikariDataSource);
    this.getServer().getPluginManager().registerEvents(new AsyncPlayerJoinListener(this, userService), this);
  }

  @Override
  public void onDisable() {
    this.hikariDataSource.close();
  }

  private HikariDataSource setupDatasource() {
    var hikariConfig = new HikariConfig();
    hikariConfig.setJdbcUrl(this.pluginConfiguration.sqlUri);
    hikariConfig.addDataSourceProperty("cachePrepStmts", "true");
    hikariConfig.addDataSourceProperty("prepStmtCacheSize", "250");
    hikariConfig.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");

    var datasource = new HikariDataSource(hikariConfig);

    try (var connection = datasource.getConnection();
        var statement = connection.prepareStatement(SCHEMA_SQL)
    ) {
      statement.executeUpdate();
    } catch (SQLException e) {
      throw new RuntimeException("failed to connect to database", e);
    }

    return datasource;
  }
}
