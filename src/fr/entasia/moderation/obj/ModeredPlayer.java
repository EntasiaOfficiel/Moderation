package fr.entasia.moderation.obj;

import org.bukkit.entity.Player;

import java.util.ArrayList;

public class ModeredPlayer {

	public Player p;
	public int cps;
	public ArrayList<CPSSeen> seen = new ArrayList<>();

	public ModeredPlayer(Player p){
		this.p = p;
	}
}
