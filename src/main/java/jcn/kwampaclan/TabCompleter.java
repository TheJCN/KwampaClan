package jcn.kwampaclan;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class TabCompleter implements org.bukkit.command.TabCompleter {
    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        List<String> completions = new ArrayList<>();

        if (command.getName().equalsIgnoreCase("clan")) {
            if (strings.length == 1) {
                completions.add("help");
                completions.add("gui");
                completions.add("create");
                completions.add("invite");
                completions.add("leave");
                completions.add("kick");
                completions.add("accept");
                completions.add("list");
                completions.add("delete");
            } else if (strings.length == 2) {
                if (strings[0].equalsIgnoreCase("kick") || strings[0].equalsIgnoreCase("invite")) {
                    for (String playerName : getAllOnlinePlayerNames()) {
                        completions.add(playerName);
                    }
                }
            }
        }

        return completions;
    }

    private List<String> getAllOnlinePlayerNames() {
        List<String> playerNames = new ArrayList<>();
        for (Player player : Bukkit.getOnlinePlayers()) {
            playerNames.add(player.getName());
        }
        return playerNames;
    }
}

