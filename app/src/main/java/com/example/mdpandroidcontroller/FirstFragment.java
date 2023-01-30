package com.example.mdpandroidcontroller;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.DragEvent;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
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
    private static int mapLeft; // only at robot generate button
    private static int mapTop;
    private static int rotation = 0;

    private static ImageView robot;
    float pastX, pastY;


    private static int[][] originalObstacleCoords = new int[6][2];


    private List<ImageView> obstacleViews = new ArrayList<>(); // cant be static!! - COS ITS REGENRATED ALL THE TIME



    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        //binding = FragmentFirstBinding.inflate(inflater, container, false);
        //return binding.getRoot();
        System.out.println("createView");

        View view = inflater.inflate(R.layout.fragment_first, container, false);
        //System.out.println(savedInstanceState == null);
        if (savedInstanceState != null) {
            map = (MapDrawer) savedInstanceState.getSerializable("map");
        } else{
            map = view.findViewById(R.id.gridView);
        }

        for (int i = 0; i < originalObstacleCoords.length; i++) {
            for (int j = 0; j < originalObstacleCoords[i].length; j++) {
                originalObstacleCoords[i][j] = -1;
            }
        }
        //CHECK if this is okay
        //rotation = 0;

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

        //ROBOT settings
        // KEEP IT INVISIBLE AT FIRST
        robot = (ImageView) view.findViewById(R.id.robotcar);
        if (map.getCanDrawRobot()) {
            robot.setVisibility(View.VISIBLE);
            rotation = map.convertFacingToRotation(map.getRobotFacing());
            //System.out.println("ROBOT ROTATION");

            trackRobot(robot, rotation);

        } else {
            robot.setVisibility(View.INVISIBLE);
        }



        Button robotButton = (Button) view.findViewById(R.id.button2);
        view.findViewById(R.id.button2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean pastDrawRobot = map.getCanDrawRobot();

                mapLeft = map.getLeft();
                mapTop = map.getTop();

                if (pastDrawRobot) {
                    // NEED TO CLEAR THE MAP ALSO -- ERROR FIX LATER
                    map.setCanDrawRobot(false);
                    robot.setVisibility(View.INVISIBLE);
                } else {
                    map.saveFacingWithRotation(rotation); // error: between this button and onresume, map's facing reset to 0
                    map.setCanDrawRobot(true);
                    robot.setVisibility(View.VISIBLE);
                    //rotation = map.convertFacingToRotation(map.getRobotFacing()); - if issue check this
                    trackRobot(robot, rotation);

                }
                map.invalidate();
            }
        });





        //MOVEMENT BUTTONS
        ImageButton forwardButton = (ImageButton) view.findViewById(R.id.arrowForward);
        forwardButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                masterRobotMovement(Constants.UP);
            }
        });

        ImageButton rightButton = (ImageButton) view.findViewById(R.id.arrowRight);
        rightButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                masterRobotMovement(Constants.RIGHT);
            }
        });

        ImageButton backButton = (ImageButton) view.findViewById(R.id.arrowBack);
        backButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                masterRobotMovement(Constants.DOWN);
            }
        });

        ImageButton leftButton = (ImageButton) view.findViewById(R.id.arrowLeft);
        leftButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                masterRobotMovement(Constants.LEFT);
            }
        });


        //OBSTACLES
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


                    // save the original obstacle coord (for snapping back if out of grid)
                    if (originalObstacleCoords[0][0] == -1) {
                        originalObstacleCoords[0][0] = obstacle.getLeft();
                        originalObstacleCoords[0][1] = obstacle.getTop();
                    }
                    //System.out.println(pastX);
                    //System.out.println(pastY);

                    return true;
                } else {
                    return false;
                }
            }
        });

        //HOW TO DO IT WITHOUT REPEATING CODE???
        ImageView obstacle2 = (ImageView) view.findViewById(R.id.obstacle2);
        //int[] tempcoord2 = {(int) (obstacle2.getX()),(int) (obstacle2.getY())};
        //originalObstacleCoords.add(tempcoord2);
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

                    // save the original obstacle coord (for snapping back if out of grid)
                    if (originalObstacleCoords[1][0] == -1) {
                        originalObstacleCoords[1][0] = obstacle2.getLeft();;
                        originalObstacleCoords[1][1] = obstacle2.getTop();
                    }
                    //System.out.println(pastX);
                    //System.out.println(pastY);

                    return true;
                } else {
                    return false;
                }
            }
        });

        Button test = (Button) view.findViewById(R.id.button_test);
        test.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View popUpView = inflater.inflate(R.layout.popup_window, null);
                int width = LinearLayout.LayoutParams.WRAP_CONTENT;
                int height = LinearLayout.LayoutParams.WRAP_CONTENT;
                PopupWindow popUpWindow = new PopupWindow(popUpView, width, height, true);
                popUpWindow.showAtLocation(v, Gravity.CENTER, 0, 0);
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
                        //myImage.setVisibility(View.INVISIBLE);
                        // loop through obstacleviews to find the obstacle name
                        // set according to obstacle coord
                        for (int i = 0; i < obstacleViews.size(); i++) {
                            if (myImage == obstacleViews.get(i)) {
                                myImage.setX(originalObstacleCoords[i][0]);
                                myImage.setY(originalObstacleCoords[i][1]);
                            }
                        }


                        //outside map to outside map

                        if(pastX >= mapCoord[0] && pastX <= mapCoord[0] + mapWidth && pastY >= mapCoord[1] && pastY <= mapCoord[1] + mapHeight){
                            // in of map to out of map!!
                            map.removeObstacleOnBoard(pastX - map.getX() + map.getCellSize()/2,pastY - map.getY() + map.getCellSize()/2);
                            map.invalidate();
                        }

                    } else {
                        // obstacle was dropped inside the map
                    }
                }
                return true;
            }
        });



    }

    public void onPause() {
        super.onPause();


        System.out.println("pause- save");
        // save rotation value

        // Save the ImageView locations
        SharedPreferences preferences = getActivity().getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        for (int i = 0; i < obstacleViews.size(); i++) {
            ImageView imageView = obstacleViews.get(i);

            // x and y = current position
            int x = (int) imageView.getX();
            int y = (int) imageView.getY();

            // left and top = og position
            int left = (int) imageView.getLeft();
            int top = (int) imageView.getTop();

            editor.putInt("image_view_" + i + "_x", x);
            editor.putInt("image_view_" + i + "_y", y);
            editor.putInt("image_view_" + i + "_left", left);
            editor.putInt("image_view_" + i + "_top", top);

            //System.out.println(i);
            //System.out.println(x);
            //System.out.println(y);
        }
        editor.apply();
    }

    /** Sometimes map.getleft etc doesnt work here?
     *
     */
    public void onResume() {
        super.onResume();
        System.out.println("resume");
        // Retrieve the ImageView locations
        SharedPreferences preferences = getActivity().getPreferences(Context.MODE_PRIVATE);
        for (int i = 0; i < obstacleViews.size(); i++) {
            ImageView imageView = obstacleViews.get(i);
            int x = preferences.getInt("image_view_" + i + "_x", 0);
            int y = preferences.getInt("image_view_" + i + "_y", 0);
            int left = preferences.getInt("image_view_" + i + "_left", 0);
            int top = preferences.getInt("image_view_" + i + "_top", 0);

            // setX and setY == 0 is the bASE POSITION - use this to reset
            //imageView.setX(0);
            //imageView.setY(0);

            //System.out.println(i);
            //System.out.println(x);
            //System.out.println(y);

            if (i == 0) {
                imageView.setX(x - left);
                imageView.setY(y - top);  //top == 1022
            }
            if (i == 1) {
                imageView.setX(x - left);
                imageView.setY(y - top); // top = -912
            }

            // To save the robot image location
            boolean pastDrawRobot = map.getCanDrawRobot();
            if (pastDrawRobot) {
                // NEED TO CLEAR THE MAP ALSO -- ERROR FIX LATER
                map.setCanDrawRobot(false);
                robot.setVisibility(View.INVISIBLE);
            } else {
                map.setCanDrawRobot(true);
                robot.setVisibility(View.VISIBLE);

                //CHANGE THIS EVENTUALLY ALSO
                int[] robotImageCoord = map.getCurCoord();
                int[] robotLocation = map.setRobotImagePosition(robotImageCoord[0],map.convertRow(robotImageCoord[1]), mapLeft,mapTop); // ONLY WORKS IF GENERATE WAS DONE BEFORE
                //System.out.println(robotLocation[0]);
                //System.out.println(robotLocation[1]);
                robot.setX(robotLocation[0]);
                robot.setY(robotLocation[1]);
            }

            System.out.println("end resume");


        }
    }

    /**
     * Summarize the move buttons actions.
     * @param direction
     */
    public void masterRobotMovement(String direction) {
        map.saveFacingWithRotation(rotation); // error: between this button and onresume, map's facing reset to 0
        map.setRobotMovement(direction);
        map.moveRobot();
        map.invalidate();
        rotation = map.convertFacingToRotation(map.getRobotFacing());
        trackRobot(robot, rotation);
    }

    /**
     * Purpose is to track the image of the robot to the current coord of the robot in map class. and follows the right rotation
     * The robot will be paired accordingly
     * @param robot
     * @param rotation
     */
    public void trackRobot(ImageView robot, int rotation) {
        //ImageView robot = (ImageView) view.findViewById(R.id.robotcar);
        //System.out.println("TRACK ROBOT FUNCTION");

        int[] robotImageCoord = map.getCurCoord();
        int[] robotLocation = map.setRobotImagePosition(robotImageCoord[0],map.convertRow(robotImageCoord[1]), map.getLeft(),map.getTop());
        robot.setX(robotLocation[0]);
        robot.setY(robotLocation[1]);
        robot.setRotation(rotation);
    }
    
    



    //WAS USED FOR SERIALIZABLE
    //public void onSaveInstanceState(Bundle outState) {
    //    super.onSaveInstanceState(outState);

    //    outState.putSerializable("map", map);

        // Save the chess board state to the bundle
        //outState.putStringArray("map", map);
    //}

    @Override
    public void onDestroyView() {

        //dont need? doesnt work
        for (int i = 0; i < obstacleViews.size(); i++) {
            obstacleViews.get(i).setX(originalObstacleCoords[i][0]);
            obstacleViews.get(i).setY(originalObstacleCoords[i][1]);
        }

        super.onDestroyView();
        binding = null;
    }

}