package fr.entasia.moderation.utils.tasks;

import fr.entasia.moderation.obj.FrozenPlayer;
import org.bukkit.scheduler.BukkitRunnable;

public class FreezeTask extends BukkitRunnable {

	public FrozenPlayer fp;

	public FreezeTask(FrozenPlayer fp){
		this.fp = fp;
	}

	@Override
	public void run() {
		if(fp.p.isOnline()){
			fp.p.teleport(fp.loc);
		}
	}
}
