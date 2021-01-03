package net.lldv.llamaboosters.components.api;

import cn.nukkit.AdventureSettings;
import cn.nukkit.Player;
import cn.nukkit.potion.Effect;
import lombok.RequiredArgsConstructor;
import net.lldv.llamaboosters.LlamaBoosters;
import net.lldv.llamaboosters.components.data.Booster;
import net.lldv.llamaboosters.components.language.Language;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

@RequiredArgsConstructor
public class API {

    private final LlamaBoosters instance;

    public final Map<String, Booster> currentBoosters = new HashMap<>();
    public final Map<Booster.Type, Double> boosterPrices = new HashMap<>();

    public void startBooster(final Player starter, final Booster.Type booster) {
        this.currentBoosters.put(starter.getName(), new Booster(starter.getName(), booster, System.currentTimeMillis() + ((this.instance.getActiveTime() * 20L) * 60)));
        starter.sendMessage(Language.get("boosters.activate", this.convertTypeToName(booster), this.instance.getActiveTime()));
        this.instance.getServer().getOnlinePlayers().values().forEach(e -> {
            switch (booster) {
                case FLY:
                    e.getAdventureSettings().set(AdventureSettings.Type.ALLOW_FLIGHT, true);
                    e.getAdventureSettings().update();
                    break;
                case BREAK:
                    e.addEffect(Effect.getEffect(Effect.HASTE).setAmplifier(2).setDuration(Integer.MAX_VALUE).setVisible(false));
                    break;
                default:
                    break;
            }
            e.sendMessage(Language.get("boosters.activate.info", starter.getName(), this.convertTypeToName(booster), this.instance.getActiveTime()));
        });
        this.instance.getRunning().put(new Booster(starter.getName(), booster, System.currentTimeMillis() + ((this.instance.getActiveTime() * 20L) * 60)), this.instance.getActiveTime() * 60);
    }

    public boolean isActive(final Booster.Type booster) {
        AtomicBoolean b = new AtomicBoolean(false);
        this.currentBoosters.values().forEach(e -> {
            if (e.getType() == booster) b.set(true);
        });
        return b.get();
    }

    public String convertTypeToName(final Booster.Type booster) {
        return Language.getNP("booster." + booster.name().toLowerCase());
    }

}
