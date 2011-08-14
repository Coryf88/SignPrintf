package com.coryf88.bukkit.signprintf.sign;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import org.bukkit.Material;

public class SignCounter implements Serializable {
	private static final long serialVersionUID = 8044630069135540993L;

	/**
	 * The type of the counter.
	 */
	public SignCounterType type = null;

	/**
	 * The count.
	 */
	public int counter = 0;

	public SignCounter(SignCounterType type) {
		this.type = type;
	}

	public SignCounter(SignCounterType type, int counter) {
		this(type);
		this.counter = counter;
	}

	/**
	 * Serializable
	 */
	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
		this.type = SignCounterType.fromValue(in.readInt());
		this.counter = in.readInt();
	}

	/**
	 * Serializable
	 */
	private void writeObject(ObjectOutputStream out) throws IOException {
		out.writeInt(this.type.toValue());
		out.writeInt(this.counter);
	}

	/**
	 * Clear the counter.
	 */
	public void clear() {
		if (this.type != null) {
			switch (this.type) {
				case COUNTER:
				case COUNTER_LARGE:
					this.counter = 0;
					break;
			}
		}
	}

	/**
	 * Increment the counter.
	 * 
	 * @param holding What the player is holding.
	 */
	public void add(Material holding) {
		if (this.type != null) {
			if (holding == null) {
				holding = Material.AIR;
			}
			switch (this.type) {
				case COUNTER:
				case COUNTER_NORESET:
					this.counter++;
					break;
				default:
					switch (holding) {
						case DIAMOND_SWORD:
						case DIAMOND_SPADE:
						case DIAMOND_PICKAXE:
						case DIAMOND_AXE:
						case DIAMOND_HOE:
							this.counter += 100;
							break;
						case GOLD_SWORD:
						case GOLD_SPADE:
						case GOLD_PICKAXE:
						case GOLD_AXE:
						case GOLD_HOE:
							this.counter += 50;
							break;
						case IRON_SPADE:
						case IRON_PICKAXE:
						case IRON_AXE:
						case IRON_SWORD:
						case IRON_HOE:
							this.counter += 25;
							break;
						case STONE_SWORD:
						case STONE_SPADE:
						case STONE_PICKAXE:
						case STONE_AXE:
						case STONE_HOE:
							this.counter += 10;
							break;
						case WOOD_SWORD:
						case WOOD_SPADE:
						case WOOD_PICKAXE:
						case WOOD_AXE:
						case WOOD_HOE:
							this.counter += 5;
							break;
						default:
							this.counter++;
							break;
					}
					break;
			}
		}
	}

	/**
	 * Increment the counter
	 * 
	 * @param holding What the player is holding.
	 */
	public void add(int holding) {
		this.add(Material.getMaterial(holding));
	}
}