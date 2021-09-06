package net.cybercake.ghost.ffa.commands;

import net.cybercake.ghost.ffa.Main;
import net.cybercake.ghost.ffa.commands.maincommand.CommandManager;
import net.cybercake.ghost.ffa.utils.DataUtils;
import net.cybercake.ghost.ffa.utils.PlayerDataUtils;
import net.cybercake.ghost.ffa.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.List;
import java.util.UUID;

public class Seen implements CommandExecutor, TabCompleter {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(args.length == 0) {
            Utils.commandStatus(sender, Utils.Status.FAILED, "Invalid arguments");
        }else if(args.length > 0) {
            UUID uuid = Bukkit.getPlayerUniqueId(args[0]);
            if(uuid == null) {
                Utils.commandStatus(sender, Utils.Status.FAILED, "Invalid offline player"); return true;
            }
            OfflinePlayer target = Bukkit.getOfflinePlayer(uuid);
            File customYml = new File(Main.getPlugin().getDataFolder() + "/playerdata/" + uuid + ".yml");
            if(!customYml.exists()) {
                Utils.commandStatus(sender, Utils.Status.FAILED, "That player has never joined before"); return true;
            }
            FileConfiguration customConfig = YamlConfiguration.loadConfiguration(customYml);

            String username = "USERNAME_UNKNOWN";
            if(customConfig.getString("generic.username") == null) {
                username = args[0];
            }else{
                username = customConfig.getString("generic.username");
            }

            if(target.isOnline()) {
                if(customConfig.getLong("joinLeave.joinDate") == 0) {
                    Utils.commandStatus(sender, Utils.Status.TECHNICAL_FAULT, "That player hasn't even joined before (joinDate unset:error)"); return true;
                }
                Utils.commandStatus(sender, Utils.Status.INFO, "&b" + username + " &fhas been &aonline &ffor &e" + Utils.getBetterTimeFromLongs(Utils.getUnix(), customConfig.getLong("joinLeave.joinDate"), true));
            }else{
                if(customConfig.getLong("joinLeave.leaveDate") == 0) {
                    Utils.commandStatus(sender, Utils.Status.TECHNICAL_FAULT, "That player hasn't even left before (leaveDate unset:error)"); return true;
                }
                Utils.commandStatus(sender, Utils.Status.INFO, "&b" + username + " &fhas been &coffline &ffor &e" + Utils.getBetterTimeFromLongs(Utils.getUnix(), customConfig.getLong("joinLeave.leaveDate"), true));
            }
        }

        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        if(args.length == 1) {
            return CommandManager.createReturnList(DataUtils.getCustomYmlStringList("playerdata/-allnames", "names"), args[0]);
        }
        return CommandManager.emptyList;
    }
}
