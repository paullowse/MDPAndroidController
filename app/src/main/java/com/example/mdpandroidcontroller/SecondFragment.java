package com.example.mdpandroidcontroller;

import static androidx.core.content.ContextCompat.registerReceiver;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.mdpandroidcontroller.databinding.FragmentSecondBinding;
import com.google.android.material.snackbar.Snackbar;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

public class SecondFragment extends Fragment {

    private FragmentSecondBinding binding;

    Button bt_paired;

    private View view;

    ListView listview_paireddevices;
    ListView listview_availabledevices;
    ArrayList<String> availabledevicelist = new ArrayList<>();
    ArrayList<String> paireddevicelist = new ArrayList<>();
    ArrayList<BluetoothDevice> mBTDevices = new ArrayList<>();
    ArrayList<BluetoothDevice> mPairDevices = new ArrayList<>();

    private Context globalContext = null;
    private static final String TAG = "btlog";

    private static final UUID my_uuid_insecure = UUID.fromString("996c1b5f-170b-4f38-a5e0-85eef5acf12c");

    BluetoothDevice mBTDevice;
    BluetoothDevice mPairDevice;
    EditText etsend;
    Button btnSend;

    BluetoothConnectionService mBluetoothConnection;

    BluetoothAdapter mBlueAdapter;
    ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Log.e("Activity result", "OK");
                    // There are no request codes
                    Intent data = result.getData();
                }
            });

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        if (view != null)
            return view;
        //binding = FragmentSecondBinding.inflate(inflater, container, false);
        //return binding.getRoot();

        view = inflater.inflate(R.layout.fragment_second, container, false);
        listview_availabledevices = view.findViewById(R.id.listavailabledevice);
        mBlueAdapter = BluetoothAdapter.getDefaultAdapter();
        btnSend = view.findViewById(R.id.btnsend);
        etsend = (EditText) view.findViewById(R.id.editText123);

        globalContext = this.getActivity();

        //Broadcasts when bond state changes (ie:pairing)
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        getActivity().registerReceiver(mBroadcastReceiver2, filter);

        ArrayAdapter arrayAdapter = new ArrayAdapter(SecondFragment.this.getActivity(), android.R.layout.simple_list_item_1, availabledevicelist);
        listview_availabledevices.setAdapter(arrayAdapter);

        return view;
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        bt_paired = view.findViewById(R.id.bt_paired);
        listview_paireddevices = view.findViewById(R.id.listview_paireddevices);

        view.findViewById(R.id.button_second).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavHostFragment.findNavController(SecondFragment.this).navigate(R.id.action_SecondFragment_to_FirstFragment);
            }
        });

        view.findViewById(R.id.bt_paired).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listpaireddevices();
            }
        });

        view.findViewById(R.id.bt_listavailabledevices).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DiscoverDevices(v);
            }
        });

        view.findViewById(R.id.btnsend).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                byte[] bytes = etsend.getText().toString().getBytes(Charset.defaultCharset());
                //Toast.makeText(globalContext, etsend.getText().toString(), Toast.LENGTH_SHORT).show();

                mBluetoothConnection.write(bytes);
                etsend.setText("");
            }
        });

        AdapterView adapterView2 = (AdapterView) view.findViewById(R.id.listview_paireddevices);
        AdapterView adapterView1 = (AdapterView) view.findViewById(R.id.listavailabledevice);

        //discover devices adapter view
        adapterView1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if (position >= 0) {
                    Snackbar mysnackbar = Snackbar.make(view, "Connecting to " + availabledevicelist.get(position), 999);

                    //first cancel discovery because its very memory intensive.
                    if (ActivityCompat.checkSelfPermission(SecondFragment.this.getActivity(), Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                        mBlueAdapter.cancelDiscovery();
                        Toast.makeText(getContext(), "Bluetooth Discovery Off", Toast.LENGTH_SHORT).show();
                    }

                    Log.d(TAG, "onItemClick: You Clicked on a device.");
                    String deviceName = mBTDevices.get(position).getName();
                    String deviceAddress = mBTDevices.get(position).getAddress();

                    Log.d(TAG, "onItemClick: deviceName = " + deviceName);
                    Log.d(TAG, "onItemClick: deviceAddress = " + deviceAddress);

                    //create the bond.
                    //NOTE: Requires API 17+? I think this is JellyBean
                    if(Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN_MR2){
                        Log.d(TAG, "Trying to pair with " + deviceName);
                        mBTDevices.get(position).createBond();

                        mBTDevice = mBTDevices.get(position);
                        //mBluetoothConnection = new BluetoothConnectionService(SecondFragment.this.getActivity());
                        mBluetoothConnection = new BluetoothConnectionService(getActivity());
                        startConnection();;
                    }

                    mysnackbar.show();
                }
            }
        });

        //pair device adapter view
        /*adapterView2.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if (position >= 0) {
                    Snackbar mysnackbar = Snackbar.make(view, "Connecting back to " + paireddevicelist.get(position), 999);

                    //first cancel discovery because its very memory intensive.
                    if (ActivityCompat.checkSelfPermission(SecondFragment.this.getActivity(), Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                        mBlueAdapter.cancelDiscovery();
                        Toast.makeText(getContext(), "Bluetooth Discovery Off", Toast.LENGTH_SHORT).show();
                    }

                    Log.d(TAG, "onItemClick: You Clicked on a paired device.");
                    String deviceName = mPairDevices.get(position).getName();
                    String deviceAddress = mPairDevices.get(position).getAddress();

                    Log.d(TAG, "onItemClick: deviceName = " + deviceName);
                    Log.d(TAG, "onItemClick: deviceAddress = " + deviceAddress);

                    //create the bond.
                    //NOTE: Requires API 17+? I think this is JellyBean
                    if(Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN_MR2){
                        Log.d(TAG, "Pairing: " + deviceName);
                        mPairDevices.get(position).createBond();

                        mPairDevice = mPairDevices.get(position);
                        //mBluetoothConnection = new BluetoothConnectionService(SecondFragment.this.getActivity());
                        mBluetoothConnection = new BluetoothConnectionService(getActivity());
                        startConnection();
                    }

                    mysnackbar.show();
                }
            }
        });*/

        adapterView2.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if (position >= 0) {
                    Snackbar mysnackbar = Snackbar.make(view, "Select discoverable device to connect.", 999);

                    mysnackbar.show();
                }
            }
        });
    }

    //second fragment - list paired devices
    public void listpaireddevices() {
        if (mBlueAdapter.isEnabled()) {
            if (ActivityCompat.checkSelfPermission(SecondFragment.this.getActivity(), Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                ActivityCompat.requestPermissions(SecondFragment.this.getActivity(), new String[]{Manifest.permission.BLUETOOTH_ADMIN}, 1);
                //Set<BluetoothDevice> devices = mBlueAdapter.getBondedDevices();
                //for (BluetoothDevice device: devices){
                //    mPairedTv.append("\nDevice: " + device.getName() + ", " + device);
                //}
                Set<BluetoothDevice> pairedDevices = mBlueAdapter.getBondedDevices();
                for (BluetoothDevice bt : pairedDevices) {
                    paireddevicelist.add(bt.getName() + "\n" + bt.getAddress());
                }
                //BluetoothDevice deviceInfo = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                //mPairDevices.add(deviceInfo);
                ArrayAdapter arrayAdapter = new ArrayAdapter(SecondFragment.this.getActivity(), android.R.layout.simple_list_item_1, paireddevicelist);
                listview_paireddevices.setAdapter(arrayAdapter);
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        if (availabledevicelist != null && availabledevicelist.size() > 0){
            ArrayAdapter arrayAdapter = new ArrayAdapter(SecondFragment.this.getActivity(), android.R.layout.simple_list_item_1, availabledevicelist);
            listview_availabledevices.setAdapter(arrayAdapter);
        }
        if (paireddevicelist != null && paireddevicelist.size() > 0){
            ArrayAdapter arrayAdapter = new ArrayAdapter(SecondFragment.this.getActivity(), android.R.layout.simple_list_item_1, paireddevicelist);
            listview_paireddevices.setAdapter(arrayAdapter);
        }
    }

    public void DiscoverDevices(View v){
        if (ActivityCompat.checkSelfPermission(SecondFragment.this.getActivity(), Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            mBlueAdapter.startDiscovery();
        }
    }

    //second fragment - show available bluetooth devices
    BroadcastReceiver mReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {

            //if you have found the devices
            if (BluetoothDevice.ACTION_FOUND.equals(intent.getAction())) {

                // Now that you have found the device. Get the Bluetooth Device
                // object and its info from the Intent.
                //listview_availabledevices = SecondFragment.this.getActivity().findViewById(R.id.listavailabledevice);

                if (ActivityCompat.checkSelfPermission(globalContext, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {

                    ActivityCompat.requestPermissions((Activity) SecondFragment.this.globalContext, new String[]{Manifest.permission.BLUETOOTH_ADMIN}, 1);
                    //ActivityCompat.requestPermissions(SecondFragment.this.getActivity(), new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
                    //ActivityCompat.requestPermissions((Activity) SecondFragment.this.getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    Toast.makeText(context, "Inside toast message", Toast.LENGTH_SHORT).show();
                }

                BluetoothDevice deviceInfo = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                availabledevicelist.add(deviceInfo.getName() + "\n" + deviceInfo.getAddress());
                mBTDevices.add(deviceInfo);
                mPairDevices.add(deviceInfo);
                //BluetoothDevice[] devices = availdevices.toArray(new BluetoothDevice[availdevices.size()]);
                //ArrayAdapter arrayAdapter = new ArrayAdapter(SecondFragment.this.getActivity(), android.R.layout.simple_list_item_1, availabledevicelist);
                //listview_availabledevices.setAdapter(arrayAdapter);



                Toast.makeText(context, "Outside toast", Toast.LENGTH_SHORT).show();

            }
        }
    };

    //second fragment - pair bluetooth devices
    BroadcastReceiver mBroadcastReceiver2 = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();

            //if you have found the devices
            if (BluetoothDevice.ACTION_FOUND.equals(intent.getAction())) {

                // Now that you have found the device. Get the Bluetooth Device
                // object and its info from the Intent.
                //listview_availabledevices = SecondFragment.this.getActivity().findViewById(R.id.listavailabledevice);

                if (ActivityCompat.checkSelfPermission(globalContext, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {

                    ActivityCompat.requestPermissions((Activity) SecondFragment.this.globalContext, new String[]{Manifest.permission.BLUETOOTH_ADMIN}, 1);
                    //ActivityCompat.requestPermissions(SecondFragment.this.getActivity(), new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
                    //ActivityCompat.requestPermissions((Activity) SecondFragment.this.getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    Toast.makeText(context, "Inside toast message", Toast.LENGTH_SHORT).show();
                }

                if(action.equals(BluetoothDevice.ACTION_BOND_STATE_CHANGED)){
                    BluetoothDevice mDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    //3 cases:
                    //case1: bonded already
                    if (mDevice.getBondState() == BluetoothDevice.BOND_BONDED){
                        Log.d(TAG, "BroadcastReceiver: BOND_BONDED.");

                        mBTDevice = mDevice;
                        mPairDevice = mDevice;
                    }
                    //case2: creating a bond
                    if (mDevice.getBondState() == BluetoothDevice.BOND_BONDING) {
                        Log.d(TAG, "BroadcastReceiver: BOND_BONDING.");
                    }
                    //case3: breaking a bond
                    if (mDevice.getBondState() == BluetoothDevice.BOND_NONE) {
                        Log.d(TAG, "BroadcastReceiver: BOND_NONE.");
                    }
                }

            }
        }
    };

    //method to start connection
    //***remember the conncction will fail and app will crash if you haven't paired first
    public void startConnection(){
        startBTConnection(mBTDevice, my_uuid_insecure);
    }

    /**
     * starting chat service method
     */
    public void startBTConnection(BluetoothDevice device, UUID uuid){
        Log.d(TAG, "startBTConnection: Initializing RFCOM Bluetooth Connection.");

        mBluetoothConnection.startClient(device,uuid);
    }

    public void onStart(){
        super.onStart();
        IntentFilter bluetoothFilter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        getActivity().registerReceiver(mReceiver, bluetoothFilter);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}