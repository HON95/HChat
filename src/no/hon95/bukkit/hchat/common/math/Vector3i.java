package no.hon95.bukkit.hchat.common.math;

public class Vector3i {

	private int gX;
	private int gY;
	private int gZ;

	public Vector3i() {
		this(0, 0, 0);
	}

	public Vector3i(int x, int y, int z) {
		gX = x;
		gY = y;
		gZ = z;
	}

	public int getX() {
		return gX;
	}

	public int getY() {
		return gY;
	}

	public int getZ() {
		return gZ;
	}

	public Vector3i setX(int x) {
		gX = x;
		return this;
	}

	public Vector3i setY(int y) {
		gY = y;
		return this;
	}

	public Vector3i setZ(int z) {
		gZ = z;
		return this;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		} else if (obj == this) {
			return true;
		} else if (obj instanceof Vector3i) {
			Vector3i vector = (Vector3i) obj;
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
		result = prime * result + getZ();
		return result;
	}

	@Override
	public String toString() {
		return String.format("[%d,%d,%d]", getX(), getY(), getZ());
	}
}
