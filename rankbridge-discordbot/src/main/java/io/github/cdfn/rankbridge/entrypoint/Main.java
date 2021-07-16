package io.github.cdfn.rankbridge.entrypoint;

import io.github.cdfn.rankbridge.RankBridgeBot;

public class Main {

  public static void main(String[] args) {
    var bot = new RankBridgeBot();
    bot.start();
    bot.stop();
  }
}
