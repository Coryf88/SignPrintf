package com.coryf88.bukkit.signprintf.listeners;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.entity.EntityExplodeEvent;

import com.coryf88.bukkit.signprintf.SignPrintf;
import com.coryf88.bukkit.signprintf.sign.Sign;

public class EntityListener extends org.bukkit.event.entity.EntityListener {
	@Override
	public void onEntityExplode(EntityExplodeEvent event) {
		if (!event.isCancelled()) {
			for (Block block : event.blockList()) {
				Material material = block.getType();
				if (material == Material.SIGN_POST || material == Material.WALL_SIGN) {
					Sign sign = SignPrintf.instance.signs.get(block.getLocation());
					if (sign != null) {
						SignPrintf.instance.signs.remove(sign);
					}
				}
			}
		}
	}
}