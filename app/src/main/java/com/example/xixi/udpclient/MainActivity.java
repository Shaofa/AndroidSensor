package com.example.xixi.udpclient;

import java.net.*;
import java.io.*;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;


public class MainActivity extends ActionBarActivity {

    private static final int MAX_DATA_PACKET_LENGTH = 1024;
    private byte[] sendBuf = new byte[MAX_DATA_PACKET_LENGTH];
    private int serverPort = 8898;
    private String serverHost = "192.168.1.100";
    private DatagramSocket clientSock = null;
    private DatagramPacket packet = new DatagramPacket(sendBuf,sendBuf.length);
    private UdpSendThread udpSendThread = null;
    private String isBtnDown[] = {"false"};
    private EditText editText_data;
    private EditText editText_host;
    private EditText editText_port;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editText_data = (EditText)findViewById(R.id.editText_sendData);
        editText_host = (EditText)findViewById(R.id.editText_host);
        editText_port = (EditText)findViewById(R.id.editText_port);
        editText_host.setText(serverHost);
        editText_port.setText(String.valueOf(serverPort));
        try {
            clientSock = new DatagramSocket(serverPort+1);
        }catch (SocketException e){
            e.printStackTrace();
        }
        udpSendThread = new UdpSendThread();
        udpSendThread.start();
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

    public void onClickBtnSendData(View arg0){
        synchronized (udpSendThread){
            udpSendThread.notify();
        }

    }

    public class UdpSendThread extends Thread{
        public UdpSendThread(){

        }
        public void run() {
            String text;
            while (true) {
                synchronized (this){
                    try {
                        wait();
                    }catch (InterruptedException e){
                        e.printStackTrace();
                    }
                }
                text = editText_data.getText().toString();
                packet.setPort(serverPort);
                packet.setData(text.getBytes());
                packet.setLength(text.length());
                try {
                    packet.setAddress(InetAddress.getByName(serverHost));
                    clientSock.send(packet);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
