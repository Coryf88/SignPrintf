package com.coryf88.bukkit.signprintf.sign;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class SignWaypoint implements Serializable {
	private static final long serialVersionUID = 3191684671255812387L;

	/**
	 * The key of this waypoint.
	 */
	public int key = 0;

	/**
	 * The destination of this waypoint.
	 */
	public Location destination = null;

	public SignWaypoint(int key, Location destination) {
		this.key = key;
		this.destination = destination;
	}

	public SignWaypoint(int key, Player player) {
		this.key = key;
		this.destination = player.getLocation();
		player.sendMessage("Sign waypoint " + key + " has been created at your location.");
	}

	/**
	 * Serializable
	 */
	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
		this.key = in.readInt();
		this.destination = new Location(Bukkit.getServer().getWorld(in.readUTF()), in.readDouble(), in.readDouble(), in.readDouble(), in.readFloat(), in.readFloat());
	}

	/**
	 * Serializable
	 */
	private void writeObject(ObjectOutputStream out) throws IOException {
		out.writeInt(this.key);
		out.writeUTF(this.destination.getWorld().getName());
		out.writeDouble(this.destination.getX());
		out.writeDouble(this.destination.getY());
		out.writeDouble(this.destination.getZ());
		out.writeFloat(this.destination.getYaw());
		out.writeFloat(this.destination.getPitch());
	}
}