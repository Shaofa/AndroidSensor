package com.example.xixi.udpclient;

import java.net.*;
import java.io.*;
import java.text.DecimalFormat;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;


public class MainActivity extends Activity implements SensorEventListener{
    /*======= UDP var =======*/
    private static final int MAX_DATA_PACKET_LENGTH = 1024;
    private byte[] sendBuf = new byte[MAX_DATA_PACKET_LENGTH];
    private int serverPort = 8898;
    private String serverHost = "192.168.1.100";
    private DatagramSocket clientSock = null;
    private DatagramPacket packet = new DatagramPacket(sendBuf,sendBuf.length);
    private UdpSendThread udpSendThread = null;

    /*======= Sensor var =======*/
    private SensorManager sensorManager;
    private Sensor magneticSensor;
    private Sensor accelSensor;
    private Sensor gyroscopeSensor;
    private long accelTimestamp;
    private long gyrosTimestamp;
    private float[] accelData = new float[3];
    private float[] gyrosData = new float[3];

    /*======= UI var =======*/
    private EditText editText_data;
    private EditText editText_host;
    private EditText editText_port;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /*======= UDP initial =======*/
        try {
            clientSock = new DatagramSocket(serverPort+1);
        }catch (SocketException e){
            e.printStackTrace();
        }
        udpSendThread = new UdpSendThread();
        udpSendThread.start();

        /*======= Sensor Initial =======*/
        sensorManager = (SensorManager)getSystemService(Context.SENSOR_SERVICE);
        accelSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        magneticSensor = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        gyroscopeSensor = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        sensorManager.registerListener(this, accelSensor, SensorManager.SENSOR_DELAY_FASTEST);
//        sensorManager.registerListener(this, magneticSensor, SensorManager.SENSOR_DELAY_UI);
//        sensorManager.registerListener(this, gyroscopeSensor, SensorManager.SENSOR_DELAY_UI);

        /*======= UI Initial =======*/
        editText_data = (EditText)findViewById(R.id.editText_sendData);
        editText_host = (EditText)findViewById(R.id.editText_host);
        editText_port = (EditText)findViewById(R.id.editText_port);
        editText_host.setText(serverHost);
        editText_port.setText(String.valueOf(serverPort));
    }



    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor == null) {
            return;
        }
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            accelData[0] = event.values[0];
            accelData[1] = event.values[1];
            accelData[2] = event.values[2];
            accelTimestamp = event.timestamp;
            synchronized (udpSendThread) {
                udpSendThread.notify();
            }
        }
        if (event.sensor.getType() == Sensor.TYPE_GYROSCOPE) {
            gyrosData[0] = event.values[0];
            gyrosData[1] = event.values[1];
            gyrosData[2] = event.values[2];
            gyrosTimestamp = event.timestamp;
            synchronized (udpSendThread) {
                udpSendThread.notify();
            }
        }
    }

    public class UdpSendThread extends Thread{
        public UdpSendThread(){

        }
        public void run() {
            String text;
            DecimalFormat bigDecimal = new DecimalFormat("#.####");
            while (true) {
                synchronized (this) {
                    try {
                        wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                text = bigDecimal.format(accelData[0]);
                text +=",";
                text += bigDecimal.format(accelData[1]);
                text += ",";
                text += bigDecimal.format(accelData[2]);
                packet.setData(text.getBytes());
                packet.setLength(text.length());
                serverPort = Integer.valueOf(editText_port.getText().toString());
                serverHost = editText_host.getText().toString();
                try{
                    packet.setPort(serverPort);
                    packet.setAddress(InetAddress.getByName(serverHost));
                    clientSock.send(packet);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onAccuracyChanged(Sensor arg0, int arg1) {
    }
}
