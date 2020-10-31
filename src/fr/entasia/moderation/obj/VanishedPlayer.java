package fr.entasia.moderation.obj;

import fr.entasia.apis.other.ItemBuilder;
import fr.entasia.apis.utils.Serialization;
import fr.entasia.moderation.Main;
import fr.entasia.moderation.utils.Vanisher;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Collection;

public class VanishedPlayer {

	public Player p;
	public String inv;
	public GameMode gamemode = GameMode.SURVIVAL;
	public double maxhealth=20;
	public double health=20;
	public int feed=20;
	public Collection<PotionEffect> effects;
	public int xp;

	public boolean visible = false;

	public boolean toUnVanish = false;


	public VanishedPlayer(){
	}

	public VanishedPlayer(Player p){
		this.p = p;
		inv = Serialization.serialiseInv(p.getInventory().getContents());
		maxhealth = p.getMaxHealth();
		health = p.getHealth();
		gamemode = p.getGameMode();
		feed = p.getFoodLevel();
		effects = p.getActivePotionEffects();
		xp = p.getTotalExperience();
	}


	public boolean isOnline(){
		return p != null && p.isOnline();
	}


	public void applyVanish(){ // actualiser le vanish
		p.setFlySpeed(0.1f);
		p.setWalkSpeed(0.2f);
		p.getInventory().clear();
		p.setGameMode(GameMode.CREATIVE);
		p.setFoodLevel(20);
		p.setHealth(20);
		p.setMaxHealth(20);
		p.getActivePotionEffects().clear();
		p.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, 99999, 255, false));
		p.setTotalExperience(0);

		ItemStack item = new ItemStack(Material.BLAZE_ROD);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName("§eKB Stick");
		meta.addEnchant(Enchantment.KNOCKBACK, 3, true);
		item.setItemMeta(meta);
		p.getInventory().setItem(0, item);

		item = new ItemStack(Material.CHEST);
		meta = item.getItemMeta();
		meta.setDisplayName("§6Voir l'inventaire");
		item.setItemMeta(meta);
		p.getInventory().setItem(2, item);

		item = new ItemStack(Material.ENDER_EYE);
		meta = item.getItemMeta();
		meta.setDisplayName("§cTest de CPS");
		item.setItemMeta(meta);
		p.getInventory().setItem(3, item);

		if(visible) setInviOn();
		else setInviOff();

		item = new ItemStack(Material.GLOWSTONE_DUST);
		meta = item.getItemMeta();
		meta.setDisplayName("§aPing");
		item.setItemMeta(meta);
		p.getInventory().setItem(6, item);

		item = new ItemStack(Material.FEATHER);
		meta = item.getItemMeta();
		meta.setDisplayName("§bChanger la vitesse");
		item.setItemMeta(meta);
		p.getInventory().setItem(7, item);

		item = new ItemStack(Material.ICE);
		meta = item.getItemMeta();
		meta.setDisplayName("§3Freeze");
		item.setItemMeta(meta);
		p.getInventory().setItem(8, item);


		item = new ItemStack(Material.LEATHER_HELMET);
		LeatherArmorMeta mmeta = (LeatherArmorMeta)item.getItemMeta();
		mmeta.setColor(Color.fromRGB(0,204,255));

		item.setItemMeta(mmeta);
		p.getInventory().setHelmet(item);
		item.setType(Material.LEATHER_CHESTPLATE);
		p.getInventory().setChestplate(item);
		item.setType(Material.LEATHER_LEGGINGS);
		p.getInventory().setLeggings(item);
		item.setType(Material.LEATHER_BOOTS);
		p.getInventory().setBoots(item);
	}

	private void setInviOn(){
		ItemBuilder builder = new ItemBuilder(Material.LIME_DYE).name("§7Désactiver l'invisibilité");
		p.getInventory().setItem(4, builder.build());
	}

	private void setInviOff(){
		ItemBuilder builder = new ItemBuilder(Material.LIGHT_GRAY_DYE).name("§3Réactiver l'invisibilité");
		p.getInventory().setItem(4, builder.build());
	}

	public void setVisible(boolean mode){
		visible = mode;

		if(mode){
			for(Player lp : Bukkit.getOnlinePlayers()){
				lp.showPlayer(Main.main, p);
			}
			setInviOff();
			p.sendMessage("§3Vanish » §cInvisibilité désactivée !");
		}else{
			for(Player lp : Bukkit.getOnlinePlayers()) {
				if(!Vanisher.vanisheds.containsKey(lp.getName())){
					lp.hidePlayer(Main.main, p);
				}
			}
			setInviOn();
			p.sendMessage("§3Vanish » §aInvisibilité activée !");
		}
	}
}
