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
		if (obj == this) {
			return true;
		} else if (obj instanceof Vector3d) {
			Vector3d vector = (Vector3d) obj;
			return (vector.getX() == getX() && vector.getY() == getY() && vector.getZ() == getZ());
		}
		return false;
	}
}
