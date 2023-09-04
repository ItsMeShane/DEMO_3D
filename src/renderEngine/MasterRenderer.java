package renderEngine;

import entities.Camera;
import entities.Entity;
import entities.Light;
import models.TexturedModel;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector4f;
import shaders.StaticShader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MasterRenderer {

    private static final float NEAR_PLANE = 0.1f; // how close you can see
    private static final float FAR_PLANE = 750;   // how far you can see

    private static final float SKY_RED   =  88 / 256f;
    private static final float SKY_GREEN =  66 / 256f;
    private static final float SKY_BLUE  =  66 / 256f;


    private Matrix4f projectionMatrix;

    private final StaticShader entityShader = new StaticShader();


    private final EntityRenderer entityRenderer;

    private final Map<TexturedModel, List<Entity>> entities = new HashMap<>();

    public MasterRenderer() {
        enableCulling();
        createProjectionMatrix();
        entityRenderer = new EntityRenderer(entityShader, projectionMatrix);
    }

    public void renderScene(List<Entity> entities, List<Light> lights, Camera camera, Vector4f clipPlane) {



        // entities
        for (Entity entity : entities)
            processEntity(entity);

        render(lights, camera, clipPlane);

    }


    public Matrix4f getProjectionMatrix() {
        return projectionMatrix;
    }

    public static void enableCulling() { // dont render faces you cant see
        GL11.glEnable(GL11.GL_CULL_FACE);
    }
    public static void disableCulling() {
        GL11.glDisable(GL11.GL_CULL_FACE);
    }

    private void render(List<Light> lights, Camera camera, Vector4f clipPlane) {
        prepare();
        // entities
        entityShader.start();
        entityShader.loadClipPlane(clipPlane);
        entityShader.loadSkyColor(SKY_RED, SKY_GREEN, SKY_BLUE);
        entityShader.loadLights(lights);
        entityShader.loadViewMatrix(camera);
        entityRenderer.render(entities);
        entityShader.stop();

        entities.clear();
    }


    public void processEntity(Entity entity) {
        TexturedModel entityModel = entity.getModel();
        List<Entity> batch = entities.get(entityModel);
        if (batch != null) {
            batch.add(entity);
        } else {
            List<Entity> newBatch = new ArrayList<>();
            newBatch.add(entity);
            entities.put(entityModel, newBatch);
        }
    }

    public void cleanUp() {
        entityShader.cleanUp();
    }

    public void prepare() {
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT|GL11.GL_DEPTH_BUFFER_BIT);
        GL11.glClearColor(SKY_RED, SKY_GREEN, SKY_BLUE, 1);
    }

    public void createProjectionMatrix(){
        float aspectRatio = (float) Display.getWidth() / (float) Display.getHeight();
        float y_scale = (float) ((1f / Math.tan(Math.toRadians(Camera.FOV / 2f))) * aspectRatio);
        float x_scale = y_scale / aspectRatio;
        float frustum_length = FAR_PLANE - NEAR_PLANE;

        projectionMatrix = new Matrix4f();
        projectionMatrix.m00 = x_scale;
        projectionMatrix.m11 = y_scale;
        projectionMatrix.m22 = -((FAR_PLANE + NEAR_PLANE) / frustum_length);
        projectionMatrix.m23 = -1;
        projectionMatrix.m32 = -((2 * NEAR_PLANE * FAR_PLANE) / frustum_length);
        projectionMatrix.m33 = 0;
    }

}
