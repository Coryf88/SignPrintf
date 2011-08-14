package com.coryf88.bukkit.signprintf.sign;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;

import com.coryf88.bukkit.signprintf.SignPrintf;
import com.coryf88.bukkit.signprintf.util.TimeHelper;

// *TODO Redo the updating, so don't have to parse every update.
public class Sign implements Serializable {
	private static final long serialVersionUID = 224113302086757047L;
	public String owner;
	public org.bukkit.block.Sign sign;
	public Location location;
	public long placed;
	public boolean error;
	public String[] raw;

	private Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT"));

	public SignCounter counter = null;
	public SignWaypoint waypoint = null;
	public SignWarp warp = null;

	public Sign(Player owner, org.bukkit.block.Sign sign) {
		if (owner != null && sign != null) {
			this.owner = owner.getName();
			this.sign = sign;
			this.location = sign.getBlock().getLocation();
			this.placed = Calendar.getInstance(TimeZone.getTimeZone("GMT")).getTimeInMillis();
			this.raw = sign.getLines().clone();
			this.error = false;
			this.update(true);
		} else {
			// This shouldn't happen...
			this.error("Internal error 0x10" + (owner == null && sign == null ? "2" : owner == null ? "0" : "1") + ", please contact the plugin author.");
		}
	}

	/**
	 * Serializable
	 * 
	 * Note: Should not be called manually.
	 */
	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
		this.owner = in.readUTF();
		int x = in.readInt();
		int y = in.readInt();
		int z = in.readInt();
		this.placed = in.readLong();
		this.error = in.readBoolean();
		this.raw = new String[] {in.readUTF(), in.readUTF(), in.readUTF(), in.readUTF()};
		this.counter = (SignCounter)in.readObject();
		this.waypoint = (SignWaypoint)in.readObject();
		this.warp = (SignWarp)in.readObject();

		World world = Bukkit.getServer().getWorld(in.readUTF());
		if (world == null) {
			this.error = true;
		} else {
			try {
				this.sign = (org.bukkit.block.Sign)world.getBlockAt(x, y, z).getState();
			} catch (ClassCastException e) {
				this.error = true;
			}
			this.location = this.sign.getBlock().getLocation();
		}

		this.calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
	}

	/**
	 * Serializable
	 * 
	 * Note: Should not be called manually.
	 */
	private void writeObject(ObjectOutputStream out) throws IOException {
		out.writeUTF(this.owner);
		out.writeInt(this.location.getBlockX());
		out.writeInt(this.location.getBlockY());
		out.writeInt(this.location.getBlockZ());
		out.writeLong(this.placed);
		out.writeBoolean(this.error);
		out.writeUTF(this.raw[0]);
		out.writeUTF(this.raw[1]);
		out.writeUTF(this.raw[2]);
		out.writeUTF(this.raw[3]);
		out.writeObject(this.counter);
		out.writeObject(this.waypoint);
		out.writeObject(this.warp);
		out.writeUTF(this.location.getWorld().getName());
	}

	/**
	 * Handle an error message.
	 * 
	 * @param message
	 */
	private void error(String message) {
		this.error = true;

		Player player = Bukkit.getServer().getPlayer(this.owner);
		if (player != null && player.isOnline()) {
			player.sendMessage("Error occured during sign creation: " + message);
		}
	}

	/**
	 * Parse a sign's line.
	 * 
	 * @param line The line to parse.
	 * @param l The line number, used for errors.
	 * @return The parsed line.
	 * @throws SignParserException When a parse error occurs.
	 */
	private String parse(String line, int l) throws SignParserException {
		byte state = 0;
		StringBuilder parsed = new StringBuilder();
		StringBuilder parameter = new StringBuilder();
		for (int i = 0; i < line.length(); i++) {
			char c = line.charAt(i);
			switch (state) {
				case 0:
					if (c == '%') {
						state = 1;
					} else {
						parsed.append(c);
					}
					break;
				case 1:
					if (c == '%') {
						parsed.append(c);
						state = 0;
					} else {
						if (c == '-' || Character.isDigit(c)) {
							parameter.append(c);
						} else if (Character.isLetter(c)) {
							int arg = 0;
							switch (c) { // These use the parameter...
								case 'z': // %#z
								case 'c': // %#c
								case 'C': // %#C
								case 'x': // %#x
								case 'X': // %#X
								case 'w': // %#w
								case 'W': // %#W
									if (parameter.length() == 0) {
										parameter.append(0);
									} else if (parameter.length() == 1 && parameter.charAt(0) == '-') throw new SignParserException("Invalid parameter", line, i + 1);
									try {
										arg = Integer.parseInt(parameter.toString());
									} catch (Exception e) {
										throw new SignParserException("Invalid parameter", line, i);
									}
									break;
							}
							switch (c) {
								case 'z': // %#z
									this.calendar.setTimeZone(TimeZone.getTimeZone(String.format("GMT%+d", arg)));
									break;
								case 'h': // %h
									parsed.append(String.format("%02d", this.calendar.get(Calendar.HOUR_OF_DAY)));
									break;
								case 'j': // %j
									parsed.append(String.format("%02d", this.calendar.get(Calendar.HOUR) + 1));
									break;
								case 'i': // %i
									parsed.append(String.format("%02d", this.calendar.get(Calendar.MINUTE)));
									break;
								case 'p': // %p
									parsed.append(this.calendar.get(Calendar.AM_PM) == 0 ? "AM" : "PM");
									break;
								case 't': // %t
									parsed.append(String.format("%02d:%02d", this.calendar.get(Calendar.HOUR_OF_DAY), this.calendar.get(Calendar.MINUTE)));
									break;
								case 'u': // %u
									parsed.append(String.format("%02d:%02d %s", (this.calendar.get(Calendar.HOUR) + 11) % 12 + 1, this.calendar.get(Calendar.MINUTE), this.calendar.get(Calendar.AM_PM) == 0 ? "AM" : "PM"));
									break;
								case 'd': // %d
									parsed.append(String.format("%02d", this.calendar.get(Calendar.DAY_OF_MONTH)));
									break;
								case 'm': // %m
									parsed.append(String.format("%02d", this.calendar.get(Calendar.MONTH) + 1));
									break;
								case 'y': // %y
									parsed.append(String.format("%04d", this.calendar.get(Calendar.YEAR)));
									break;
								case 'a': // %a
									parsed.append(String.format("%02d/%02d/%04d", this.calendar.get(Calendar.MONTH) + 1, this.calendar.get(Calendar.DAY_OF_MONTH), this.calendar.get(Calendar.YEAR)));
									break;
								case 'H': // %H
								case 'J': // %J
								case 'I': // %I
								case 'P': // %P
								case 'T': // %T
								case 'U': // %U
									this.calendar.setTimeInMillis(TimeHelper.Minecraft2Time(this.location.getWorld().getTime()) * 1000);
									int hours = this.calendar.get(Calendar.HOUR_OF_DAY);
									int minutes = this.calendar.get(Calendar.MINUTE);
									//int seconds = this.calendar.get(Calendar.SECOND);
									int twelvehours = this.calendar.get(Calendar.HOUR);
									String timeofday = this.calendar.get(Calendar.AM_PM) == 0 ? "AM" : "PM";
									switch (c) {
										case 'H': // %H
											parsed.append(String.format("%02d", hours));
											break;
										case 'J': // %J
											parsed.append(String.format("%02d", twelvehours));
											break;
										case 'I': // %I
											parsed.append(String.format("%02d", minutes));
											break;
										case 'P': // %P
											parsed.append(timeofday);
											break;
										case 'T': // %T
											parsed.append(String.format("%02d:%02d", hours, minutes));
											break;
										case 'U': // %U
											parsed.append(String.format("%02d:%02d %s", twelvehours, minutes, timeofday));
											break;
									}
									this.calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
									break;
								case 'D': // %D
								case 'M': // %M
								case 'Y': // %Y
								case 'A': // %A
									long time = this.location.getWorld().getFullTime();
									long year = TimeHelper.Minecraft2Year(time);
									long month = TimeHelper.Minecraft2Month(time);
									long day = TimeHelper.Minecraft2Day(time);
									switch (c) {
										case 'D': // %D
											parsed.append(String.format("%02d", day));
											break;
										case 'M': // %M
											parsed.append(String.format("%02d", month));
											break;
										case 'Y': // %Y
											parsed.append(String.format("%04d", year));
											break;
										case 'A': // %A
											parsed.append(String.format("%02d/%02d/%04d", month, day, year));
											break;
									}
									break;
								case 'c': // %#c
								case 'C': // %#C
								case 'x': // %#x
								case 'X': // %#X
									if (this.counter == null) {
										SignCounterType counterType = null;
										switch (c) {
											case 'c': // %#c
												counterType = SignCounterType.COUNTER;
												break;
											case 'C': // %#C
												counterType = SignCounterType.COUNTER_NORESET;
												break;
											case 'x': // %#x
												counterType = SignCounterType.COUNTER_LARGE;
												break;
											case 'X': // %#X
												counterType = SignCounterType.COUNTER_NORESET_LARGE;
												break;
										}
										this.counter = new SignCounter(counterType);
									}
									parsed.append(this.counter.counter + arg);
									break;
								case 'w': // %#w
									if (this.warp == null) {
										if (!SignPrintf.instance.config.canCreateWarpSign(Bukkit.getServer().getPlayer(this.owner))) throw new SignParserException("Cannot create a warp sign.", this.raw[l], i + 1);

										if (SignPrintf.instance.signs.getDestination(arg) == null) throw new SignParserException("Waypoint " + Integer.toString(arg) + " doesn't exist", this.raw[l], i);

										this.warp = new SignWarp(arg);
									}
									break;
								case 'W': // %#W
									if (this.waypoint == null) {
										if (!SignPrintf.instance.config.canCreateWarpSign(Bukkit.getServer().getPlayer(this.owner))) throw new SignParserException("Cannot create a warp sign.", this.raw[l], i + 1);

										if (SignPrintf.instance.signs.getDestination(arg) != null) throw new SignParserException("Waypoint " + Integer.toString(arg) + " already exists", this.raw[l], i);

										this.waypoint = new SignWaypoint(arg, Bukkit.getServer().getPlayer(this.owner));
									}
									break;
								case 'v': // %v
									parsed.append(SignPrintf.instance.getDescription().getVersion());
									break;
								default:
									parsed.append(c);
									break;
							}
							state = 0;
						} else {
							state = 0;
						}
					}
					break;
			}
		}
		return parsed.toString();
	}

	/**
	 * Check if the sign is at the specified location.
	 * 
	 * @param location The location to check.
	 * @return True if it is, otherwise false.
	 */
	public boolean isAt(Location location) {
		return location == null ? false : this.location.getWorld().getName().equals(location.getWorld().getName()) && this.location.getBlockX() == location.getBlockX() && this.location.getBlockY() == location.getBlockY() && this.location.getBlockZ() == location.getBlockZ();
	}

	/**
	 * Handle a player interacting with this sign.
	 * 
	 * Note: Should not be called manually.
	 * 
	 * @param player The interacting player.
	 * @param holding The material the player is holding.
	 * @param action The action the player used to interact.
	 */
	public void onPlayerInteract(Player player, Action action, Material holding) {
		switch (action) {
			case RIGHT_CLICK_BLOCK:
				if (holding != null && holding == Material.FEATHER && (player.isOp() || player.getName().equals(this.owner))) {
					ArrayList<String> parts = new ArrayList<String>();
					parts.add("This sign was placed by " + (player.getName().equals(this.owner) ? "you" : this.owner) + " on " + DateFormat.getInstance().format(new Date(this.placed)));
					if (this.warp != null) {
						parts.add("teleports to waypoint " + this.warp.key);
					}
					if (this.waypoint != null) {
						parts.add("is waypoint " + this.waypoint.key);
					}
					StringBuilder sb = new StringBuilder();
					for (int i = 0; i < parts.size(); i++) {
						if (i > 0) {
							sb.append(", " + (i == parts.size() - 1 ? "and " : ""));
						}
						sb.append(parts.get(i));
					}
					player.sendMessage(sb.toString());
				} else {
					if (this.counter != null) {
						this.counter.add(holding);
					}
					if (this.warp != null) {
						Location destination = SignPrintf.instance.signs.getDestination(this.warp.key).clone();
						if (destination != null) {
							destination.setPitch(player.getLocation().getPitch());
							destination.setYaw(player.getLocation().getYaw());
							player.teleport(destination);
						} else {
							player.sendMessage("This sign teleports to a waypoint, " + this.warp.key + ", which doesn't exist.");
							SignPrintf.instance.log.warning("The sign located at <" + this.location.getWorld().getName() + ", " + this.location.getBlockX() + ", " + this.location.getBlockY() + ", " + this.location.getBlockZ() + ">, teleports to an invalid waypoint " + this.warp.key);
						}
					}
				}
				break;
			case LEFT_CLICK_BLOCK:
				if (this.counter != null) {
					this.counter.clear();
				}
				break;
		}
	}

	/**
	 * Update the sign, only if the data has changed.
	 */
	public void update() {
		this.update(false);
	}

	/**
	 * Update the sign.
	 * 
	 * @param force True to force an update. False to update only if the data has changed.
	 */
	private void update(boolean force) {
		if (this.sign != null) {
			this.calendar.setTimeZone(TimeZone.getTimeZone("GMT"));
			this.calendar.setTime(new Date());
			for (int i = 0; i < 4; i++) {
				try {
					String line = this.parse(this.raw[i], i);
					if (!line.equals(this.sign.getLine(i))) {
						this.sign.setLine(i, line);
						force = true;
					}
				} catch (SignParserException ex) {
					this.error(ex.getError() + " at " + ex.getPosition() + " in " + ex.getLine());
				}
			}
			if (force) {
				this.sign.update();
			}
		}
	}
}