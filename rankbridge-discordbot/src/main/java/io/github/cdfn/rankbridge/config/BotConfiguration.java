package io.github.cdfn.rankbridge.config;

import eu.okaeri.configs.OkaeriConfig;
import eu.okaeri.configs.annotation.Comment;

public class BotConfiguration extends OkaeriConfig {

  @Comment("URI used to connect to DB")
  public String sqlUri = "jdbc:mysql://user:password@localhost:3306/database";

  @Comment("Bot's token (https://discord.com/developers/applications/)")
  public String token = "Put your token here";

  @Comment("Guild ID bot should look up ranks on (RMB on server icon -> Copy ID)")
  public String guildId = "790334148883382272";

  @Comment("Rank ID allowing joining the server (RMB on role -> Copy ID)")
  public String rankId = "861327930851590144";

  @Comment("Rank ID allowing unlinking others accounts (RMB on role -> Copy ID)")
  public String adminId = "861593148114599946";

  @Comment("Channel ID where bot listens to nicknames")
  public String channelId = "862658678910091264";
}
