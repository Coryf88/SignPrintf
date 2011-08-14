package com.coryf88.bukkit.signprintf.sign;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.coryf88.bukkit.signprintf.SignPrintf;

public class SignWarp implements Serializable {
	private static final long serialVersionUID = 6524017903078905153L;

	/**
	 * The waypoint key this warp points to.
	 */
	public int key = 0;

	public SignWarp(int key) {
		this.key = key;
	}

	/**
	 * Serializable
	 */
	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
		this.key = in.readInt();
	}

	/**
	 * Serializable
	 */
	private void writeObject(ObjectOutputStream out) throws IOException {
		out.writeInt(this.key);
	}

	/**
	 * Handle interaction with a warp sign.
	 * 
	 * @param player The interacting player.
	 */
	public void handle(Player player) {
		Location destination = SignPrintf.instance.signs.getDestination(this.key).clone();
		if (destination != null) {
			destination.setPitch(player.getLocation().getPitch());
			destination.setYaw(player.getLocation().getYaw());
			player.teleport(destination);
			player.sendMessage("You have been teleported by the sign.");
		} else {
			player.sendMessage("Teleport failed. The waypoint, " + this.key + ", doesn't exist.");
		}
	}
}