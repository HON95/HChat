package no.hon95.bukkit.hchat.common.math;

public class Vector3d {

	private double gX;
	private double gY;
	private double gZ;

	public Vector3d() {
		this(0, 0, 0);
	}

	public Vector3d(double x, double y, double z) {
		gX = x;
		gY = y;
		gZ = z;
	}

	public double getX() {
		return gX;
	}

	public double getY() {
		return gY;
	}

	public double getZ() {
		return gZ;
	}

	public Vector3d setX(double x) {
		gX = x;
		return this;
	}

	public Vector3d setY(double y) {
		gY = y;
		return this;
	}

	public Vector3d setZ(double z) {
		gZ = z;
		return this;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		} else if (obj == this) {
			return true;
		} else if (obj instanceof Vector3d) {
			Vector3d vector = (Vector3d) obj;
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
		result = prime * result + Double.valueOf(getZ()).hashCode();
		return result;
	}

	@Override
	public String toString() {
		return String.format("[%.3f,%.3f,%.3f]", getX(), getY(), getZ());
	}
}
