package com.coryf88.bukkit.signprintf;

import org.bukkit.event.Event;
import org.bukkit.event.Event.Priority;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import com.coryf88.bukkit.signprintf.listeners.BlockListener;
import com.coryf88.bukkit.signprintf.listeners.EntityListener;
import com.coryf88.bukkit.signprintf.listeners.PlayerListener;
import com.coryf88.bukkit.signprintf.sign.Signs;
import com.coryf88.bukkit.signprintf.util.Logger;

public class SignPrintf extends JavaPlugin {
	public static SignPrintf instance;
	public Settings config;
	public Logger log;

	private PlayerListener playerListener = new PlayerListener();
	private BlockListener blockListener = new BlockListener();
	private EntityListener entityListener = new EntityListener();
	private CommandExecutor commandExecutor = null;

	public Signs signs = new Signs();

	@Override
	public void onLoad() {
		SignPrintf.instance = this;
		this.log = new Logger(this);
		this.config = new Settings(this);
		this.commandExecutor = new CommandExecutor();
	}

	@Override
	public void onEnable() {
		System.out.println("[" + this.getDescription().getName() + "] Enabled (Author: " + this.getDescription().getAuthors().get(0) + "; Version: " + this.getDescription().getVersion() + ")");
		this.config.load();
		this.registerEvents();

		this.signs.load();

		this.getCommand("signhelp").setExecutor(this.commandExecutor);
		this.getCommand("waypoints").setExecutor(this.commandExecutor);
		this.getCommand("signwarp").setExecutor(this.commandExecutor);

		this.getServer().getScheduler().scheduleAsyncRepeatingTask(this, this.signs, 0L, this.config.updateTime);
	}

	@Override
	public void onDisable() {
		this.signs.save();
		System.out.println("[" + this.getDescription().getName() + "] Disabled");
	}

	private void registerEvents() {
		PluginManager pm = this.getServer().getPluginManager();

		pm.registerEvent(Event.Type.PLAYER_INTERACT, this.playerListener, Priority.Monitor, this);
		pm.registerEvent(Event.Type.ENTITY_EXPLODE, this.entityListener, Priority.Monitor, this);
		pm.registerEvent(Event.Type.SIGN_CHANGE, this.blockListener, Priority.Monitor, this);
		pm.registerEvent(Event.Type.BLOCK_BREAK, this.blockListener, Priority.Highest, this);
	}
}