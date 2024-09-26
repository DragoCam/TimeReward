package pl.lunarhost.timereward;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;

public class TimeRewardPlugin extends JavaPlugin {

    private Economy economy;

    @Override
    public void onEnable() {
        setupEconomy();
        startRewardTask();
    }

    private void setupEconomy() {
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp != null) {
            economy = rsp.getProvider();
        }
    }

    private void startRewardTask() {
        new BukkitRunnable() {
            @Override
            public void run() {
                for (Player player : getServer().getOnlinePlayers()) {
                    giveReward(player);
                }
            }
        }.runTaskTimer(this, 0, 10 * 60 * 20);
    }

    private void giveReward(Player player) {
        int amount = 15;
        EconomyResponse response = economy.depositPlayer(player, amount);
        if (response.transactionSuccess()) {
            sendRewardMessage(player, amount);
        } else {
            player.sendMessage(formatMessage("&cError: Unable to give reward!"));
        }
    }

    private void sendRewardMessage(Player player, int amount) {
        String message = "&8«&6*&8»&8&m-----------&8«&6*&8»&2 Time Reward &8«&6*&8»&8&m-----------&8«&6*&8»\n" +
                centerMessage("       &7Otrzymałeś &a$" + amount + "&7 za aktywność na serwerze!") + "\n" +
                "&8«&6*&8»&8&m-----------&8«&6*&8»&2 Time Reward &8«&6*&8»&8&m-----------&8«&6*&8»";
        player.sendMessage(formatMessage(message));
    }

    private String centerMessage(String message) {
        int maxLength = 50;
        int messageLength = message.length();
        int spaces = (maxLength - messageLength) / 2;

        StringBuilder centeredMessage = new StringBuilder();
        for (int i = 0; i < spaces; i++) {
            centeredMessage.append(" ");
        }
        centeredMessage.append(message);

        return centeredMessage.toString();
    }

    private String formatMessage(String message) {
        return ChatColor.translateAlternateColorCodes('&', message);
    }
}
