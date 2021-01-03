package net.lldv.llamaboosters.components.tasks;

import cn.nukkit.AdventureSettings;
import cn.nukkit.potion.Effect;
import cn.nukkit.scheduler.Task;
import lombok.RequiredArgsConstructor;
import net.lldv.llamaboosters.LlamaBoosters;
import net.lldv.llamaboosters.components.language.Language;

@RequiredArgsConstructor
public class BoosterTask extends Task {

    private final LlamaBoosters instance;

    @Override
    public void onRun(int i) {
        this.instance.getRunning().forEach((booster, integer) -> {
            if (integer != 0) {
                switch (integer) {
                    case 60:
                    case 30:
                    case 20:
                    case 10:
                    case 5:
                        this.instance.getServer().getOnlinePlayers().values().forEach(e -> {
                            e.sendMessage(Language.get("boosters.deactivating.incoming.pl", integer, this.instance.getApi().convertTypeToName(booster.getType())));
                        });
                        break;
                }
                this.instance.getRunning().put(booster, integer - 1);
            } else {
                this.instance.getApi().currentBoosters.remove(booster.getStarter());
                this.instance.getServer().getOnlinePlayers().values().forEach(e -> {
                    e.sendMessage(Language.get("boosters.deactivated", this.instance.getApi().convertTypeToName(booster.getType())));
                    switch (booster.getType()) {
                        case FLY:
                            e.getAdventureSettings().set(AdventureSettings.Type.ALLOW_FLIGHT, false);
                            e.getAdventureSettings().update();
                            break;
                        case BREAK:
                            e.removeEffect(Effect.HASTE);
                            break;
                        default:
                            break;
                    }
                });
                this.instance.getRunning().put(booster, integer - 1);
            }
        });
    }

}
