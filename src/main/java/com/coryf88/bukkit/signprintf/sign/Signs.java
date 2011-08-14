package com.coryf88.bukkit.signprintf.sign;

import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.logging.Level;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.coryf88.bukkit.signprintf.SignPrintf;

public class Signs implements Runnable, Iterable<Sign> {
	private ArrayList<Sign> signs = new ArrayList<Sign>();

	private static final int signs_db = 0;

	/**
	 * Load the signs from the database.
	 */
	public void load() {
		File file = new File(SignPrintf.instance.getDataFolder(), "signs.db");
		if (file.exists()) {
			ObjectInputStream objectStream = null;
			try {
				objectStream = new ObjectInputStream(new FileInputStream(file));
				Object obj = null;

				if (objectStream.readInt() < Signs.signs_db) {
					// Update database. Not needed, yet...
				}

				while ((obj = objectStream.readObject()) != null) {
					if (obj instanceof Sign) {
						Sign sign = (Sign)obj;
						this.signs.add(sign);
						SignPrintf.instance.log.Log(Level.FINEST, "Sign loaded at " + sign.location.getWorld().getName() + ", " + sign.location.getBlockX() + ", " + sign.location.getBlockY() + ", " + sign.location.getBlockZ() + ".");
					}
				}
			} catch (EOFException ex) {} catch (Exception ex) {
				ex.printStackTrace();
			} finally {
				try {
					if (objectStream != null) {
						objectStream.close();
					}
				} catch (Exception ex) {}
			}
		}
	}

	/**
	 * Save the signs to the database.
	 */
	public void save() {
		SignPrintf.instance.getDataFolder().mkdirs();
		File file = new File(SignPrintf.instance.getDataFolder(), "signs.db");
		file.delete();
		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		ObjectOutputStream objectStream = null;
		try {
			objectStream = new ObjectOutputStream(new FileOutputStream(file));
			objectStream.writeInt(Signs.signs_db);
			for (Sign sign : this.signs) {
				objectStream.writeObject(sign);
				SignPrintf.instance.log.Log(Level.FINEST, "Sign saved at " + sign.location.getWorld().getName() + ", " + sign.location.getBlockX() + ", " + sign.location.getBlockY() + ", " + sign.location.getBlockZ() + ".");
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			try {
				if (objectStream != null) {
					objectStream.close();
				}
			} catch (Exception ex) {}
		}
	}

	/**
	 * Clear the signs.
	 */
	public void clear() {
		this.signs.clear();
	}

	/**
	 * Add a sign.
	 * 
	 * @param player The player who placed the sign.
	 * @param s The sign to add.
	 * @return true if the sign was added, otherwise false.
	 */
	public boolean add(Player player, org.bukkit.block.Sign s) {
		Sign sign = new Sign(player, s);
		if (!sign.error) {
			SignPrintf.instance.signs.save();
			SignPrintf.instance.log.Log(Level.FINE, "Sign created at " + sign.location.getWorld().getName() + ", " + sign.location.getBlockX() + ", " + sign.location.getBlockY() + ", " + sign.location.getBlockZ() + ".");
			return this.signs.add(sign);
		}
		return false;
	}

	/**
	 * Returns an iterator to iterate through the signs.
	 * 
	 * @return The iterator.
	 */
	public Iterator<Sign> getIterator() {
		return this.signs.iterator();
	}

	/**
	 * Get a sign.
	 * 
	 * @param index The index of the sign to retrieve.
	 * @return The sign.
	 */
	public Sign get(int index) throws IndexOutOfBoundsException {
		return this.signs.get(index);
	}

	/**
	 * Get a sign.
	 * 
	 * @param location The location of the sign to retrieve.
	 * @return The sign, or null if no sign exists at the location.
	 */
	public Sign get(Location location) {
		for (Sign sign : this.signs) {
			if (sign.isAt(location)) return sign;
		}
		return null;
	}

	/**
	 * Check if the sign exists.
	 * 
	 * @param s
	 * @return true if the sign exists, otherwise false.
	 */
	public boolean contains(Sign s) {
		return this.signs.contains(s);
	}

	/**
	 * Remove a sign.
	 * 
	 * @param index The index of the sign to remove.
	 * @return The sign that was removed.
	 */
	public Sign remove(int index) throws IndexOutOfBoundsException {
		return this.signs.remove(index);
	}

	/**
	 * Remove a sign.
	 * 
	 * @param s The sign to remove.
	 * @return true if the sign was removed, otherwise false.
	 */
	public boolean remove(Sign sign) {
		boolean result = this.signs.remove(sign);
		if (result) {
			this.save();
			SignPrintf.instance.log.Log(Level.FINE, "Sign removed at " + sign.location.getWorld().getName() + ", " + sign.location.getBlockX() + ", " + sign.location.getBlockY() + ", " + sign.location.getBlockZ() + ".");
		}
		return result;
	}

	/**
	 * Get the number of signs.
	 * 
	 * @return The number of signs.
	 */
	public int size() {
		return this.signs.size();
	}

	/**
	 * Get a waypoint's destination.
	 * 
	 * @param key The key of the waypoint.
	 * @return The destination if the waypoint exists, otherwise null.
	 */
	public Location getDestination(int key) {
		for (Sign sign : this.signs) {
			if (!sign.error && sign.waypoint != null && sign.waypoint.key == key) return sign.waypoint.destination;
		}
		return null;
	}

	@Override
	/**
	 * Update the signs.
	 * 
	 * Note: Should not be called manually.
	 */
	public void run() {
		Iterator<Sign> iter = this.signs.iterator();
		while (iter.hasNext()) {
			Sign s = iter.next();
			if (s != null) {
				if (s.error) {
					this.signs.remove(s);
				} else if (s.sign.getWorld().isChunkLoaded(s.sign.getChunk())) {
					s.update();
				}
			}
		}
	}

	@Override
	/**
	 * Get an iterator for the signs.
	 * 
	 * @return An iterator for the signs.
	 */
	public Iterator<Sign> iterator() {
		return this.signs.iterator();
	}
}