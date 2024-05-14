package entities;


import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.util.vector.Vector3f;
import renderEngine.DisplayManager;
import terrains.Terrain;

import java.io.Serializable;

public class Camera implements Serializable {

	private float distanceFromFocusPoint = 75;
	private float angleAroundFocusPoint = 180;
	private float angleAboveFocusPoint = 0;

	public static float FOV = 70;
	private final Vector3f viewPointPosition = new Vector3f();
	private float pitch = 25;	// rotation around the Y axis
	private float yaw; 			// rotation around the Z axis

	private final FocusPoint focusPoint = new FocusPoint(new Vector3f(0, 0, 0));

	public Camera() {

	}


	public void move(Terrain terrain) {

		focusPoint.move(terrain, angleAroundFocusPoint);

		calculateZoom();
		calculatePitch();
		calculateYaw();

		float horizonDistance = calculateHorizonDistance();
		float verticalDistance = calculateVerticalDistance();
		calculateCameraPosition(horizonDistance, verticalDistance);
		setYaw(180 - angleAroundFocusPoint);
	}

	private void calculateCameraPosition(float horizontalDistance, float verticalDistance) {
		float theta = angleAroundFocusPoint;
		float offsetX = (float) (horizontalDistance * Math.sin(Math.toRadians(theta)));
		float offsetZ = (float) (horizontalDistance * Math.cos(Math.toRadians(theta)));
		viewPointPosition.x = focusPoint.getPosition().x - offsetX;
		viewPointPosition.z = focusPoint.getPosition().z - offsetZ;
		viewPointPosition.y = focusPoint.getPosition().y + verticalDistance + 5;
	}


	private void calculateZoom() {
		float zoomLevel = Mouse.getDWheel() * 0.1f;
		distanceFromFocusPoint -= zoomLevel;
		distanceFromFocusPoint = Math.min(distanceFromFocusPoint, 125);// max distance from focus point
		distanceFromFocusPoint = Math.max(distanceFromFocusPoint, 25); // min distance from focus point

	}

	private void calculatePitch() {
		if (Mouse.isButtonDown(0)) {
			float pitchChange = Mouse.getDY() * 0.1f;
			setPitch(getPitch() - pitchChange);
//			pitch = Math.min(pitch, 70);// max view angle
//			pitch = Math.max(pitch, 0); // min view angle
		}
	}

	private void calculateYaw() {
		if (Mouse.isButtonDown(0)) {
			float angleChange = Mouse.getDX() * 0.3f;
			angleAroundFocusPoint -= angleChange;
		}
	}

	private float calculateHorizonDistance() {
		return (float) (distanceFromFocusPoint * Math.cos(Math.toRadians(getPitch())));
	}

	private float calculateVerticalDistance() {
		return (float) (distanceFromFocusPoint * Math.sin(Math.toRadians(getPitch())));
	}


	public Vector3f getViewPointPosition() {
		return viewPointPosition;
	}

	public FocusPoint getFocusPoint() {
		return focusPoint;
	}

	public float getPitch() {
		return pitch;
	}

	public void setAngleAroundFocusPoint(float angleAroundFocusPoint) {
		this.angleAroundFocusPoint = angleAroundFocusPoint;
	}

	public void setAngleAboveFocusPoint(float angleAboveFocusPoint) {
		this.pitch = angleAboveFocusPoint;
	}

	public void setYaw(float yaw) {
		this.yaw = yaw;
	}

	public void setPitch(float pitch) {
		this.pitch = pitch;
	}


	public float getYaw() {
		return yaw;
	}

	public void invertPitch() {
		pitch = -pitch;
	}

	// point camera follows
	public static class FocusPoint implements Serializable{

		private float currentSpeedX = 0;
		private float currentSpeedZ = 0;
		private final Vector3f position;
		private final Vector3f positionTranslation = new Vector3f();

		private FocusPoint(Vector3f position) {
			this.position = position;
		}

		public void move(Terrain terrain, float cameraRotation) {
			checkInputs();

			float delta = DisplayManager.getFrameTimeSeconds();

			// Calculate translation components for X axis
			float dx = positionTranslation.x * delta;
			float dz = positionTranslation.z * delta;


			// Calculate rotation components
			float sinRotation = (float) Math.sin(Math.toRadians(-cameraRotation));
			float cosRotation = (float) Math.cos(Math.toRadians(-cameraRotation));

			// Calculate translation components for Z axis using currentSpeedZ
			float translatedDx = dx * cosRotation - dz * sinRotation;
			float translatedDz = dx * sinRotation + dz * cosRotation;

			increasePosition(translatedDx, 0, translatedDz);
			getPosition().y = terrain.getHeightOfTerrain(getPosition().x, getPosition().z);
		}

		private void increasePosition(float dx, float dy, float dz) {
			position.translate(dx, dy, dz);
		}

		private void checkInputs() {
			handleInput(Keyboard.KEY_W, Keyboard.KEY_S, true);
			handleInput(Keyboard.KEY_A, Keyboard.KEY_D, false);
		}

		private void handleInput(int positiveKey, int negativeKey, boolean isZAxis) {
			final float MAX_CAMERA_SPEED = 100f;
			if (Keyboard.isKeyDown(positiveKey)) accelerate(isZAxis, MAX_CAMERA_SPEED);
			else if (Keyboard.isKeyDown(negativeKey)) accelerate(isZAxis, -MAX_CAMERA_SPEED);
			else decelerate(isZAxis);
		}

		private void accelerate(boolean isZAxis, float targetSpeed) {
			// determine axis
			float currentSpeed = isZAxis ? currentSpeedZ : currentSpeedX;
			final float ACCELERATION_SPEED = 2.5f;

			// determine speed
			if (currentSpeed < targetSpeed) currentSpeed += ACCELERATION_SPEED;
			else if (currentSpeed > targetSpeed) currentSpeed -= ACCELERATION_SPEED;

			// assign speed
			if (isZAxis) currentSpeedZ = currentSpeed;
			else currentSpeedX = currentSpeed;

			// set position
			setPositionTranslation(isZAxis, currentSpeed);
		}

		private void decelerate(boolean isZAxis) {
			// determine axis
			float currentSpeed = isZAxis ? currentSpeedZ : currentSpeedX;
			final float DECELERATION_SPEED = 5f;

			// determine speed
			if (currentSpeed > 0) {
				currentSpeed -= DECELERATION_SPEED;
				if (currentSpeed < 0) {
					currentSpeed = 0;
				}
			} else if (currentSpeed < 0) {
				currentSpeed += DECELERATION_SPEED;
				if (currentSpeed > 0) {
					currentSpeed = 0;
				}
			}

			// assign speed
			if (isZAxis) currentSpeedZ = currentSpeed;
			else currentSpeedX = currentSpeed;

			// set position
			setPositionTranslation(isZAxis, currentSpeed);
		}

		private void setPositionTranslation(boolean isZAxis, float currentSpeed) {
			if (isZAxis) {
				positionTranslation.z = currentSpeed;
			} else {
				positionTranslation.x = currentSpeed;
			}
		}


		public Vector3f getPosition() {
			return position;
		}
	}



}
