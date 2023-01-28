package com.example.mdpandroidcontroller;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.Context;
import android.content.SharedPreferences;
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

import java.util.ArrayList;
import java.util.List;

public class FirstFragment extends Fragment {

    private FragmentFirstBinding binding;
    String randomInfo;

    TextView displaytv;

    // grid stuff
    private static MapDrawer map;
    float pastX, pastY;

    ArrayList<int[]> originalObstacleCoords = new ArrayList<>();

    List<ImageView> obstacleViews = new ArrayList<>();

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        //binding = FragmentFirstBinding.inflate(inflater, container, false);
        //return binding.getRoot();

        View view = inflater.inflate(R.layout.fragment_first, container, false);
        System.out.println(savedInstanceState == null);
        if (savedInstanceState != null) {
            map = (MapDrawer) savedInstanceState.getSerializable("map");
        } else{
            map = view.findViewById(R.id.gridView);
        }

        return view;

    }

    @SuppressLint("ClickableViewAccessibility")
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


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

        ImageView obstacle = (ImageView) view.findViewById(R.id.obstacle);
        obstacleViews.add(obstacle);
        obstacle.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    ClipData data = ClipData.newPlainText("", "");
                    View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(view);
                    view.startDrag(data, shadowBuilder, view, 0);
                    //view.setVisibility(View.INVISIBLE);
                    pastX = obstacle.getX();
                    pastY = obstacle.getY();
                    return true;
                } else {
                    return false;
                }
            }
        });

        //HOW TO DO IT WITHOUT REPEATING CODE???
        ImageView obstacle2 = (ImageView) view.findViewById(R.id.obstacle2);
        originalObstacleCoords.add((int) (obstacle2.getX()),(int) (obstacle2.getY()));

        
        obstacleViews.add(obstacle2);
        obstacle2.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    ClipData data = ClipData.newPlainText("", "");
                    View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(view);
                    view.startDrag(data, shadowBuilder, view, 0);
                    //view.setVisibility(View.INVISIBLE);
                    pastX = obstacle2.getX();
                    pastY = obstacle2.getY();
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
                        // when dropped into the grid
                        ImageView myImage = (ImageView) dragEvent.getLocalState();

                        int x = (int) dragEvent.getX();
                        int y = (int) dragEvent.getY();


                        // this is the exact location - but we want to snap to grid
                        //myImage.setX(x + map.getX() - map.getCellSize()/2);
                        //myImage.setY(y+ map.getY() - map.getCellSize()/2);

                        // create a duplicate
                        //ImageView newObst = new ImageView(getContext());
                        //newObst.setImageDrawable(myImage.getDrawable());

                        // if the past location of obstacle was in the map, u remove the old one.
                        if (pastX >= map.getX() && pastX <= map.getX() + map.getWidth() && pastY >= map.getY() && pastY <= map.getY() + map.getHeight()) {
                            //System.out.println("IN MAP");
                            map.removeObstacleOnBoard(pastX - map.getX() + map.getCellSize()/2,pastY - map.getY() + map.getCellSize()/2);
                        }
                        // to add the new obstacle
                        int[] newObstCoord = map.updateObstacleOnBoard(x, y, myImage);

                        // MUST get from the map class to snap to grid
                        myImage.setX(newObstCoord[0]+ map.getX());
                        myImage.setY(newObstCoord[1]+ map.getY());

                        map.invalidate();

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

        ViewGroup parentView = (ViewGroup) map.getParent();
        parentView.setOnDragListener(new View.OnDragListener() {
            @Override
            public boolean onDrag(View view, DragEvent event) {
                int x = (int) event.getX();
                int y = (int) event.getY();
                int mapWidth = map.getWidth();
                int mapHeight = map.getHeight();
                int[] mapCoord = new int[2];
                map.getLocationOnScreen(mapCoord);

                if (event.getAction() == DragEvent.ACTION_DROP) {
                    if(x < mapCoord[0] || x > mapCoord[0] + mapWidth || y < mapCoord[1] || y > mapCoord[1] + mapHeight){
                        //System.out.println("out of map");
                        ImageView myImage = (ImageView) event.getLocalState();
                        myImage.setVisibility(View.INVISIBLE);
                        map.removeObstacleOnBoard(pastX - map.getX() + map.getCellSize()/2,pastY - map.getY() + map.getCellSize()/2);
                        map.invalidate();
                    } else {
                        // obstacle was dropped inside the map
                    }
                }
                return true;
            }
        });



    }



    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putSerializable("map", map);

        // Save the chess board state to the bundle
        //outState.putStringArray("map", map);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}