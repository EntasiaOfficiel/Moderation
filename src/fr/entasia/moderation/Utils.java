package fr.entasia.moderation;

import fr.entasia.apis.utils.PlayerUtils;
import fr.entasia.apis.utils.ServerUtils;
import fr.entasia.apis.utils.ReflectionUtils;
import fr.entasia.moderation.obj.CPSSeen;
import fr.entasia.moderation.obj.ModeredPlayer;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.UUID;

public class Utils {

	public static HashMap<UUID, ModeredPlayer> playerCache = new HashMap<>();
	public static Field blockStrength;
	public static Method blockGetById;

	protected static void init() throws Throwable {
		Class<?> block = ReflectionUtils.getNMSClass("Block");

		blockStrength = block.getDeclaredField("strength");
		blockStrength.setAccessible(true);

		blockGetById = block.getDeclaredMethod("getById", int.class);

	}

	public static void acMessage(Player by, String perm, String message){
		ServerUtils.permMsg(perm, "§cAC-Alerte §8» §c"+by.getName()+" §8» §4"+message+" §7("+ PlayerUtils.getPing(by, true)+"§7 ms)");
	}

	public static boolean cpsCheck(Player on, Player by, int time){
		return cpsCheck(playerCache.get(on.getUniqueId()), playerCache.get(by.getUniqueId()), time);
	}

	public static boolean cpsCheck(ModeredPlayer on, ModeredPlayer by, int time){
		for(CPSSeen l : on.seen){
			if(l.by.equals(by))return false;
		}
		on.seen.add(new CPSSeen(by, time));
		return true;
	}

}
