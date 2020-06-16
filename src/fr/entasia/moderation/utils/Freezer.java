package fr.entasia.moderation.utils;

import fr.entasia.moderation.obj.FrozenPlayer;
import org.bukkit.Particle;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class Freezer {


	public static Map<String, FrozenPlayer> frozens = new HashMap<>();

	public static void freeze(Player p) {
		if(frozens.containsKey(p.getName()))return;
		FrozenPlayer fp = new FrozenPlayer(p);
		frozens.put(p.getName(), fp);

		fp.applyFreeze();
		fp.p.sendMessage("§cTu as été freeze ! Merci de ne pas te déconnecter , ou cela te menera à un bannissement !");
		p.spawnParticle(Particle.MOB_APPEARANCE, p.getLocation(),15);
	}

	public static void unfreeze(Player p){
		FrozenPlayer fp = frozens.get(p.getName());
		if(fp==null)return;
		frozens.remove(p.getName());
		fp.task.cancel();
		p.getInventory().setContents(fp.inv);

		p.setFlySpeed(0.1f);
		p.setWalkSpeed(0.2f);

		p.sendMessage("§aTon Freeze à été retiré !");
		p.spawnParticle(Particle.MOB_APPEARANCE, p.getLocation(),15);
	}

}
