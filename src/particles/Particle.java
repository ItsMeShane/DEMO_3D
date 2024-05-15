package particles;


import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;
import renderEngine.DisplayManager;
import textures.ParticleTexture;

public class Particle {

    private final float gravity;
    private final Vector3f position;
    private final Vector3f velocity;
    private final float lifeLength;
    private final float rotation;
    private final float scale;
    private float elapsedTime = 0;

    private final ParticleTexture texture;
    private final Vector2f textureOffset1 = new Vector2f();
    private final Vector2f textureOffset2 = new Vector2f();
    private float blend;

    public Particle(ParticleTexture texture, Vector3f position, Vector3f velocity, float lifeLength, float rotation, float scale, float gravity) {
        this.texture = texture;
        this.position = position;
        this.velocity = velocity;
        this.lifeLength = lifeLength;
        this.rotation = rotation;
        this.scale = scale;
        this.gravity = gravity;

        ParticleMaster.addParticle(this);
    }

    private final Vector3f reusableParticle = new Vector3f();
    protected boolean update() {
        float delta = DisplayManager.getFrameTimeSeconds();
        velocity.y += gravity * delta;
        reusableParticle.set(velocity);
        reusableParticle.scale(delta);
        Vector3f.add(reusableParticle, position, position);
        elapsedTime += delta;
        updateTextureCoords();
        return elapsedTime < lifeLength;
    }

    private void updateTextureCoords() {
        float lifeFactor = elapsedTime / lifeLength;
        int stageCount = texture.numberOfRows() * texture.numberOfRows();
        float atlasProgression = lifeFactor * stageCount;

        int index1 = (int) Math.floor(atlasProgression);
        int index2 = index1 < stageCount - 1 ? index1 + 1 : index1;
        blend = atlasProgression % 1;
        setTextureOffset(textureOffset1, index1);
        setTextureOffset(textureOffset2, index2);
    }

    private void setTextureOffset(Vector2f offset, int index) {
        int col = index % texture.numberOfRows();
        int row = index / texture.numberOfRows();
        offset.x = (float) col / texture.numberOfRows();
        offset.y = (float) row / texture.numberOfRows();
    }

    public Vector3f getPosition() {
        return position;
    }

    public float getRotation() {
        return rotation;
    }

    public float getScale() {
        return scale;
    }

    protected ParticleTexture getTexture() {
        return texture;
    }

    public Vector2f getTextureOffset1() {
        return textureOffset1;
    }

    public Vector2f getTextureOffset2() {
        return textureOffset2;
    }

    public float getBlend() {
        return blend;
    }
}
