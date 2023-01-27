package com.example.mdpandroidcontroller;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
    float x, y;
    float dx, dy;

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

    @SuppressLint("ClickableViewAccessibility")
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

        ImageView myImage = (ImageView) view.findViewById(R.id.obstacle);
        myImage.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    ClipData data = ClipData.newPlainText("", "");
                    View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(view);
                    view.startDrag(data, shadowBuilder, view, 0);
                    //view.setVisibility(View.INVISIBLE);
                    return true;
                } else {
                    return false;
                }
            }
        });

        // Droppable chess board
        //CustomView chessBoard = (CustomView) view.findViewById(R.id.gridView);
        map.setOnDragListener(new View.OnDragListener() {
            @Override
            public boolean onDrag(View view, DragEvent dragEvent) {
                int action = dragEvent.getAction();
                switch (action) {
                    case DragEvent.ACTION_DRAG_STARTED:
                        // Do nothing
                        break;
                    case DragEvent.ACTION_DRAG_ENTERED:
                        // Highlight the cell on the chess board where the piece is being dragged over
                        break;
                    case DragEvent.ACTION_DRAG_EXITED:
                        // Remove the highlight from the cell
                        break;
                    case DragEvent.ACTION_DROP:
                        ImageView myImage = (ImageView) dragEvent.getLocalState();
                        //Mapdrawer targetBoard = (Mapdrawer) view;
                        int x = (int) dragEvent.getX();
                        int y = (int) dragEvent.getY();

                        // use x and y to determine the target cell on the chess board
                        // check the move is valid or not and update the chess board
                        map.updateObstacleOnBoard(x, y, myImage);
                        break;
                    case DragEvent.ACTION_DRAG_ENDED:
                        // Remove the highlight from the cell
                        break;
                    default:
                        break;
                }
                return true;
            }
        });




    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}