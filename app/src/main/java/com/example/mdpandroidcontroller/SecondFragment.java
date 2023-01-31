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
import android.nfc.Tag;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.ListAdapter;

import com.example.mdpandroidcontroller.databinding.FragmentSecondBinding;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SecondFragment extends Fragment {

    private FragmentSecondBinding binding;

    Button bt_paired;

    TextView mStatusBlueTv;

    ListView listview_paireddevices;
    ListView listview_availabledevices;
    ArrayList<String> availabledevicelist = new ArrayList<>();
    HashSet<BluetoothDevice> availdevices = new HashSet<>();

    private Context globalContext = null;
    private static final String TAG = "btlog";

    BluetoothAdapter mBlueAdapter;
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
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        //binding = FragmentSecondBinding.inflate(inflater, container, false);
        //return binding.getRoot();

        View view = inflater.inflate(R.layout.fragment_second, container, false);
        listview_availabledevices = view.findViewById(R.id.listavailabledevice);
        mBlueAdapter = BluetoothAdapter.getDefaultAdapter();

        globalContext = this.getActivity();

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
                ((MainActivity)getActivity()).listpaireddevices();
            }
        });

        view.findViewById(R.id.bt_listavailabledevices).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DiscoverDevices(v);
            }
        });

        AdapterView adapterView = (AdapterView) view.findViewById(R.id.listavailabledevice);
        adapterView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if(position >= 0){
                    Snackbar mysnackbar = Snackbar.make(view, "Connecting to " + availabledevicelist.get(position), 999);

                    mysnackbar.show();
                }
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();

        if (availabledevicelist != null && availabledevicelist.size() > 0){
            ArrayAdapter arrayAdapter = new ArrayAdapter(SecondFragment.this.getActivity(), android.R.layout.simple_list_item_1, availabledevicelist);
            listview_availabledevices.setAdapter(arrayAdapter);
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
                    }
                    //case2: creating a bone
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

    public void onStart(){
        super.onStart();
        IntentFilter bluetoothFilter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        getActivity().registerReceiver(mReceiver, bluetoothFilter);

        //Broadcasts when bond state changes (ie:pairing)
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        //getActivity().registerReceiver(mBroadcastReceiver2, filter);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}