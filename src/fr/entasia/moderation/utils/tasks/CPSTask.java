package fr.entasia.moderation.utils.tasks;

import fr.entasia.apis.TextUtils;
import fr.entasia.moderation.Utils;
import fr.entasia.moderation.obj.CPSSeen;
import fr.entasia.moderation.obj.ModeredPlayer;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;

public class CPSTask extends BukkitRunnable{

	@Override
	public void run() {
		for(ModeredPlayer mp : Utils.playerCache.values()){

			if(mp.cps > 18){
				Utils.acMessage(mp.p, "ac.notify.cps", "§4CPS §7"+mp.cps);
			}

			for(CPSSeen cs : new ArrayList<>(mp.seen)) {
				if (cs.go) {
					cs.time++;
					cs.by.p.sendMessage("§4CPS §c" + mp.p.getName() + "§8» " + TextUtils.fill(String.valueOf(cs.time), ' ', 2) + " §4|" + " §7" + mp.cps + " §4CPS");
					if(mp.cps>cs.max)cs.max = mp.cps;
					if(mp.cps<cs.min)cs.min = mp.cps;
					cs.moy += mp.cps;
					if (cs.time >= cs.maxtime){
						cs.by.p.sendMessage("§cTest de CPS terminé pour "+mp.p.getName());
						cs.by.p.sendMessage("§cMaximum/Minimum : "+cs.max+"/"+cs.min);
						cs.by.p.sendMessage("§cMoyenne : "+(cs.moy/(float)cs.maxtime));
						mp.seen.remove(cs);
					}
				} else cs.go = true;
			}

			mp.cps = 0;

		}
	}


	public static class CPSPlayer {
		public Integer click = 0;
	}

}
