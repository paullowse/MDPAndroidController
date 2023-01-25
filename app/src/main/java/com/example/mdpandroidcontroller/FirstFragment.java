package com.example.mdpandroidcontroller;

import android.content.Context;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.fragment.NavHostFragment;

import com.example.mdpandroidcontroller.databinding.FragmentFirstBinding;

public class FirstFragment extends Fragment {

    private FragmentFirstBinding binding;
    String randomInfo;

    TextView displaytv;

    // grid stuff
    private static MapDrawer map;
    private static Context context;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        //binding = FragmentFirstBinding.inflate(inflater, container, false);
        //return binding.getRoot();

        View view = inflater.inflate(R.layout.fragment_first, container, false);
        return view;

    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //grid
        //new im just trying...
        //map = new MapDrawer(context);
        map = view.findViewById(R.id.gridView);
        // end of new stuff...


        view.findViewById(R.id.button_first).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavHostFragment.findNavController(FirstFragment.this)
                        .navigate(R.id.action_FirstFragment_to_SecondFragment);
            }
        });

        view.findViewById(R.id.onBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity)getActivity()).turnonbluetooth();
            }
        });

        view.findViewById(R.id.offBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity)getActivity()).turnoffbluetooth();
            }
        });

        view.findViewById(R.id.discoverableBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity)getActivity()).bluetooth_discoverable();
            }
        });

        //testing imagebuttons
        ImageButton backButton = (ImageButton) view.findViewById(R.id.arrowBack);
        backButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                //map.setEndCoordinate(15,10);
                map.setRobotDirection(Constants.DOWN);
                map.moveRobot();
                map.invalidate();

                //showToast("does this work?");
            }
        });

        ImageButton forwardButton = (ImageButton) view.findViewById(R.id.arrowForward);
        forwardButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                map.setRobotDirection(Constants.UP);
                map.moveRobot();
                map.invalidate();

            }
        });

        ImageButton rightButton = (ImageButton) view.findViewById(R.id.arrowRight);
        rightButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                map.setRobotDirection(Constants.RIGHT);
                map.moveRobot();
                map.invalidate();

            }
        });

        ImageButton leftButton = (ImageButton) view.findViewById(R.id.arrowLeft);
        leftButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                map.setRobotDirection(Constants.LEFT);
                map.moveRobot();
                map.invalidate();

            }
        });

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}