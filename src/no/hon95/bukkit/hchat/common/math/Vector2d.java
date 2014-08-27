package no.hon95.bukkit.hchat.common.math;

public class Vector2d {

	private double gX;
	private double gY;

	public Vector2d() {
		this(0, 0);
	}

	public Vector2d(double x, double y) {
		gX = x;
		gY = y;
	}

	public double getX() {
		return gX;
	}

	public double getY() {
		return gY;
	}

	public Vector2d setX(double x) {
		gX = x;
		return this;
	}

	public Vector2d setY(double y) {
		gY = y;
		return this;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		} else if (obj instanceof Vector2d) {
			Vector2d vector = (Vector2d) obj;
			return (vector.getX() == getX() && vector.getY() == getY());
		}
		return false;
	}
}
