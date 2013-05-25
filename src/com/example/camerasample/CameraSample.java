package com.example.camerasample;

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

		getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		// SensorManager
		mSensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
		
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
	private Camera mCamera;

	public CameraView(Context context) {
		super(context);
		getHolder().addCallback(this);
		getHolder().setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
		Log.i("CAMERA", "surfaceChaged");
//		Camera.Parameters parameters = mCamera.getParameters();
//		parameters.setPreviewSize(width, height);
//		mCamera.setParameters(parameters);

		mCamera.startPreview();
	}


	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		Log.i("CAMERA", "surfaceCreated");

		mCamera = Camera.open();
		try {
			mCamera.setPreviewDisplay(holder);
		} catch (Exception e) {
		}
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		Log.i("CAMERA", "surfaceDestroyed");

		mCamera.stopPreview();
		mCamera.release();
		mCamera = null;
	}
}

class MyView extends View {

	private int x;

	private int y;
	
	private String roll;

	private String yaw;

	private String pitch;
	
	public MyView(Context context) {
		super(context);
		setFocusable(true);
	}
	
	public void setOrientation(String yaw, String pitch, String roll){
		this.yaw = yaw;
		this.pitch = pitch;
		this.roll = roll;
		invalidate();
	}
	
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		canvas.drawColor(Color.TRANSPARENT);

		Paint mainPaint = new Paint();
		mainPaint.setStyle(Paint.Style.FILL);
		mainPaint.setARGB(255, 255, 255, 100);

		canvas.drawLine(x, y, 50, 50, mainPaint);
		mainPaint.setTextSize(40);
		canvas.drawText(""+yaw, 10, 40, mainPaint);
		canvas.drawText(""+roll, 10, 80, mainPaint);
		canvas.drawText(""+pitch, 10, 120, mainPaint);
	}

	public boolean onTouchEvent(MotionEvent event) {

		x = (int) event.getX();
		y = (int) event.getY();

		invalidate();

		return true;
	}
}