package com.hotmail.AdrianSR.MiniClanwars;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import com.hotmail.AdrianSR.MiniClanwars.Config.Config;
import com.hotmail.AdrianSRJose.AnniPro.anniGame.AnniPlayer;
import com.hotmail.AdrianSRJose.AnniPro.anniGame.AnniTeam;
import com.hotmail.AdrianSRJose.AnniPro.anniGame.Game;
import com.hotmail.AdrianSRJose.AnniPro.main.AnnihilationMain;
import com.hotmail.AdrianSRJose.AnniPro.main.TeamCommand;
import com.hotmail.AdrianSRJose.AnniPro.main.TeamCommandNew;
import com.hotmail.AdrianSRJose.AnniPro.utils.Util;
import com.hotmail.AdrianSRJose.AnniPro.utils.VersionUtils;

public class ClanWarsCommand implements CommandExecutor
{
	public ClanWarsCommand(JavaPlugin pl)
	{
		pl.getCommand("ClanWars").setExecutor(this);
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) 
	{
		if (!(sender instanceof Player))
		{
			if (args.length > 0)
			{
				if (args[0].equalsIgnoreCase("on") || args[0].equalsIgnoreCase("off")) 
				{
					Main.setPrivateMode(args[0].equalsIgnoreCase("on") ? true : false);
					
					if (args[0].equalsIgnoreCase("on")) {
						sender.sendMessage(Config.MODO_PRIVADO_ACTIVO.toString());
					}
					else {
						sender.sendMessage(Config.MODO_PRIVADO_DESACTIVO.toString());
					}
				}
				else sender.sendMessage(Config.COMANDO_INVALIDO.toString());
			}
			else sender.sendMessage(Config.COMANDO_INVALIDO.toString());
		}
		else if (sender instanceof Player)
		{
			if (args.length > 0)
			{
				if (args[0].equalsIgnoreCase("Lider")) 
				{
					if (sender.hasPermission("CW.Set.Lider")) 
					{
						if (args.length > 1) 
						{
							if (args[1].equalsIgnoreCase("set") || args[1].equalsIgnoreCase("remove")) 
							{
								if (args[1].equalsIgnoreCase("set")) 
								{
									if (args.length > 3) 
									{
										Player lider = Bukkit.getPlayer(args[2]);
										AnniPlayer ap = AnniPlayer.getPlayer(lider);
										if (ap != null && ap.isOnline()) 
										{
											AnniTeam team = AnniTeam.getTeamByName(args[3]);
											if (team != null)
											{
												if (team.isTeamDead()) {
													sender.sendMessage(ChatColor.RED + "Este equipo esta destruido!");
													return true;
												}
												
												if (!hasLider(team)) 
												{
													sender.sendMessage(Config.LIDER_ESTABLECIDO.toStringReplacement(team.getExternalColoredName()));
													setTeamLider(ap, team);
												}
												else sender.sendMessage(Config.EQUIPO_CON_LIDER.toString());
											}
											else sender.sendMessage(Config.EQUIPO_INVALIDO.toString());
										}
										else sender.sendMessage(Config.JUGADOR_INVALIDO.toString());
									}
									else sender.sendMessage(Config.COMANDO_INVALIDO.toString());
								}
								else
								{
									if (args.length > 3)
									{
										Player lider = Bukkit.getPlayer(args[2]);
										AnniPlayer ap = AnniPlayer.getPlayer(lider);
										if (ap != null && ap.isOnline()) 
										{
											AnniTeam team = AnniTeam.getTeamByName(args[3]);
											if (team != null)
											{
												if (team.isTeamDead()) {
													sender.sendMessage(ChatColor.RED + "Este equipo esta destruido!");
													return true;
												}
												
												if (hasLider(team)) 
												{
													sender.sendMessage(Config.LIDER_BORRADO.toStringReplacement(team.getExternalColoredName()));
													//
													removeTeamLider(ap, team);
													//
													if (!lider.getUniqueId().toString().equals(((Player)sender).getUniqueId().toString())) {
														lider.kickPlayer(Config.JUGADOR_SIN_CLAN.toString());
													}
												}
												else sender.sendMessage(Config.EQUIPO_SIN_LIDER.toString());
											}
											else sender.sendMessage(Config.EQUIPO_INVALIDO.toString());
										}
										else sender.sendMessage(Config.JUGADOR_INVALIDO.toString());
									}
									else sender.sendMessage(Config.COMANDO_INVALIDO.toString());
								}
							}
							else sender.sendMessage(Config.COMANDO_INVALIDO.toString());
						}
						else sender.sendMessage(Config.COMANDO_INVALIDO.toString());
					}
					else sender.sendMessage(Config.WITH_OUT_PERMISSIONS.toString());
				}
				else if (args[0].equalsIgnoreCase("add")) 
				{
					if (args.length > 1) 
					{
						Player p = (Player)sender;
						AnniPlayer ap = AnniPlayer.getPlayer(p);
						if (ap != null && ap.isOnline()) 
						{
							if (Main.isLider(ap)) 
							{
								AnniTeam team = Main.getLiderClanTeam(ap);
								if (team != null)
								{
									if (team.isTeamDead()) {
										sender.sendMessage(ChatColor.RED + "Este equipo esta destruido!");
										return true;
									}
									
									int pls = Main.getTeamWhiteList(team).size();
									if (pls < Config.MAX_PLAYERS_PER_TEAM.toInt())
									{
										String name = args[1];
										if (!Main.hasClan(name) && !Main.isLider(AnniPlayer.getPlayer(Bukkit.getPlayer(name)))) 
										{
											addPlayerToClan(name, team);
											sender.sendMessage(Config.JUGADOR_AGREGADO.toStringReplacement(name));
										}
										else sender.sendMessage(Config.JUGADOR_CON_CLAN.toString());
									}
									else sender.sendMessage(Config.MAXIMO_JUGADORES.toStringReplacement(Config.MAX_PLAYERS_PER_TEAM.toInt()));
								}
								else sender.sendMessage(Config.EQUIPO_INVALIDO.toString());
							} 
							else sender.sendMessage(Config.NO_ERES_LIDER.toString());
						}
						else sender.sendMessage(Config.JUGADOR_INVALIDO.toString()+".");
					}
					else sender.sendMessage(Config.COMANDO_INVALIDO.toString());
				}
				else if (args[0].equalsIgnoreCase("remove")) 
				{
					if (args.length > 1) 
					{
						Player p = (Player)sender;
						AnniPlayer ap = AnniPlayer.getPlayer(p);
						if (ap != null && ap.isOnline()) 
						{
							if (Main.isLider(ap)) 
							{
								AnniTeam team = Main.getLiderClanTeam(ap);
								if (team != null)
								{
									if (team.isTeamDead()) {
										sender.sendMessage(ChatColor.RED + "Este equipo esta destruido!");
										return true;
									}
									
									String name = args[1];
									if (Main.hasClan(name) && !Main.isLider(AnniPlayer.getPlayer(Bukkit.getPlayer(name)))) {
										removePlayerFromClan(name, team);
										sender.sendMessage(Config.JUGADOR_REMOVIDO.toStringReplacement(name));
									}
									else sender.sendMessage(Config.JUGADOR_SIN_CLAN.toString());
								}
								else sender.sendMessage(Config.EQUIPO_INVALIDO.toString());
							} 
							else sender.sendMessage(Config.NO_ERES_LIDER.toString());
						}
						else sender.sendMessage(Config.JUGADOR_INVALIDO.toString()+".");
					}
					else sender.sendMessage(Config.COMANDO_INVALIDO.toString());
				}
				else if (args[0].equalsIgnoreCase("Help"))
				{
					for (String help : Config.COMMAND_HELP_LINES.toStringList()) {
						sender.sendMessage(Main.wc(help));
					}
				}
				else if (args[0].equalsIgnoreCase("list"))
				{
					Player p = (Player)sender;
					AnniPlayer ap = AnniPlayer.getPlayer(p);
					if (ap != null && ap.isOnline()) 
					{
						if (Main.isLider(ap)) 
						{
							AnniTeam team = Main.getLiderClanTeam(ap);
							if (team != null)
							{
								sender.sendMessage(Config.PLAYER_LIST.toString());
								sender.sendMessage("");
								for (String s : Main.getTeamWhiteList(team)) 
								{
									sender.sendMessage(Config.PLAYER_LIST_PLAYER_MOSTRADO.toStringReplacement(s));
								}
							}
						}
						else sender.sendMessage(Config.NO_ERES_LIDER.toString());
					}
					else sender.sendMessage(Config.JUGADOR_INVALIDO.toString()+".");
				}
				else if (args[0].equalsIgnoreCase("team")) 
				{
					if (args.length > 1) 
					{
						if (args[1].equalsIgnoreCase("destroy") || args[1].equalsIgnoreCase("remove")
								|| args[1].equalsIgnoreCase("destruir") || args[1].equalsIgnoreCase("remover"))
						{
							if (args.length > 2) {
								AnniTeam team = AnniTeam.getTeamByName(args[2]);
								if (team != null) {
									if (team.isTeamDead()) {
										sender.sendMessage(ChatColor.RED + "Este equipo ya esta destruido!");
										return true;
									}
									
									// Check Game is running
									if (!Game.isGameRunning()) {
										sender.sendMessage(ChatColor.RED + "El Juego no esta iniciado!");
										return true;
									}
									
									// Destroy
									AnnihilationMain.API.destroyTeam(team);
									sender.sendMessage(team.getExternalColoredName() +  Util.wc(" &7Destruido!"));
									return true;
								}
								else sender.sendMessage(ChatColor.RED + "Invalid Team");
							}
						}
						else if (args[1].equalsIgnoreCase("health") || args[1].equalsIgnoreCase("vida"))
						{
							if (args.length > 2) {
								AnniTeam team = AnniTeam.getTeamByName(args[2]);
								if (team != null) {
									
									if (team.isTeamDead()) {
										sender.sendMessage(ChatColor.RED + "Este equipo esta destruido!");
										return true;
									}
									
									if (args.length > 3) {
										try {
											Integer t = Integer.valueOf(args[3]);
											if (t != null) {
												if (!Game.isGameRunning()) {
													sender.sendMessage(ChatColor.RED + "El Juego no esta iniciado!");
													return true;
												}
												
												if (t.intValue() > 99) {
													sender.sendMessage(ChatColor.RED + "La vida maxima es 99!");
													return true;
												}
												// Set Health
												AnnihilationMain.API.setTeamHealth(team, t.intValue());
												sender.sendMessage(team.getExternalColoredName() + ChatColor.BOLD + " Vida editada correctamente!");
												// 
												return true;
											}
										}
										catch(Throwable t) {
											sender.sendMessage(ChatColor.RED + "Numero Invalid");
											return true;
										}
									}
								}
								else sender.sendMessage(ChatColor.RED + "Invalid Team");
							}
						}
					}
				}
				else if (args[0].equalsIgnoreCase("on") || args[0].equalsIgnoreCase("off")) 
				{
					if (sender.hasPermission("CW.set.priv"))
					{
						Main.setPrivateMode(args[0].equalsIgnoreCase("on") ? true : false);
						
						if (args[0].equalsIgnoreCase("on")) {
							sender.sendMessage(Config.MODO_PRIVADO_ACTIVO.toString());
						}
						else {
							sender.sendMessage(Config.MODO_PRIVADO_DESACTIVO.toString());
						}
					}
					else sender.sendMessage(Config.WITH_OUT_PERMISSIONS.toString());
				}
				else sender.sendMessage(Config.COMANDO_INVALIDO.toString());
			}
			else sender.sendMessage(Config.COMANDO_INVALIDO.toString());
		}
		return true;
	}
	
	private void addPlayerToClan(String name, AnniTeam team) 
	{
		if (name == null) {
			return;
		}
		
		if (team == null) {
			return;
		}
		
		Main.getTeamWhiteList(team).add(name);
		Player p = Bukkit.getPlayer(name);
		AnniPlayer ap = AnniPlayer.getPlayer(p);
		if (ap != null && ap.isOnline()) {
			joinTeam(ap, team);
		}
	}
	
	private void removePlayerFromClan(final String name, final AnniTeam team) 
	{
		if (name == null) {
			return;
		}
		
		if (team == null) {
			return;
		}
		
		final Player p = Bukkit.getPlayer(name);
		final AnniPlayer ap = AnniPlayer.getPlayer(p);
		try {
			final AnniTeam old = ap.getTeam();
			if (ap.hasTeam()) {
				if (old != null) {
					old.leaveTeam(ap);
				}
				team.leaveTeam(ap);
			}
		}
		catch(Throwable t) {}
		
		Main.getTeamWhiteList(team).remove(name);
		if (ap != null && ap.isOnline()) {
			p.kickPlayer(Config.KICK_MESSAGE.toString());
		}
	}
	
	private void removeTeamLider(AnniPlayer lider, AnniTeam team)
	{
		if (lider == null || team == null) {
			return;
		}

		try {
			final AnniTeam old = lider.getTeam();
			if (lider.hasTeam()) {
				if (old != null) {
					old.leaveTeam(lider);
				}
				team.leaveTeam(lider);
			}
		}
		catch(Throwable t) {}
		
		Main.getLideres().remove(lider.getID());
		Main.getLideres().remove(lider.getID(), team);
	}
	
	private void setTeamLider(AnniPlayer lider, AnniTeam team) 
	{
		if (lider == null || team == null) {
			return;
		}
		
		Main.getLideres().put(lider.getID(), team);
		
		if (VersionUtils.is1_11() || VersionUtils.is1_12()) 
			TeamCommandNew.joinTeam(lider, team, true);
		else
			TeamCommand.joinTeam(lider, team, true);
	}
	
	public static void joinTeam(AnniPlayer lider, AnniTeam team) 
	{
		if (VersionUtils.is1_11() || VersionUtils.is1_12()) 
			TeamCommandNew.joinTeam(lider, team, true);
		else
			TeamCommand.joinTeam(lider, team, true);
	}
	
	private boolean hasLider(AnniTeam team)
	{
		for (UUID id : Main.getLideres().keySet())
		{
			if (id != null)
			{
				AnniTeam other = Main.getLideres().get(id);
				if (other != null && other.equals(team)) {
					return true;
				}
			}
		}
		return false;
	}
}
