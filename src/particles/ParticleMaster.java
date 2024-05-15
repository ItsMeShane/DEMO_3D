package particles;

import entities.Camera;
import org.lwjgl.util.vector.Matrix4f;
import renderEngine.ParticleRenderer;
import textures.ParticleTexture;

import java.util.*;

public class ParticleMaster {

    private static final Map<ParticleTexture, List<Particle>> particles = new HashMap<>();
    private static ParticleRenderer renderer;

    public static void init(Matrix4f projectionMatrix) {
        renderer = new ParticleRenderer(projectionMatrix);
    }

    public static void update() {
        Iterator<Map.Entry<ParticleTexture, List<Particle>>> mapIterator = particles.entrySet().iterator();
        while (mapIterator.hasNext()) {
            List<Particle> list = mapIterator.next().getValue();
            Iterator<Particle> iterator = list.iterator();
            while (iterator.hasNext()) {
                Particle p = iterator.next();
                boolean isAlive = p.update();
                if (!isAlive) {
                    iterator.remove();
                    if (list.isEmpty()) {
                        mapIterator.remove();
                    }
                }
            }
        }

    }

    public static void renderParticles(Camera camera) {
        renderer.render(particles, camera);
    }

    public static void cleanUp() {
        renderer.cleanUp();
    }
    public static void addParticle(Particle particle) {
        List<Particle> list = particles.computeIfAbsent(particle.getTexture(), p -> new ArrayList<>());
        list.add(particle);
    }

}
