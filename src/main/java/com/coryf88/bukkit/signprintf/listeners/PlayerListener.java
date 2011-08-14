package com.coryf88.bukkit.signprintf.listeners;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.player.PlayerInteractEvent;

import com.coryf88.bukkit.signprintf.SignPrintf;
import com.coryf88.bukkit.signprintf.sign.Sign;

public class PlayerListener extends org.bukkit.event.player.PlayerListener {
	@Override
	public void onPlayerInteract(PlayerInteractEvent event) {
		if (!event.isCancelled() && event.hasBlock()) {
			Block block = event.getClickedBlock();
			if (block.getType() == Material.SIGN_POST || block.getType() == Material.WALL_SIGN) {
				Sign sign = SignPrintf.instance.signs.get(block.getLocation());
				if (sign != null) {
					sign.onPlayerInteract(event.getPlayer(), event.getAction(), event.hasItem() ? event.getItem().getType() : null);
				}
			}
		}
	}
}