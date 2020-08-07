package fr.entasia.moderation.listeners;

import fr.entasia.moderation.Utils;
import fr.entasia.moderation.obj.ModeredPlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

public class Moderation implements Listener {

	@EventHandler()
	public void a(PlayerInteractEvent e) throws Exception {
		if(e.getAction()==Action.LEFT_CLICK_BLOCK){
			if(Utils.getStrength(e.getClickedBlock())!=0){
				ModeredPlayer mp = Utils.playerCache.get(e.getPlayer().getUniqueId());
				mp.cps++;
			}
		}

//		e.setCancelled(true);
//			field.setFloat(nmsBlock, 0F);
//			Bukkit.broadcastMessage("OK");
	}
}
