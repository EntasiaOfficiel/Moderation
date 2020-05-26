package fr.entasia.moderation;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.BlockPosition;
import fr.entasia.apis.utils.ReflectionUtils;
import fr.entasia.moderation.obj.ModeredPlayer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_12_R1.util.CraftMagicNumbers;
import org.bukkit.event.block.Action;

import java.lang.reflect.Field;

public class Packets {

	public static ProtocolManager pm;


	protected static void init() throws Throwable {

		pm = ProtocolLibrary.getProtocolManager();
		pm.addPacketListener(new PacketAdapter(Main.main, PacketType.Play.Client.USE_ENTITY) {
			// ---------- CPS ----------
			@Override
			public void onPacketReceiving(PacketEvent e) {
				ModeredPlayer mp = Utils.playerCache.get(e.getPlayer().getUniqueId());
				if (e.getPacket().getModifier().read(1).toString().charAt(0) == 'A') { // ATTACK = clic gauche
					mp.cps++;
				}
			}
		});
	}

}
