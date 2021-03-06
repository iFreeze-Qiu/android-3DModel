package org.andresoviedo.app.model3D.impl1;

import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import org.andresoviedo.app.model3D.models.BoundingBox;
import org.andresoviedo.app.model3D.util.GLUtil;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import android.util.Log;

public class GLES20Object {

//@formatter:off
	protected static final String vertexShaderCode =
	// This matrix member variable provides a hook to manipulate
	// the coordinates of the objects that use this vertex shader
			"uniform mat4 u_MVPMatrix;" + 
			"attribute vec4 a_Position;" + 
			"void main() {" +
				// The matrix must be included as a modifier of gl_Position.
				// Note that the uMVPMatrix factor *must be first* in order
				// for the matrix multiplication product to be correct.
				"  gl_Position = u_MVPMatrix * a_Position;" +
			"}";
	// @formatter:on

	// @formatter:off
	protected static final String fragmentShaderCode = 
			"precision mediump float;"+ 
			"uniform vec4 a_Color;" + 
			"void main() {"	+ 
			"  gl_FragColor = a_Color;" +
			"}";
	// @formatter:on

	// @formatter:off
				protected static final String fragmentShaderCode_lighted = 
						"precision mediump float;"+
				
						//The position of the light in eye space.
						"uniform vec3 u_LightPos;"+
						
	 					// Interpolated position for this fragment.
	 					"varying vec3 v_Position;"+
	 					
	          			// This is the color from the vertex shader interpolated across the
						"varying vec4 v_Color;"+
	                    
						// triangle per fragment.
						// Interpolated normal for this fragment.
						"varying vec3 v_Normal;"+         
						
						"uniform sampler2D u_Texture;"+    // The input texture.
						"varying vec2 v_TexCoordinate;"+ // Interpolated texture coordinate per fragment.
						
						"void main() {"	+ 
						// Will be used for attenuation.
					    "  float distance = length(u_LightPos - v_Position);"+
						  
	  					// Get a lighting direction vector from the light to the vertex.
						"  vec3 lightVector = normalize(u_LightPos - v_Position);"+
	  					 
						// Calculate the dot product of the light vector and vertex normal. If the normal and light vector are
						// pointing in the same direction then it will get max illumination.
						"  float diffuse = max(dot(v_Normal, lightVector), 0.1);"+
						
						//  Add attenuation.
//						"  diffuse = diffuse * (1.0 / (1.0 + (0.25 * distance * distance)));"+
						"  diffuse = diffuse * (1.0 / (1.0 + (0.10 * distance)));"+
//	                    "  diffuse = 1.0;"+
						
						//  Add ambient lighting
						"  diffuse = diffuse + 0.3;"+
									
						//  Multiply the color by the diffuse illumination level to get final output color.
						"  gl_FragColor = v_Color * diffuse;"+
						"}";
				// @formatter:on

	// @formatter:off
				protected static final String vertexShaderCode_textured =
						// This matrix member variable provides a hook to manipulate
						// the coordinates of the objects that use this vertex shader
								"uniform mat4 u_MVPMatrix;" + 
								"uniform mat4 u_MVMatrix;"+       // A constant representing the combined model/view matrix.
								
								"attribute vec4 a_Position;"+    // Per-vertex position information we will pass in.
								"attribute vec3 a_Normal;"+      // Per-vertex normal information we will pass in.
								"uniform vec4 a_Color;"+       // Per-vertex color information we will pass in.
								
								"varying vec3 v_Position;"+       // This will be passed into the fragment shader.
								"varying vec4 v_Color;"+         // This will be passed into the fragment shader.
								"varying vec3 v_Normal;"+         // This will be passed into the fragment shader.
								
//								"attribute int a_useTextures;"+
//								"varying int v_useTextures;"+
								"attribute vec2 a_TexCoordinate;"+ // Per-vertex texture coordinate information we will pass in.
								"varying vec2 v_TexCoordinate;"+   // This will be passed into the fragment shader.
								
								"void main() {" +
								    // Transform the vertex into eye space.
									"  v_Position = vec3(u_MVMatrix * a_Position);" + 
								
									// Pass through the color.
									"  v_Color = a_Color;"+
									
									// Transform the normal's orientation into eye space.
									"  v_Normal = vec3(u_MVMatrix * vec4(a_Normal, 0.0));"+
									
//									"  v_useTextures = a_useTextures;"+
									"  v_TexCoordinate = a_TexCoordinate;"+
//									"  v_TexCoordinate = a_TexCoordinate.st * vec2(1.0, -1.0);"+
									
									// gl_Position is a special variable used to store the final position.
								    // Multiply the vertex by the matrix to get the final point in normalized screen coordinates.
								    "  gl_Position = u_MVPMatrix * a_Position;"+
									
								"}";
						// @formatter:on

	// @formatter:off
			protected static final String fragmentShaderCode_textured = 
					"precision mediump float;"+
			
					//The position of the light in eye space.
					"uniform vec3 u_LightPos;"+
					
 					// Interpolated position for this fragment.
 					"varying vec3 v_Position;"+
 					
          			// This is the color from the vertex shader interpolated across the
					"varying vec4 v_Color;"+
                    
					// triangle per fragment.
					// Interpolated normal for this fragment.
					"varying vec3 v_Normal;"+         
					
					"uniform sampler2D u_Texture;"+    // The input texture.
					"varying vec2 v_TexCoordinate;"+ // Interpolated texture coordinate per fragment.
					
					"void main() {"	+ 
					// Will be used for attenuation.
				    "  float distance = length(u_LightPos - v_Position);"+
					  
  					// Get a lighting direction vector from the light to the vertex.
					"  vec3 lightVector = normalize(u_LightPos - v_Position);"+
  					 
					// Calculate the dot product of the light vector and vertex normal. If the normal and light vector are
					// pointing in the same direction then it will get max illumination.
					"  float diffuse = max(dot(v_Normal, lightVector), 0.1);"+
					
					//  Add attenuation.
//					"  diffuse = diffuse * (1.0 / (1.0 + (0.25 * distance * distance)));"+
					"  diffuse = diffuse * (1.0 / (1.0 + (0.10 * distance)));"+
//                    "  diffuse = 1.0;"+
					
					//  Add ambient lighting
					"  diffuse = diffuse + 0.3;"+
								
					//  Multiply the color by the diffuse illumination level to get final output color.
//					"  gl_FragColor = v_Color * diffuse;"+
//					"  gl_FragColor = (v_Color * diffuse * texture2D(u_Texture, v_TexCoordinate));"+
					"  gl_FragColor = texture2D(u_Texture, v_TexCoordinate);"+
					"}";
			// @formatter:on

//	// @formatter:off
//			protected static final String fragmentShaderCode_stippled = 
//					"precision mediump float;"+ 
//					"uniform vec4 vColor;" + 
//					"attribute vec4 vPosition;" + 
//					"vec4 noir = vec4(0.0,0.0,0.0,1.0);"+
//					"void main() {"	+ 
//					// cos(1000.0*abs(vPosition.x + vPosition.y + vPosition.z)) + 0.5 > 0.0
//					"  if (cos(1000.0*abs(vPosition.x + vPosition.y + vPosition.z)) + 0.5 > 0.0) {"+
//				    "    gl_FragColor = vColor;"+
//				    "  } else {"+
//				    "    gl_FragColor = vColor;"+
//				    "  }"+
//					"}";
//			// @formatter:on

	// number of coordinates per vertex in this array
	protected static final int COORDS_PER_VERTEX = 3;

	protected final int vertexStride = COORDS_PER_VERTEX * 4; // 4 bytes per
	// vertex

	protected final int mProgram;
	protected final FloatBuffer vertexBuffer;
	protected final ShortBuffer drawListBuffer;
	protected final FloatBuffer normalsBuffer;
	protected final FloatBuffer textureCoordBuffer;

	// protected float objCoords[] = { -0.5f, 0.5f, 0.0f, // top left
	// -0.5f, -0.5f, 0.0f, // bottom left
	// 0.5f, -0.5f, 0.0f, // bottom right
	// 0.5f, 0.5f, 0.0f }; // top right

	protected float lightPos[] = { 0.0f, 1, 0f, 0.0f };
	protected float color[] = { 0.2f, 0.709803922f, 0.898039216f, 1.0f };
	protected float[] position = new float[] { 0f, 0f, 0f };
	protected float[] rotation = new float[] { 0f, 0f, 0f };

	protected int mMVPMatrixHandle;
	protected int mMVMatrixHandle;
	protected int mPositionHandle;
	protected int mColorHandle;
	protected int normalHandle;
	protected int lightPositionHandle;

	// Lazy objects
	protected final int drawType;
	protected final int drawSize;
	protected BoundingBox boundingBox;

	protected GLES20Object boundingBoxObject;

	private final int vertexShaderHandle;
	private final int fragmentShaderHandle;

	private final Integer textureId;

	private static ByteBuffer createNativeByteBuffer(int length) {
		// initialize vertex byte buffer for shape coordinates
		ByteBuffer bb = ByteBuffer.allocateDirect(
		// (number of coordinate values * n bytes per type)
				length);
		// use the device hardware's native byte order
		bb.order(ByteOrder.nativeOrder());
		return bb;
	}

	public GLES20Object(float[] objCoords, short[] drawOrder, float[] vNormals, float[] textCoord, int drawType, int drawSize,
			InputStream open) {
		this(createNativeByteBuffer(4 * objCoords.length).asFloatBuffer().put(objCoords).asReadOnlyBuffer(), createNativeByteBuffer(
				2 * drawOrder.length).asShortBuffer().put(drawOrder).asReadOnlyBuffer(), createNativeByteBuffer(4 * vNormals.length)
				.asFloatBuffer().put(vNormals).asReadOnlyBuffer(), textCoord == null ? null : createNativeByteBuffer(4 * objCoords.length)
				.asFloatBuffer().put(textCoord).asReadOnlyBuffer(), drawType, drawSize, open);
	}

	/**
	 * Sets up the drawing object data for use in an OpenGL ES context.
	 * 
	 * @param objCoords
	 * @param textureIs
	 *            TODO
	 */
	public GLES20Object(FloatBuffer objCoords, ShortBuffer drawOrder, FloatBuffer normalsBuffer, FloatBuffer textureCoords, int drawType,
			int drawSize, InputStream textureIs) {
		// { 0, 1, 2, 0, 2, 3, 3, 4, 5, 5, 4, 0 }

		this.vertexBuffer = objCoords;
		vertexBuffer.position(0);

		this.drawListBuffer = drawOrder;
		drawListBuffer.position(0);

		this.normalsBuffer = normalsBuffer;
		normalsBuffer.position(0);

		this.textureCoordBuffer = textureCoords;

		this.drawType = drawType;
		this.drawSize = drawSize;

		// prepare shaders and OpenGL program
		vertexShaderHandle = GLUtil.loadShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode_textured);

		Log.i("fa", "Using texture [" + textureIs + "]");
		fragmentShaderHandle = GLUtil.loadShader(GLES20.GL_FRAGMENT_SHADER, textureIs != null ? fragmentShaderCode_textured
				: fragmentShaderCode_lighted);

		mProgram = GLES20.glCreateProgram(); // create empty OpenGL Program

		GLES20.glAttachShader(mProgram, vertexShaderHandle); // add the vertex shader

		// GLES20.glBindAttribLocation(mProgram, 0, "a_Position");
		// GLES20.glBindAttribLocation(mProgram, 1, "u_MVPMatrix");

		// to program
		GLES20.glAttachShader(mProgram, fragmentShaderHandle); // add the fragment
																// shader to program

		if (textureIs != null) {
			Log.i("globject", "Binding texture...");

			// GLES20.glBindAttribLocation(mProgram, 0, "a_Color");
			// GLES20.glBindAttribLocation(mProgram, 1, "u_Texture");
			// GLES20.glBindAttribLocation(mProgram, 2, "a_TexCoordinate");
			textureId = loadTexture(textureIs);

		} else {
			textureId = null;
		}

		GLES20.glLinkProgram(mProgram); // create OpenGL program executables
	}

	public static int loadTexture(final InputStream is) {
		Log.v("loadTexture", "Loading texture from stream...");

		final int[] textureHandle = new int[1];

		GLES20.glGenTextures(1, textureHandle, 0);
		GLUtil.checkGlError("glGenTextures");

		if (textureHandle[0] != 0) {
			Log.i("texture", "Handler: " + textureHandle[0]);

			final BitmapFactory.Options options = new BitmapFactory.Options();
			options.inScaled = false; // No pre-scaling

			// Read in the resource
			final Bitmap bitmap = BitmapFactory.decodeStream(is, null, options);
			if (bitmap == null) {
				throw new RuntimeException("couldnt load bitmap");
			}

			// Bind to the texture in OpenGL
			GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureHandle[0]);
			GLUtil.checkGlError("glBindTexture");

			// Set filtering
			GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);
			GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_NEAREST);

			// Load the bitmap into the bound texture.
			GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0);
			GLUtil.checkGlError("texImage2D");

			// Recycle the bitmap, since its data has been loaded into OpenGL.
			bitmap.recycle();
		}

		if (textureHandle[0] == 0) {
			throw new RuntimeException("Error loading texture.");
		}

		return textureHandle[0];
	}

	public static void checkGlError(String glOperation) {
		int error;
		while ((error = GLES20.glGetError()) != GLES20.GL_NO_ERROR) {
			Log.e("objModel", glOperation + ": glError " + error);
			throw new RuntimeException(glOperation + ": glError " + error);
		}
	}

	public int getTextureId() {
		return textureId;
	}

	public float[] getPosition() {
		return position;
	}

	public void setPosition(float[] position) {
		this.position = position;
	}

	public float[] getColor() {
		return color;
	}

	public void setColor(float[] color) {
		this.color = color;
	}

	public void draw(float[] mvpMatrix, float[] mvMatrix) {
		this.draw_with_textures(mvpMatrix, mvMatrix);
	}

	/**
	 * Encapsulates the OpenGL ES instructions for drawing this shape.
	 * 
	 * @param mvpMatrix
	 *            - The Model View Project matrix in which to draw this shape.
	 */
	public void draw_ok(float[] mvpMatrix, float[] mvMatrix) {
		// Add program to OpenGL environment
		GLES20.glUseProgram(mProgram);

		// get handle to vertex shader's vPosition member
		mPositionHandle = GLES20.glGetAttribLocation(mProgram, "a_Position");

		// Enable a handle to the triangle vertices
		GLES20.glEnableVertexAttribArray(mPositionHandle);

		// Prepare the triangle coordinate data
		GLES20.glVertexAttribPointer(mPositionHandle, COORDS_PER_VERTEX, GLES20.GL_FLOAT, false, vertexStride, vertexBuffer);

		// get handle to fragment shader's vColor member
		mColorHandle = GLES20.glGetUniformLocation(mProgram, "a_Color");

		// Set color for drawing the triangle
		GLES20.glUniform4fv(mColorHandle, 1, color, 0);

		// get handle to shape's transformation matrix
		mMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "u_MVPMatrix");
		GLUtil.checkGlError("glGetUniformLocation");

		// Apply the projection and view transformation
		GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mvpMatrix, 0);
		GLUtil.checkGlError("glUniformMatrix4fv");

		//
		if (drawListBuffer.limit() % drawSize != 0) {
			throw new RuntimeException(drawListBuffer.limit() + "<>" + drawSize);
		}

		//
		if (drawSize != -1 && drawListBuffer.capacity() % drawSize != 0) {
			throw new RuntimeException(drawListBuffer.capacity() + "<>" + drawSize);
		}

		// Draw the square
		if (drawSize == -1) {
			GLES20.glDrawElements(drawType, drawListBuffer.capacity(), GLES20.GL_UNSIGNED_SHORT, drawListBuffer);
		} else {
			for (int i = 0; i < drawListBuffer.capacity(); i += drawSize) {
				drawListBuffer.position(i);
				GLES20.glDrawElements(drawType, drawSize, GLES20.GL_UNSIGNED_SHORT, drawListBuffer);
			}
		}
		// Disable vertex array
		GLES20.glDisableVertexAttribArray(mPositionHandle);
	}

	/**
	 * Encapsulates the OpenGL ES instructions for drawing this shape.
	 * 
	 * @param mvpMatrix
	 *            - The Model View Project matrix in which to draw this shape.
	 * @param mvMatrix
	 *            TODO
	 */
	public void draw_with_textures(float[] mvpMatrix, float[] mvMatrix) {
		// Add program to OpenGL environment
		GLES20.glUseProgram(mProgram);

		int mTextureCoordinateHandle = -1;
		if (textureId != null) {

			int mTextureUniformHandle = GLES20.glGetUniformLocation(mProgram, "u_Texture");
			checkGlError("glGetUniformLocation");

			// Set the active texture unit to texture unit 0.
			GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
			checkGlError("glActiveTexture");

			// Bind to the texture in OpenGL
			GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId);
			checkGlError("glBindTexture");

			// Tell the texture uniform sampler to use this texture in the shader by binding to texture unit 0.
			GLES20.glUniform1i(mTextureUniformHandle, 0);
			checkGlError("glUniform1i");

			mTextureCoordinateHandle = GLES20.glGetAttribLocation(mProgram, "a_TexCoordinate");
			GLUtil.checkGlError("glGetAttribLocation");

			// Enable a handle to the triangle vertices
			GLES20.glEnableVertexAttribArray(mTextureCoordinateHandle);
			GLUtil.checkGlError("glEnableVertexAttribArray");

			// Prepare the triangle coordinate data
			textureCoordBuffer.position(0);
			GLES20.glVertexAttribPointer(mTextureCoordinateHandle, 2, GLES20.GL_FLOAT, false, 0, textureCoordBuffer);
			GLUtil.checkGlError("glVertexAttribPointer");
		}

		// get handle to vertex shader's vPosition member
		mPositionHandle = GLES20.glGetAttribLocation(mProgram, "a_Position");
		checkGlError("glGetAttribLocation");

		// Enable a handle to the triangle vertices
		GLES20.glEnableVertexAttribArray(mPositionHandle);
		checkGlError("glEnableVertexAttribArray");

		// Prepare the triangle coordinate data
		vertexBuffer.position(0);
		GLES20.glVertexAttribPointer(mPositionHandle, COORDS_PER_VERTEX, GLES20.GL_FLOAT, false, 0, vertexBuffer);

		// get handle to fragment shader's vColor member
		mColorHandle = GLES20.glGetUniformLocation(mProgram, "a_Color");
		GLUtil.checkGlError("glGetUniformLocation");

		// Set color for drawing the triangle
		GLES20.glUniform4fv(mColorHandle, 1, color, 0);
		GLUtil.checkGlError("glUniform4fv");

		// get handle to shape's transformation matrix
		mMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "u_MVPMatrix");
		GLUtil.checkGlError("glGetUniformLocation");

		// Apply the projection and view transformation
		GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mvpMatrix, 0);
		GLUtil.checkGlError("glUniformMatrix4fv");

		// -- testing start

		lightPositionHandle = GLES20.glGetUniformLocation(mProgram, "u_LightPos");
		GLUtil.checkGlError("glGetUniformLocation");
		GLES20.glUniform3fv(lightPositionHandle, 1, lightPos, 0);

		// Enable a handle to the triangle vertices
		GLES20.glEnableVertexAttribArray(normalHandle);
		normalsBuffer.position(0);
		GLES20.glVertexAttribPointer(normalHandle, COORDS_PER_VERTEX, GLES20.GL_FLOAT, false, 0, normalsBuffer);

		// get handle to shape's transformation matrix
		mMVMatrixHandle = GLES20.glGetUniformLocation(mProgram, "u_MVMatrix");
		GLUtil.checkGlError("glGetUniformLocation");
		GLES20.glUniformMatrix4fv(mMVMatrixHandle, 1, false, mvMatrix, 0);
		GLUtil.checkGlError("glUniformMatrix4fv");

		//
		if (drawSize != -1 && drawListBuffer.capacity() % drawSize != 0) {
			throw new RuntimeException(drawListBuffer.capacity() + "<>" + drawSize);
		}

		// Draw the square
		if (drawSize == -1) {
			// GLES20.glDrawElements(drawType, drawListBuffer.capacity(), GLES20.GL_UNSIGNED_SHORT, drawListBuffer);
			// GLES20.glDrawArrays(drawType, 0, vertexBuffer.limit());
			GLES20.glDrawElements(drawType, drawListBuffer.capacity(), GLES20.GL_UNSIGNED_SHORT, drawListBuffer);
		} else {
			for (int i = 0; i < drawListBuffer.capacity(); i += drawSize) {
				drawListBuffer.position(i);
				GLES20.glDrawElements(drawType, drawSize, GLES20.GL_UNSIGNED_SHORT, drawListBuffer);
			}
		}

		// Disable vertex array
		GLES20.glDisableVertexAttribArray(mPositionHandle);

		// Disable vertex array
		GLES20.glDisableVertexAttribArray(normalHandle);

		if (textureId != null) {
			GLES20.glDisableVertexAttribArray(mTextureCoordinateHandle);
			// GLES20.glDisable(GLES20.GL_TEXTURE0);
			// GLES20.glDisable(GLES20.GL_TEXTURE_2D);

		}
	}

	/**
	 * Encapsulates the OpenGL ES instructions for drawing this shape.
	 * 
	 * @param mvpMatrix
	 *            - The Model View Project matrix in which to draw this shape.
	 * @param mvMatrix
	 *            TODO
	 */
	public void drawOkWithLighting_OK(float[] mvpMatrix, float[] mvMatrix) {
		// Add program to OpenGL environment
		GLES20.glUseProgram(mProgram);

		// get handle to vertex shader's vPosition member
		mPositionHandle = GLES20.glGetAttribLocation(mProgram, "a_Position");

		// Enable a handle to the triangle vertices
		GLES20.glEnableVertexAttribArray(mPositionHandle);

		// Prepare the triangle coordinate data
		GLES20.glVertexAttribPointer(mPositionHandle, COORDS_PER_VERTEX, GLES20.GL_FLOAT, false, vertexStride, vertexBuffer);

		// get handle to fragment shader's vColor member
		mColorHandle = GLES20.glGetUniformLocation(mProgram, "a_Color");

		// Set color for drawing the triangle
		GLES20.glUniform4fv(mColorHandle, 1, color, 0);

		// get handle to shape's transformation matrix
		mMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "u_MVPMatrix");
		GLUtil.checkGlError("glGetUniformLocation");

		// Apply the projection and view transformation
		GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mvpMatrix, 0);
		GLUtil.checkGlError("glUniformMatrix4fv");

		// -- testing start

		lightPositionHandle = GLES20.glGetUniformLocation(mProgram, "u_LightPos");
		GLUtil.checkGlError("glGetUniformLocation");
		GLES20.glUniform3fv(lightPositionHandle, 1, lightPos, 0);

		// Enable a handle to the triangle vertices
		GLES20.glEnableVertexAttribArray(normalHandle);
		GLES20.glVertexAttribPointer(normalHandle, COORDS_PER_VERTEX, GLES20.GL_FLOAT, false, vertexStride, normalsBuffer);

		// get handle to shape's transformation matrix
		mMVMatrixHandle = GLES20.glGetUniformLocation(mProgram, "u_MVMatrix");
		GLUtil.checkGlError("glGetUniformLocation");
		GLES20.glUniformMatrix4fv(mMVMatrixHandle, 1, false, mvMatrix, 0);
		GLUtil.checkGlError("glUniformMatrix4fv");

		//
		if (drawSize != -1 && drawListBuffer.capacity() % drawSize != 0) {
			throw new RuntimeException(drawListBuffer.capacity() + "<>" + drawSize);
		}

		// Draw the square
		if (drawSize == -1) {
			GLES20.glDrawElements(drawType, drawListBuffer.capacity(), GLES20.GL_UNSIGNED_SHORT, drawListBuffer);
		} else {
			for (int i = 0; i < drawListBuffer.capacity(); i += drawSize) {
				drawListBuffer.position(i);
				GLES20.glDrawElements(drawType, drawSize, GLES20.GL_UNSIGNED_SHORT, drawListBuffer);
			}
		}

		// Disable vertex array
		GLES20.glDisableVertexAttribArray(mPositionHandle);

		// Disable vertex array
		GLES20.glDisableVertexAttribArray(normalHandle);

		// if (textureId != null){
		// if (materials != null) {
		// materials.readMaterials();
		// }
		// modelDispList = gl.glGenLists(1);
		// gl.glNewList(modelDispList, GL.GL_COMPILE);
		//
		// // gl.glPushMatrix();
		// // render the model face-by-face
		// String faceMat;
		// for (int i = 0; i < faces.getNumFaces(); i++) {
		// faceMat = faceMats.findMaterial(i); // get material used by face i
		// if (faceMat != null)
		// flipTexCoords = materials.renderWithMaterial(faceMat, gl); // render
		// // using
		// // that
		// // material
		// faces.renderFace(i, flipTexCoords, gl); // draw face i
		// }
		// if (materials != null)
		// materials.switchOffTex(gl);
		// // gl.glPopMatrix();
		//
		// gl.glEndList();
		// return modelDispList;
		// }
	}

	public void drawBoundingBox(float[] mvpMatrix, float[] mvMatrix) {
		if (boundingBox == null) {
			// init bounding box
			boundingBox = new BoundingBox(vertexBuffer.asReadOnlyBuffer());
			boundingBoxObject = new GLES20Object(boundingBox.getVertices(), boundingBox.getDrawOrder(), boundingBox.getNormals(), null,
					boundingBox.getDrawType(), boundingBox.getDrawSize(), null);
			boundingBoxObject.setPosition(getPosition());
			boundingBoxObject.setColor(getColor());
		}
		boundingBoxObject.draw(mvpMatrix, mvMatrix);

	}

	public void translateX(float f) {
		position[0] += f;
	}

	public void translateY(float f) {
		position[1] += f;

	}

	public float[] getRotation() {
		return rotation;
	}

	public void setRotationZ(float rz) {
		rotation[2] = rz;
	}

	public float getRotationZ() {
		return rotation[2];
	}

	public void setRotation(float[] rotation) {
		this.rotation = rotation;
	}

	// public static GLES20Object createSphere(float radius, int stacks, int slices) {
	// int vertexCount = (stacks + 1) * (slices + 1);
	// FloatBuffer vertexBuffer = ByteBuffer.allocateDirect(vertexCount * GLHelpers.BYTES_PER_VERTEX).order(ByteOrder.nativeOrder())
	// .asFloatBuffer();
	// FloatBuffer normalBuffer = ByteBuffer.allocateDirect(vertexCount * GLHelpers.BYTES_PER_NORMAL).order(ByteOrder.nativeOrder())
	// .asFloatBuffer();
	// FloatBuffer textureCoordBuffer = ByteBuffer.allocateDirect(vertexCount * GLHelpers.BYTES_PER_TEXTURE_COORD)
	// .order(ByteOrder.nativeOrder()).asFloatBuffer();
	// ShortBuffer indexBuffer = ByteBuffer.allocateDirect(vertexCount * GLHelpers.BYTES_PER_TRIANGLE_INDEX)
	// .order(ByteOrder.nativeOrder()).asShortBuffer();
	//
	// for (int stackNumber = 0; stackNumber <= stacks; ++stackNumber) {
	// for (int sliceNumber = 0; sliceNumber <= slices; ++sliceNumber) {
	// float theta = (float) (stackNumber * Math.PI / stacks);
	// float phi = (float) (sliceNumber * 2 * Math.PI / slices);
	// float sinTheta = FloatMath.sin(theta);
	// float sinPhi = FloatMath.sin(phi);
	// float cosTheta = FloatMath.cos(theta);
	// float cosPhi = FloatMath.cos(phi);
	//
	// float nx = cosPhi * sinTheta;
	// float ny = cosTheta;
	// float nz = sinPhi * sinTheta;
	//
	// float x = radius * nx;
	// float y = radius * ny;
	// float z = radius * nz;
	//
	// float u = 1.f - ((float) sliceNumber / (float) slices);
	// float v = (float) stackNumber / (float) stacks;
	//
	// normalBuffer.put(nx);
	// normalBuffer.put(ny);
	// normalBuffer.put(nz);
	//
	// vertexBuffer.put(x);
	// vertexBuffer.put(y);
	// vertexBuffer.put(z);
	//
	// textureCoordBuffer.put(u);
	// textureCoordBuffer.put(v);
	// }
	// }
	//
	// for (int stackNumber = 0; stackNumber < stacks; ++stackNumber) {
	// for (int sliceNumber = 0; sliceNumber < slices; ++sliceNumber) {
	// int second = (sliceNumber * (stacks + 1)) + stackNumber;
	// int first = second + stacks + 1;
	//
	// // int first = (stackNumber * slices) + (sliceNumber % slices);
	// // int second = ((stackNumber + 1) * slices) + (sliceNumber % slices);
	//
	// indexBuffer.put((short) first);
	// indexBuffer.put((short) second);
	// indexBuffer.put((short) (first + 1));
	//
	// indexBuffer.put((short) second);
	// indexBuffer.put((short) (second + 1));
	// indexBuffer.put((short) (first + 1));
	// }
	// }
	//
	// vertexBuffer.rewind();
	// normalBuffer.rewind();
	// indexBuffer.rewind();
	// textureCoordBuffer.rewind();
	//
	// GLES20Object sphere = new GLES20Object().setVertexBuffer(vertexBuffer).setNormalBuffer(normalBuffer).setIndexBuffer(indexBuffer)
	// .setTexture(R.drawable.earth).setTextureCoordBuffer(textureCoordBuffer).setDiffuseLighting(-3f, 2.3f, 2f);
	// return sphere;
	//
	// }

}