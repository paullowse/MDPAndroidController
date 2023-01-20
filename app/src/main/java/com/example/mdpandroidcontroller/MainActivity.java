package com.example.mdpandroidcontroller;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.View;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.PackageManagerCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.mdpandroidcontroller.databinding.ActivityMainBinding;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration appBarConfiguration;
    private ActivityMainBinding binding;

    private static final String[] BL_PERMISSIONS = new String[]{
            Manifest.permission.BLUETOOTH,
            Manifest.permission.BLUETOOTH_ADMIN,
            Manifest.permission.BLUETOOTH_ADVERTISE,
            Manifest.permission.BLUETOOTH_CONNECT,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.BLUETOOTH_SCAN
    };
    private static final int BT1_PERMISSION_CODE = 101;
    private static final int BT2_PERMISSION_CODE = 102;
    private static final int BT3_PERMISSION_CODE = 103;
    private static final int BT4_PERMISSION_CODE = 104;
    private static final int BT5_PERMISSION_CODE = 105;
    private static final int BT6_PERMISSION_CODE = 106;
    private static final int BT7_PERMISSION_CODE = 107;
    private static final int request_code = 200;

    private static final int REQUEST_ENABLE_BT = 0;
    private static final int REQUEST_DISCOVER_BT = 1;

    TextView mStatusBlueTv;
    TextView mPairedTv;

    Button mOnBtn;
    Button mOffBtn;
    Button mDiscoverBtn;
    Button mPairedBtn;

    ListView listview;

    BluetoothAdapter mBlueAdapter = BluetoothAdapter.getDefaultAdapter();
    ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Log.e("Activity result","OK");
                    // There are no request codes
                    Intent data = result.getData();
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        appBarConfiguration = new AppBarConfiguration.Builder(navController.getGraph()).build();
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);


        mStatusBlueTv = findViewById(R.id.statusBluetoothTv);
        mPairedTv = findViewById(R.id.pairedTv);

        mOnBtn = findViewById(R.id.onBtn);
        mOffBtn = findViewById(R.id.offBtn);
        mDiscoverBtn = findViewById(R.id.discoverableBtn);
        mPairedBtn = findViewById(R.id.pairedBtn);
        listview = findViewById(R.id.listview);

        // Adapter


        //Check if bluetooth is available or not
        if (mBlueAdapter == null) {
            mStatusBlueTv.setText("Bluetooth is NOT Available");
        } else {
            mStatusBlueTv.setText("Bluetooth is Available");

            mOnBtn.setOnClickListener(this::onClick);        // Turn on Bluetooth btn click
            mDiscoverBtn.setOnClickListener(this::onClick);    // Discover bluetooth btn click
            mOffBtn.setOnClickListener(this::onClick);         // Turn off Bluetooth btn click
            mPairedBtn.setOnClickListener(this::onClick);      // Get Paired devices button click
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
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, appBarConfiguration)
                || super.onSupportNavigateUp();
    }

    //@Override
    public void onClick(View view) {
        switch (view.getId()) {

            // Turn on Bluetooth btn click
            case R.id.onBtn:

                if (!mBlueAdapter.isEnabled()) {
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.BLUETOOTH_ADMIN}, 1);
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                        Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                        activityResultLauncher.launch(intent);
                        //return;
                    }
                    mStatusBlueTv.setText("Bluetooth is on");

                    // Intent to On Bluetooth
                    //Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    //  startActivityForResult(intent, REQUEST_ENABLE_BT);

                    //activityResultLauncher.launch(intent);
                }
                else {
                    showToast("Bluetooth is already on");
                }

                break;

            // Discover bluetooth btn click
            case R.id.discoverableBtn:

                if (!mBlueAdapter.isDiscovering()){
                    showToast("Making Your Device Discoverable");
                    Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
                    //startActivityForResult(intent ,REQUEST_DISCOVER_BT);
                    activityResultLauncher.launch(intent);
                }

                break;

            // Turn off Bluetooth btn click
            case R.id.offBtn:

                if (mBlueAdapter.isEnabled()) {
                    mBlueAdapter.disable();
                    showToast("Turning Bluetooth Off");
                    mStatusBlueTv.setText("Bluetooth is off");
                    //mBlueIv.setImageResource(R.drawable.ic_action_off);
                }
                else {
                    showToast("Bluetooth is already off");
                }

                break;
            // Get Paired devices button click
            case R.id.pairedBtn:

                if (mBlueAdapter.isEnabled()) {
                    mPairedTv.setText("Paired Devices");
                    //Set<BluetoothDevice> devices = mBlueAdapter.getBondedDevices();
                    //for (BluetoothDevice device: devices){
                    //    mPairedTv.append("\nDevice: " + device.getName() + ", " + device);
                    //}
                    Set<BluetoothDevice> pairedDevices = mBlueAdapter.getBondedDevices();
                    ArrayList<String> devices = new ArrayList<>();
                    for (BluetoothDevice bt : pairedDevices) {
                        devices.add(bt.getName() + "\n" + bt.getAddress());
                    }
                    ArrayAdapter arrayAdapter = new ArrayAdapter(MainActivity.this, android.R.layout.simple_list_item_1, devices);
                    listview.setAdapter(arrayAdapter);
                }
                else {
                    //bluetooth is off so can't get paired devices
                    showToast("Turn ON Bluetooth to get paired devices");
                }

                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 1){
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED){
                showToast("Permission Granted");
            }
            else {
                showToast("Permission Denied");
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        switch (requestCode){
            case REQUEST_ENABLE_BT:

                if (resultCode == RESULT_OK) {
                    // Bluetooth is on
                    //mBlueIv.setImageResource(R.drawable.ic_action_on);
                    showToast("Bluetooth is on");
                }
                else {
                    showToast("Failed to connect to bluetooth");
                }

                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    //Toast message function
    private void showToast (String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
    }

    public void listpaireddevices(){
        ListView listview_paireddevices = findViewById(R.id.listview_paireddevices);
        if (mBlueAdapter.isEnabled()) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.BLUETOOTH_ADMIN}, 1);
                //Set<BluetoothDevice> devices = mBlueAdapter.getBondedDevices();
                //for (BluetoothDevice device: devices){
                //    mPairedTv.append("\nDevice: " + device.getName() + ", " + device);
                //}
                Set<BluetoothDevice> pairedDevices = mBlueAdapter.getBondedDevices();
                ArrayList<String> devices = new ArrayList<>();
                for (BluetoothDevice bt : pairedDevices) {
                    devices.add(bt.getName() + "\n" + bt.getAddress());
                }
                ArrayAdapter arrayAdapter = new ArrayAdapter(MainActivity.this, android.R.layout.simple_list_item_1, devices);
                listview_paireddevices.setAdapter(arrayAdapter);
            }
        }
    }

}