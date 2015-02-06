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
		if (obj == null) {
			return false;
		} else if (obj == this) {
			return true;
		} else if (obj instanceof Vector2d) {
			Vector2d vector = (Vector2d) obj;
			return hashCode() == vector.hashCode();
		}
		return false;
	}

	@Override
	public int hashCode() {
		final int prime = 37;
		int result = 13;
		result = prime * result + Double.valueOf(getX()).hashCode();
		result = prime * result + Double.valueOf(getY()).hashCode();
		return result;
	}

	@Override
	public String toString() {
		return String.format("[%.3f,%.3f]", getX(), getY());
	}
}
