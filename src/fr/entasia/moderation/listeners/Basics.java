package fr.entasia.moderation.listeners;

import fr.entasia.apis.utils.PlayerUtils;
import fr.entasia.apis.utils.ReflectionUtils;
import fr.entasia.apis.utils.ServerUtils;
import fr.entasia.moderation.Utils;
import fr.entasia.moderation.obj.FrozenPlayer;
import fr.entasia.moderation.obj.ModeredPlayer;
import fr.entasia.moderation.obj.VanishedPlayer;
import fr.entasia.moderation.utils.Freezer;
import fr.entasia.moderation.utils.Vanisher;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.projectiles.ProjectileSource;

import java.util.stream.StreamSupport;

public class Basics implements Listener {

	@EventHandler(priority=EventPriority.HIGHEST)
	public void onJoin(PlayerJoinEvent e){

		Utils.playerCache.put(e.getPlayer().getUniqueId(), new ModeredPlayer(e.getPlayer()));

		VanishedPlayer vp = Vanisher.vanisheds.get(e.getPlayer().getName());
		if(vp==null){
			for(Player lp : Bukkit.getOnlinePlayers()){
				if(!Vanisher.isVisible(lp.getName())) e.getPlayer().hidePlayer(lp);
			}
			FrozenPlayer fp = Freezer.frozens.get(e.getPlayer().getName());
			if(fp!=null){
				fp.applyFreeze();
			}
		}else{
			vp.p = e.getPlayer();
			if(vp.toUnVanish){
				Vanisher.desactivate(vp, false);
				vp.p.sendMessage("§3Vanish » §cDésactivé §bpar une source externe !");
			}else{
				e.setJoinMessage("");
				vp.visible = false;
				for (Player lp : Bukkit.getOnlinePlayers()) {
					if (!Vanisher.vanisheds.containsKey(lp.getName())) lp.hidePlayer(vp.p);
				}

				if(vp.inv==null){

					Vanisher.activate(vp.p, false);
					vp.p.sendMessage("§3Vanish » §aActivé §bpar une source externe !");

				}else{
					vp.applyVanish();
					vp.p.sendMessage("§3Vanish » §bVanish restauré !");
				}
			}

		}
	}
	@EventHandler(priority=EventPriority.HIGHEST)
	public void onleave(PlayerQuitEvent e) {

		Utils.playerCache.remove(e.getPlayer().getUniqueId());

		if(!Vanisher.isVisible(e.getPlayer().getName()))e.setQuitMessage("");
		else{
			FrozenPlayer fp = Freezer.frozens.get(e.getPlayer().getName());
			if(fp!=null) {
				fp.p.getInventory().clear();
				ServerUtils.permMsg("staff.freeze", "§bFreeze » §3Joueur déconnecté en freeze : " + fp.p.getName());
			}
		}
	}

	@EventHandler
	public void onDamage(EntityDamageEvent e){

		if(e.getEntity() instanceof Player){
			if(Freezer.frozens.containsKey(e.getEntity().getName())) {
				e.setCancelled(true);
				return;
			}
			if(Vanisher.vanisheds.containsKey(e.getEntity().getName())) e.setCancelled(true);
		}
	}

	@EventHandler
	public void onDrop(PlayerDropItemEvent e){

		if(Freezer.frozens.containsKey(e.getPlayer().getName())&&!e.isCancelled()){
			e.getPlayer().sendMessage("§cTu est en état de Freeze !");
			e.setCancelled(true);
			return;
		}

		VanishedPlayer vp = Vanisher.vanisheds.get(e.getPlayer().getName());
		if(vp==null)return;
		if(!vp.visible){
			vp.p.sendMessage("§cTu ne peux pas dropper d'items en étant invisible !");
			e.setCancelled(true);
		}
	}

	@EventHandler
	public void onPickup(PlayerPickupItemEvent e){
		if(Freezer.frozens.containsKey(e.getPlayer().getName())){
			e.setCancelled(true);
			return;
		}

		if(!Vanisher.vanisheds.containsKey(e.getPlayer().getName()))return;
		VanishedPlayer vp = Vanisher.vanisheds.get(e.getPlayer().getName());
		if(!vp.visible)e.setCancelled(true);
	}


	@EventHandler
	public void onDamage(EntityDamageByEntityEvent e){

		if(Freezer.frozens.containsKey(e.getDamager().getName())) {
			e.getDamager().sendMessage("§cTu est en état de Freeze !");
			e.setCancelled(true);
			return;
		}

		if(e.getEntity() instanceof Player){
			if(Freezer.frozens.containsKey(e.getEntity().getName())){
				e.getDamager().sendMessage("§cCe joueur est dans un état de Freeze !");
				e.setCancelled(true);
				return;
			}

			VanishedPlayer vp=null;
			if(e.getDamager() instanceof Player){
				Player p = (Player)e.getDamager();
				if(p.getInventory().getItemInMainHand().hasItemMeta()&&p.getInventory().getItemInMainHand().getItemMeta().hasDisplayName()&&
						p.getInventory().getItemInMainHand().getItemMeta().getDisplayName().equals("§eKB Stick"))return;
				vp = Vanisher.vanisheds.get(e.getDamager().getName());
			}else if(e.getDamager() instanceof Projectile){
				ProjectileSource ps = ((Projectile) e.getDamager()).getShooter();
				if(ps instanceof Player) vp = Vanisher.vanisheds.get(((Player) ps).getName());
			}else return;
			if(vp==null)return;
			if(!vp.visible)e.setCancelled(true);
		}
	}

	@EventHandler
	public void onClick(PlayerInteractEntityEvent e){
		if(e.getRightClicked() instanceof Player){
			Player target = (Player) e.getRightClicked();
			VanishedPlayer vp = Vanisher.vanisheds.get(e.getPlayer().getName());
			if(vp==null)return;
			ItemStack item = e.getPlayer().getInventory().getItemInMainHand();
			if(item.hasItemMeta()&&item.getItemMeta().hasDisplayName()){
				switch(item.getItemMeta().getDisplayName()){
					case "§6Voir l'inventaire":
						e.getPlayer().sendMessage("§aVous avez ouvert l'inventaire de §7" + target.getName() + "§a !");
						e.getPlayer().openInventory(target.getInventory());
						break;
					case "§cTest de CPS":
						if(Utils.cpsCheck(target, e.getPlayer(), 7)) {
							e.getPlayer().sendMessage("§cTest de CPS commencé sur " + target.getName() + " !");
						}else{
							e.getPlayer().sendMessage("§cTest de CPS déja en cours pour §l"+target.getName()+"§c !");
						}
						break;
					case "§aPing":
						vp.p.sendMessage("§7Ping de §b"+target.getName()+"§7 : "+ PlayerUtils.getPingColor((Player)e.getRightClicked()));
						break;
					case "§bFreeze":
						if(Freezer.frozens.containsKey(target.getName())) {
							Freezer.unfreeze(target);
							e.getPlayer().sendMessage("§bFreeze » §cDésactivé sur "+target.getName()+" !");
						}else{
							if(Vanisher.vanisheds.containsKey(target.getName()))e.getPlayer().sendMessage("§cCette personne est en vanish !");
							else{
								Freezer.freeze(target);
								e.getPlayer().sendMessage("§bFreeze » §aActivé sur "+target.getName()+" !");
							}
						}
						break;
				}
			}
		}
	}

	@EventHandler
	public void onClick(PlayerInteractEvent e){
		if(e.getHand()!=EquipmentSlot.HAND)return;

//		if(e.getAction().toString().startsWith("LE")){
//			for(PlayerCPSTask ct : PlayerCPSTask.list){
//				if(ct.target.getUniqueId()==e.getPlayer().getUniqueId()) ct.lclick++;
//			}
//		}else if (e.getAction().toString().startsWith("RI")){
//			for(PlayerCPSTask ct : PlayerCPSTask.list){
//				if(ct.target.getUniqueId()==e.getPlayer().getUniqueId()) ct.rclick++;
//			}
//		}

		if(Freezer.frozens.containsKey(e.getPlayer().getName())){
			e.setCancelled(true);
			e.getPlayer().sendMessage("§cTu est en état de Freeze !");
			return;
		}

		VanishedPlayer vp = Vanisher.vanisheds.get(e.getPlayer().getName());
		if(vp==null)return;
		ItemStack item = e.getPlayer().getInventory().getItemInMainHand();

		if(item.hasItemMeta()&&item.getItemMeta().hasDisplayName()){
			switch(item.getItemMeta().getDisplayName()){
				case "§7Désactiver l'invisibilité":
					vp.setVisible(true);
					break;
				case "§3Réactiver l'invisibilité":
					vp.setVisible(false);
					break;
				case "§bChanger la vitesse":
					if(vp.p.getFlySpeed()==0.1f){
						vp.p.setFlySpeed(0.5f);
						vp.p.setWalkSpeed(0.5f);
						vp.p.sendMessage("§aVitesse accélérée !");
					}else{
						vp.p.setFlySpeed(0.1f);
						vp.p.setWalkSpeed(0.2f);
						vp.p.sendMessage("§cVitesse diminuée !");
					}
					break;
				default:
					if(!vp.visible)e.setCancelled(true);
					return;
			}
			e.setCancelled(true);
		}else if(!vp.visible)e.setCancelled(true);
	}

}
