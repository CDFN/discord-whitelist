package io.github.cdfn.rankbridge.service;

import com.zaxxer.hikari.HikariDataSource;
import io.github.cdfn.rankbridge.AllowStatus;
import java.sql.SQLException;

public class UserService {

  private final HikariDataSource dataSource;

  public UserService(HikariDataSource dataSource) {
    this.dataSource = dataSource;
  }

  public AllowStatus allowJoining(String discordId, String name) {
    try (var connection = this.dataSource.getConnection();
        var statement = connection.prepareStatement("INSERT INTO `rank_bridge`(discord_user_id, ingame_nickname) VALUES (?, ?)")
    ) {
      statement.setString(1, discordId);
      statement.setString(2, name);
      statement.executeUpdate();
    } catch (SQLException e) {
      if (e.getErrorCode() == 1062) {
        //duplicate
        return AllowStatus.DUPLICATE;
      }
      e.printStackTrace();
      return AllowStatus.ERROR;
    }
    return AllowStatus.ALLOWED;
  }

  public boolean disallowJoining(String discordId) {
    try (var connection = this.dataSource.getConnection();
        var statement = connection.prepareStatement("DELETE FROM `rank_bridge` WHERE `discord_user_id`=?")
    ) {
      statement.setString(1, discordId);
      statement.executeUpdate();
      return true;
    }catch (SQLException e){
      e.printStackTrace();
      return false;
    }
  }
}
