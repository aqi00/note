package com.example.exmopengl;

import android.app.Activity;
import android.opengl.GLSurfaceView;
import android.opengl.GLU;
import android.os.Bundle;
import android.util.Log;

import java.nio.FloatBuffer;
import java.util.ArrayList;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import com.example.exmopengl.util.FileUtil;

public class GlBallActivity extends Activity {
	private final static String TAG = "GlBallActivity";
	private GLSurfaceView glsv_content;
	private ArrayList<FloatBuffer> mVertices = new ArrayList<FloatBuffer>();
	private int divide = 20; // 将经纬度等分的面数
	private float radius = 4; // 球半径
	private int angle = 0; // 旋转角度

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_gl_ball);
		// 计算球面顶点坐标
		getBallVertices();
		glsv_content = (GLSurfaceView) findViewById(R.id.glsv_content);
		// 注册渲染器
		glsv_content.setRenderer(new GLRender());
	}

	private class GLRender implements GLSurfaceView.Renderer {
		@Override
		public void onSurfaceCreated(GL10 gl, EGLConfig config) {
			// 背景：白色
			gl.glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
			// 参数mode可以是GL_SMOOTH（默认值）或GL_FLAT。采用恒定着色时（即GL_FLAT），使用图元中某个顶点的颜色来渲染整个图元。
			// 启动阴影平滑
			gl.glShadeModel(GL10.GL_SMOOTH);
			// 复位深度缓存
			//如果指定“当前像素值”为1时，我们知道，一个模型深度值取值和范围为[0,1]。这个时候你往里面画一个物体， 由于物体的每个像素的深度值都小于等于1， 所以整个物体都被显示了出来。
			//如果指定“当前像素值”为0， 物体的每个像素的深度值都大于等于0， 所以整个物体都不可见。 
			//如果指定“当前像素值”为0.5， 那么物体就只有深度小于等于0.5的那部分才是可见的
			gl.glClearDepthf(1f);// 这个值要大于等于1，必须设置,指定深度缓冲区的清除值
			// 所做深度测试的类型，同时必须开启GL10.GL_DEPTH_TEST
			//GL10.GL_NEVER：永不绘制
			//GL10.GL_LESS：只绘制模型中像素点的z值<当前像素z值的部分
			//GL10.GL_EQUAL：只绘制模型中像素点的z值=当前像素z值的部分
			//GL10.GL_LEQUAL：只绘制模型中像素点的z值<=当前像素z值的部分
			//GL10.GL_GREATER ：只绘制模型中像素点的z值>当前像素z值的部分
			//GL10.GL_NOTEQUAL：只绘制模型中像素点的z值!=当前像素z值的部分
			//GL10.GL_GEQUAL：只绘制模型中像素点的z值>=当前像素z值的部分
			//GL10.GL_ALWAYS：总是绘制
			gl.glDepthFunc(GL10.GL_LEQUAL);
			// 启动某功能，对应的glDisable是关闭某功能
			gl.glEnable(GL10.GL_DEPTH_TEST);// 用来开启更新深度缓冲区的功能，也就是，如果通过比较后深度值发生变化了，
			// 会进行更新深度缓冲区的操作。启动它，OpenGL就可以跟踪在Z轴上的像素，这样，它只会在那个像素前方没有东西时，才会绘制这个像素。
			// 在绘制三维图形时，这个功能最好启动，视觉效果比较真实。
			//除了深度测试，还可以开启以下功能
			//开启灯照效果
			//gl.glEnable(GL10.GL_LIGHTING);
			// 启用光源0
			//gl.glEnable(GL10.GL_LIGHT0);
			// 启用颜色追踪
			//gl.glEnable(GL10.GL_COLOR_MATERIAL);
			//启用纹理
			//gl.glEnable(GL10.GL_TEXTURE_2D);

			// GL_FOG_HINT
			// 指示雾计算的准确性。 如果 OpenGL 实现不有效地支持每像素雾计算，提示 GL_DONT_CARE 或
			// GL_FASTEST 会导致每个顶点雾化效果计算。
			// GL_LINE_SMOOTH_HINT
			// 指示是锯消除行的采样质量。如果应用了一个较大的筛选器函数，则将提示 GL_NICEST
			// 可能会导致生成过程中栅格化，更多像素碎片。
			// GL_PERSPECTIVE_CORRECTION_HINT
			// 表示颜色和纹理坐标插补的质量。 如果角度更正参数插值不有效地支持由 OpenGL 实现，提示 GL_DONT_CARE 或
			// GL_FASTEST 可以导致简单线性插值的颜色和/或纹理坐标。
			// GL_POINT_SMOOTH_HINT
			// 表示是锯消除点采样的质量。 如果应用了一个较大的筛选器函数， 则将提示 GL_NICEST 可能会导致生成
			// 过程中栅格化，更多像素碎片。
			// GL_POLYGON_SMOOTH_HINT
			// 指示是锯消除多边形的采样质量 。如果应用了一个较大的筛选器函数，则将提示GL_NICEST 可能会导致生 成过程中栅格化，
			// 更多像素碎片。
			// 对透视进行修正
			gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT, GL10.GL_NICEST);
			//GL_FASTEST：选择速度最快选项。
			//GL_NICEST：选择最高质量选项。
			//GL_DONT_CARE：对选项不做考虑。
		}

		@Override
		public void onSurfaceChanged(GL10 gl, int width, int height) {
			// glViewport在默认情况下，视口被设置为占据打开窗口的整个像素矩形
			// 窗口大小和设置视口大小相同，所以为了选择一个更小的绘图区域，就可以用glViewport函数来实现这一变换，
			// 在窗口中定义一个像素矩形，最终将图像映射到这个矩形中。例如可以对窗口区域进行划分，在同一个窗口中显示分割屏幕的效果，以显示多个视图。
			// 设置输出屏幕大小
			gl.glViewport(0, 0, width, height);
			// glMatrixMode设置当前矩阵模式:
			// GL_MODELVIEW,对模型视景矩阵堆栈应用随后的矩阵操作.
			// GL_PROJECTION,对投影矩阵应用随后的矩阵操作.
			// GL_TEXTURE,对纹理矩阵堆栈应用随后的矩阵操作.
			// 设置投影矩阵，对应gluPerspective（调整相机）、glFrustumf（调整透视投影）、glOrthof（调整正投影）
			gl.glMatrixMode(GL10.GL_PROJECTION);
			// 与glLoadIdentity()一同使用
			// glLoadIdentity():将当前的用户坐标系的原点移到了屏幕中心：类似于一个复位操作
			// 重置投影矩阵，即去掉所有的平移、缩放、旋转操作
			gl.glLoadIdentity();

			// gluPerspective这个函数指定了观察的视景体（frustum为锥台的意思，通常译为视景体）在世界坐标系中的具体大小，
			// 一般而言，其中的参数aspect应该与窗口的宽高比大小相同。比如说，aspect=2.0表示在观察者的角度中物体的宽度是高度的两倍，
			// 在视口中宽度也是高度的两倍，这样显示出的物体才不会被扭曲。
			// 设置透视图视口大小
			GLU.gluPerspective(gl, 50, (float) width / height, 0.1f, 100.0f);
			// 选择模型观察矩阵，对应gluLookAt（人动）、glTranslatef/glScalef/glRotatef（物动）
			gl.glMatrixMode(GL10.GL_MODELVIEW);
			// 重置模型矩阵
			gl.glLoadIdentity();
		}

		@Override
		public void onDrawFrame(GL10 gl) {
			Log.d(TAG, "onDrawFrame");
			// 清除屏幕和深度缓存
			gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
			// 重置当前的模型观察矩阵
			gl.glLoadIdentity();
			// 设置画笔颜色
			gl.glColor4f(0.0f, 0.0f, 1.0f, 1.0f);
			// 设置视图点
			GLU.gluLookAt(gl, 0.0f, 5.0f, 15.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f);
			// 旋转图形
			gl.glRotatef(angle, 0, 0, -1);
			gl.glRotatef(angle, 0, -1, 0);
			//沿x轴方向移动1个单位
			//gl.glTranslatef(1, 0, 0);
			//x，y，z方向缩放0.1倍
			//gl.glScalef(0.1f, 0.1f, 0.1f);
			// 画图
			drawBall(gl);
			// 增加旋转的角度
			angle++;
		}
	}

	private void drawBall(GL10 gl) {
		//GL_VERTEX_ARRAY顶点数组
		//GL_COLOR_ARRAY颜色数组
		// 打开顶点开关
		gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
		// 每次画两条相邻的纬度线
		for (int i = 0; i <= divide; i++) {
			// 将顶点坐标传给 OpenGL 管道
			//size: 每个顶点有几个数值描述。必须是2，3 ，4 之一。
			//type: 数组中每个顶点的坐标类型。取值：GL_BYTE,GL_SHORT, GL_FIXED, GL_FLOAT。
			//stride：数组中每个顶点间的间隔，步长（字节位移）。取值若为0，表示数组是连续的
			//pointer：即存储顶点的Buffer
			gl.glVertexPointer(3, GL10.GL_FLOAT, 0, mVertices.get(i));
			// 用画线的方式将点连接并画出来
			//first：一般填0
			//count：每个面画的线段-1。如果count=3表示这个面画两条线段
			gl.glDrawArrays(GL10.GL_LINE_STRIP, 0, divide * 2 + 2);
			//画颜色
			//size: 每种颜色组件的数量。 值必须为 3 或 4。
			//type: 颜色数组中的每个颜色分量的数据类型。 使用下列常量指定可接受的数据类型：GL_BYTE GL_UNSIGNED_BYTE，GL_SHORT GL_UNSIGNED_SHORT，GL_INT GL_UNSIGNED_INT，GL_FLOAT，或 GL_DOUBLE。
			//stride：连续颜色之间的字节偏移量。 当偏移量为0时，表示数据是连续的。
			//pointer：即颜色的Buffer
			//gl.glColorPointer
			//画三角区域
			//mode：有三种取值 
			//--GL_TRIANGLES：每三个点之间绘制三角形，之间不连接
			//--GL_TRIANGLE_FAN：以V0 V1 V2,V0 V2 V3,V0 V3 V4，……的形式绘制三角形
			//--GL_TRIANGLE_STRIP：顺序在每三个顶点之间均绘制三角形。这个方法可以保证从相同的方向上所有三角形均被绘制。以V0 V1 V2 ,V1 V2 V3,V2 V3 V4,……的形式绘制三角形
			//first：从数组缓存中的哪一位开始绘制，一般都定义为0
			//count：顶点的数量
			//gl.glDrawArrays
		}
		// 关闭顶点开关
		gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
	}

	private void getBallVertices() {
		float latitude; // 纬度
		float latitudeDelta; // 下一层纬度
		float longitude; // 经度
		float ex; // 点坐标x
		float ey; // 点坐标y
		float ez; // 点坐标z
		// 将纬度等分成divide份，这样就能计算出每一等份的纬度值
		for (int i = 0; i <= divide; i++) {
			// 获取当前等份的纬度值
			latitude = (float) (Math.PI / 2.0 - i * (Math.PI) / divide);
			// 获取下一等份的纬度值
			latitudeDelta = (float) (Math.PI / 2.0 - (i + 1) * (Math.PI) / divide);
			// 当前纬度和下一纬度的点坐标
			float[] vertices = new float[divide * 6 + 6];
			// 将经度等分成divide份，这样就能得到当前纬度值和下一纬度值的每一份经度值
			for (int j = 0; j <= divide; j++) {
				// 计算经度值
				longitude = (float) (j * (Math.PI * 2) / divide);
				ex = (float) (Math.cos(latitude) * Math.cos(longitude));
				ey = (float) Math.sin(latitude);
				ez = (float) -(Math.cos(latitude) * Math.sin(longitude));
				// 此经度值下的当前纬度的点坐标
				vertices[6 * j + 0] = radius * ex;
				vertices[6 * j + 1] = radius * ey;
				vertices[6 * j + 2] = radius * ez;
				ex = (float) (Math.cos(latitudeDelta) * Math.cos(longitude));
				ey = (float) Math.sin(latitudeDelta);
				ez = (float) -(Math.cos(latitudeDelta) * Math.sin(longitude));
				// 此经度值下的下一纬度的点坐标
				vertices[6 * j + 3] = radius * ex;
				vertices[6 * j + 4] = radius * ey;
				vertices[6 * j + 5] = radius * ez;
			}
			// 将点坐标转换成FloatBuffer类型添加到点坐标集合ArrayList<FloatBuffer>里
			mVertices.add(FileUtil.getFloatBuffer(vertices));
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		glsv_content.onPause();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		glsv_content.onResume();
	}
	
}
