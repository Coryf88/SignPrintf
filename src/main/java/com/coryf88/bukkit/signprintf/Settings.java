package com.coryf88.bukkit.signprintf;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.bukkit.entity.Player;
import org.bukkit.util.config.Configuration;

import com.coryf88.bukkit.signprintf.sign.Sign;
import com.coryf88.bukkit.signprintf.util.TimeHelper;

public class Settings {
	protected Configuration config;

	/**
	 * How often the server updates the signs, in Minecraft ticks.
	 */
	public int updateTime = 10;

	public Settings(SignPrintf plugin) {
		this.config = plugin.getConfiguration();
	}

	/**
	 * Load configuration settings.
	 */
	public void load() {
		File configFile = new File(SignPrintf.instance.getDataFolder(), "config.yml");
		if (!configFile.exists()) {
			configFile.getParentFile().mkdirs();
			InputStream defaultConfig = this.getClass().getResourceAsStream("/config.yml");
			if (defaultConfig != null) {
				FileOutputStream output = null;
				try {
					output = new FileOutputStream(configFile);
					byte[] buf = new byte[8192];
					int length = 0;
					while ((length = defaultConfig.read(buf)) > 0) {
						output.write(buf, 0, length);
					}
				} catch (IOException e) {
					e.printStackTrace();
				} finally {
					try {
						if (defaultConfig != null) {
							defaultConfig.close();
						}
					} catch (IOException e) {}
					try {
						if (output != null) {
							output.close();
						}
					} catch (IOException e) {}
				}
			}
		}
		this.config.load();
		this.updateTime = TimeHelper.Milliseconds2MCTicks(this.config.getInt("update-time", TimeHelper.MCTicks2Milliseconds(this.updateTime)));
		if (this.updateTime < 1) {
			this.updateTime = 1;
		}
	}

	/**
	 * Save configuration settings.
	 */
	public void save() {
		this.config.setProperty("update-time", TimeHelper.MCTicks2Milliseconds(this.updateTime));
		this.config.save();
	}

	/**
	 * Check if a player can list all waypoints or waypoints of a different player.
	 * 
	 * @param player The player to check.
	 * @return True if the player can, otherwise false.
	 */
	public boolean canListAllWaypoints(Player player) {
		return player.hasPermission("signprintf.listwaypoints");
	}

	/**
	 * Check if a player can break the specified sign.
	 * 
	 * @param sign The sign to check.
	 * @param player The player breaking the sign.
	 * @return True if the player can break the sign, otherwise false.
	 */
	public boolean canBreakSign(Player player, Sign sign) {
		return player == null ? false : player.hasPermission("signprintf.removesigns") || sign.owner.equals(player.getName());
	}

	/**
	 * Check if a player can create a warp sign.
	 * 
	 * @param player The player to check.
	 * @return True if the player can create a warp sign, otherwise false.
	 */
	public boolean canCreateWarpSign(Player player) {
		return player == null ? false : player.hasPermission("signprintf.createwarp");
	}
}