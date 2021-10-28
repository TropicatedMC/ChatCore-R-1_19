package me.cranked.crankedcore.events;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import me.cranked.crankedcore.CrankedCore;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class AntiAd implements Listener {
    private final CrankedCore plugin;

    public AntiAd(CrankedCore plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent e) {
        // Config check
        if (!plugin.getConfig().getBoolean("anti-ad-enabled"))
            return;

        // Permission check
        Player player = e.getPlayer();
        if (player.hasPermission("crankedcore.antiad.bypass"))
            return;

        // Check if contains whitelist (TODO this could be improved)
        for (String allowed : plugin.getConfig().getStringList("anti-ad-whitelist")) {
            if (e.getMessage().toLowerCase().contains(allowed.toLowerCase()))
                return;
        }

        Pattern pattern = Pattern.compile("\\b[0-9]{1,3}(\\.|dot|\\(dot\\)|-|;|:|,|(\\W|\\d|_)*\\s)+[0-9]{1,3}(\\.|dot|\\(dot\\)|-|;|:|,|(\\W|\\d|_)*\\s)+[0-9]{1,3}(\\.|dot|\\(dot\\)|-|;|:|,|(\\W|\\d|_)*\\s)+[0-9]{1,3}\\b");
        Pattern pattern2 = Pattern.compile("\\b[0-9]{1,3}(\\.|dot|\\(dot\\)|-|;|:|,)+[0-9]{1,3}(\\.|dot|\\(dot\\)|-|;|:|,)+[0-9]{1,3}(\\.|dot|\\(dot\\)|-|;|:|,)+[0-9]{1,3}\\b");
        Pattern pattern3 = Pattern.compile("[a-zA-Z0-9\\-.]+\\s?(\\.|dot|\\(dot\\)|-|;|:|,)\\s?(c(| +)o(| +)m|o(| +)r(| +)g|n(| +)e(| +)t|c(| +)z|c(| +)o|u(| +)k|s(| +)k|b(| +)i(| +)z|m(| +)o(| +)b(| +)i|x(| +)x(| +)x|e(| +)u|m(| +)e|i(| +)o)\\b");
        Pattern pattern4 = Pattern.compile("[a-zA-Z0-9\\-.]+\\s?(\\.|dot|\\(dot\\)|;|:|,)\\s?(com|org|net|cz|co|uk|sk|biz|mobi|xxx|eu|io)\\b");
        String msg = e.getMessage();
        Matcher matcher = pattern.matcher(msg);
        Matcher matcher2 = pattern2.matcher(msg);
        Matcher matcher3 = pattern3.matcher(msg);
        Matcher matcher4 = pattern4.matcher(msg);
        if (matcher.find() || matcher2.find() || matcher3.find() || matcher4.find()) {
            if (plugin.getConfig().getInt("anti-ad-setting") == 1) {
                e.setCancelled(true);
            } else {
                // TODO check this logic
                e.setMessage(msg.replaceAll("\\.", " "));
            }

            // Send warning message
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(plugin.getConfig().getString("anti-ad-msg"))));

            // Broadcast to staff
            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                if (onlinePlayer.hasPermission("crankedcore.antiad.alert"))
                    onlinePlayer.sendMessage(ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(plugin.getConfig().getString("anti-ad-inform-msg")).replace("%player%", player.getName()).replace("%message%", e.getMessage())));
            }
        }
    }
}
