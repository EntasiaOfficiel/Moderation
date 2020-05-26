package fr.entasia.moderation;

import fr.entasia.apis.Serialization;
import fr.entasia.apis.socket.SocketClient;
import fr.entasia.apis.socket.SocketEvent;
import fr.entasia.apis.sql.SQLConnection;
import fr.entasia.moderation.commands.CPSCommand;
import fr.entasia.moderation.commands.FreezeCommand;
import fr.entasia.moderation.commands.VanishCommand;
import fr.entasia.moderation.listeners.Basics;
import fr.entasia.moderation.listeners.Moderation;
import fr.entasia.moderation.obj.FrozenPlayer;
import fr.entasia.moderation.obj.VanishedPlayer;
import fr.entasia.moderation.utils.Freezer;
import fr.entasia.moderation.utils.Vanisher;
import fr.entasia.moderation.utils.tasks.CPSTask;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.sql.ResultSet;
import java.util.Map.Entry;

public class Main extends JavaPlugin{

	public static SQLConnection sqlConnection;
	public static Main main;
	public static File datafile;
	public static FileConfiguration dataconfig;
	public static boolean syncMode;

	@Override
	public void onEnable() {
		try{
			main = this;
			Bukkit.getConsoleSender().sendMessage("Plugin activé !");

			saveDefaultConfig();
			loadConfig();

			getCommand("vanish").setExecutor(new VanishCommand());
			getCommand("freeze").setExecutor(new FreezeCommand());
			getCommand("cps").setExecutor(new CPSCommand());
			getServer().getPluginManager().registerEvents(new Basics(), this);
			getServer().getPluginManager().registerEvents(new Moderation(), this);

			Packets.init();
			Utils.init();

			new CPSTask().runTaskTimerAsynchronously(this, 0, 20);

			if(syncMode){
				sqlConnection = new SQLConnection("moderation");
				ResultSet rs = sqlConnection.connection.prepareStatement("SELECT * from global.vanishs").executeQuery();
				while(rs.next()){
					Vanisher.vanisheds.putIfAbsent(rs.getString("name"), new VanishedPlayer());
				}
				initSockets();
			}

				
		}catch(Throwable e){
			e.printStackTrace();
			getLogger().severe("Une erreur est survenue ! ARRET DU SERVEUR");
			getServer().shutdown();
		}
	}


	@Override
	public void onDisable() {
		Bukkit.getConsoleSender().sendMessage("Plugin désactivé !");
		for(Entry<String, FrozenPlayer> e : Freezer.frozens.entrySet()){
			System.out.println("Inventaire de l'utilisateur "+e.getKey()+" ( freeze ) : " +
					Serialization.SerialiseInv(e.getValue().inv));
			Player p = Bukkit.getPlayer(e.getKey());
			if(p!=null){
				p.getInventory().clear();
			}

		}
	}


	public static void loadConfig(){
		main.reloadConfig();
		syncMode = main.getConfig().getBoolean("sync", true);

		datafile = new File(main.getDataFolder()+"/data.yml");
		if(!datafile.exists())main.saveResource("data.yml", false);
		dataconfig = YamlConfiguration.loadConfiguration(datafile);

		ConfigurationSection config = dataconfig.getConfigurationSection("vanishs");
		if(config!=null){
			for(String name : config.getKeys(false)){
				VanishedPlayer vp = new VanishedPlayer();
				String[] s =  config.getString(name).split("!");
				vp.inv = s[0];
				vp.xp = Integer.parseInt(s[1]);
				vp.toUnVanish = true;
				Vanisher.vanisheds.put(name, vp);
			}
		}
	}

	public static void initSockets(){

		SocketClient.addListener(new SocketEvent("vanish") {
			@Override
			public void onEvent(String[] data) {
				if (data[0].equals("0")) { // unvanish un joueur
					VanishedPlayer vp = Vanisher.vanisheds.get(data[1]);
					if (vp != null) {
						if (vp.inv == null) {
							Vanisher.vanisheds.remove(data[1]);
						} else {
							if (vp.isOnline()) {
								new BukkitRunnable() {
									@Override
									public void run() {
										Vanisher.desactivate(vp, false);
										vp.p.sendMessage("§3Vanish » §cDésactivé §bpar une source externe !");
									}
								}.runTask(Main.main);
							} else {
								vp.toUnVanish = true;
							}
						}
					}
				} else if (data[0].equals("1")) { // vanish un joueur
					VanishedPlayer vp = Vanisher.vanisheds.get(data[1]);
					if (vp == null) {
						Player p = Bukkit.getPlayer(data[1]);
						if (p == null) Vanisher.vanisheds.put(data[1], new VanishedPlayer());
						else {
							new BukkitRunnable() {
								@Override
								public void run() {
									Vanisher.activate(p, false);
									p.sendMessage("§3Vanish » §aActivé §bpar une source externe !");
								}
							}.runTask(Main.main);
						}
					} else vp.toUnVanish = false;
				}
			}
		});
	}
}
