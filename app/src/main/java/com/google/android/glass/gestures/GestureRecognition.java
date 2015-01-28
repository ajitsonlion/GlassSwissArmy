package com.google.android.glass.gestures;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;

import java.util.List;

/**
 * Created by ajit on 27.01.15.
 */
public class GestureRecognition implements IGestureRecognitionService{

    private IBinder gestureListenerStub= new IGestureRecognitionListener.Stub() {

        @Override
        public void onGestureLearned(String gestureName) throws RemoteException {
            System.out.println("Gesture" + gestureName + "learned!");
        }

        @Override
        public void onGestureRecognized(Distribution distribution) throws RemoteException {
            System.out.println(String.format("%s %f", distribution.getBestMatch(),distribution.getBestDistance()));
        }

        @Override
        public void onTrainingSetDeleted(String trainingSet) throws RemoteException {
            System.out.println("Training Set " + trainingSet + " deleted!");
        }
    };


    private IGestureRecognitionService recognitionService;

    private final ServiceConnection gestureConnection = new ServiceConnection() {

        public void onServiceConnected(ComponentName className, IBinder service) {
            recognitionService = IGestureRecognitionService.Stub.asInterface(service);
            try {
                recognitionService.registerListener(IGestureRecognitionListener.Stub.asInterface(gestureListenerStub));
            } catch (RemoteException e) {
                e.printStackTrace();
            }}

        public void onServiceDisconnected(ComponentName className) {

        }
    };



    public GestureRecognition(Context context){

        Intent gestureBindIntent = new Intent("com.google.android.glass.gestures.GESTURE_RECOGNIZER");
        context.bindService(gestureBindIntent, gestureConnection, Context.BIND_AUTO_CREATE);
    }


    @Override
    public void startClassificationMode(String trainingSetName) throws RemoteException {

    }

    @Override
    public void stopClassificationMode() throws RemoteException {

    }

    @Override
    public void registerListener(IGestureRecognitionListener listener) throws RemoteException {

    }

    @Override
    public void unregisterListener(IGestureRecognitionListener listener) throws RemoteException {

    }

    @Override
    public void startLearnMode(String trainingSetName, String gestureName) throws RemoteException {

    }

    @Override
    public void stopLearnMode() throws RemoteException {

    }

    @Override
    public void onPushToGesture(boolean pushed) throws RemoteException {

    }

    @Override
    public void deleteTrainingSet(String trainingSetName) throws RemoteException {

    }

    @Override
    public void deleteGesture(String trainingSetName, String gestureName) throws RemoteException {

    }

    @Override
    public List<String> getGestureList(String trainingSet) throws RemoteException {
        return null;
    }

    @Override
    public boolean isLearning() throws RemoteException {
        return false;
    }

    @Override
    public IBinder asBinder() {
        return null;
    }
}

