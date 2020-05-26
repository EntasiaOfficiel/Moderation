package fr.entasia.moderation.obj;

public class CPSSeen {

	public ModeredPlayer by;
	public int time = 0;
	public final int maxtime;
	public boolean go = false;

	public int max;
	public int min;
	public int moy;

	public CPSSeen(ModeredPlayer by, int maxtime) {
		this.by = by;
		this.maxtime = maxtime;
	}
}
