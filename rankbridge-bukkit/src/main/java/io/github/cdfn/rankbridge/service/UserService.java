package io.github.cdfn.rankbridge.service;

import com.zaxxer.hikari.HikariDataSource;
import java.sql.SQLException;

public class UserService {

  private final HikariDataSource hikariDataSource;

  public UserService(HikariDataSource hikariDataSource) {
    this.hikariDataSource = hikariDataSource;
  }

  public boolean canJoin(String username) {
    try (var connection = hikariDataSource.getConnection();
        var statement = connection.prepareStatement("SELECT * FROM `rank_bridge` WHERE `ingame_nickname` = ?")) {
      statement.setString(1, username);
      return statement.executeQuery().next();
    } catch (SQLException e) {
      e.printStackTrace();
      return false;
    }

  }

}
