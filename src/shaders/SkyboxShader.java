package shaders;

import entities.Camera;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;
import renderEngine.DisplayManager;
import toolbox.Maths;

public class SkyboxShader extends ShaderProgram{

	private static final String VERTEX_FILE = "shaders/glsl/skyboxVertexShader.glsl";
	private static final String FRAGMENT_FILE = "shaders/glsl/skyboxFragmentShader.glsl";

	private static final float ROTATION_SPEED = 0.3f;
	private float rotation = 0;
	
	private int location_projectionMatrix;
	private int location_viewMatrix;
	private int location_fogColor;
	private int location_cubeMap1;
	private int location_cubeMap2;
	private int location_blendFactor;

	public SkyboxShader() {
		super(VERTEX_FILE, FRAGMENT_FILE);
	}
	
	public void loadProjectionMatrix(Matrix4f matrix){
		super.loadMatrix(location_projectionMatrix, matrix);
	}

	public void loadViewMatrix(Camera camera){
		Matrix4f matrix = Maths.createViewMatrix(camera);
		matrix.m30 = 0;
		matrix.m31 = 0;
		matrix.m32 = 0;
		rotation += ROTATION_SPEED * DisplayManager.getFrameTimeSeconds();
		Matrix4f.rotate((float) Math.toRadians(rotation), new Vector3f(0, 1, 0), matrix, matrix);
		super.loadMatrix(location_viewMatrix, matrix);
	}

	public void loadFogColor(float r, float g, float b) {
		super.loadVector(location_fogColor, new Vector3f(r, g, b));
	}
	public void loadBlendFactor(float blendFactor) {
		super.loadFloat(location_blendFactor, blendFactor);
	}

	public void connectTextures() {
		super.loadInteger(location_cubeMap1, 0);
		super.loadInteger(location_cubeMap2, 1);
	}

	@Override
	protected void getAllUniformLocations() {
		location_projectionMatrix = super.getUniformLocation("projectionMatrix");
		location_viewMatrix = super.getUniformLocation("viewMatrix");
		location_fogColor = super.getUniformLocation("fogColor");
		location_cubeMap1 = super.getUniformLocation("cubeMap1");
		location_cubeMap2 = super.getUniformLocation("cubeMap1");
		location_blendFactor = super.getUniformLocation("blendFactor");
	}

	@Override
	protected void bindAttributes() {
		super.bindAttribute(0, "position");
	}

}