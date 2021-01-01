package net.lldv.llamaboosters.components.api;

import cn.nukkit.AdventureSettings;
import cn.nukkit.Player;
import cn.nukkit.potion.Effect;
import lombok.RequiredArgsConstructor;
import net.lldv.llamaboosters.LlamaBoosters;
import net.lldv.llamaboosters.components.data.Booster;
import net.lldv.llamaboosters.components.language.Language;

import java.text.SimpleDateFormat;
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
        this.instance.getServer().getScheduler().scheduleDelayedTask(() -> {
            this.instance.getServer().getOnlinePlayers().values().forEach(e -> {
               e.sendMessage(Language.get("boosters.deactivating.incoming", this.convertTypeToName(booster)));
            });
            this.instance.getServer().getScheduler().scheduleDelayedTask(() -> {
                this.currentBoosters.remove(starter.getName());
                this.instance.getServer().getOnlinePlayers().values().forEach(e -> {
                    e.sendMessage(Language.get("boosters.deactivated", this.convertTypeToName(booster)));
                    switch (booster) {
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
            }, 600);
        }, ((this.instance.getActiveTime() * 20) * 60) - 30);
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

    public String getRemainingTime(long duration) {
        if (duration == -1L) {
            return Language.getNP("Permanent");
        } else {
            SimpleDateFormat today = new SimpleDateFormat("dd.MM.yyyy");
            today.format(System.currentTimeMillis());
            SimpleDateFormat future = new SimpleDateFormat("dd.MM.yyyy");
            future.format(duration);
            long time = future.getCalendar().getTimeInMillis() - today.getCalendar().getTimeInMillis();
            int days = (int) (time / 86400000L);
            int hours = (int) (time / 3600000L % 24L);
            int minutes = (int) (time / 60000L % 60L);
            String day = Language.getNP("Days");
            if (days == 1) {
                day = Language.getNP("Day");
            }

            String hour = Language.getNP("Hours");
            if (hours == 1) {
                hour = Language.getNP("Hour");
            }

            String minute = Language.getNP("Minutes");
            if (minutes == 1) {
                minute = Language.getNP("Minute");
            }

            if (minutes < 1 && days == 0 && hours == 0) {
                return Language.getNP("Seconds");
            } else if (hours == 0 && days == 0) {
                return minutes + " " + minute;
            } else {
                return days == 0 ? hours + " " + hour + " " + minutes + " " + minute : days + " " + day + " " + hours + " " + hour + " " + minutes + " " + minute;
            }
        }
    }

}
