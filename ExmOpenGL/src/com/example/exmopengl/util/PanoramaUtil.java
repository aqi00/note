package com.example.exmopengl.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLUtils;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import static android.opengl.GLES20.GL_CLAMP_TO_EDGE;
import static android.opengl.GLES20.GL_FRAGMENT_SHADER;
import static android.opengl.GLES20.GL_LINEAR;
import static android.opengl.GLES20.GL_LINEAR_MIPMAP_LINEAR;
import static android.opengl.GLES20.GL_NEAREST;
import static android.opengl.GLES20.GL_NO_ERROR;
import static android.opengl.GLES20.GL_REPEAT;
import static android.opengl.GLES20.GL_TEXTURE_2D;
import static android.opengl.GLES20.GL_TEXTURE_MAG_FILTER;
import static android.opengl.GLES20.GL_TEXTURE_MIN_FILTER;
import static android.opengl.GLES20.GL_TEXTURE_WRAP_S;
import static android.opengl.GLES20.GL_TEXTURE_WRAP_T;
import static android.opengl.GLES20.GL_VERTEX_SHADER;
import static android.opengl.GLES20.glAttachShader;
import static android.opengl.GLES20.glBindTexture;
import static android.opengl.GLES20.glCompileShader;
import static android.opengl.GLES20.glCreateProgram;
import static android.opengl.GLES20.glCreateShader;
import static android.opengl.GLES20.glGenTextures;
import static android.opengl.GLES20.glGenerateMipmap;
import static android.opengl.GLES20.glGetError;
import static android.opengl.GLES20.glLinkProgram;
import static android.opengl.GLES20.glShaderSource;
import static android.opengl.GLES20.glTexParameterf;
import static android.opengl.GLES20.glTexParameteri;

public class PanoramaUtil {
	public static String SOURCE_DEFAULT_NAME_FRAGMENT = "fragment.glsl";
	public static String SOURCE_DEFAULT_NAME_VERTEX = "vertex.glsl";

	public static int getProgram(Context context) {
		String vertexStr = getShaderSource(context, SOURCE_DEFAULT_NAME_VERTEX);
		String fragmentStr = getShaderSource(context, SOURCE_DEFAULT_NAME_FRAGMENT);
		return getProgram(vertexStr, fragmentStr);
	}

	public static int getProgram(String vertexStr, String fragmentStr) {
		int program = glCreateProgram();
		int fragmentShader = glCreateShader(GL_FRAGMENT_SHADER);
		int vertexShader = glCreateShader(GL_VERTEX_SHADER);
		glShaderSource(vertexShader, vertexStr);
		glShaderSource(fragmentShader, fragmentStr);
		glCompileShader(vertexShader);
		glCompileShader(fragmentShader);
		glAttachShader(program, vertexShader);
		glAttachShader(program, fragmentShader);
		glLinkProgram(program);
		return program;
	}

	public static String getShaderSource(Context context, String sourseName) {
		StringBuffer shaderSource = new StringBuffer();
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(
					context.getAssets().open(sourseName)));
			String tempStr = null;
			while (null != (tempStr = br.readLine())) {
				shaderSource.append(tempStr);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return shaderSource.toString();
	}

	protected static int bindTexture(Context context, int drawable) {
		BitmapFactory.Options option = new BitmapFactory.Options();
		option.inScaled = false;
		return bindTexture(BitmapFactory.decodeResource(context.getResources(),
				drawable, option));
	}

	protected static int bindTexture(Bitmap bitmap) {
		int[] textures = new int[1];
		glGenTextures(1, textures, 0);
		glBindTexture(GL_TEXTURE_2D, textures[0]);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER,
				GL_LINEAR_MIPMAP_LINEAR);
		GLUtils.texImage2D(GL_TEXTURE_2D, 0, bitmap, 0);
		glGenerateMipmap(GL_TEXTURE_2D);
		glBindTexture(GL_TEXTURE_2D, 0);
		return textures[0];
	}

	public static void checkGlError(String op) {
		int error;
		while ((error = glGetError()) != GL_NO_ERROR) {
			Log.e("ES20_ERROR", op + ": glError " + error);
			throw new RuntimeException(op + ": glError " + error);
		}
	}

	public static int initTexture(Context context, int drawableId) {
		int[] textures = new int[1];
		glGenTextures(1, textures, 0);
		int textureId = textures[0];
		glBindTexture(GL_TEXTURE_2D, textureId);
		glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
		glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
		glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
		glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);

		InputStream is = context.getResources().openRawResource(drawableId);
		Bitmap bitmapTmp;
		try {
			bitmapTmp = BitmapFactory.decodeStream(is);
		} finally {
			try {
				is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		GLUtils.texImage2D(GL_TEXTURE_2D, 0, bitmapTmp, 0);
		bitmapTmp.recycle();
		return textureId;
	}

}
