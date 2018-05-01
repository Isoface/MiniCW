package com.hotmail.AdrianSR.MiniClanwars;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.messaging.PluginMessageListener;

import com.hotmail.AdrianSR.MiniClanwars.Config.Config;
import com.hotmail.AdrianSRJose.AnniPro.anniGame.AnniPlayer;
import com.hotmail.AdrianSRJose.AnniPro.anniGame.AnniTeam;

public class Main extends JavaPlugin implements PluginMessageListener
{
	private static Main instance;
	private static boolean privateMode = false;
	private static final Map<AnniTeam, List<String>> whiteLists = new HashMap<AnniTeam, List<String>>();
	private static final Map<UUID, AnniTeam> lideres = new HashMap<UUID, AnniTeam>();

	@Override
	public void onEnable() 
	{
		// Set instance
		instance = this;

		Plugin annihilation = Bukkit.getPluginManager().getPlugin("Annihilation");
		if (annihilation == null || !annihilation.isEnabled()) 
		{
			Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "[SilencioCraftCW] No se encontro el Annihilation. Desactivando...");
			Bukkit.getPluginManager().disablePlugin(this);
			return;
		}
		
		this.getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
	    this.getServer().getMessenger().registerIncomingPluginChannel(this, "BungeeCord", this);

		// Save Default Config
		this.saveDefaultConfig();
		loadConfig();

		// Print Enabled Message
		Bukkit.getConsoleSender().sendMessage(ChatColor.DARK_AQUA + "[SilencioCraftCW] " + ChatColor.GREEN + "Enabled");

		// Add AnniTeams WhiteLists
		for (AnniTeam team : AnniTeam.Teams) {
			List<String> toPut = new ArrayList<String>();
			whiteLists.put(team, toPut);
		}

		// Register Command
		new ClanWarsCommand(this);

		// Register Events
		new Eventos(this);
	}

	private void loadConfig()
	{
		// Get Config File
		File f = new File(this.getDataFolder(), "config.yml");
		YamlConfiguration config = YamlConfiguration.loadConfiguration(f);
		for (Config c : Config.values()) {
			if (!config.isSet(c.getPath())) {
				config.set(c.getPath(), c.getDefault());
			}
		}
		//
		Config.setFile(config);
	}

	public static Main getInstance() 
	{
		return instance;
	}

	public static Map<AnniTeam, List<String>> getWhiteLists()
	{
		return whiteLists;
	}

	public static Map<UUID, AnniTeam> getLideres()
	{
		return lideres;
	}

	public static List<String> getTeamWhiteList(final AnniTeam team)
	{
		return whiteLists.get(team);
	}
	
	public static boolean privateMode()
	{
		return privateMode;
	}
	
	public static void setPrivateMode(boolean b)
	{
		privateMode = b;
		if (!b) {
			return;
		}
		
		for (AnniPlayer ap : AnniPlayer.getPlayers())
		{
			if (ap != null && ap.isOnline()) 
			{
				boolean isLider = false;
				boolean hasClan = false;
				boolean isOp = false;
				boolean hasBypass = false;

				if (isLider(ap)) {
					isLider = true;
				}
				
				if (hasClan(ap)) {
					hasClan = true;
				}
				
				if (ap.getPlayer().hasPermission("CW.Bypass")) {
					hasBypass = true;
				}
				
				if (ap.getPlayer().isOp()) {
					isOp = true;
				}

				if (!isLider && !hasClan && !hasBypass && !isOp) {
					ap.getPlayer().kickPlayer(Config.MUST_BE_IN_A_CLAN.toString());
				}
			}
		}
	}

	public static boolean isLider(AnniPlayer ap) 
	{
		if (ap == null) {
			return false;
		}
		
		boolean tor = false;
		
		for (UUID p : lideres.keySet()) {
			if (p != null && p.equals(ap.getID())) {
				tor = true;
			}
		}
		
		return tor;
	}

	public static AnniTeam getLiderClanTeam(AnniPlayer lider) 
	{
		return lider != null ? lideres.get(lider.getID()) : null;
	}

	public static boolean hasClan(AnniPlayer ap)
	{
		if (ap == null) {
			return false;
		}
		
		boolean tor = false;
		
		for (AnniTeam team : AnniTeam.Teams) {
			for (String name : getTeamWhiteList(team)) {
				if (name != null && name.equalsIgnoreCase(ap.getName())) {
					tor = true;
				}
			}
		}
		
		return tor;
	}

	public static boolean hasClan(String plName) 
	{
		if (plName == null) {
			return false;
		}

		for (AnniTeam team : AnniTeam.Teams) {
			for (String name : getTeamWhiteList(team)) {
				if (name != null && name.equalsIgnoreCase(plName)) {
					return true;
				}
			}
		}
		return false;
	}

	public static AnniTeam getUserClanTeam(AnniPlayer ap) 
	{
		if (ap == null) {
			return null;
		}

		for (AnniTeam t : AnniTeam.Teams) {
			for (String name : getTeamWhiteList(t)) {
				if (name != null && name.equalsIgnoreCase(ap.getName())) {
					return t;
				}
			}
		}
		return null;
	}

	public static String wc(String g)
	{
		return g == null ? "null String" : ChatColor.translateAlternateColorCodes('&', g);
	}

	public static String remC(String g)
	{
		return g == null ? "null string" : ChatColor.stripColor(g);
	}

	public static String replaceWithNothing(String text, String toReplace)
	{
		return text == null ? "null String" : text.replace(toReplace, "");
	}

	@Override
	public void onPluginMessageReceived(String channel, Player player, byte[] data) 
	{
		
	}
}
