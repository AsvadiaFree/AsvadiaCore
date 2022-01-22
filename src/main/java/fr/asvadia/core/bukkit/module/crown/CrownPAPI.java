package fr.asvadia.core.bukkit.module.crown;

import fr.asvadia.core.bukkit.module.PlayerModule;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

public class CrownPAPI extends PlaceholderExpansion {

    CrownModule crownModule;

    public CrownPAPI(CrownModule crownModule) {
        this.crownModule = crownModule;
    }

    @Override
    public @NotNull String getIdentifier() {
        return "crown";
    }

    @Override
    public @NotNull String getAuthor() {
        return "AziRixX";
    }

    @Override
    public @NotNull String getVersion() {
        return "0.5";
    }


    @Override
    public String onRequest(OfflinePlayer player, @NotNull String params) {
        try {
            PlayerModule.AsvadiaPlayer crownPlayer = crownModule.getMain().getPlayerModule().getPlayer(player.getUniqueId());
            if(params.equals("amount")) return crownPlayer.getCrown(false).toString();
            else if(params.equals("amount_exact")) return crownPlayer.getCrown(true).toString();
        } catch (NullPointerException ignored) {}
        return "0";
    }
}
