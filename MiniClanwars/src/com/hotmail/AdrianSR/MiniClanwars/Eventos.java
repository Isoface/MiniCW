package com.hotmail.AdrianSR.MiniClanwars;


import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

import com.google.common.collect.Iterables;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.hotmail.AdrianSR.MiniClanwars.Config.Config;
import com.hotmail.AdrianSRJose.AnniPro.anniEvents.PlayerInteractAtAnniItemEvent;
import com.hotmail.AdrianSRJose.AnniPro.anniEvents.PlayerJoinTeamEvent;
import com.hotmail.AdrianSRJose.AnniPro.anniEvents.PlayerLeaveTeamEvent;
import com.hotmail.AdrianSRJose.AnniPro.anniGame.AnniPlayer;
import com.hotmail.AdrianSRJose.AnniPro.anniGame.AnniTeam;
import com.hotmail.AdrianSRJose.AnniPro.kits.CustomItem;
import com.hotmail.AdrianSRJose.AnniPro.kits.KitUtils;

public class Eventos implements Listener 
{
	public Eventos(JavaPlugin pl)
	{
		Bukkit.getPluginManager().registerEvents(this, pl);
	}
	
	@EventHandler
	public void onJoin(final PlayerJoinEvent eve)
	{
		//Bukkit.getConsoleSender().sendMessage("[ZapingCW] ----- " + "0");
		final Player p = eve.getPlayer();
		final AnniPlayer ap = AnniPlayer.getPlayer(p);
		if (ap != null && ap.isOnline())
		{
			//Bukkit.getConsoleSender().sendMessage("[ZapingCW] ----- " + "1");
			boolean isLider = false;
			boolean hasClan = false;
			boolean isOp = false;
			boolean hasBypass = false;

			if (Main.isLider(ap)) {
				isLider = true;
				//Bukkit.getConsoleSender().sendMessage("[ZapingCW] ----- " + "is Lider");
			}
			
			if (Main.hasClan(ap)) {
				hasClan = true;
				//Bukkit.getConsoleSender().sendMessage("[ZapingCW] ----- " + "has Clan");
			}
			
			if (ap.getPlayer().hasPermission("CW.Bypass")) {
				hasBypass = true;
				//Bukkit.getConsoleSender().sendMessage("[ZapingCW] ----- " + "Has Bypass");
			}
			
			if (ap.getPlayer().isOp()) {
				isOp = true;
				//Bukkit.getConsoleSender().sendMessage("[ZapingCW] ----- " + "is OP");
			}

			if (!isLider && !hasClan && !hasBypass && !isOp)
			{
				//Bukkit.getConsoleSender().sendMessage("[ZapingCW] ----- " + "No tiene Nada");
				if (Main.privateMode())
				{
					//Bukkit.getConsoleSender().sendMessage("[ZapingCW] ----- " + "Nada y modo privado activado");
					p.sendMessage(Config.MUST_BE_IN_A_CLAN.toString());
					if (Config.SERVER_BUGEE_USE.toBoolean()) {
						bungeeSender(p, Config.SERVER_BUNGEE_LOBBY.toString());
						//Bukkit.getConsoleSender().sendMessage("[ZapingCW] ----- " + "Enviado al lobby: '" + Config.SERVER_BUNGEE_LOBBY.toString()+"'");
					}
				}
			}
			else 
			{
				//Bukkit.getConsoleSender().sendMessage("[ZapingCW] ----- " + "Puede Entrar");
				if (!ap.hasTeam())
				{
					//Bukkit.getConsoleSender().sendMessage("[ZapingCW] ----- " + "No Tiene Equipo");
					AnniTeam t = Main.getUserClanTeam(ap);
					if (t != null) 
					{
						ClanWarsCommand.joinTeam(ap, t);
						//Bukkit.getConsoleSender().sendMessage("[ZapingCW] ----- " + "Agregando al Equipo...");
					}
				}
			}
		}
	}
	
	@EventHandler
	public void onJoinTeam(PlayerJoinTeamEvent eve) 
	{
		eve.setCancelled(true);
	}
	
	@EventHandler
	public void onLeave(PlayerLeaveTeamEvent eve) 
	{
		eve.setCancelled(true);
	}
	
	@EventHandler
	public void noSelectTeam(final PlayerInteractAtAnniItemEvent eve) 
	{
		if (KitUtils.itemHasName(eve.getItem(), CustomItem.TEAMMAP.getName())) {
			eve.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onExecuteCommand(PlayerCommandPreprocessEvent eve) 
	{
		if (eve.getMessage().startsWith("/team")) {
			eve.setCancelled(true);
		}
	}
	
	private void bungeeSender(final Player p, String to)
	{
		Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getInstance(), new Runnable()
		{
			@Override
			public void run() 
			{
				sendBungeecordMessage("ConnectOther", p.getName(), to);
				//Bukkit.getConsoleSender().sendMessage("[ZapingCW] (2) Sending player " + p.getName() + " to " + to + " (via bungeecord!)");
			}
		}, 20L);
	}
	
    private void sendBungeecordMessage(String... data) 
    {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        for (String element : data)
        {
            out.writeUTF(element);
        }
        sendBungeeMessage(out.toByteArray());
    }
    
    private void sendBungeeMessage(byte[] bytes) 
    {
        Player player = Iterables.getFirst(Bukkit.getOnlinePlayers(), null);
        if (player != null)
        {
            player.sendPluginMessage(Main.getInstance(), "BungeeCord", bytes);
        }
    }
}
