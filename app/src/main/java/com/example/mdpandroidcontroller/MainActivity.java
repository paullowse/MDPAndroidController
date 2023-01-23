package com.example.mdpandroidcontroller;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.view.View;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.PackageManagerCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.mdpandroidcontroller.databinding.ActivityMainBinding;
import com.example.mdpandroidcontroller.MapDrawer;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration appBarConfiguration;
    private ActivityMainBinding binding;
    private static MapDrawer map;
    private static Context context;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        //grid
        //new im just trying...
        map = new MapDrawer(this);
        map = findViewById(R.id.gridView);
        // end of new stuff...


        //bluetooth
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        appBarConfiguration = new AppBarConfiguration.Builder(navController.getGraph()).build();
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);



        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkPermission(BL_PERMISSIONS, request_code);
            }

            public void checkPermission(String[] BL_PERMISSIONS, int requestCode){
                if(ContextCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_DENIED){
                    ActivityCompat.requestPermissions(MainActivity.this, BL_PERMISSIONS, requestCode);
                }
                else{
                    Toast.makeText(MainActivity.this, "Permission already granted", Toast.LENGTH_SHORT).show();
                }
            }
        });



        Button testbuttonthing = (Button) findViewById(R.id.button3);
        testbuttonthing.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                map.setEndCoordinate(15,10);
            }
        });



    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults){
        MainActivity.super.onRequestPermissionsResult(requestCode, permissions, grantResults);

       if(requestCode == request_code){
           if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
               Toast.makeText(this, "wow", Toast.LENGTH_SHORT).show();
           }
           else{
               Toast.makeText(this, "sleep", Toast.LENGTH_SHORT).show();
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
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, appBarConfiguration)
                || super.onSupportNavigateUp();
    }







}