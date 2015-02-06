package no.hon95.bukkit.hchat.common.math;

public class Vector2i {

	private int gX;
	private int gY;

	public Vector2i() {
		this(0, 0);
	}

	public Vector2i(int x, int y) {
		gX = x;
		gY = y;
	}

	public int getX() {
		return gX;
	}

	public int getY() {
		return gY;
	}

	public Vector2i setX(int x) {
		gX = x;
		return this;
	}

	public Vector2i setY(int y) {
		gY = y;
		return this;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		} else if (obj == this) {
			return true;
		} else if (obj instanceof Vector2i) {
			Vector2i vector = (Vector2i) obj;
			return hashCode() == vector.hashCode();
		}
		return false;
	}

	@Override
	public int hashCode() {
		final int prime = 37;
		int result = 13;
		result = prime * result + getX();
		result = prime * result + getY();
		return result;
	}

	@Override
	public String toString() {
		return String.format("[%d,%d]", getX(), getY());
	}
}
