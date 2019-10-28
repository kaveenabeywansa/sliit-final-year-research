package com.stark.smartwearableheadset.services;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.util.Log;
import android.widget.Toast;

import java.io.InputStream;
import java.util.Set;
import java.util.UUID;

public class BluetoothService {
    String address = null, name = null;
    BluetoothAdapter myBluetooth = null;
    BluetoothSocket btSocket = null;
    Set<BluetoothDevice> pairedDevices;
    static final UUID myUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    public BluetoothService() {
        connectDevice();
    }

    private void connectDevice() {
        try {
            myBluetooth = BluetoothAdapter.getDefaultAdapter();
            address = myBluetooth.getAddress();
            pairedDevices = myBluetooth.getBondedDevices();
            if (pairedDevices.size() > 0) {
                for (BluetoothDevice bt : pairedDevices) {
                    address = bt.getAddress().toString();
                    name = bt.getName().toString();
                    Log.i("Test", "Connect");
                }
            }

            myBluetooth = BluetoothAdapter.getDefaultAdapter();//get the mobile bluetooth device
            BluetoothDevice dispositivo = myBluetooth.getRemoteDevice(address);//connects to the device's address and checks if it's available
            btSocket = dispositivo.createInsecureRfcommSocketToServiceRecord(myUUID);//create a RFCOMM (SPP) connection
            btSocket.connect();
            Log.e("Test", "\"BT Name: " + name + " \nBT Address: " + address);

            sendCommand();
        } catch (Exception e) {
            Log.e("Error", e.getMessage());
        }
    }

    private void sendCommand() {
        try {
            if (btSocket != null) {
                String cmd = "r";
                btSocket.getOutputStream().write(cmd.toString().getBytes());
                InputStream socketInputStream = btSocket.getInputStream();
                byte[] buffer = new byte[256];
                int bytes;
                bytes = socketInputStream.read(buffer);            //read bytes from input buffer
                String readMessage = new String(buffer, 0, bytes);
//                Toast.makeText(this, "BPM: " + readMessage, Toast.LENGTH_SHORT).show();
                Log.i("Test", "BPM: " + readMessage);
            }
        } catch (Exception e) {
            Log.e("Error", e.getMessage());
        }
    }

    public String readBPM() {
        String user_bpm = "null";
        try {
            if (btSocket != null) {
                String cmd = "r";
                btSocket.getOutputStream().write(cmd.toString().getBytes());
                InputStream socketInputStream = btSocket.getInputStream();
                byte[] buffer = new byte[256];
                int bytes;
                bytes = socketInputStream.read(buffer);            //read bytes from input buffer
                String readMessage = new String(buffer, 0, bytes);
                user_bpm = readMessage;
                Log.i("Test", "BPM: " + readMessage);
            }
        } catch (Exception e) {
            Log.e("Error", e.getMessage());
        }
        return user_bpm;
    }

    public String readStressLevel() {
        String stressLevel = "null";
        try {
            if (btSocket != null) {
                String cmd = "s";
                btSocket.getOutputStream().write(cmd.toString().getBytes());
                InputStream socketInputStream = btSocket.getInputStream();
                byte[] buffer = new byte[256];
                int bytes;
                bytes = socketInputStream.read(buffer);            //read bytes from input buffer
                String readMessage = new String(buffer, 0, bytes);
                stressLevel = readMessage;
                Log.i("Test", "Stress Level: " + readMessage);
            }
        } catch (Exception e) {
            Log.e("Error", e.getMessage());
        }
        return stressLevel;
    }
}
