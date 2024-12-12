package com.mjm.hello.navar_mrigaya;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.util.Log;

import org.andresoviedo.android_3d_model_engine.animation.Animator;
import org.andresoviedo.android_3d_model_engine.drawer.DrawerFactory;
import org.andresoviedo.android_3d_model_engine.drawer.Object3DImpl;
import org.andresoviedo.android_3d_model_engine.model.AnimatedModel;
import org.andresoviedo.android_3d_model_engine.model.Camera;
import org.andresoviedo.android_3d_model_engine.model.Object3D;
import org.andresoviedo.android_3d_model_engine.model.Object3DData;
import org.andresoviedo.android_3d_model_engine.services.Object3DBuilder;
import org.andresoviedo.util.android.GLUtil;

import java.io.ByteArrayInputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class ModelRenderer implements GLSurfaceView.Renderer {

	private final static String TAG = ModelRenderer.class.getName();

	// stereoscopic variables
	private float EYE_DISTANCE = 0.64f;
	private final float[] COLOR_RED = {1.0f, 0.0f, 0.0f, 1f};
	private final float[] COLOR_BLUE = {0.0f, 1.0f, 0.0f, 1f};

	// 3D window (parent component)
	private com.mjm.hello.navar_mrigaya.ModelSurfaceView main;
	// width of the screen
	private int width;
	// height of the screen
	private int height;
	// frustrum - nearest pixel
	private final float near = 1f;
	// frustrum - fartest pixel
	private final float far = 100f;

    /**
     * Drawer factory to get right renderer/shader based on object attributes
     */
	private DrawerFactory drawer;
    /**
     * 3D Axis (to show if needed)
     */
    private final Object3DData axis = Object3DBuilder.buildAxis().setId("axis");

	// The wireframe associated shape (it should be made of lines only)
	private Map<Object3DData, Object3DData> wireframes = new HashMap<Object3DData, Object3DData>();
	// The loaded textures
	private Map<byte[], Integer> textures = new HashMap<byte[], Integer>();
	// The corresponding opengl bounding boxes and drawer
	private Map<Object3DData, Object3DData> boundingBoxes = new HashMap<Object3DData, Object3DData>();
	// The corresponding opengl bounding boxes
	private Map<Object3DData, Object3DData> normals = new HashMap<Object3DData, Object3DData>();
	private Map<Object3DData, Object3DData> skeleton = new HashMap<>();

	// 3D matrices to project our 3D world
	private final float[] viewMatrix = new float[16];
	private final float[] projectionMatrix = new float[16];
	private final float[] viewProjectionMatrix = new float[16];
	private final float[] lightPosInEyeSpace = new float[4];

	// 3D stereoscopic matrix (left & right camera)
    private final float[] viewMatrixLeft = new float[16];
    private final float[] projectionMatrixLeft = new float[16];
	private final float[] viewProjectionMatrixLeft = new float[16];
	private final float[] lightPosInEyeSpaceLeft = new float[4];
	private final float[] viewMatrixRight = new float[16];
	private final float[] projectionMatrixRight = new float[16];
	private final float[] viewProjectionMatrixRight = new float[16];
	private final float[] lightPosInEyeSpaceRight = new float[4];



	/**
	 * Whether the info of the model has been written to console log
	 */
	private boolean firstDraw = true;
	/**
	 * Switch to akternate drawing of right and left image
	 */
	private boolean anaglyphSwitch = false;

	/**
	 * Skeleton Animator
	 */
	private Animator animator = new Animator();

	/**
	 * Construct a new renderer for the specified surface view
	 *
	 * @param modelSurfaceView
	 *            the 3D window
	 */
	public ModelRenderer(ModelSurfaceView modelSurfaceView) {
		this.main = modelSurfaceView;
	}

	public float getNear() {
		return near;
	}

	public float getFar() {
		return far;
	}

	@Override
	public void onSurfaceCreated(GL10 unused, EGLConfig config) {
		// Set the background frame color
		float[] backgroundColor = main.getModelActivity().getBackgroundColor();
		GLES20.glClearColor(backgroundColor[0], backgroundColor[1], backgroundColor[2], backgroundColor[3]);

		// Use culling to remove back faces.
		// Don't remove back faces so we can see them
		// GLES20.glEnable(GLES20.GL_CULL_FACE);

		// Enable depth testing for hidden-surface elimination.
		GLES20.glEnable(GLES20.GL_DEPTH_TEST);

		// Enable blending for combining colors when there is transparency
		GLES20.glEnable(GLES20.GL_BLEND);
		GLES20.glBlendFunc(GLES20.GL_ONE, GLES20.GL_ONE_MINUS_SRC_ALPHA);

		// Enable not drawing out of view port
		GLES20.glEnable(GLES20.GL_SCISSOR_TEST);

		// This component will draw the actual models using OpenGL
		drawer = new DrawerFactory();
	}

	@Override
	public void onSurfaceChanged(GL10 unused, int width, int height) {
		this.width = width;
		this.height = height;

		// Adjust the viewport based on geometry changes, such as screen rotation
		GLES20.glViewport(0, 0, width, height);

		// the projection matrix is the 3D virtual space (cube) that we want to project
		float ratio = (float) width / height;
		Log.d(TAG, "projection: [" + -ratio + "," + ratio + ",-1,1]-near/far[1,10]");
		Matrix.frustumM(projectionMatrix, 0, -ratio, ratio, -1, 1, getNear(), getFar());
		Matrix.frustumM(projectionMatrixRight, 0, -ratio, ratio, -1, 1, getNear(), getFar());
		Matrix.frustumM(projectionMatrixLeft, 0, -ratio, ratio, -1, 1, getNear(), getFar());
	}

	@Override
	public void onDrawFrame(GL10 unused) {

		GLES20.glViewport(0, 0, width, height);
		GLES20.glScissor(0,0,width,height);

		// Draw background color
		GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

		SceneLoader scene = main.getModelActivity().getScene();
		if (scene == null) {
			// scene not ready
			return;
		}

		// animate scene
		scene.onDrawFrame();

		// recalculate mvp matrix according to where we are looking at now
		Camera camera = scene.getCamera();
		if (camera.hasChanged() || firstDraw) {
			// INFO: Set the camera position (View matrix)
			// The camera has 3 vectors (the position, the vector where we are looking at, and the up position (sky)

			// the projection matrix is the 3D virtual space (cube) that we want to project
			float ratio = (float) width / height;
			Log.d(TAG, "Camera changed: projection: [" + -ratio + "," + ratio + ",-1,1]-near/far[1,10], ");

			if (!scene.isStereoscopic()) {
				Matrix.setLookAtM(viewMatrix, 0, camera.xPos, camera.yPos, camera.zPos, camera.xView, camera.yView,
						camera.zView, camera.xUp, camera.yUp, camera.zUp);
				Matrix.multiplyMM(viewProjectionMatrix, 0, projectionMatrix, 0, viewMatrix, 0);
			} else {
				float EYE_DISTANCE = this.EYE_DISTANCE;

				Camera[] stereoCamera = camera.toStereo(EYE_DISTANCE);
				Camera leftCamera = stereoCamera[0];
				Camera rightCamera = stereoCamera[1];

				// camera on the left for the left eye
				Matrix.setLookAtM(viewMatrixLeft, 0, leftCamera.xPos, leftCamera.yPos, leftCamera.zPos, leftCamera
								.xView,
						leftCamera.yView, leftCamera.zView, leftCamera.xUp, leftCamera.yUp, leftCamera.zUp);
				// camera on the right for the right eye
				Matrix.setLookAtM(viewMatrixRight, 0, rightCamera.xPos, rightCamera.yPos, rightCamera.zPos, rightCamera
								.xView,
						rightCamera.yView, rightCamera.zView, rightCamera.xUp, rightCamera.yUp, rightCamera.zUp);

				if (scene.isAnaglyph()) {
					Matrix.frustumM(projectionMatrixRight, 0, -ratio, ratio, -1, 1, getNear(), getFar());
					Matrix.frustumM(projectionMatrixLeft, 0, -ratio, ratio, -1, 1, getNear(), getFar());
				} else if (scene.isVRGlasses()){
					float ratio2 = (float) width / 2 / height;
					Matrix.frustumM(projectionMatrixRight, 0, -ratio2, ratio2, -1, 1, getNear(), getFar());
					Matrix.frustumM(projectionMatrixLeft, 0, -ratio2, ratio2, -1, 1, getNear(), getFar());
				}
				// Calculate the projection and view transformation
				Matrix.multiplyMM(viewProjectionMatrixLeft, 0, projectionMatrixLeft, 0, viewMatrixLeft, 0);
				Matrix.multiplyMM(viewProjectionMatrixRight, 0, projectionMatrixRight, 0, viewMatrixRight, 0);

			}

			camera.setChanged(false);

		}


		if (!scene.isStereoscopic()) {
			this.onDrawFrame(viewMatrix, projectionMatrix, viewProjectionMatrix, lightPosInEyeSpace, null);
			return;
		}


		if (scene.isAnaglyph()) {
			// INFO: switch because blending algorithm doesn't mix colors
			if (anaglyphSwitch) {
				this.onDrawFrame(viewMatrixLeft, projectionMatrixLeft, viewProjectionMatrixLeft, lightPosInEyeSpaceLeft,
						COLOR_RED);
			} else {
				this.onDrawFrame(viewMatrixRight, projectionMatrixRight, viewProjectionMatrixRight, lightPosInEyeSpaceRight,
						COLOR_BLUE);
			}
			anaglyphSwitch = !anaglyphSwitch;
			return;
		}

		if (scene.isVRGlasses()) {

			// draw left eye image
			GLES20.glViewport(0, 0, width / 2, height);
			GLES20.glScissor(0,0,width/2,height);
			this.onDrawFrame(viewMatrixLeft, projectionMatrixLeft, viewProjectionMatrixLeft, lightPosInEyeSpaceLeft,
					null);

			// draw right eye image
			GLES20.glViewport(width / 2, 0, width / 2, height);
			GLES20.glScissor(width/2,0,width/2,height);
			this.onDrawFrame(viewMatrixRight, projectionMatrixRight, viewProjectionMatrixRight, lightPosInEyeSpaceRight,
					null);
		}
	}

	private void onDrawFrame(float[] viewMatrix, float[] projectionMatrix, float[] viewProjectionMatrix,
							float[] lightPosInEyeSpace, float[] colorMask) {


		SceneLoader scene = main.getModelActivity().getScene();

		// draw light
		if (scene.isDrawLighting()) {

			Object3DImpl lightBulbDrawer = (Object3DImpl) drawer.getPointDrawer();

			float[] lightModelViewMatrix = lightBulbDrawer.getMvMatrix(lightBulbDrawer.getMMatrix(scene.getLightBulb()), viewMatrix);

			// Calculate position of the light in eye space to support lighting
			Matrix.multiplyMV(lightPosInEyeSpace, 0, lightModelViewMatrix, 0, scene.getLightPosition(), 0);

			// Draw a point that represents the light bulb
			lightBulbDrawer.draw(scene.getLightBulb(), projectionMatrix, viewMatrix, -1, lightPosInEyeSpace,
					colorMask);

		}

		// draw axis
        if (scene.isDrawAxis()){
			Object3D basicDrawer = drawer.getPointDrawer();
			basicDrawer.draw(axis, projectionMatrix, viewMatrix, axis.getDrawMode(), axis
					.getDrawSize(),-1, lightPosInEyeSpace, colorMask);
        }



		List<Object3DData> objects = scene.getObjects();
		for (int i=0; i<objects.size(); i++) {
			Object3DData objData = null;
			try {
				objData = objects.get(i);
				boolean changed = objData.isChanged();

				Object3D drawerObject = drawer.getDrawer(objData, scene.isDrawTextures(), scene.isDrawLighting(),
                        scene.isDrawAnimation());

				if (firstDraw) {
					Log.i("ModelRenderer","Using drawer "+drawerObject.getClass());
					firstDraw = false;
				}

				// load model texture
				Integer textureId = textures.get(objData.getTextureData());
				if (textureId == null && objData.getTextureData() != null) {
					Log.i("ModelRenderer","Loading GL Texture...");
					ByteArrayInputStream textureIs = new ByteArrayInputStream(objData.getTextureData());
					textureId = GLUtil.loadTexture(textureIs);
					textureIs.close();
					textures.put(objData.getTextureData(), textureId);
				}

				// draw points
				if (objData.getDrawMode() == GLES20.GL_POINTS){
					Object3DImpl basicDrawer = (Object3DImpl) drawer.getPointDrawer();
					basicDrawer.draw(objData, projectionMatrix, viewMatrix, GLES20.GL_POINTS,lightPosInEyeSpace);
				}

				// draw wireframe
				else if (scene.isDrawWireframe() && objData.getDrawMode() != GLES20.GL_POINTS
						&& objData.getDrawMode() != GLES20.GL_LINES && objData.getDrawMode() != GLES20.GL_LINE_STRIP
						&& objData.getDrawMode() != GLES20.GL_LINE_LOOP) {
					// Log.d("ModelRenderer","Drawing wireframe model...");
					try{
						// Only draw wireframes for objects having faces (triangles)
						Object3DData wireframe = wireframes.get(objData);
						if (wireframe == null || changed) {
							Log.i("ModelRenderer","Generating wireframe model...");
							wireframe = Object3DBuilder.buildWireframe(objData);
							wireframes.put(objData, wireframe);
						}
						drawerObject.draw(wireframe, projectionMatrix, viewMatrix, wireframe.getDrawMode(),
								wireframe.getDrawSize(), textureId != null ? textureId : -1, lightPosInEyeSpace,
								colorMask);
					}catch(Error e){
						Log.e("ModelRenderer",e.getMessage(),e);
					}
				}

				// draw points
				else if (scene.isDrawPoints() || objData.getFaces() == null || !objData.getFaces().loaded()){
						Log.i("ModelRenderer","Drawing points (no stereo): "+ drawerObject.getClass());
						drawerObject.draw(objData, projectionMatrix, viewMatrix
								, GLES20.GL_POINTS, objData.getDrawSize(),
								textureId != null ? textureId : -1, lightPosInEyeSpace, colorMask);
				}

				// draw skeleton
				else if (scene.isDrawSkeleton() && objData instanceof AnimatedModel && ((AnimatedModel) objData)
						.getAnimation() != null){
					Object3DData skeleton = this.skeleton.get(objData);
					if (skeleton == null){
						skeleton = Object3DBuilder.buildSkeleton((AnimatedModel) objData);
						this.skeleton.put(objData, skeleton);
					}
					animator.update(skeleton);
					drawerObject = drawer.getDrawer(skeleton, false, scene.isDrawLighting(), scene
                            .isDrawAnimation());
					drawerObject.draw(skeleton, projectionMatrix, viewMatrix,-1, lightPosInEyeSpace, colorMask);
				}

				// draw solids
				else {
						drawerObject.draw(objData, projectionMatrix, viewMatrix,
								textureId != null ? textureId : -1, lightPosInEyeSpace, colorMask);
				}

				// Draw bounding box
				if (scene.isDrawBoundingBox() || scene.getSelectedObject() == objData) {
					Object3DData boundingBoxData = boundingBoxes.get(objData);
                    if (boundingBoxData == null || changed) {
                        boundingBoxData = Object3DBuilder.buildBoundingBox(objData);
                        boundingBoxes.put(objData, boundingBoxData);
                    }
                    Object3D boundingBoxDrawer = drawer.getBoundingBoxDrawer();
					boundingBoxDrawer.draw(boundingBoxData, projectionMatrix, viewMatrix, -1,
							lightPosInEyeSpace, colorMask);
				}

				// Draw normals
				if (scene.isDrawNormals()) {
					Object3DData normalData = normals.get(objData);
					if (normalData == null || changed) {
						normalData = Object3DBuilder.buildFaceNormals(objData);
						if (normalData != null) {
							// it can be null if object isnt made of triangles
							normals.put(objData, normalData);
						}
					}
					if (normalData != null) {
						Object3D normalsDrawer = drawer.getFaceNormalsDrawer();
						normalsDrawer.draw(normalData, projectionMatrix, viewMatrix, -1, null);
					}
				}

				// TODO: enable this only when user wants it
				// obj3D.drawVectorNormals(result, viewMatrix);
			} catch (Exception ex) {
				Log.e("ModelRenderer","There was a problem rendering the object '"+objData.getId()+"':"+ex.getMessage(),ex);
			}
		}
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public float[] getModelProjectionMatrix() {
		return projectionMatrix;
	}

	public float[] getModelViewMatrix() {
		return viewMatrix;
	}
}