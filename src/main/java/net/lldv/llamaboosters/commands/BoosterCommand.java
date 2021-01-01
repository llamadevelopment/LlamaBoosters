package net.lldv.llamaboosters.commands;

import cn.nukkit.Player;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.PluginCommand;
import net.lldv.llamaboosters.LlamaBoosters;
import net.lldv.llamaboosters.components.language.Language;

public class BoosterCommand extends PluginCommand<LlamaBoosters> {

    public BoosterCommand(LlamaBoosters owner) {
        super(owner.getConfig().getString("Commands.Booster.Name"), owner);
        this.setDescription(owner.getConfig().getString("Commands.Booster.Description"));
        this.setPermission(owner.getConfig().getString("Commands.Booster.Permission"));
        this.setAliases(owner.getConfig().getStringList("Commands.Booster.Aliases").toArray(new String[]{}));
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        if (sender instanceof Player) {
            final Player player = (Player) sender;
            if (player.hasPermission(this.getPermission())) {
                this.getPlugin().getFormWindows().openBoosters(player);
            } else player.sendMessage(Language.get("permission.insufficient"));
        }
        return true;
    }
}
