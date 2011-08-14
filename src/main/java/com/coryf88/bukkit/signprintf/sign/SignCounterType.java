package com.coryf88.bukkit.signprintf.sign;

/**
 * Sign counter type.
 */
public enum SignCounterType {
	COUNTER(0),
	COUNTER_NORESET(1),
	COUNTER_LARGE(2),
	COUNTER_NORESET_LARGE(3);

	private final int value;

	private SignCounterType(int value) {
		this.value = value;
	}

	/**
	 * Convert a value to a SignCounterType.
	 * 
	 * @param value The value.
	 * @return The SignCounterType.
	 */
	public static SignCounterType fromValue(int value) {
		for (SignCounterType type : SignCounterType.values()) {
			if (type.value == value) return type;
		}
		return COUNTER;
	}

	/**
	 * Convert to a value.
	 * 
	 * @return The value.
	 */
	public int toValue() {
		return this.value;
	}
}