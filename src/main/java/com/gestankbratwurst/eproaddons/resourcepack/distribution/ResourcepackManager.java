package com.gestankbratwurst.eproaddons.resourcepack.distribution;

import com.gestankbratwurst.eproaddons.EproAddons;
import com.gestankbratwurst.eproaddons.io.EproConfig;
import com.gestankbratwurst.eproaddons.resourcepack.distribution.ResourcepackServer.ResourceServerConnection;
import com.google.common.hash.Hashing;
import com.google.common.io.Files;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.CompletableFuture;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class ResourcepackManager {

  public static final long SERVER_TIMESTAMP = System.currentTimeMillis();
  public static final String RESOURCEPACK_FILE_NAME = "serverpack.zip";

  // "176.9.38.108"
  private final int port;
  private final String host;
  private final String hash;

  private ResourcepackServer server;
  private final File pack;

  public ResourcepackManager() {
    final EproAddons plugin = EproAddons.getInstance();
    final EproConfig eproConfig = plugin.getEproFileManager().getEproConfig();
    this.port = eproConfig.getResourcePackServerPort();
    this.host = eproConfig.getResourcePackServerIP();
    final File stampFolder = new File(plugin.getDataFolder() + File.separator + ResourcepackManager.SERVER_TIMESTAMP);
    this.pack = new File(stampFolder, ResourcepackManager.RESOURCEPACK_FILE_NAME);
    this.hash = ResourcepackManager.getFileHashChecksum(this.pack, plugin);
    CompletableFuture.runAsync(() -> this.startServer(plugin));
  }

  public String getResourceHash() {
    return this.hash;
  }

  public String getDownloadURL() {
    return "http://" + this.host + ":" + this.port + "/" + ResourcepackManager.SERVER_TIMESTAMP
        + "/" + ResourcepackManager.RESOURCEPACK_FILE_NAME;
  }

  public void shutdown() {
    this.server.terminate();
  }


  private void startServer(final JavaPlugin plugin) {
    plugin.getLogger().info("Starting async HTTP Server");
    try {
      this.server = new ResourcepackServer(this.port) {

        @Override
        public File requestFileCallback(final ResourceServerConnection connection,
            final String request) {
          final Player player = ResourcepackManager.getAddress(connection);

          if (player == null) {
            // Connection from unknown IP, refuse connection.
            return null;
          }
          plugin.getLogger().info(
              "Connection " + connection.getClient().getInetAddress() + " is requesting + "
                  + request);
          // Return the .zip file
          return ResourcepackManager.this.pack;
        }

        @Override
        public void onSuccessfulRequest(final ResourceServerConnection connection,
            final String request) {
          plugin.getLogger().info(
              "Successfully served " + request + " to " + connection.getClient().getInetAddress()
                  .getHostAddress());
        }

        @Override
        public void onClientRequest(final ResourceServerConnection connection,
            final String request) {
          plugin.getLogger()
              .info(connection.getClient().getInetAddress() + " is requesting the resourcepack");
        }

        @Override
        public void onRequestError(final ResourceServerConnection connection, final int code) {
          plugin.getLogger().info(
              "Error " + code + " while attempting to serve " + connection.getClient()
                  .getInetAddress().getHostAddress());
        }
      };

      this.server.start();
      plugin.getLogger().info("Successfully started the HTTP Server");
    } catch (final IOException ex) {
      plugin.getLogger().severe("Failed to start HTTP ResourceServer!");
      ex.printStackTrace();
    }
  }

  private static String getFileHashChecksum(final File input, final JavaPlugin plugin) {
    try {
      return Files.hash(input, Hashing.sha1()).toString();
    } catch (final IOException e) {
      plugin.getLogger().severe("Failed to calculate resourcepack hashcode - " + e.getMessage());
      return null;
    }
  }


  private static Player getAddress(final ResourceServerConnection connection) {
    final byte[] ip = connection.getClient().getInetAddress().getAddress();

    for (final Player player : Bukkit.getOnlinePlayers()) {
      if (Arrays.equals(player.getAddress().getAddress().getAddress(), ip)) {
        return player;
      }
    }
    return null;
  }


}
