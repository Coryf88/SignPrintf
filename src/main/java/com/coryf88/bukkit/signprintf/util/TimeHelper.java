package com.coryf88.bukkit.signprintf.util;

public class TimeHelper {
	/**
	 * <p>Converts Minecraft full time to years.</p>
	 * 
	 * <p>Example:<br />
	 * <code>Minecraft2Day(Bukkit.getServer().getWorld("world").getFullTime());</code></p>
	 * 
	 * @param time The Minecraft time, in Minecraft ticks, to convert.
	 * @return The Minecraft year.
	 */
	public static long Minecraft2Year(long time) {
		return time / 8640000 + 1;
	}

	/**
	 * <p>Converts Minecraft full time to months.</p>
	 * 
	 * <p>Example:<br />
	 * <code>Minecraft2Day(Bukkit.getServer().getWorld("world").getFullTime());</code></p>
	 * 
	 * @param time The Minecraft time, in Minecraft ticks, to convert.
	 * @return The Minecraft month.
	 */
	public static long Minecraft2Month(long time) {
		return time / 720000 + 1;
	}

	/**
	 * <p>Converts Minecraft full time to days.</p>
	 * 
	 * <p>Example:<br />
	 * <code>Minecraft2Day(Bukkit.getServer().getWorld("world").getFullTime());</code></p>
	 * 
	 * @param time The Minecraft time, in Minecraft ticks, to convert.
	 * @return The Minecraft day.
	 */
	public static long Minecraft2Day(long time) {
		return time / 24000 + 1;
	}

	/**
	 * <p>Converts Minecraft time to 'normal', 86000 seconds in a day, time.</p>
	 * 
	 * <p>Example:<br />
	 * <code>Minecraft2Time(Bukkit.getServer().getWorld("world").getTime());</code></p>
	 * <p><i>Note: World.getFullTime() should also work.</i></p>
	 * 
	 * <p>0: 6am;
	 * 6000: 12pm (noon);
	 * 12000: 6pm;
	 * 18000: 12am (midnight);
	 * 24000: 6am</p>
	 * 
	 * @param time The Minecraft time, in Minecraft ticks, to convert.
	 * @return The 'normal' time.
	 */
	public static long Minecraft2Time(long time) {
		return (long)((time + 6000) % 24000 * 3.6);
	}

	/**
	 * <p>Converts 'normal', 86000 seconds in a day, time to Minecraft time.<br>
	 * E.g. <code>Time2Minecraft(System.currentTimeMillis() / 1000);</code></p>
	 * 
	 * <p>0: 6am;
	 * 6000: 12pm (noon);
	 * 12000: 6pm;
	 * 18000: 12am (midnight);
	 * 24000: 6am</p>
	 * 
	 * @param time The time, in seconds, to convert.
	 * @return The Minecraft time.
	 */
	public static long Time2Minecraft(long time) {
		return (long)(time % 86000 / 3.6 - 6000);
	}

	/**
	 * <p>Convert milliseconds to Minecraft ticks.</p>
	 * 
	 * @param ms
	 * @return
	 */
	public static int Milliseconds2MCTicks(int ms) {
		return (int)(ms / 1000.0 * 20);
	}

	/**
	 * <p>Convert Minecraft ticks to milliseconds.</p>
	 * 
	 * @param ticks
	 * @return
	 */
	public static int MCTicks2Milliseconds(int ticks) {
		return (int)(ticks / 20.0 * 1000);
	}
}