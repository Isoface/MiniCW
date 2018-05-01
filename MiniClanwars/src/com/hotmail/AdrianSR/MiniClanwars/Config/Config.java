package com.hotmail.AdrianSR.MiniClanwars.Config;

import java.util.Arrays;
import java.util.List;

import org.bukkit.configuration.file.YamlConfiguration;

import com.hotmail.AdrianSR.MiniClanwars.Main;

public enum Config
{
	MAX_PLAYERS_PER_TEAM("Max-Players-Per-Team", Integer.valueOf(35)),
	
	SERVER_BUNGEE_LOBBY("Server-Bungee", "my bungee lobby"),
	//SERVER_BUNGEE_SECONDS("Server-Bungee-Seconds", 3),
	SERVER_BUGEE_USE("Server-Bungee-Use", true),
	
	
	WITH_OUT_PERMISSIONS("Sin-permiso", "&cNo Tienes Permisos para utilizar este comando!"),
	MUST_BE_A_PLAYER("Tienes-que-ser-un-Jugador", "&cTienes que ser un jugador para utilizar este comando!"),
	
	MODO_PRIVADO_ACTIVO("Modo-privado-activado", "&aModo privado Activado"),
	MODO_PRIVADO_DESACTIVO("Modo-privado-desactivado", "&aModo privado &cDesactivado"),
	
	EQUIPO_CON_LIDER("Equipo-con-Lider", "&cEste equipo ya tiene un lider establecido!"),
	EQUIPO_SIN_LIDER("Equipo-sin-Lider", "&cEste equipo no tiene un lider establecido!"),
	
	MUST_BE_IN_A_CLAN("Sin-Clan", "&cTienes que estar en un clan para jugar!"),
	
	EQUIPO_INVALIDO("Equipo-Invalido", "&cEste equipo no Existe!"),
	JUGADOR_INVALIDO("Juador-Invalido", "&cNo se encontro el jugador!"),
	MAXIMO_JUGADORES("Jugadores-Maximo-Exedido", "&cEl maximo de jugadores por clan es de %#"),
	
	KICK_MESSAGE("Kick-Mensaje", "&cFuiste kickeado de tu clan!"),
	
	COMANDO_INVALIDO("Comando-Invalido", "&cComando Invalido! Use: /ClanWars help, para obtener la lista de comandos."),
	
	LIDER_ESTABLECIDO("Lider-Establecido", "&aSe establecio el lider del equipo %w"),
	LIDER_BORRADO("Lider-Removido", "&aSe removio el lider del equipo %w"),
	
	JUGADOR_AGREGADO("Juagdor-Agregado", "&aAgregaste el jugador &6%w a tu clan!"),
	JUGADOR_REMOVIDO("Juagdor-Removido", "&eRemoviste el jugador &6%w de tu clan!"),
	JUGADOR_CON_CLAN("Jugador-con-clan", "&cEste jugador ya esta en un clan!"),
	JUGADOR_SIN_CLAN("Jugador-sin-clan", "&cEste jugador no esta en un clan!"),
	
	NO_ERES_LIDER("No-Eres-Lider", "&cTienes que ser el lider de un clan para utilizar este comando!"),
	
	PLAYER_LIST("Player-list", "&aJugadores en mi clan:"),
	PLAYER_LIST_PLAYER_MOSTRADO("Player-list-player-mostrado", "- &6%w"),
	
	COMMAND_HELP_LINES("Comand-Help", Arrays.asList(new String[] 
	{
		"&aComandos:",
		"&a- /ClanWars &6lider [nick] [Team]: &aEstablecer un jugador como lider de ese equipo.",
		"&a- /ClanWars &6dd [Player]: &aAgregar jugador a tu clan.",
		"&a- /ClanWars &6remove [Player]: &aRemover jugador de tu clan.",
		"&a- /ClanWars &6list: &aVer la lista de jugadores en tu clan."
	}));
	
	// Variables
	private final String path;
    private final Object def;
    private static YamlConfiguration file;
	
	Config(String path, Object def) 
	{
		this.path = path;
		this.def = def;
	}
	
	public String getPath()
	{
		return path;
	}
	
	public Object getDefault() 
	{
		return def;
	}
	
	/**
	 * Set the {@code YamlConfiguration} to use.
	 * 
	 * @param config
	 *            The config to set.
	 */
	public static void setFile(YamlConfiguration config)
	{
		file = config;
	}
	
	@Override
	public String toString()
	{
		if (!(def instanceof String))
			return null;
		//
		final String gg = file.getString(path, (String)def);
		//
		return Main.wc(gg != null ? gg : "" + def);
	}
	
	public String toShortenString(int characters)
	{
		final String string = toString();
		//
		if(string.length() <= characters)
			return string;
		//
		return string.substring(0, characters);
	}
	
	public String toStringReplaceAll(String oldValue, String newValue)
	{
		return toString().replaceAll(oldValue, newValue);
	}
	
	public String toStringReplacement(String word)
	{
		return replaceWord(toString(), word);
	}
	
	public String toStringReplacement(int number)
	{
		return replaceNumber(toString(),number);
	}
	
	public String toStringReplacement(int number, String word)
	{
		return replaceWord(replaceNumber(toString(),number),word);
	}
	
	private String replaceWord(String string, String word)
	{
		return string.replace("%w", word);
	}
	
	private String replaceNumber(String string, int number)
	{
		return string.replace("%#", ""+number);
	}
	
	public String[] toStringArray()
	{
		String s = toString();
		//
		if(s.contains("{n}"))
			return s.split("{n}");
		else return new String[] {s};
	}
	
	public String[] toStringArray(int number)
	{
		String s = this.toStringReplacement(number);
		if(s.contains("{n}"))
			return s.split("{n}");
		else return new String[] {s};
	}
	
	public String[] toStringArray(int number, String word)
	{
		String s = this.toStringReplacement(number, word);
		if(s.contains("{n}"))
			return s.split("{n}");
		else return new String[] {s};
	}
	
	public String[] toStringArray(String word)
	{
		String s = this.toStringReplacement(word);
		if(s.contains("{n}"))
			return s.split("{n}");
		else return new String[] {s};
	}
	
	public int toInt() 
	{
		if (!(def instanceof Integer))
			return 0;
		//
		return file.getInt(path, (Integer)def);
	}
	
	public boolean toBoolean() 
	{
		if (!(def instanceof Boolean))
			return false;
		//
		return file.getBoolean(path, (Boolean)def);
	}
	
	@SuppressWarnings("unchecked")
	public List<String> toStringList()
	{
		if (!(def instanceof List))
			return null;
		//
		final List<String> list = file.getStringList(path);
		//
		return list != null ? list : (List<String>)def;
	}
	
	@SuppressWarnings("unchecked")
	public List<Integer> toIntegerList()
	{
		if (!(def instanceof List))
			return null;
		//
		final List<Integer> list = file.getIntegerList(path);
		//
		return list != null ? list : (List<Integer>)def;
	}
}
