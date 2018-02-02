package com.example.exmopengl;

import java.nio.FloatBuffer;
import java.util.ArrayList;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import com.example.exmopengl.util.FileUtil;

import android.app.Activity;
import android.opengl.GLSurfaceView;
import android.opengl.GLU;
import android.os.Bundle;

public class GlCubeActivity extends Activity {
	private final static String TAG = "GlCubeActivity";
	private GLSurfaceView glsv_content;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_gl_cube);

		initVertexs();
		glsv_content = (GLSurfaceView) findViewById(R.id.glsv_content);
		// 注册渲染器
		glsv_content.setRenderer(new GLRender());
	}

    private ArrayList<FloatBuffer> mVertices = new ArrayList<FloatBuffer>();
    float verticesFront[] = { 1f, 1f, 1f,   1f, 1f, -1f,   -1f, 1f, -1f,  -1f, 1f, 1f };
    float verticesBack[] = { 1f, -1f, 1f,  1f, -1f, -1f,  -1f, -1f, -1f, -1f, -1f, 1f };
    float verticesTop[] = { 1f, 1f, 1f,   1f, -1f, 1f,   -1f, -1f, 1f,  -1f, 1f, 1f };
    float verticesBottom[] = { 1f, 1f, -1f,  1f, -1f, -1f,  -1f, -1f, -1f, -1f, 1f, -1f };
    float verticesLeft[] = { -1f, 1f, 1f,  -1f, 1f, -1f,  -1f, -1f, -1f,  -1f, -1f, 1f };
    float verticesRight[] = { 1f, 1f, 1f,   1f, 1f, -1f,   1f, -1f, -1f,  1f, -1f, 1f };
    int pointCount = verticesFront.length/3;
    
	private void initVertexs() {
		mVertices.add(FileUtil.getFloatBuffer(verticesFront));
		mVertices.add(FileUtil.getFloatBuffer(verticesBack));
		mVertices.add(FileUtil.getFloatBuffer(verticesTop));
		mVertices.add(FileUtil.getFloatBuffer(verticesBottom));
		mVertices.add(FileUtil.getFloatBuffer(verticesLeft));
		mVertices.add(FileUtil.getFloatBuffer(verticesRight));
	}

	private class GLRender implements GLSurfaceView.Renderer {
		@Override
		public void onSurfaceCreated(GL10 gl, EGLConfig config) {
			// 背景：白色
			gl.glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
			// 启动阴影平滑
			gl.glShadeModel(GL10.GL_SMOOTH);
		}

		@Override
		public void onSurfaceChanged(GL10 gl, int width, int height) {
			// 设置输出屏幕大小
			gl.glViewport(0, 0, width, height);
			// 设置投影矩阵，对应gluPerspective（调整相机）、glFrustumf（调整透视投影）、glOrthof（调整正投影）
			gl.glMatrixMode(GL10.GL_PROJECTION);
			// 重置投影矩阵，即去掉所有的平移、缩放、旋转操作
			gl.glLoadIdentity();
			// 设置透视图视口大小
			GLU.gluPerspective(gl, 40, (float) width / height, 0.1f, 20.0f);
			//GLU.gluPerspective(gl, 45.0f, (float) width / height, 1.0f, 30.0f);
			// 选择模型观察矩阵，对应gluLookAt（人动）、glTranslatef/glScalef/glRotatef（物动）
			gl.glMatrixMode(GL10.GL_MODELVIEW);
			// 重置模型矩阵
			gl.glLoadIdentity();
		}

		@Override
		public void onDrawFrame(GL10 gl) {
			// 清除屏幕和深度缓存
			gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
			// 重置当前的模型观察矩阵
			gl.glLoadIdentity();
			// 设置画笔颜色
			gl.glColor4f(0.0f, 0.0f, 1.0f, 1.0f);
			// 设置视图点
			GLU.gluLookAt(gl, 10.0f, 8.0f, 6.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f);
			//GLU.gluLookAt(gl, 0, 0, 10f, 0, 0, 0, 0, 1, 0f);
			// 旋转图形
			//gl.glRotatef(angle, 0, 0, -1);
			//gl.glRotatef(angle, 0, -1, 0);
			//沿x轴方向移动1个单位
			//gl.glTranslatef(1, 0, 0);
			//x，y，z方向缩放0.1倍
			//gl.glScalef(0.1f, 0.1f, 0.1f);
			drawCube(gl);
		}
	}

	private void drawCube(GL10 gl) {
		// 启用顶点开关
		gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
		for (FloatBuffer buffer : mVertices) {
			// 将顶点坐标传给 OpenGL 管道
			//size: 每个顶点有几个数值描述。必须是2，3 ，4 之一。
			//type: 数组中每个顶点的坐标类型。取值：GL_BYTE,GL_SHORT, GL_FIXED, GL_FLOAT。
			//stride：数组中每个顶点间的间隔，步长（字节位移）。取值若为0，表示数组是连续的
			//pointer：即存储顶点的Buffer
			gl.glVertexPointer(3, GL10.GL_FLOAT, 0, buffer);
			// 用画线的方式将点连接并画出来
			//GL_POINTS ————绘制独立的点
			//GL_LINE_STRIP————绘制连续的线段，不封闭
			//GL_LINE_LOOP————绘制连续的线段，封闭
			//GL_LINES————顶点两两连接，为多条线段构成
			//GL_TRIANGLES————每隔三个顶点构成一个三角形
			//GL_TRIANGLE_STRIP————每相邻三个顶点组成一个三角形
			//GL_TRIANGLE_FAN————以一个点为三角形公共顶点，组成一系列相邻的三角形
			gl.glDrawArrays(GL10.GL_LINE_LOOP, 0, pointCount);
		}
		// 禁用顶点开关
		gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
	}

}
