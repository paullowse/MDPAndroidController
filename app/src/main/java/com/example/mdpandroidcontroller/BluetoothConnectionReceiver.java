package com.example.mdpandroidcontroller;

import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;

public class BluetoothConnectionReceiver extends BroadcastReceiver {

    BluetoothDevice myBTConnectionDevice;

    @Override
    public void onReceive(Context context, Intent intent) {
        Toast.makeText(context, "Inside receiver", Toast.LENGTH_SHORT).show();

        String connectionStatus = intent.getStringExtra("ConnectionStatus");
        myBTConnectionDevice = intent.getParcelableExtra("Device");


        //DISCONNECTED FROM BLUETOOTH CHAT
        if (connectionStatus.equals("disconnect")) {

            Log.d("ConnectAcitvity:", "Device Disconnected");

            //RECONNECT DIALOG MSG
            AlertDialog alertDialog = new AlertDialog.Builder(context).create();
            alertDialog.setTitle("BLUETOOTH DISCONNECTED");
            if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
            }
            alertDialog.setMessage("Connection with device: '" + myBTConnectionDevice.getName() + "' has ended. Do you want to reconnect?");

            alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "No",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            Toast.makeText(context, "Fire up broadcastreceiver", Toast.LENGTH_SHORT).show();
                            //startBTConnection(myBTConnectionDevice, Connect.myUUID);
                            dialog.dismiss();
                        }
                    });
            alertDialog.show();
        }

        //SUCCESSFULLY CONNECTED TO BLUETOOTH DEVICE
        else if (connectionStatus.equals("connect")) {


            Log.d("ConnectAcitvity:", "Device Connected");
            Toast.makeText(context, "Connection Established: " + myBTConnectionDevice.getName(),
                    Toast.LENGTH_LONG).show();
        }

        //BLUETOOTH CONNECTION FAILED
        else if (connectionStatus.equals("connectionFail")) {
            Toast.makeText(context, "Connection Failed: " + myBTConnectionDevice.getName(),
                    Toast.LENGTH_LONG).show();
        }

    }
}