package fr.entasia.moderation.commands;

import fr.entasia.moderation.Utils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CPSCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(!(sender instanceof Player))return false;
		Player p = (Player)sender;
		if(p.hasPermission("mod.cps")){
			if(args.length==0)p.sendMessage("§cSyntaxe : /cps <joueur> [<nombre d'essais>]");
			else{
				Player target = Bukkit.getPlayer(args[0]);
				if(target==null)p.sendMessage("§cCe joueur n'est pas connecté ou n'existe pas !");
				else{
					int n=10;
					if(args.length>1){
						try{
							n=Integer.parseInt(args[1]);
							if(n>20){
								p.sendMessage("§c20 sequences de test au maximum ! Le chiffre à été réduit à 20");
								n=20;
							}
						}catch(NumberFormatException e){
							p.sendMessage("§c Le nombre "+args[1]+" est invalide !");
						}
					}
					if(Utils.cpsCheck(target, p, n)) p.sendMessage("§cTest de CPS commencé sur "+target.getName()+" !");
					else p.sendMessage("§cTest de CPS déja en cours pour §l"+target.getName()+"§c !");
				}
			}
		}else p.sendMessage("§cTu n'as pas accès à cette commande !");
	return true;
	}
}
