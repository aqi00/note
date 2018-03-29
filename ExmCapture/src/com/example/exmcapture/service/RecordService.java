package com.example.exmcapture.service;

import android.annotation.TargetApi;
import android.app.Service;
import android.content.Intent;
import android.hardware.display.DisplayManager;
import android.hardware.display.VirtualDisplay;
import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaFormat;
import android.media.MediaMuxer;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.os.Build;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import java.nio.ByteBuffer;

import com.example.exmcapture.MainApplication;
import com.example.exmcapture.R;
import com.example.exmcapture.util.DisplayUtil;
import com.example.exmcapture.util.FileUtil;
import com.example.exmcapture.util.Utils;
import com.example.exmcapture.widget.FloatView;
import com.example.exmcapture.widget.FloatView.FloatClickListener;

@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class RecordService extends Service implements FloatClickListener {
	private static final String TAG = "RecordService";
	private String mVideoPath, mVideoName;
	private MediaProjectionManager mMpMgr;
	private MediaProjection mMP;
	private VirtualDisplay mVirtualDisplay;
	private int mScreenWidth, mScreenHeight, mScreenDensity;

	private MediaCodec mMediaCodec;
	private MediaMuxer mMediaMuxer;
	private boolean isRecording = false, isMuxerStarted = false;
	private MediaCodec.BufferInfo mBufferInfo = new MediaCodec.BufferInfo();
	private int mVideoTrackIndex = -1;
	private FloatView mFloatView;
	private ImageView iv_record;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		mVideoPath = Environment.getExternalStoragePublicDirectory(
				Environment.DIRECTORY_DOWNLOADS).getAbsolutePath() + "/ScreenRecords/";
		mMpMgr = MainApplication.getInstance().getMpMgr();
		mScreenWidth = DisplayUtil.getSreenWidth(this);
		mScreenHeight = DisplayUtil.getSreenHeight(this);
		mScreenDensity = DisplayUtil.getSreenDensityDpi(this);
		if (mFloatView == null) {
			mFloatView = new FloatView(MainApplication.getInstance());
			mFloatView.setLayout(R.layout.float_record);
		}
		mFloatView.setOnFloatListener(this);
		iv_record = (ImageView) mFloatView.mContentView.findViewById(R.id.iv_record);
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		if (mFloatView != null && mFloatView.isShow() == false) {
			mFloatView.show();
		}
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public void onFloatClick(View v) {
		isRecording = !isRecording;
		if (isRecording) {
			iv_record.setImageResource(R.drawable.ic_record_pause);
			Toast.makeText(RecordService.this, "开始录屏", Toast.LENGTH_SHORT).show();
			recordStart();
		} else {
			iv_record.setImageResource(R.drawable.ic_record_begin);
			Toast.makeText(RecordService.this, "结束录屏："+mVideoPath+mVideoName, Toast.LENGTH_SHORT).show();
		}
	}
	
	private String prepare() {
		MediaFormat format = MediaFormat.createVideoFormat(
				MediaFormat.MIMETYPE_VIDEO_AVC, mScreenWidth, mScreenHeight); //视频格式与宽高
		format.setInteger(MediaFormat.KEY_BIT_RATE, 300*1024*8); //每秒多少位，这里设置每秒300K
		format.setInteger(MediaFormat.KEY_FRAME_RATE, 20); //每秒多少帧，每秒20帧则每帧大小15K
		format.setInteger(MediaFormat.KEY_COLOR_FORMAT, MediaCodecInfo.CodecCapabilities.COLOR_FormatSurface); //设置颜色格式
		format.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, 2); //设置关键帧的间隔
		try {
			mMediaCodec = MediaCodec.createEncoderByType(MediaFormat.MIMETYPE_VIDEO_AVC);
			mMediaCodec.configure(format, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE);
			mMediaCodec.start(); //开始视频编码
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			return e.getMessage();
		}
	}

	private void recordStart() {
		String result = prepare();
		if (result != null) {
			Toast.makeText(this, "准备录屏发生异常："+result, Toast.LENGTH_SHORT).show();
			return;
		}
		if (mMP == null) {
			mMP = mMpMgr.getMediaProjection(MainApplication.getInstance().getResultCode(), 
					MainApplication.getInstance().getResultIntent());
		}
		mVirtualDisplay = mMP.createVirtualDisplay("ScreenRecords", mScreenWidth, mScreenHeight, mScreenDensity, 
				DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR, mMediaCodec.createInputSurface(), null, null);
		new RecordThread().start();
	}

	private class RecordThread extends Thread {
		@Override
		public void run() {
			try {
				Log.d(TAG, "RecordThread Start");
				FileUtil.createDir(mVideoPath);
				mVideoName = Utils.getNowDateTime() + ".mp4"; //文件格式为MPEG-4
				mMediaMuxer = new MediaMuxer(mVideoPath+mVideoName, MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4);
				while (isRecording) {
					int index = mMediaCodec.dequeueOutputBuffer(mBufferInfo, 10000); //返回缓冲区的索引
					Log.d(TAG, "缓冲区的索引为" + index);
					if (index == MediaCodec.INFO_OUTPUT_FORMAT_CHANGED) { //输出格式发生变化
						if (isMuxerStarted) {
							throw new IllegalStateException("输出格式已经发生变化");
						}
						MediaFormat newFormat = mMediaCodec.getOutputFormat();
						mVideoTrackIndex = mMediaMuxer.addTrack(newFormat);
						mMediaMuxer.start();
						isMuxerStarted = true;
						Log.d(TAG, "新的输出格式是："+newFormat.toString()+"，媒体转换器的轨道索引是"+mVideoTrackIndex);
					} else if (index == MediaCodec.INFO_TRY_AGAIN_LATER) { //请求超时
						Thread.sleep(50);
					} else if (index >= 0) { //正常输出
						if (!isMuxerStarted) {
							throw new IllegalStateException("媒体转换器尚未添加格式轨道");
						}
						encodeToVideo(index);
						mMediaCodec.releaseOutputBuffer(index, false);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				release();
			}
		}
	}

	private void encodeToVideo(int index) {
		ByteBuffer encoded = mMediaCodec.getOutputBuffer(index);
		if ((mBufferInfo.flags & MediaCodec.BUFFER_FLAG_CODEC_CONFIG) != 0) { //如果不是媒体数据
			mBufferInfo.size = 0;
		}
		if (mBufferInfo.size == 0) { //缓冲区不存在有效数据
			encoded = null;
		} else {
			Log.d(TAG, "缓冲区大小=" + mBufferInfo.size
					+ ", 持续时间=" + mBufferInfo.presentationTimeUs
					+ ", 偏移=" + mBufferInfo.offset);
		}
		if (encoded != null) {
			encoded.position(mBufferInfo.offset);
			encoded.limit(mBufferInfo.offset + mBufferInfo.size);
			mMediaMuxer.writeSampleData(mVideoTrackIndex, encoded, mBufferInfo); //写入视频文件
		}
	}

	private void release() {
		isRecording = false;
		isMuxerStarted = false;
		if (mMediaCodec != null) {
			mMediaCodec.stop();
			mMediaCodec.release();
			mMediaCodec = null;
		}
		if (mVirtualDisplay != null) {
			mVirtualDisplay.release();
			mVirtualDisplay = null;
		}
		if (mMediaMuxer != null) {
			mMediaMuxer.stop();
			mMediaMuxer.release();
			mMediaMuxer = null;
		}
	}

	@Override
	public void onDestroy() {
		release();
		if (mFloatView != null && mFloatView.isShow() == true) {
			mFloatView.close();
		}
		if (mMP != null) {
			mMP.stop();
		}
		super.onDestroy();
	}
}
