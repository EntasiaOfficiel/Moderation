package fr.entasia.moderation.listeners;

import fr.entasia.moderation.Utils;
import fr.entasia.moderation.obj.ModeredPlayer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_12_R1.block.CraftBlock;
import org.bukkit.craftbukkit.v1_12_R1.util.CraftMagicNumbers;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import java.lang.reflect.Field;

public class Moderation implements Listener {

	@EventHandler()
	public void a(PlayerInteractEvent e) throws Exception {
		if(e.getAction()==Action.LEFT_CLICK_BLOCK){
			Object nmsBlock = Utils.blockGetById.invoke(null, e.getClickedBlock().getType().getId());
			float h = Utils.blockStrength.getFloat(nmsBlock);
			if(h!=0){
				ModeredPlayer mp = Utils.playerCache.get(e.getPlayer().getUniqueId());
				mp.cps++;
			}
		}

//		e.setCancelled(true);
//			field.setFloat(nmsBlock, 0F);
//			Bukkit.broadcastMessage("OK");
	}
}
