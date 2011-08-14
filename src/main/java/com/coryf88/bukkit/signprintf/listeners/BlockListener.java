package com.coryf88.bukkit.signprintf.listeners;

import java.util.ArrayList;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.SignChangeEvent;

import com.coryf88.bukkit.signprintf.SignPrintf;
import com.coryf88.bukkit.signprintf.sign.Sign;

public class BlockListener extends org.bukkit.event.block.BlockListener {
	@Override
	public void onBlockBreak(BlockBreakEvent event) {
		if (!event.isCancelled() && event.getBlock() != null) {
			Block block = event.getBlock();

			Sign[] removing = this.canRemove(event.getPlayer(), block);
			if (removing == null) {
				event.setCancelled(true);
				return;
			}
			for (Sign sign : removing) {
				SignPrintf.instance.signs.remove(sign);
			}
		}
	}

	@Override
	public void onSignChange(SignChangeEvent event) {
		if (!event.isCancelled()) {
			for (String line : event.getLines()) {
				if (line.contains("%")) {
					org.bukkit.block.Sign sign = (org.bukkit.block.Sign)event.getBlock().getState();
					for (int i = 0; i < 4; i++) {
						sign.setLine(i, event.getLine(i));
					}
					SignPrintf.instance.signs.add(event.getPlayer(), sign);
					break;
				}
			}
		}
	}

	private Sign[] canRemove(Player player, Block block) {
		ArrayList<Sign> removing = new ArrayList<Sign>();
		for (BlockFace blockFace : new BlockFace[] {BlockFace.SELF, BlockFace.UP, BlockFace.NORTH, BlockFace.WEST, BlockFace.EAST, BlockFace.SOUTH, BlockFace.DOWN}) {
			Block b = block.getRelative(blockFace);
			Material material = b.getType();
			if (material == Material.SIGN_POST || material == Material.WALL_SIGN) {
				if (blockFace != BlockFace.SELF && !blockFace.equals(((org.bukkit.material.Sign)b.getState().getData()).getAttachedFace().getOppositeFace())) {
					continue;
				}

				Sign sign = SignPrintf.instance.signs.get(b.getLocation());
				if (sign == null) {
					continue;
				}
				if (SignPrintf.instance.config.canBreakSign(player, sign)) {
					removing.add(sign);
				} else
					return null;
			}
		}
		return removing.toArray(new Sign[removing.size()]);
	}
}