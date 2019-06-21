package tec.utils;

import tec.exceptions.DefinitonException;

public enum DebugLevel {
	NONE(0), NORMAL(1), ADVANCED(2);
	private int debugLevel;
	DebugLevel(int debugLevel) {
		this.debugLevel = debugLevel;
	}
	public static DebugLevel getDebugLevel(int level) {
		for (DebugLevel d : values()) {
			if (d.debugLevel == level) {
				return d;
			}
		}
		return null;
	}
	public static DebugLevel getDebugLevel(String level) {
		switch (level.toUpperCase()) {
			case "NONE":
				return DebugLevel.NONE;
			case "NORMAL":
				return DebugLevel.NORMAL;
			case "ADVANCED":
				return DebugLevel.ADVANCED;
			default:
				return null;
		}
	}
	public int getDebugLevel() {
		return this.debugLevel;
	}

}
