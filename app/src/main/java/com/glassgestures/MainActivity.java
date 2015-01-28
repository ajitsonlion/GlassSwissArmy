package  com.glassgestures;

import com.communication.SocketIOSingelton;

import com.github.nkzawa.socketio.client.Socket;
import com.google.android.glass.Sensors.SensorHelper;
import com.google.android.glass.Shake.ShakeDetector;
import com.google.android.glass.eye.EyeGesture;
import com.google.android.glass.eye.EyeGestureManager;
import com.google.android.glass.eye.EyeGesturesHelper;
import com.google.android.glass.gestures.Distribution;
import com.google.android.glass.gestures.GestureRecognition;
import com.google.android.glass.gestures.IGestureRecognitionListener;
import com.google.android.glass.gestures.IGestureRecognitionService;
import com.google.android.glass.head.HeadGestureDetector;
import com.google.android.glass.media.Sounds;
import com.google.android.glass.touchpad.Gesture;
import com.google.android.glass.touchpad.GestureDetector;
import com.google.android.glass.widget.CardBuilder;
import com.google.android.glass.widget.CardScrollAdapter;
import com.google.android.glass.widget.CardScrollView;



import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.ServiceConnection;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.os.Bundle;

import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends Activity implements SensorEventListener,HeadGestureDetector.OnHeadGestureListener,ShakeDetector.Listener

{
    private static final String TAG = "MainActivity";

    private CardScrollView mCardScroller;
    private static final String GESTURE="gesture";
     private ArrayList<CardBuilder> cards=new ArrayList<CardBuilder>();
    private SensorManager mSensorManager;
    private int id=0;
    private HeadGestureDetector mHeadGestureDetector;
    private EyeGestureManager mEyeGestureManager;
    private EyeGestureListener mEyeGestureListener;
    private EyeGesturesHelper meyeGesturesHelper;
    SocketIOSingelton socketIOSingelton;
    Socket socket;
    private float[] rotationMatrix;
    private IGestureRecognitionService recognitionService;
    private final float[] mValuesOrientation = new float[3];
    private int HEAD_POSITION_DOWN = 70;
    //private final int PROGRESS_BAR_SCALE_FACTOR = (50 / (CALIBRATION_ANGLE));

    private double PROGRESS_BAR_SCALE_FACTOR = 2;
     private AudioManager mAudioManager;
    private final float[] mValuesAccelerometer = new float[3];
    private final float[] mValuesMagnet = new float[3];
    SensorHelper sensorHelper;
    private GestureDetector mGestureDetector;

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        mHeadGestureDetector = new HeadGestureDetector(this);
        mHeadGestureDetector.setOnHeadGestureListener(this);
        ShakeDetector sd = new ShakeDetector(this);
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        sd.start(mSensorManager);
        mEyeGestureManager = EyeGestureManager.from(this);
        mEyeGestureListener = new EyeGestureListener();
        meyeGesturesHelper=new EyeGesturesHelper(mEyeGestureListener,mEyeGestureManager);
        mGestureDetector = createGestureDetector(this);
        sensorHelper=new SensorHelper(this);

       new GestureRecognition(this);


        mCardScroller = new CardScrollView(this);
        mCardScroller.setAdapter(new CardScrollAdapter() {
            @Override
            public int getCount() {
                return cards.size();
            }

            @Override
            public Object getItem(int position) {
                return cards.get(position);
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                return cards.get(position).getView();
            }

            @Override
            public int getPosition(Object item) {
                return 0;
            }
        });
        // Handle the TAP event.
        mCardScroller.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Plays disallowed sound to indicate that TAP actions are not supported.
                AudioManager am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
                am.playSoundEffect(Sounds.DISALLOWED);
            }
        });
        setContentView(mCardScroller);

        try {

            socketIOSingelton=SocketIOSingelton.getInstance();
              socket=socketIOSingelton.getSocket();

        } catch (URISyntaxException e) {
            e.printStackTrace();
        }



    }

    @Override
    protected void onStart() {
        super.onStart();

        sensorHelper.subscribeAllSensors();

        meyeGesturesHelper.registerEyeGestures();
        mSensorManager.registerListener(
                this,
                mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_FASTEST);
        mSensorManager.registerListener(
                this,
                mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD),
                SensorManager.SENSOR_DELAY_FASTEST);
    }

    @Override
    protected void onStop() {
        super.onStop();
        sensorHelper.unSubscribeAllSensors();
        mSensorManager.unregisterListener(this);
        meyeGesturesHelper.unRegisterEyeGestures();

    }

    @Override
    protected void onResume() {
        super.onResume();

        sensorHelper.subscribeAllSensors();
        meyeGesturesHelper.registerEyeGestures();
        mCardScroller.activate();
        mHeadGestureDetector.start();
         mSensorManager.registerListener(
                this,
                mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_UI);
        mSensorManager.registerListener(
                this,
                mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD),
                SensorManager.SENSOR_DELAY_UI);

                    addcard();
    }

    @Override
    protected void onPause() {

        super.onPause();
        sensorHelper.unSubscribeAllSensors();
        meyeGesturesHelper.unRegisterEyeGestures();
        mHeadGestureDetector.stop();
        mSensorManager.unregisterListener(this);
        mCardScroller.deactivate();

    }


    @Override
    public void hearShake() {

        socket.emit(GESTURE, "Shook Head");

        // socket.emit("dblBlink","hearShake");

    }

    @Override
    public void onNod() {

        socket.emit(GESTURE, "Nod");
        //socket.emit("dblBlink","onNod");
     }

    @Override
    public void onShakeToLeft() {

        socket.emit(GESTURE, "ShakeLeft");
        //socket.emit("dblBlink","onShakeToLeft");
     }

    @Override
    public void onShakeToRight() {

        socket.emit(GESTURE, "ShakeRight");
        //socket.emit("dblBlink","onShakeToRight");
     }

    private GestureDetector createGestureDetector(Context context){
        GestureDetector gestureDetector = new GestureDetector(context);
        //Create a base listener for generic gestures
        gestureDetector.setBaseListener( new GestureDetector.BaseListener() {
            @Override
            public boolean onGesture(Gesture gesture) {
                Log.e(TAG,"gesture = " + gesture);
                switch (gesture) {
                    case TAP:
                        Log.e(TAG,"TAP called.");
                        socket.emit(GESTURE,"TAP called.");
                        break;
                    case LONG_PRESS:
                        socket.emit(GESTURE, "LONG_PRESS called.");
                        return true;
                    case SWIPE_DOWN:
                        socket.emit(GESTURE, "SWIPE_DOWN called.");
                        return true;
                    case SWIPE_LEFT:
                        socket.emit(GESTURE, "SWIPE_LEFT called.");
                        return true;
                    case SWIPE_RIGHT:
                        socket.emit(GESTURE, "SWIPE_RIGHT called.");
                        return true;
                    case SWIPE_UP:
                        socket.emit(GESTURE, "SWIPE_UP called.");
                        return true;
                    case THREE_LONG_PRESS:
                        socket.emit(GESTURE, "THREE_LONG_PRESS called.");
                        return true;
                    case THREE_TAP:
                        socket.emit(GESTURE, "THREE_TAP called.");
                        return true;
                    case TWO_LONG_PRESS:
                        socket.emit(GESTURE, "TWO_LONG_PRESS called.");
                        return true;
                    case TWO_SWIPE_DOWN:
                        socket.emit(GESTURE, "TWO_SWIPE_DOWN called.");
                        return true;
                    case TWO_SWIPE_LEFT:
                        socket.emit(GESTURE, "TWO_SWIPE_LEFT called.");
                        return true;
                    case TWO_SWIPE_RIGHT:
                        socket.emit(GESTURE, "TWO_SWIPE_RIGHT called.");
                        return true;
                    case TWO_SWIPE_UP:
                        socket.emit(GESTURE, "TWO_SWIPE_UP called.");
                        return true;
                    case TWO_TAP:
                        socket.emit(GESTURE, "TWO_TAP called.");
                        return true;
                }

                return false;
            }
        });
        gestureDetector.setFingerListener(new com.google.android.glass.touchpad.GestureDetector.FingerListener() {
            @Override
            public void onFingerCountChanged(int previousCount, int currentCount) {
                // do something on finger count changes
                socket.emit(GESTURE, "onFingerCountChanged()");

            }
        });
        gestureDetector.setScrollListener(new com.google.android.glass.touchpad.GestureDetector.ScrollListener() {
            @Override
            public boolean onScroll(float displacement, float delta, float velocity) {
                // do something on scrolling
                socket.emit(GESTURE, "onScroll()");
                return false;
            }
        });
        gestureDetector.setTwoFingerScrollListener(new GestureDetector.TwoFingerScrollListener() {
            @Override
            public boolean onTwoFingerScroll(float v, float v2, float v3) {
                // do something on scrolling
                socket.emit(GESTURE, "onTwoFingerScroll()");

            return false;
            }
        });

         return gestureDetector;
    }


    /*
* Send generic motion events to the gesture detector
*/
    @Override
    public boolean onGenericMotionEvent(MotionEvent event) {
        if (mGestureDetector != null) {
            return mGestureDetector.onMotionEvent(event);
        }
        return false;
    }

    public void addcard(){

        try {


            for (String sensor:sensorHelper.getAllSensorsByName()){
                 CardBuilder card = new CardBuilder(this, CardBuilder.Layout.TEXT_FIXED);
                card.setFootnote(sensor);
               cards.add(card);

            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    @Override
    public void onSensorChanged(SensorEvent event) {

        if (event.sensor.getType()==Sensor.TYPE_ACCELEROMETER){

        }


        int type = event.sensor.getType();

        //Smoothing the sensor data a bit
        if (type == Sensor.TYPE_MAGNETIC_FIELD) {
            mValuesMagnet[0] = (mValuesMagnet[0] * 1 + event.values[0]) * 0.5f;
            mValuesMagnet[1] = (mValuesMagnet[1] * 1 + event.values[1]) * 0.5f;
            mValuesMagnet[2] = (mValuesMagnet[2] * 1 + event.values[2]) * 0.5f;

           // socket.emit("acceleration",event.values[0],event.values[1],event.values[2]);

        } else if (type == Sensor.TYPE_ACCELEROMETER) {
            mValuesAccelerometer[0] = (mValuesAccelerometer[0] * 2 + event.values[0]) * 0.33334f;
            mValuesAccelerometer[1] = (mValuesAccelerometer[1] * 2 + event.values[1]) * 0.33334f;
            mValuesAccelerometer[2] = (mValuesAccelerometer[2] * 2 + event.values[2]) * 0.33334f;

           // socket.emit("acceleration",mValuesAccelerometer[0],mValuesAccelerometer[1],mValuesAccelerometer[2]);

        }

        if ((type == Sensor.TYPE_MAGNETIC_FIELD) || (type == Sensor.TYPE_ACCELEROMETER)) {
            rotationMatrix = new float[16];
            SensorManager.getRotationMatrix(rotationMatrix, null, mValuesAccelerometer, mValuesMagnet);

        }
        SensorManager.getOrientation(rotationMatrix, mValuesOrientation);

        int tiltX = (int) Math.floor(Math.toDegrees(mValuesOrientation[0])); //roll
        int tiltY = (int) Math.floor(Math.toDegrees(mValuesOrientation[1])); //roll
        int tiltZ = (int) Math.floor(Math.toDegrees(mValuesOrientation[2])); //roll
        //socket.emit("rotation",tiltX,tiltY,tiltZ);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    public class EyeGestureListener implements EyeGestureManager.Listener {

        @Override
        public void onEnableStateChange(EyeGesture eyeGesture, boolean paramBoolean) {
        }
        @Override
        public void onDetected(final EyeGesture eyeGesture) {
            //  Log.v("EYE",eyeGesture.name());
            socket.emit(GESTURE,"dblBlink");
            // Toast.makeText(getApplicationContext(),eyeGesture.name(),Toast.LENGTH_LONG).show();

        }
    }

}


