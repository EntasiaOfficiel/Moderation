package fr.entasia.moderation.commands;

import fr.entasia.moderation.utils.Freezer;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class FreezeCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		Player p = (Player)sender;
		if(p.hasPermission("staff.freeze")) {
			if (args.length == 0)
				p.sendMessage("§bFreeze » §3Choisi une personne !");
			else if(args[0].equalsIgnoreCase("list")){
				StringBuilder sb = new StringBuilder();
				for(String f : Freezer.frozens.keySet()) sb.append("\n§7- §3").append(f);
				if(sb.length()==0)p.sendMessage("§7Personne n'est §bFreeze !");
				else p.sendMessage("§7Joueurs §bFreeze §7actuellement : "+sb.toString());
			}else{
				Player target = Bukkit.getPlayer(args[0]);
				if(target==null) p.sendMessage("§cCe joueur n'est pas connecté ou n'existe pas !");
				else {
					if (Freezer.frozens.containsKey(target.getName())){
						Freezer.unfreeze(target);
						p.sendMessage("§bFreeze » §cDésactivé §3pour §9" + target.getName() + "§3 !");
					} else {
						Freezer.freeze(target);
						p.sendMessage("§bFreeze » §aActivé §3sur §9" + target.getName() + "§3 !");
					}
				}
			}
		}else p.sendMessage("§cTu n'as pas accès à cette commande !");
	return true;
	}
}
