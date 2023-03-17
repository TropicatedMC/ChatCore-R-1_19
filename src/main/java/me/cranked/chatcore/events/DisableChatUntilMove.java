package me.cranked.chatcore.events;

import java.util.*;
import me.cranked.chatcore.ConfigManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class DisableChatUntilMove implements Listener {
    private final Set<Player> notMoved = new HashSet<>();

    @EventHandler
    public void onJoin(PlayerLoginEvent e) {
        // Config check
        if (!ConfigManager.getEnabled("disable-chat-until-move"))
            return;

        // Bypass check
        if (!e.getPlayer().hasPermission("chatcore.disablechatuntilmove.bypass"))
            this.notMoved.add(e.getPlayer());
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent e) {
        this.notMoved.remove(e.getPlayer());
    }

    @EventHandler
    public void onMove(PlayerMoveEvent e) {
        this.notMoved.remove(e.getPlayer());
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent e) {
        if (e.isCancelled()) return;
        if (!ConfigManager.getEnabled("disable-chat-until-move"))
            return;
        Player player = e.getPlayer();
        if (this.notMoved.contains(player)) {
            e.setCancelled(true);
            player.sendMessage(ConfigManager.get("disable-chat-until-move"));
        }
    }

    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent e) {
        // Config check
        if (!ConfigManager.getEnabled("disable-chat-until-move"))
            return;

        // Bypass / moved check
        Player player = e.getPlayer();
        if (!this.notMoved.contains(player) || player.hasPermission("chatcore.disablechatuntilmove.bypass"))
            return;

        // Only cancel disabled commands
        List<String> blockedCommands = ConfigManager.getList("disable-commands-until-move");
        String msg = e.getMessage();
        for (String blockedCommand : blockedCommands) {
            if ((msg.length() == blockedCommand.length() && msg.equalsIgnoreCase(blockedCommand)) || (msg.length() > blockedCommand.length() && msg.substring(0, blockedCommand.length() + 1).equalsIgnoreCase(blockedCommand + " "))) {
                e.setCancelled(true);
                player.sendMessage(ConfigManager.get("disable-command-until-move"));
            }
        }
    }
}
