package io.github.cdfn.rankbridge;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import eu.okaeri.configs.ConfigManager;
import eu.okaeri.configs.hjson.HjsonConfigurer;
import io.github.cdfn.rankbridge.config.BotConfiguration;
import io.github.cdfn.rankbridge.listener.DirectMessageListener;
import io.github.cdfn.rankbridge.service.UserService;
import java.io.File;
import java.sql.SQLException;
import javax.security.auth.login.LoginException;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.requests.GatewayIntent;

public class RankBridgeBot {
  public static final String SCHEMA_SQL =
      """
                create table if not exists rank_bridge
                (
                  `discord_user_id` varchar(20),
                  `ingame_nickname` varchar(30),
                  primary key (`discord_user_id`)
                );
          """;
  private HikariDataSource dataSource;
  private BotConfiguration config;

  public void start() {
    this.config = ConfigManager.create(BotConfiguration.class, (it) -> {
      it.withBindFile(new File("config.hjson"));
      it.withConfigurer(new HjsonConfigurer());
      it.saveDefaults();
      it.load(true);
    });
    this.dataSource = this.setupDatasource();
    var userService = new UserService(this.dataSource);
    try {
      var jda = JDABuilder
          .create(GatewayIntent.DIRECT_MESSAGES, GatewayIntent.GUILD_MEMBERS, GatewayIntent.GUILD_MESSAGES)
          .setToken(this.config.token)
          .build();
      jda.addEventListener(new DirectMessageListener(this.config, jda, userService));
    } catch (LoginException e) {
      throw new RuntimeException("failed to login", e);
    }
  }

  public void stop() {
  }

  private HikariDataSource setupDatasource() {
    var hikariConfig = new HikariConfig();
    hikariConfig.setJdbcUrl(this.config.sqlUri);
    hikariConfig.setDriverClassName("com.mysql.cj.jdbc.Driver");
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
