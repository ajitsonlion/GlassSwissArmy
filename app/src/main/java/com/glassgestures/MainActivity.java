package  com.glassgestures;

import com.communication.SocketIOSingelton;
import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.Socket;
import com.google.android.glass.Shake.ShakeDetector;
import com.google.android.glass.eye.EyeGesture;
import com.google.android.glass.eye.EyeGestureManager;
import com.google.android.glass.eye.EyeGesturesHelper;
import com.google.android.glass.head.HeadGestureDetector;
import com.google.android.glass.media.Sounds;
import com.google.android.glass.widget.CardBuilder;
import com.google.android.glass.widget.CardScrollAdapter;
import com.google.android.glass.widget.CardScrollView;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Toast;

import java.net.URISyntaxException;
import java.util.ArrayList;




public class MainActivity extends Activity implements SensorEventListener,HeadGestureDetector.OnHeadGestureListener,ShakeDetector.Listener

{

    private CardScrollView mCardScroller;

    private ArrayList<CardBuilder> cards=new ArrayList<CardBuilder>();
    private SensorManager mSensorManager;
    private int id=0;
    private HeadGestureDetector mHeadGestureDetector;
    private EyeGestureManager mEyeGestureManager;
    private EyeGestureListener mEyeGestureListener;
    private EyeGesturesHelper meyeGesturesHelper;
    SocketIOSingelton socketIOSingelton;
    Socket socket;

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
        addcard();

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
        mSensorManager.unregisterListener(this);
        meyeGesturesHelper.unRegisterEyeGestures();

    }

    @Override
    protected void onResume() {
        super.onResume();
        meyeGesturesHelper.registerEyeGestures();
        mCardScroller.activate();
        mHeadGestureDetector.start();
         mSensorManager.registerListener(
                this,
                mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_GAME);
        mSensorManager.registerListener(
                this,
                mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD),
                SensorManager.SENSOR_DELAY_GAME);
    }

    @Override
    protected void onPause() {

        super.onPause();
        meyeGesturesHelper.unRegisterEyeGestures();
        mHeadGestureDetector.stop();
        mSensorManager.unregisterListener(this);
        mCardScroller.deactivate();

    }

    /**
     * Builds a Glass styled "Hello World!" view using the {@link CardBuilder} class.
     */
    private View buildView() {
        CardBuilder card = new CardBuilder(this, CardBuilder.Layout.TEXT);

        card.setText(R.string.hello_world);
        return card.getView();
    }


    @Override
    public void hearShake() {

        addcard();
        Toast.makeText(this,"Card Added",Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onNod() {

    }

    @Override
    public void onShakeToLeft() {

    }

    @Override
    public void onShakeToRight() {

    }

    public void addcard(){
        CardBuilder card = new CardBuilder(this, CardBuilder.Layout.TEXT);
        card.setText(++id+"");
        cards.add(card);

    }


    @Override
    public void onSensorChanged(SensorEvent event) {

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
            runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    Log.v("EYE",eyeGesture.name());
                }
            });
        }
    }
}
