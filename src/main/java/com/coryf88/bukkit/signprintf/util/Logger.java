package com.coryf88.bukkit.signprintf.util;

import java.util.logging.Level;

import org.bukkit.plugin.java.JavaPlugin;

public class Logger {
	private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger("Minecraft");
	private final String pluginName;

	public Logger(final JavaPlugin plugin) {
		this.pluginName = plugin.getDescription().getName();
	}

	public void config(final String msg) {
		this.Log(Level.CONFIG, msg);
	}

	public void info(final String msg) {
		this.Log(Level.INFO, msg);
	}

	public void warning(final String msg) {
		this.Log(Level.WARNING, msg);
	}

	public void severe(final String msg) {
		this.Log(Level.SEVERE, msg);
	}

	public void severe(final String msg, final Exception ex) {
		this.Log(Level.SEVERE, msg, ex);
	}

	public void Log(final String msg) {
		this.info(msg);
	}

	public void Log(final Level loglevel, final String msg) {
		Logger.logger.log(loglevel, this.format(msg));
	}

	public void Log(final Level loglevel, final String msg, final Exception ex) {
		if (msg == null) {
			this.Log(loglevel, ex);
		} else {
			Logger.logger.log(loglevel, this.format(msg), ex);
		}
	}

	public void Log(final Level loglevel, final Exception err) {
		Logger.logger.log(loglevel, this.format(err == null ? "An unknown exception has occured." : err.getMessage()), err);
	}

	private String format(final String msg) {
		return String.format("[%s] %s", this.pluginName, msg == null ? "" : msg);
	}
}