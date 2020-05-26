package fr.entasia.moderation.utils;

import fr.entasia.apis.Serialization;
import fr.entasia.apis.socket.SocketClient;
import fr.entasia.moderation.Main;
import fr.entasia.moderation.obj.VanishedPlayer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.io.IOException;
import java.util.HashMap;

public class Vanisher {

	public static HashMap<String, VanishedPlayer> vanisheds = new HashMap<>();

	public static boolean isVanished(String name){
		VanishedPlayer vp = vanisheds.get(name);
		if(vp==null||vp.toUnVanish)return false;
		else return true;
	}

	public static boolean isVisible(String name){
		VanishedPlayer vp = vanisheds.get(name);
		return vp == null || vp.visible;
	}

	public static boolean activate(Player p, boolean global) { // activation première du vanish
		if(global&&Main.syncMode){
			SocketClient.sendData("broadcast vanish 1 "+p.getName());
			if(Main.sqlConnection.fastUpdate("INSERT INTO global.vanishs VALUES(?)", p.getName())==-1){
				p.sendMessage("§cUne erreur est survenue !");
				return false;
			}
		}
		VanishedPlayer vp = new VanishedPlayer(p);
		vanisheds.put(p.getName(), vp);

		for(Player lp : Bukkit.getOnlinePlayers()) {
			if(vanisheds.containsKey(lp.getName())) {
				vp.p.showPlayer(lp);
			}else lp.hidePlayer(vp.p);
		}

		vp.applyVanish();
		saveVanishedPlayer(vp);
		return true;
	}

	public static boolean desactivate(VanishedPlayer vp, boolean global){ // suppression du vanish
		if(global&&Main.syncMode){
			SocketClient.sendData("broadcast vanish 0 "+vp.p.getName());
			Main.sqlConnection.checkConnect();
			if (Main.sqlConnection.fastUpdate("DELETE FROM global.vanishs where name=?", vp.p.getName()) == -1) {
				vp.p.sendMessage("§cdUne erreur est survenue !");
				return false;
			}
		}
		vanisheds.remove(vp.p.getName());

		vp.p.getInventory().setContents(Serialization.DeserialiseInv(vp.inv));
		vp.p.setMaxHealth(vp.maxhealth);
		vp.p.setHealth(vp.health);
		vp.p.setGameMode(vp.gamemode);
		vp.p.setFoodLevel(vp.feed);
		vp.p.getActivePotionEffects().clear();
		if(vp.effects!=null)vp.p.getActivePotionEffects().addAll(vp.effects);
		vp.p.setTotalExperience(vp.xp);

		vp.p.setFlySpeed(0.1f);
		vp.p.setWalkSpeed(0.2f);

		for(Player lp : Bukkit.getOnlinePlayers()){
			lp.showPlayer(vp.p);
			if(!isVisible(lp.getName())) vp.p.hidePlayer(lp);
		}

		Main.dataconfig.set("vanishs."+vp.p.getName(), null);
		try{
			Main.dataconfig.save(Main.datafile);
		}catch (IOException e){
			e.printStackTrace();
		}
		return true;
	}


	private static void saveVanishedPlayer(VanishedPlayer vp) {
		if (vp.p != null) {
			Main.dataconfig.set("vanishs." + vp.p.getName(), vp.inv + "!" + vp.xp);
			try {
				Main.dataconfig.save(Main.datafile);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

}
