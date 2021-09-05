package net.cybercake.ghost.ffa.commands.maincommand.subcommands;

import net.cybercake.ghost.ffa.Main;
import net.cybercake.ghost.ffa.commands.maincommand.CommandManager;
import net.cybercake.ghost.ffa.commands.maincommand.SubCommand;
import net.cybercake.ghost.ffa.utils.DataUtils;
import net.cybercake.ghost.ffa.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

public class Version extends SubCommand {

     public static Long lastUpdateCheck;
     public static String latestVersion;
     public static Integer latestProtocol;

     public Version() {
          super("version", "ghostffa.subcommand.version", "Checks if the plugin is out-of-date", "/ghostffa version", "ver");
     }

     @Override
     public void perform(CommandSender sender, String[] args, Command command) {
          if(lastUpdateCheck == null || (Utils.getUnix() - lastUpdateCheck) >= 600) {
               sender.sendMessage(Utils.chat("&f&oChecking version, please wait..."));
          }

          getLatestUpdate();

          int latestProtocol = Version.latestProtocol;
          String latestVersion = Version.latestVersion;

          int yourProtocol = -1;
          String yourVersion = Main.getPlugin(Main.class).getDescription().getVersion();
          String apiVersion = "unknown";

          try {
               BufferedReader reader = new BufferedReader(new InputStreamReader(Main.getPlugin().getResource("plugin.yml")));
               String line;
               while((line = reader.readLine()) != null) {
                    if(line.contains("version-protocol")) {
                         yourProtocol = Integer.parseInt(line.replace("version-protocol: ", ""));
                    }else if(line.contains("api-version")) {
                         apiVersion = line.replace("api-version: ", "");
                    }
               }
          } catch (Exception exception) {
               Utils.error(sender, "whilst trying to read the current version", exception);
               return;
          }

          sender.sendMessage(Utils.chat("&fThis server is running GhostFFA version " + yourVersion + ", protocol " + yourProtocol + " (MC Version: " + Bukkit.getMinecraftVersion() + ") (API Version: " + apiVersion + ") (Spigot Version: " + Bukkit.getBukkitVersion() + ")"));

          if(yourProtocol == latestProtocol) {
               sender.sendMessage(Utils.chat("&aYou are running the latest version"));
          }else if(latestProtocol > yourProtocol) {
               sender.sendMessage(Utils.chat("&eYou are " + (latestProtocol-yourProtocol) + " version(s) behind"));
          }else if(latestProtocol < yourProtocol) {
               sender.sendMessage(Utils.chat("&aYou are " + (yourProtocol-latestProtocol) + " &aversion(s) ahead"));
          }else{
               sender.sendMessage(Utils.chat("&cError obtaining version difference information"));
          }
     }

     @Override
     public List<String> tab(CommandSender sender, String[] args) {
          return CommandManager.emptyList;
     }

     public int getProtocolDiff(String newestVersion, String currentVersion) {
          if((!Utils.isInteger(newestVersion)) || (!Utils.isInteger(currentVersion))) {
               return -1;
          }
          return Integer.parseInt(newestVersion) - Integer.parseInt(currentVersion);
     }

     public void getLatestUpdate() {
          latestVersion = null;
          latestProtocol = -1;

          try {
               URL url = new URL("https://pastebin.com/raw/kwWqK4gj");

               BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));

               int line = 0;
               String lineStr = "";
               while ((lineStr = in.readLine()) != null) {
                    if(line == 0) {
                         latestVersion = lineStr.replace("latestVersion=", "");
                    } else if (line == 1) {
                         latestProtocol = Integer.parseInt(lineStr.replace("protocolVersion=", ""));
                    }
                    line++;
               }
               in.close();
          } catch (MalformedURLException e) {
               Bukkit.getLogger().severe("An error occurred whilst checking for updates! (MalformedURLException)");
               Bukkit.getLogger().severe(" ");
               Bukkit.getLogger().severe("Stack trace below:");
               Utils.printBetterStackTrace(e);
               return;
          } catch (IOException e) {
               Bukkit.getLogger().severe("An error occurred whilst checking for updates! (IOException)");
               Bukkit.getLogger().severe(" ");
               Bukkit.getLogger().severe("Stack trace below:");
               Utils.printBetterStackTrace(e);
               return;
          } catch (Exception e) {
               Bukkit.getLogger().severe("An error occurred whilst checking for updates! (Exception)");
               Bukkit.getLogger().severe(" ");
               Bukkit.getLogger().severe("Stack trace below:");
               Utils.printBetterStackTrace(e);
               return;
          }

          Version.lastUpdateCheck = Utils.getUnix();
     }
}
