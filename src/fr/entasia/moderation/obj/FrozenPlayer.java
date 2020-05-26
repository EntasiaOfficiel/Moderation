package fr.entasia.moderation.obj;

import fr.entasia.moderation.Main;
import fr.entasia.moderation.utils.tasks.FreezeTask;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.List;

public class FrozenPlayer {

	public Player p;
	public ItemStack[] inv;
	public Location loc;
	public FreezeTask task;


	public FrozenPlayer(){
	}

	public FrozenPlayer(Player p){
		this.p = p;
		inv = p.getInventory().getContents();
		loc = p.getLocation();
		loc = new Location(p.getWorld(), loc.getX(), loc.getY(), loc.getZ());
		task = new FreezeTask(this);
		task.runTaskTimerAsynchronously(Main.main, 0, 10);
	}

	public void applyFreeze(){
		p.getInventory().clear();
		p.getActivePotionEffects().clear();
		p.getActivePotionEffects().add(new PotionEffect(PotionEffectType.BLINDNESS, 99999, 255, false));
		p.getActivePotionEffects().add(new PotionEffect(PotionEffectType.NIGHT_VISION, 99999, 255, false));
		p.getActivePotionEffects().add(new PotionEffect(PotionEffectType.JUMP, 99999, 255, false));
		p.getActivePotionEffects().add(new PotionEffect(PotionEffectType.SLOW, 99999, 255, false));

		ItemStack it = new ItemStack(Material.ICE);
		ItemMeta m = it.getItemMeta();
		m.setDisplayName("§3§lIce spell !");
		List<String> as = new ArrayList<>();
		as.add("§9Tsssssss ! C'est pas très très gentil d'être méchant !");
		m.setLore(as);
		it.setItemMeta(m);

		for(int i=0;i<9;i++) p.getInventory().setItem(i,it);
		p.getInventory().setHelmet(it);

		p.setFlySpeed(0f);
		p.setWalkSpeed(0f);
	}

}
