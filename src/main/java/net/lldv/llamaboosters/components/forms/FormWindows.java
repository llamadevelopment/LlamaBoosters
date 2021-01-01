package net.lldv.llamaboosters.components.forms;

import cn.nukkit.Player;
import cn.nukkit.form.element.ElementButton;
import lombok.RequiredArgsConstructor;
import net.lldv.llamaboosters.LlamaBoosters;
import net.lldv.llamaboosters.components.data.Booster;
import net.lldv.llamaboosters.components.forms.modal.ModalForm;
import net.lldv.llamaboosters.components.forms.simple.SimpleForm;
import net.lldv.llamaboosters.components.language.Language;
import net.lldv.llamaeconomy.LlamaEconomy;
import net.lldv.llamaeconomy.components.api.API;

@RequiredArgsConstructor
public class FormWindows {

    private final LlamaBoosters instance;

    public void openBoosters(final Player player) {
        SimpleForm form = new SimpleForm.Builder(Language.getNP("ui.boosters.title"), Language.getNP("ui.boosters.content"))
                .addButton(new ElementButton(Language.getNP("ui.boosters.fly")), e -> this.openBuyBooster(e, Booster.Type.FLY))
                .addButton(new ElementButton(Language.getNP("ui.boosters.break")), e -> this.openBuyBooster(e, Booster.Type.BREAK))
                .addButton(new ElementButton(Language.getNP("ui.boosters.xp")), e -> this.openBuyBooster(e, Booster.Type.XP))
                .addButton(new ElementButton(Language.getNP("ui.boosters.falldamage")), e -> this.openBuyBooster(e, Booster.Type.FALL_DAMAGE))
                .build();
        form.send(player);
    }

    public void openBuyBooster(final Player player, final Booster.Type booster) {
        if (this.instance.getApi().isActive(booster)) {
            player.sendMessage(Language.get("boosters.already.active"));
            return;
        }
        ModalForm form = new ModalForm.Builder(Language.getNP("ui.buybooster.title"), Language.getNP("ui.buybooster.content",
                this.instance.getApi().convertTypeToName(booster), this.instance.getApi().boosterPrices.get(booster)),
                Language.getNP("ui.buybooster.buy"), Language.getNP("ui.buybooster.cancel"))
                .onYes(e -> {
                    API api = LlamaEconomy.getAPI();
                    if (api.getMoney(e.getName()) >= this.instance.getApi().boosterPrices.get(booster)) {
                        this.instance.getApi().startBooster(e, booster);
                        api.reduceMoney(e.getName(), this.instance.getApi().boosterPrices.get(booster));
                    } else {
                        e.sendMessage(Language.get("boosters.not.enough.money"));
                    }
                })
                .onNo(this::openBoosters)
                .build();
        form.send(player);
    }

}
