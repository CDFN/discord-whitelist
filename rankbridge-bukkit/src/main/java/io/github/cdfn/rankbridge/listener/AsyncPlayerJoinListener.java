package io.github.cdfn.rankbridge.listener;

import io.github.cdfn.rankbridge.RankBridgePlugin;
import io.github.cdfn.rankbridge.service.UserService;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent.Result;

public class AsyncPlayerJoinListener implements Listener {

  private final RankBridgePlugin plugin;
  private final UserService service;

  public AsyncPlayerJoinListener(RankBridgePlugin plugin, UserService service) {
    this.plugin = plugin;
    this.service = service;
  }

  @EventHandler
  public void onJoin(AsyncPlayerPreLoginEvent event) {
    if(!service.canJoin(event.getName())) {
      event.disallow(Result.KICK_OTHER, Component.text("Nie jesteś na whiteliście.", NamedTextColor.RED));
      return;
    }
    event.allow();
  }

}
