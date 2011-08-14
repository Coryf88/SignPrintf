package com.coryf88.bukkit.signprintf;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.coryf88.bukkit.signprintf.sign.Sign;

public class CommandExecutor implements org.bukkit.command.CommandExecutor {
	private static final String[] pages = {
			ChatColor.GOLD + "SignPrintf v" + SignPrintf.instance.getDescription().getVersion() + " {PAGE}\n" + ChatColor.GOLD + "-----------------------------------------------------\nSignPrintf adds usage of formatting codes to signs. Which allow\n    you to have automatically updating information, counters,\n    and simple teleporters.\nFormatting codes consist of a percent symbol, possibly\n    followed by a numeric parameter (listed with a " + ChatColor.GRAY + "#" + ChatColor.WHITE + " after\n    the " + ChatColor.AQUA + "%" + ChatColor.WHITE + "), and one letter (case-sensitive).",
			ChatColor.GOLD + "Server Time {PAGE}\n" + ChatColor.GOLD + "-----------------------------------------------------\n" + ChatColor.AQUA + "%" + ChatColor.GRAY + "#" + ChatColor.AQUA + "z" + ChatColor.WHITE + "    Displays nothing. Sets the timezone used for the\n              displayed server time. The parameter is the offset\n              from GMT time.\n" + ChatColor.AQUA + "%h" + ChatColor.WHITE + "      Hour (24 hour format)\n" + ChatColor.AQUA + "%j" + ChatColor.WHITE + "      Hour (12 hour format)\n" + ChatColor.AQUA + "%i" + ChatColor.WHITE + "       Minute\n" + ChatColor.AQUA + "%p" + ChatColor.WHITE + "      AM/PM\n" + ChatColor.AQUA + "%t" + ChatColor.WHITE + "       Time, 24 hour format (\"" + ChatColor.AQUA + "%h" + ChatColor.WHITE + ":" + ChatColor.AQUA + "%i" + ChatColor.WHITE + "\")\n" + ChatColor.AQUA + "%u" + ChatColor.WHITE + "      Time, 12 hour format (\"" + ChatColor.AQUA + "%j" + ChatColor.WHITE + ":" + ChatColor.AQUA + "%i" + ChatColor.WHITE + " " + ChatColor.AQUA + "%p" + ChatColor.WHITE + "\")",
			ChatColor.GOLD + "Server Date {PAGE}\n" + ChatColor.GOLD + "-----------------------------------------------------\n" + ChatColor.AQUA + "%d" + ChatColor.WHITE + "     Day\n" + ChatColor.AQUA + "%m" + ChatColor.WHITE + "     Month\n" + ChatColor.AQUA + "%y" + ChatColor.WHITE + "     Year, 4 digit format\n" + ChatColor.AQUA + "%a" + ChatColor.WHITE + "     Date (\"" + ChatColor.AQUA + "%m" + ChatColor.WHITE + "/" + ChatColor.AQUA + "%d" + ChatColor.WHITE + "/" + ChatColor.AQUA + "%y" + ChatColor.WHITE + "\")",
			ChatColor.GOLD + "Minecraft Time {PAGE}\n" + ChatColor.GOLD + "-----------------------------------------------------\n" + ChatColor.AQUA + "%H" + ChatColor.WHITE + "     Hour (24 hour format)\n" + ChatColor.AQUA + "%J" + ChatColor.WHITE + "     Hour (12 hour format)\n" + ChatColor.AQUA + "%I" + ChatColor.WHITE + "      Minute\n" + ChatColor.AQUA + "%P" + ChatColor.WHITE + "     AM/PM\n" + ChatColor.AQUA + "%T" + ChatColor.WHITE + "     Time, 24 hour format (\"" + ChatColor.AQUA + "%H" + ChatColor.WHITE + ":" + ChatColor.AQUA + "%I" + ChatColor.WHITE + "\").\n" + ChatColor.AQUA + "%U " + ChatColor.WHITE + "    Time, 12 hour format (\"" + ChatColor.AQUA + "%J" + ChatColor.WHITE + ":" + ChatColor.AQUA + "%I" + ChatColor.WHITE + " " + ChatColor.AQUA + "%P" + ChatColor.WHITE + "\")",
			ChatColor.GOLD + "Minecraft Date {PAGE}\n" + ChatColor.GOLD + "-----------------------------------------------------\n" + ChatColor.AQUA + "%D" + ChatColor.WHITE + "     Day\n" + ChatColor.AQUA + "%M" + ChatColor.WHITE + "     Month\n" + ChatColor.AQUA + "%Y" + ChatColor.WHITE + "     Year, 4 digit format\n" + ChatColor.AQUA + "%Z" + ChatColor.WHITE + "     Date (\"" + ChatColor.AQUA + "%M" + ChatColor.WHITE + "/" + ChatColor.AQUA + "%D" + ChatColor.WHITE + "/" + ChatColor.AQUA + "%Y" + ChatColor.WHITE + "\")",
			ChatColor.GOLD + "Counter {PAGE}\n" + ChatColor.GOLD + "-----------------------------------------------------\n" + ChatColor.YELLOW + "Only one type of counter can be used per sign.\n" + ChatColor.AQUA + "%" + ChatColor.GRAY + "#" + ChatColor.AQUA + "c" + ChatColor.WHITE + "    Counter, Increased by 1 when right-clicked. Count is\n              reset when left-clicked. Parameter is the starting\n              count.\n" + ChatColor.AQUA + "%" + ChatColor.GRAY + "#" + ChatColor.AQUA + "C" + ChatColor.WHITE + "    Same as c, except cannot reset the count.\n" + ChatColor.AQUA + "%" + ChatColor.GRAY + "#" + ChatColor.AQUA + "x" + ChatColor.WHITE + "    Large counter, same as c, except increases depending\n              on what type of tool the player is holding.\n              Wooden: " + ChatColor.GREEN + "5" + ChatColor.WHITE + "; Stone: " + ChatColor.GREEN + "10" + ChatColor.WHITE + "; Iron: " + ChatColor.GREEN + "25" + ChatColor.WHITE + ";\n              Gold: " + ChatColor.GREEN + "50" + ChatColor.WHITE + "; Diamond: " + ChatColor.GREEN + "100" + ChatColor.WHITE + "; Anything else: " + ChatColor.GREEN + "1" + ChatColor.WHITE + ".\n" + ChatColor.AQUA + "%" + ChatColor.GRAY + "#" + ChatColor.AQUA + "X" + ChatColor.WHITE + "    Same as x, except cannot reset the count.",
			ChatColor.GOLD + "Teleporter {PAGE}\n" + ChatColor.GOLD + "-----------------------------------------------------\n" + ChatColor.YELLOW + "Multiple teleporters can teleport to the same waypoint.\n" + ChatColor.YELLOW + "Only one waypoint per number.\n" + ChatColor.AQUA + "%" + ChatColor.GRAY + "#" + ChatColor.AQUA + "w" + ChatColor.WHITE + "    Teleporter. Right clicking on the sign will teleport to\n              the waypoint. The parameter is the waypoint\n              to teleport to.\n" + ChatColor.AQUA + "%" + ChatColor.GRAY + "#" + ChatColor.AQUA + "W" + ChatColor.WHITE + "    Waypoint. The parameter is used to identify this waypoint\n              and will be used in a teleporter. Your location, not\n              the sign, will be the destination.",
			ChatColor.GOLD + "Misc {PAGE}\n" + ChatColor.GOLD + "-----------------------------------------------------\n" + ChatColor.AQUA + "%%" + ChatColor.WHITE + "     Displays a %\n" + ChatColor.AQUA + "%v" + ChatColor.WHITE + "     Current version of SignPrintf."
	};

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (label.equalsIgnoreCase("signhelp"))
			return this.cmdSignhelp(sender, args);
		else if (label.equalsIgnoreCase("waypoints"))
			return this.cmdWaypoints(sender, args);
		else if (label.equalsIgnoreCase("signwarp"))
			return this.cmdSignwarp(sender, args);
		return false;
	}

	private boolean cmdSignhelp(CommandSender sender, String[] args) {
		if (args.length == 0 || args.length == 1) {
			int page = 0;
			if (args.length > 0) {
				try {
					page = Integer.parseInt(args[0]) - 1;
				} catch (NumberFormatException e) {
					return false;
				}
			}
			if (page < 0) {
				page = 0;
			}

			if (page >= CommandExecutor.pages.length) {
				page = CommandExecutor.pages.length - 1;
			}

			String[] lines = CommandExecutor.pages[page].split("\n");
			for (int i = 0; i < lines.length; i++) {
				if (i == 0) {
					sender.sendMessage(lines[i].replace("{PAGE}", "(Page " + (page + 1) + "/" + CommandExecutor.pages.length + ")"));
				} else {
					for (String line : lines[i].split("\n")) {
						sender.sendMessage(line);
					}
				}
			}
			return true;
		}
		return false;
	}

	private boolean cmdWaypoints(CommandSender sender, String[] args) {
		if (args.length == 0 || args.length == 1) {
			boolean allWaypoints = false;
			String playerName = "";
			Player player = null;
			if (sender instanceof Player) {
				player = (Player)sender;
				playerName = player.getName();
			}
			if (args.length > 0 && SignPrintf.instance.config.canListAllWaypoints(player)) {
				if (args[0].equalsIgnoreCase("-a")) {
					allWaypoints = true;
				} else {
					playerName = args[0];
				}
			} else if (!(sender instanceof Player)) {
				allWaypoints = true;
				playerName = "";
			}

			if (allWaypoints && SignPrintf.instance.signs.size() == 0) {
				sender.sendMessage("No waypoints have been created.");
				return true;
			}

			StringBuilder waypoints = new StringBuilder();
			for (Sign sign : SignPrintf.instance.signs) {
				if (sign.error || sign.waypoint == null || !allWaypoints && !sign.owner.equals(playerName)) {
					continue;
				}
				if (waypoints.length() > 0) {
					waypoints.append(", ");
				}
				waypoints.append(sign.waypoint.key);
			}
			if (waypoints.length() == 0) {
				waypoints.append((playerName.equalsIgnoreCase(player.getName()) ? "You have" : playerName + " has") + "n't created any waypoints.");
			} else {
				waypoints.insert(0, (allWaypoints ? "All" : playerName.equalsIgnoreCase(player.getName()) ? "Your" : playerName + "'s") + " waypoints: ");
			}
			sender.sendMessage(waypoints.toString());
			return true;
		}
		return false;
	}

	private boolean cmdSignwarp(CommandSender sender, String[] args) {
		if (args.length == 1) {
			int key;
			try {
				key = Integer.parseInt(args[0]);
			} catch (NumberFormatException e) {
				return false;
			}

			Location destination = SignPrintf.instance.signs.getDestination(key).clone();
			if (destination != null) {
				if (sender instanceof Player) {
					Player player = (Player)sender;
					destination.setPitch(player.getLocation().getPitch());
					destination.setYaw(player.getLocation().getYaw());
					player.teleport(destination);
				} else {
					sender.sendMessage("Not supported.");
				}
			} else {
				sender.sendMessage("Waypoint " + Integer.toString(key) + " doesn't exist.");
			}
			return true;
		}
		return false;
	}
}