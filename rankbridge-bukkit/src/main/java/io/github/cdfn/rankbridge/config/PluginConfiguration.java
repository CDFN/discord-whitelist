package io.github.cdfn.rankbridge.config;

import eu.okaeri.configs.OkaeriConfig;
import eu.okaeri.configs.annotation.Comment;

public class PluginConfiguration extends OkaeriConfig {

  @Comment("URI used to connect to DB")
  public String sqlUri = "jdbc:mysql://user:password@localhost:3306/database";

}
