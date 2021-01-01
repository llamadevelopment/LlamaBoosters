package net.lldv.llamaboosters.listeners;

import cn.nukkit.AdventureSettings;
import cn.nukkit.Player;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.block.BlockBreakEvent;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.event.player.PlayerJoinEvent;
import cn.nukkit.event.player.PlayerQuitEvent;
import cn.nukkit.potion.Effect;
import lombok.RequiredArgsConstructor;
import net.lldv.llamaboosters.LlamaBoosters;
import net.lldv.llamaboosters.components.data.Booster;

@RequiredArgsConstructor
public class EventListener implements Listener {

    private final LlamaBoosters instance;

    @EventHandler
    public void on(final PlayerJoinEvent event) {
        final Player player = event.getPlayer();
        this.instance.getApi().currentBoosters.forEach((e, booster) -> {
            switch (booster.getType()) {
                case FLY:
                    player.getAdventureSettings().set(AdventureSettings.Type.ALLOW_FLIGHT, true);
                    player.getAdventureSettings().update();
                    break;
                case BREAK:
                    player.addEffect(Effect.getEffect(Effect.HASTE).setAmplifier(2).setDuration(Integer.MAX_VALUE).setVisible(false));
                    break;
                default:
                    break;
            }
        });
    }

    @EventHandler
    public void on(final PlayerQuitEvent event) {
        final Player player = event.getPlayer();
        this.instance.getApi().currentBoosters.forEach((e, booster) -> {
            switch (booster.getType()) {
                case FLY:
                    player.getAdventureSettings().set(AdventureSettings.Type.ALLOW_FLIGHT, false);
                    player.getAdventureSettings().update();
                    break;
                case BREAK:
                    player.removeEffect(Effect.HASTE);
                    break;
                default:
                    break;
            }
        });
    }

    @EventHandler
    public void on(final EntityDamageEvent event) {
        if (event.getEntity() instanceof Player) {
            if (event.getCause() == EntityDamageEvent.DamageCause.FALL) {
                this.instance.getApi().currentBoosters.values().forEach(e -> {
                    if (e.getType() == Booster.Type.FALL_DAMAGE) event.setCancelled(true);
                });
            }
        }
    }

    @EventHandler
    public void on(final BlockBreakEvent event) {
        this.instance.getApi().currentBoosters.values().forEach(e -> {
            if (e.getType() == Booster.Type.XP) event.setDropExp(event.getDropExp() * 4);
        });
    }

}
