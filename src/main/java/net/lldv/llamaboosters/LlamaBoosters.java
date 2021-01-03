package net.lldv.llamaboosters;

import cn.nukkit.plugin.PluginBase;
import lombok.Getter;
import net.lldv.llamaboosters.commands.BoosterCommand;
import net.lldv.llamaboosters.components.api.API;
import net.lldv.llamaboosters.components.data.Booster;
import net.lldv.llamaboosters.components.forms.FormListener;
import net.lldv.llamaboosters.components.forms.FormWindows;
import net.lldv.llamaboosters.components.language.Language;
import net.lldv.llamaboosters.components.tasks.BoosterTask;
import net.lldv.llamaboosters.listeners.EventListener;

import java.util.HashMap;
import java.util.Map;

public class LlamaBoosters extends PluginBase {

    @Getter
    private API api;

    @Getter
    private FormWindows formWindows;

    @Getter
    private int activeTime;

    @Getter
    private final Map<Booster, Integer> running = new HashMap<>();

    @Override
    public void onEnable() {
        try {
            this.saveDefaultConfig();
            Language.init(this);
            this.api = new API(this);
            this.formWindows = new FormWindows(this);
            this.activeTime = this.getConfig().getInt("Boosters.ActiveTime");
            this.loadPlugin();
            this.getLogger().info("§aLlamaBoosters successfully started.");
        } catch (Exception e) {
            e.printStackTrace();
            this.getLogger().error("§4Failed to load LlamaBoosters.");
        }
    }

    private void loadPlugin() {
        this.getServer().getPluginManager().registerEvents(new FormListener(), this);
        this.getServer().getPluginManager().registerEvents(new EventListener(this), this);

        this.getServer().getCommandMap().register("llamaboosters", new BoosterCommand(this));

        this.getConfig().getSection("Boosters.Prices").getAll().getKeys(false).forEach(s -> {
            this.api.boosterPrices.put(Booster.Type.valueOf(s.toUpperCase()), this.getConfig().getDouble("Boosters.Prices." + s));
        });

        this.getServer().getScheduler().scheduleDelayedRepeatingTask(this, new BoosterTask(this), 100, 20);
    }

}
