package com.google.android.glass.Sensors;

import android.content.Context;
import android.util.Log;

import com.ubhave.sensormanager.ESException;
import com.ubhave.sensormanager.ESSensorManager;
import com.ubhave.sensormanager.ESSensorManagerInterface;
import com.ubhave.sensormanager.SensorDataListener;
import com.ubhave.sensormanager.data.SensorData;
import com.ubhave.sensormanager.data.pull.AccelerometerData;
import com.ubhave.sensormanager.data.pull.LocationData;
import com.ubhave.sensormanager.sensors.SensorEnum;
import com.ubhave.sensormanager.sensors.SensorInterface;
import com.ubhave.sensormanager.sensors.SensorUtils;

import java.util.ArrayList;

/**
 * Created by ajit on 25.01.15.
 */
public class SensorHelper implements ESSensorManagerInterface,SensorDataListener {

    private   Context context;
    ArrayList<String> listOfSensors=null;

    SensorDataListener sensorDataListener;
    ESSensorManager esSensorManager;

    ArrayList<Integer> subscriptionIDs=null;

    public SensorHelper(Context context){

        this.context=context;
        sensorDataListener=this;
        subscriptionIDs=new ArrayList<>();
        listOfSensors=new ArrayList<>();
        try {
            esSensorManager=ESSensorManager.getSensorManager(context);
        } catch (ESException e) {
            e.printStackTrace();
        }
    }


    public  ArrayList<String> getAllSensorsByName(){

        for ( SensorEnum sensorEnum:SensorEnum.values()){
            listOfSensors.add( sensorEnum.getName());

        }

        return listOfSensors;
    }

    public void subscribeAllSensors(){

        for ( SensorEnum sensorEnum:SensorEnum.values()){

            try {
                subscriptionIDs.add(subscribeToSensorData(sensorEnum.getType(),sensorDataListener));

                Log.v("Subscribed",sensorEnum.getName());
            } catch (ESException e) {
                e.printStackTrace();
            }

        }

    }


    public void unSubscribeAllSensors(){


        for ( Integer subscriptionID:subscriptionIDs){

            try {
                unsubscribeFromSensorData(subscriptionID);
                Log.v("unSubscribed",SensorUtils.getSensorName(subscriptionID));
            } catch (ESException e) {
                e.printStackTrace();
            }

        }

    }



    public void pauseOtherSensors(int subscription){

        for (Integer subscriptionID:subscriptionIDs){

            if (subscriptionID!=subscription){
                try {
                    pauseSubscription(subscriptionID);
                } catch (ESException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    public void unPauseOtherSensors(int subscription){

        for (Integer subscriptionID:subscriptionIDs){

            if (subscriptionID!=subscription){
                try {
                    unPauseSubscription(subscriptionID);
                } catch (ESException e) {
                    e.printStackTrace();
                }
            }
        }
    }



    @Override
    public int subscribeToSensorData(int i, SensorDataListener sensorDataListener) throws ESException {

        return i;
    }

    @Override
    public void unsubscribeFromSensorData(int i) throws ESException {

    }

    @Override
    public SensorData getDataFromSensor(int i) throws ESException {


        return esSensorManager.getDataFromSensor(i);
    }

    @Override
    public void pauseSubscription(int i) throws ESException {

    }

    @Override
    public void unPauseSubscription(int i) throws ESException {

    }

    @Override
    public void setSensorConfig(int i, String s, Object o) throws ESException {

    }

    @Override
    public Object getSensorConfigValue(int i, String s) throws ESException {
        return null;
    }

    @Override
    public void setGlobalConfig(String s, Object o) throws ESException {

    }

    @Override
    public Object getGlobalConfig(String s) throws ESException {
        return null;
    }

    @Override
    public void onDataSensed(SensorData sensorData) {

        try {
            String msg= "Sensor "+SensorUtils.getSensorName(sensorData.getSensorType())+" data " +sensorData.getPrevSensorData();
            Log.v("SensorData",msg);
        } catch (ESException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onCrossingLowBatteryThreshold(boolean b) {

    }
}
