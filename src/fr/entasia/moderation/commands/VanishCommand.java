package fr.entasia.moderation.commands;

import fr.entasia.moderation.obj.VanishedPlayer;
import fr.entasia.moderation.utils.Vanisher;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Map;

public class VanishCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(!(sender instanceof Player))return false;
		Player p = (Player)sender;
		if(p.hasPermission("staff.vanish")){
			if(args.length==0){
				VanishedPlayer vp = Vanisher.vanisheds.get(p.getName());
				if(vp==null){
					if(Vanisher.activate(p, true))  p.sendMessage("§3Vanish » §aActivé !");
				}else{
					Vanisher.desactivate(vp, true);
					p.sendMessage("§3Vanish » §cDésactivé !");
				}
			}else{
				if(args[0].equalsIgnoreCase("list")){
					StringBuilder sb = new StringBuilder();
					for(Map.Entry<String, VanishedPlayer> e : Vanisher.vanisheds.entrySet())
						if(!e.getValue().toUnVanish)
							sb.append("\n§7- §b").append(e.getKey());
					if(sb.length()==0)p.sendMessage("§7Il n'y a aucun joueur en §3Vanish§7 actuellement ");
					else p.sendMessage("§7Joueurs en §3Vanish§7 actuellement : "+sb.toString());
				}else{
					Player target = Bukkit.getPlayer(args[0]);
					if(target==null) p.sendMessage("§cCe joueur n'est pas connecté ou n'existe pas !");
					else{
						VanishedPlayer vp = Vanisher.vanisheds.get(target.getName());
						if(vp==null){
							Vanisher.activate(target, true);
							p.sendMessage("§3Vanish » §aActivé §bpour §9"+target.getName()+"§b !");
							target.sendMessage("§3Vanish » §aActivé §bpar §9"+p.getName()+"§b !");
						}else{
							Vanisher.desactivate(vp, true);
							p.sendMessage("§3Vanish » §cDésactivé §bpour §9"+target.getName()+"§b !");
							target.sendMessage("§3Vanish » §cDésactivé §bpar §9"+p.getName()+"§b !");
						}
					}
				}
			}
		}else p.sendMessage("§cTu n'as pas accès à cette commande !");
	return true;
	}
}
