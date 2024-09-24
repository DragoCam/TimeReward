package pl.lunarhost.timereward;

import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;

public class TimeRewardPlugin extends JavaPlugin {

    private final Map<Player, Integer> playTimeMap = new HashMap<>();
    private Economy economy;

    private int rewardInterval;
    private int maxTime;
    private double rewardAmount;

    @Override
    public void onEnable() {
        // Ładuj konfigurację
        saveDefaultConfig();
        loadConfigValues();

        // Ustaw Vault (ekonomia)
        if (!setupEconomy()) {
            getLogger().severe("Vault nie został znaleziony. Wyłączam plugin.");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        // Zarejestruj zadanie cykliczne
        Bukkit.getScheduler().scheduleSyncRepeatingTask(this, this::rewardPlayers, 0L, 20L * 60 * rewardInterval);
    }

    private void loadConfigValues() {
        FileConfiguration config = getConfig();
        rewardInterval = config.getInt("reward-interval", 30);
        maxTime = config.getInt("max-time", 300);
        rewardAmount = config.getDouble("reward-amount", 10.0);
    }

    private void rewardPlayers() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            int playTime = playTimeMap.getOrDefault(player, 0);

            if (playTime < maxTime) {
                // Dodaj pieniądze za pomocą Vault
                EconomyResponse response = economy.depositPlayer(player, rewardAmount);
                if (response.transactionSuccess()) {
                    player.sendMessage(ChatColor.GREEN + "Otrzymałeś " + rewardAmount + "$ za grę!");
                    playTimeMap.put(player, playTime + rewardInterval);
                }
            }
        }
    }

    @Override
    public void onDisable() {
        // Zapisz dane graczy przy wyłączeniu pluginu (np. do pliku lub bazy danych)
    }

    // Metoda pobierająca instancję ekonomii Vault
    private boolean setupEconomy() {
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        economy = rsp.getProvider();
        return economy != null;
    }
}
