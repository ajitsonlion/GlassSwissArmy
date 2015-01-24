package com.google.android.glass.eye;

import android.util.Log;

import com.glassgestures.MainActivity;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by ajit on 24.01.15.
 */
public class EyeGesturesHelper {

    private ArrayList<EyeGesture> eyeGestures;
    private MainActivity.EyeGestureListener mEyeGestureListener;
    private EyeGestureManager mEyeGestureManager;

   public EyeGesturesHelper(MainActivity.EyeGestureListener mEyeGestureListener,EyeGestureManager mEyeGestureManager){
       this.mEyeGestureListener=mEyeGestureListener;
       this.mEyeGestureManager=mEyeGestureManager;
       getEyeGestures();
   }


    public  void getEyeGestures(){

         eyeGestures=new ArrayList<EyeGesture>();

        Collections.addAll(eyeGestures, EyeGesture.values());

    }


    public void registerEyeGestures(){

        for (EyeGesture eyeGesture:eyeGestures){
            mEyeGestureManager.register(eyeGesture,mEyeGestureListener);
            Log.v("EYE", "registered "+eyeGesture.name());
        }

    }


    public void unRegisterEyeGestures(){

        for (EyeGesture eyeGesture:eyeGestures){
            mEyeGestureManager.unregister(eyeGesture,mEyeGestureListener);
            Log.v("EYE", "unregistered "+eyeGesture.name());
        }

    }
}
