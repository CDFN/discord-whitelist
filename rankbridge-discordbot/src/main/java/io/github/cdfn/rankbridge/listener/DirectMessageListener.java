package io.github.cdfn.rankbridge.listener;

import io.github.cdfn.rankbridge.config.BotConfiguration;
import io.github.cdfn.rankbridge.service.UserService;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

public class DirectMessageListener extends ListenerAdapter {

  private final BotConfiguration config;
  private final JDA jda;
  private final UserService service;

  public DirectMessageListener(BotConfiguration config, JDA jda, UserService service) {
    this.config = config;
    this.jda = jda;
    this.service = service;
  }

  @Override
  public void onMessageReceived(@NotNull MessageReceivedEvent event) {
    if (event.getChannelType() != ChannelType.PRIVATE && !event.getChannel().getId().equals(this.config.channelId)) {
      return;
    }
    if (event.getAuthor().isBot()) {
      return;
    }

    var guild = this.jda.getGuildById(this.config.guildId);
    if (guild == null) {
      throw new RuntimeException(String.format("failed to resolve guild with ID %s", this.config.guildId));
    }

    var member = guild.getMember(event.getAuthor());
    if (member == null) {
      System.out.println("not a member");
      return;
    }
    // Handle unlinking
    if (event.getMessage().getContentDisplay().startsWith("!")) {
      var split = event.getMessage().getContentDisplay().split(" ");
      if (split.length < 2) {
        return;
      }

      if (!split[0].equalsIgnoreCase("!unlink")) {
        return;
      }

      var hasAdminRank = member.getRoles()
          .stream()
          .anyMatch(role -> role.getId().equals(this.config.adminId));
      if(!hasAdminRank) {
        event.getChannel().sendMessage(new MessageBuilder("Brak uprawnień.").build()).queue();
        return;
      }

      var success = service.disallowJoining(split[1]);
      if (success) {
        event.getChannel().sendMessage(new MessageBuilder("Odłączono konto").build()).queue();
        return;
      }
      event.getChannel().sendMessage(new MessageBuilder("Błąd podczas odłączania konta, spróbuj ponownie").build()).queue();
      return;
    }

    member.getRoles()
        .stream()
        .filter(role -> role.getId().equals(this.config.rankId))
        .findFirst()
        .ifPresentOrElse(
            role -> {
              var channel = event.getChannel();
              var mention = event.getAuthor().getAsMention();
              switch (service.allowJoining(member.getId(), event.getMessage().getContentRaw())) {
                case ALLOWED -> channel.sendMessage(new MessageBuilder(mention + " Nadano dostęp do serwera.").build()).queue();
                case DUPLICATE -> channel.sendMessage(new MessageBuilder(mention + " Twoje konto Discord jest już powiązane z kontem w grze.").build()).queue();
                case ERROR -> channel.sendMessage(new MessageBuilder(mention + " Wystąpił błąd podczas przyznawania whitelisty, skontaktuj się z administracją.").build()).queue();
              }
            },
            () -> member.getUser().openPrivateChannel().queue((channel) -> {
              channel.sendMessage(new MessageBuilder("Nie posiadasz wymaganej rangi.").build()).queue();
            })
        );

  }
}
