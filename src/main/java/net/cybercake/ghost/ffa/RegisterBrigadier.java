package net.cybercake.ghost.ffa;

import com.mojang.brigadier.tree.LiteralCommandNode;
import me.lucko.commodore.CommodoreProvider;
import me.lucko.commodore.file.CommodoreFileFormat;
import org.bukkit.command.PluginCommand;

import java.io.IOException;

public class RegisterBrigadier {

    public static void registerCommodoreCommand(PluginCommand pluginCommand, String fileName) throws IOException {
        LiteralCommandNode<?> timeCommand = CommodoreFileFormat.parse(Main.getPlugin().getResource("commodore/" + fileName + ".commodore"));
        CommodoreProvider.getCommodore(Main.getPlugin()).register(pluginCommand, timeCommand);
    }

}
