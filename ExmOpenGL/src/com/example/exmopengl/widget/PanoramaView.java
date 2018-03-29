package com.example.exmopengl.widget;

import com.example.exmopengl.R;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.opengl.GLSurfaceView;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;

public class PanoramaView extends RelativeLayout implements SensorEventListener {
    private Context mContext;
    private GLSurfaceView mGlSurfaceView;
    private ImageView img;
    //记录xy坐标位置
    private float mPreviousY, mPreviousYs;
    private float mPreviousX, mPreviousXs;
    private float predegrees = 0;
    private PanoramaRender mBall;
    private SensorManager mSensorManager;
    private Sensor mGyroscopeSensor;
    private static final float NS2S = 1.0f / 1000000000.0f; // 将纳秒转化为秒
    private float timestamp;
    private float angle[] = new float[3];
    
    public PanoramaView(Context context) {
        this(context, null);
    }

    public PanoramaView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PanoramaView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        init();
    }

    private void init() {
        initView();
    }

    /*初始化组件*/
    private void initView() {
        LayoutInflater.from(mContext).inflate(R.layout.layout_panorama, this);
        mGlSurfaceView = (GLSurfaceView) findViewById(R.id.mIViews);
        img = (ImageView) findViewById(R.id.img);
        img.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                zero();
            }
        });
    }


    private void initSensor() {
        mSensorManager = (SensorManager) mContext.getSystemService(Context.SENSOR_SERVICE);
        mGyroscopeSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        mSensorManager.registerListener(this, mGyroscopeSensor,
                SensorManager.SENSOR_DELAY_FASTEST);
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        if (sensorEvent.sensor.getType() == Sensor.TYPE_GYROSCOPE) {
            if (timestamp != 0) {
                final float dT = (sensorEvent.timestamp - timestamp) * NS2S;
                angle[0] += sensorEvent.values[0] * dT;
                angle[1] += sensorEvent.values[1] * dT;
                angle[2] += sensorEvent.values[2] * dT;
                float anglex = (float) Math.toDegrees(angle[0]);
                float angley = (float) Math.toDegrees(angle[1]);
                float anglez = (float) Math.toDegrees(angle[2]);
                Sensordt info = new Sensordt();
                info.setSensorX(angley);
                info.setSensorY(anglex);
                info.setSensorZ(anglez);
                Message msg = new Message();
                msg.what = 101;
                msg.obj = info;
                mHandler.sendMessage(msg);
            }
            timestamp = sensorEvent.timestamp;
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
    }

    Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 101:
                    Sensordt info = (Sensordt) msg.obj;
                    float y = info.getSensorY();
                    float x = info.getSensorX();
                    float dy = y - mPreviousY;// 计算触控笔Y位移
                    float dx = x - mPreviousX;// 计算触控笔X位移
                    mBall.yAngle += dx * 2.0f;// 设置填充椭圆绕y轴旋转的角度
                    mBall.xAngle += dy * 0.5f;// 设置填充椭圆绕x轴旋转的角度
                    if (mBall.xAngle < -50f) {
                        mBall.xAngle = -50f;
                    } else if (mBall.xAngle > 50f) {
                        mBall.xAngle = 50f;
                    }
                    mPreviousY = y;// 记录触控笔位置
                    mPreviousX = x;// 记录触控笔位置
                    rotate();
                    break;

                default:
                    break;
            }
        }
    };

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mSensorManager.unregisterListener(this);
        float y = event.getY();
        float x = event.getX();
        switch (event.getAction()) {
            case MotionEvent.ACTION_MOVE:
                float dy = y - mPreviousYs;
                float dx = x - mPreviousXs;

                mBall.yAngle += dx * 0.3f;
                mBall.xAngle += dy * 0.3f;
                if (mBall.xAngle < -50f) {
                    mBall.xAngle = -50f;
                } else if (mBall.xAngle > 50f) {
                    mBall.xAngle = 50f;
                }
                rotate();
                break;
            case MotionEvent.ACTION_UP:
                mSensorManager.registerListener(this, mGyroscopeSensor,
                        SensorManager.SENSOR_DELAY_FASTEST);
                break;
        }
        mPreviousYs = y;
        mPreviousXs = x;
        return true;
    }
    
    /**
     * 传入图片路径
     * @param pimgid
     */
    public void setGLPanorama(int pimgid) {
        mGlSurfaceView.setEGLContextClientVersion(2);
        mBall = new PanoramaRender(mContext, pimgid);
        mGlSurfaceView.setRenderer(mBall);
        initSensor();
    }

    /**
     * 小图标旋转，跳跃
     */
    private void rotate() {
        RotateAnimation anim = new RotateAnimation(predegrees, -mBall.yAngle,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);
        anim.setDuration(200);
        img.startAnimation(anim);
        predegrees = -mBall.yAngle;//记录这一次的起始角度作为下次旋转的初始角度
    }

    class Sensordt {
        float sensorX;
        float sensorY;
        float sensorZ;

        float getSensorX() {
            return sensorX;
        }

        void setSensorX(float sensorX) {
            this.sensorX = sensorX;
        }

        float getSensorY() {
            return sensorY;
        }

        void setSensorY(float sensorY) {
            this.sensorY = sensorY;
        }

        float getSensorZ() {
            return sensorZ;
        }

        void setSensorZ(float sensorZ) {
            this.sensorZ = sensorZ;
        }
    }

    private Handler mHandlers = new Handler();
    int yy = 0;
    /**
     * 还原位置
     */
    private void zero() {
        yy = (int) ((mBall.yAngle - 90f) / 10f);
        mHandlers.post(new Runnable() {
            @Override
            public void run() {
                if (yy != 0) {
                    if (yy > 0) {
                        mBall.yAngle = mBall.yAngle - 10f;
                        mHandlers.postDelayed(this, 16);
                        yy--;
                    }
                    if (yy < 0) {
                        mBall.yAngle = mBall.yAngle + 10f;
                        mHandlers.postDelayed(this, 16);
                        yy++;
                    }
                } else {
                    mBall.yAngle = 90f;
                }
                mBall.xAngle = 0f;
            }
        });
    }
}
