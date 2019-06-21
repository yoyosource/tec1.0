package tec.utils;

public enum Debug {
	NONE(0), NORMAL(1), ADVANCED(2);
	private int debugLevel;
	Debug(int debugLevel) {
		this.debugLevel = debugLevel;
	}
	public static Debug getDebugLevel(int level) {
		for (Debug d : values()) {
			if (d.debugLevel == level) {
				return d;
			}
		}
		return null;
	}
	public int getDebugLevel() {
		return this.debugLevel;
	}
}
