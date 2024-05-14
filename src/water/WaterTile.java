package water;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;
import toolbox.Maths;

import java.util.Objects;

public final class WaterTile {

	public static final float WATER_SIZE = 100;
	public static final float WATER_HEIGHT = -9.5f;
	private final float centerX;
	private final float centerZ;
	private final float height;

	private final Matrix4f transformationMatrix;

	public WaterTile(float centerX, float centerZ, float height) {
		this.centerX = centerX;
		this.centerZ = centerZ;
		this.height = height;
		this.transformationMatrix = Maths.createTransformationMatrix(new Vector3f(centerX(), height(), centerZ()), 0, 0, 0, WATER_SIZE);
	}

	public float centerX() {
		return centerX;
	}

	public float centerZ() {
		return centerZ;
	}

	public float height() {
		return height;
	}

	public Matrix4f transformationMatrix() {
		return transformationMatrix;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this) return true;
		if (obj == null || obj.getClass() != this.getClass()) return false;
		var that = (WaterTile) obj;
		return Float.floatToIntBits(this.centerX) == Float.floatToIntBits(that.centerX) &&
				Float.floatToIntBits(this.centerZ) == Float.floatToIntBits(that.centerZ) &&
				Float.floatToIntBits(this.height) == Float.floatToIntBits(that.height);
	}

	@Override
	public int hashCode() {
		return Objects.hash(centerX, centerZ, height);
	}

	@Override
	public String toString() {
		return "WaterTile[" +
				"centerX=" + centerX + ", " +
				"centerZ=" + centerZ + ", " +
				"height=" + height + ']';
	}


}
