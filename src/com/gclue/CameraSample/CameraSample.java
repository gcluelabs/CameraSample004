package com.gclue.CameraSample;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.hardware.Camera;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;

public class CameraSample extends Activity implements SensorEventListener {

	private MyView mView;
	
	/**
	 * Sensor Manager
	 */
	private SensorManager mSensorManager = null;  
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Notification Barを消す
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
		// Title Barを消す
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		// SensorManager
		mSensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
		
		// Sensorの取得とリスナーへの登録
		List < Sensor > sensors = mSensorManager.getSensorList(Sensor.TYPE_MAGNETIC_FIELD);
		if (sensors.size() > 0) {
			Sensor sensor = sensors.get(0);
			mSensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_FASTEST);
		}
		
		CameraView mCamera = new CameraView(this);
		setContentView(mCamera);

		mView = new MyView(this);
		addContentView(mView, new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
	}

	@Override
	public void onAccuracyChanged(Sensor arg0, int arg1) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onSensorChanged(SensorEvent sensorEvent) {
		Log.i("SURFACE", "SensorChanged()");
		if (sensorEvent.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
			Log.i("SURFACE", "yaw:" + sensorEvent.values[0]);
			Log.i("SURFACE", "picth:" + sensorEvent.values[1]);
			Log.i("SURFACE", "roll:" + sensorEvent.values[2]);
			mView.setOrientation(""+sensorEvent.values[0], ""+sensorEvent.values[1], ""+sensorEvent.values[2]);
		}
	}
	public void onDestroy() {
		super.onDestroy();
		if (mSensorManager != null) {
			mSensorManager.unregisterListener(this);
		}
	}
}

/**
 * CameraView
 */
class CameraView extends SurfaceView implements SurfaceHolder.Callback {
	/**
	 * Cameraのインスタンスを格納する変数
	 */
	private Camera mCamera;

	public CameraView(Context context) {
		super(context);
		getHolder().addCallback(this);
		getHolder().setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
	}

	/**
	 * Surfaceに変化があった場合に呼ばれる
	 */
	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
		Log.i("CAMERA", "surfaceChaged");

		// 画面設定
//		Camera.Parameters parameters = mCamera.getParameters();
//		parameters.setPreviewSize(width, height);
//		mCamera.setParameters(parameters);

		// プレビュー表示を開始
		mCamera.startPreview();
	}

	/**
	 * Surfaceが生成された際に呼ばれる
	 */
	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		Log.i("CAMERA", "surfaceCreated");

		// カメラをOpen
		mCamera = Camera.open();
		try {
			mCamera.setPreviewDisplay(holder);
		} catch (Exception e) {
		}
	}

	/**
	 * Surfaceが破棄された場合に呼ばれる
	 */
	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		Log.i("CAMERA", "surfaceDestroyed");

		// カメラをClose
		mCamera.stopPreview();
		mCamera.release();
		mCamera = null;
	}
}

/**
 * オーバーレイ描画用のクラス
 */
class MyView extends View {

	/**
	 * x座標
	 */
	private int x;

	/**
	 * y座標
	 */
	private int y;
	
	 /**
	 * Roll
	 */
	private String roll;

	/**
	* Yaw
	*/
	private String yaw;

	/**
	* Pitch
	*/
	private String pitch;
	
	/**
	 * コンストラクタ
	 * 
	 * @param context
	 */
	public MyView(Context context) {
		super(context);
		setFocusable(true);
	}
	
	/**
	 * 値を渡す
	 */
	public void setOrientation(String yaw, String pitch, String roll){
		this.yaw = yaw;
		this.pitch = pitch;
		this.roll = roll;
		invalidate();
	}
	
	/**
	 * 描画処理
	 */
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		// 背景色を設定
		canvas.drawColor(Color.TRANSPARENT);

		// 描画するための線の色を設定
		Paint mainPaint = new Paint();
		mainPaint.setStyle(Paint.Style.FILL);
		mainPaint.setARGB(255, 255, 255, 100);

		// 線で描画
		canvas.drawLine(x, y, 50, 50, mainPaint);
		mainPaint.setTextSize(40);
		// 文字を描画
		canvas.drawText(""+yaw, 10, 40, mainPaint);
		canvas.drawText(""+roll, 10, 80, mainPaint);
		canvas.drawText(""+pitch, 10, 120, mainPaint);
	}

	/**
	 * タッチイベント
	 */
	public boolean onTouchEvent(MotionEvent event) {

		// X,Y座標の取得
		x = (int) event.getX();
		y = (int) event.getY();

		// 再描画の指示
		invalidate();

		return true;
	}
}