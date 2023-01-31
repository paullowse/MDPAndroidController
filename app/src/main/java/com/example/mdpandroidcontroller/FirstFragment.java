package com.example.mdpandroidcontroller;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
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
import androidx.constraintlayout.widget.ConstraintLayout;
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

    private static ConstraintLayout obstacle1;
    private static ConstraintLayout obstacle2;

    private static ImageView robot;
    float pastX, pastY;

    private static String longPress = "null";

    private Runnable runnable;
    private Handler handler;



    private static int[][] originalObstacleCoords = new int[6][2];

    private static int[][] currentObstacleCoords = new int[2][2];


    private List<ConstraintLayout> obstacleViews = new ArrayList<>(); // cant be static!! - COS ITS REGENRATED ALL THE TIME



    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        //binding = FragmentFirstBinding.inflate(inflater, container, false);
        //return binding.getRoot();
        System.out.println("oncreateView");

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
        //printAllObstacleCoords();

        //obstacle1 = (ConstraintLayout) view.findViewById(R.id.obstacleGroup1);
        //obstacle2 = (ConstraintLayout) view.findViewById(R.id.obstacleGroup2);

        //System.out.println(obstacle1.getX());
        //System.out.println(obstacle1.getY());

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



        Button robotButton = (Button) view.findViewById(R.id.generateRobot);
        view.findViewById(R.id.generateRobot).setOnClickListener(new View.OnClickListener() {
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



        // NEW Short press and Long Press for ALL BUTTONS
        ImageButton forwardButton = (ImageButton) view.findViewById(R.id.arrowForward);
        ImageButton rightButton = (ImageButton) view.findViewById(R.id.arrowRight);
        ImageButton leftButton = (ImageButton) view.findViewById(R.id.arrowLeft);
        ImageButton backButton = (ImageButton) view.findViewById(R.id.arrowBack);

        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (view.getId()) {
                    case R.id.arrowForward:
                        masterRobotMovement(Constants.UP);
                        break;
                    case R.id.arrowRight:
                        masterRobotMovement(Constants.RIGHT);
                        break;
                    case R.id.arrowLeft:
                        masterRobotMovement(Constants.LEFT);
                        break;
                    case R.id.arrowBack:
                        masterRobotMovement(Constants.DOWN);
                        break;
                }
            }
        };

        View.OnLongClickListener onLongClickListener = new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                handler.removeCallbacks(runnable);
                handler.post(runnable);
                //String longPress = "null";
                switch (view.getId()) {
                    case R.id.arrowForward:
                        longPress = Constants.UP;
                        break;
                    case R.id.arrowRight:
                        longPress = Constants.RIGHT;
                        break;
                    case R.id.arrowLeft:
                        longPress = Constants.LEFT;
                        break;
                    case R.id.arrowBack:
                        longPress = Constants.DOWN;
                        break;
                }
                return true;
            }
        };

        View.OnTouchListener onTouchListener = new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    handler.removeCallbacks(runnable);
                }
                return false;
            }
        };

        forwardButton.setOnClickListener(onClickListener);
        forwardButton.setOnLongClickListener(onLongClickListener);
        forwardButton.setOnTouchListener(onTouchListener);

        rightButton.setOnClickListener(onClickListener);
        rightButton.setOnLongClickListener(onLongClickListener);
        rightButton.setOnTouchListener(onTouchListener);

        leftButton.setOnClickListener(onClickListener);
        leftButton.setOnLongClickListener(onLongClickListener);
        leftButton.setOnTouchListener(onTouchListener);

        backButton.setOnClickListener(onClickListener);
        backButton.setOnLongClickListener(onLongClickListener);
        backButton.setOnTouchListener(onTouchListener);

        handler = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {
                switch (longPress) {
                    case Constants.UP:
                        masterRobotMovement(Constants.UP);
                        break;
                    case Constants.RIGHT:
                        masterRobotMovement(Constants.RIGHT);
                        break;
                    case Constants.DOWN:
                        masterRobotMovement(Constants.DOWN);
                        break;
                    case Constants.LEFT:
                        masterRobotMovement(Constants.LEFT);
                        break;
                    default:
                        System.out.println("somehow its still null for buttonpress");
                }
                handler.postDelayed(runnable, 100);
            }
        };


        // OLD





        //OBSTACLES
        obstacle1 = (ConstraintLayout) view.findViewById(R.id.obstacleGroup1);
        obstacleViews.add(obstacle1);

        obstacle1.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    ClipData data = ClipData.newPlainText("", "");
                    View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(view);
                    view.startDrag(data, shadowBuilder, view, 0);
                    //view.setVisibility(View.INVISIBLE);
                    pastX = obstacle1.getX();
                    pastY = obstacle1.getY();


                    // save the original obstacle coord (for snapping back if out of grid)
                    if (originalObstacleCoords[0][0] == -1) {
                        originalObstacleCoords[0][0] = obstacle1.getLeft();
                        originalObstacleCoords[0][1] = obstacle1.getTop();
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
        obstacle2 = (ConstraintLayout) view.findViewById(R.id.obstacleGroup2);
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

        Button resetObstacles = (Button) view.findViewById(R.id.resetObstacles);
        resetObstacles.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (int i = 0; i < obstacleViews.size(); i++) {
                    obstacleViews.get(i).setX(originalObstacleCoords[i][0]);
                    obstacleViews.get(i).setY(originalObstacleCoords[i][1]);
                }

                map.printObstacleCoord();


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
                        ConstraintLayout myImage = (ConstraintLayout) dragEvent.getLocalState();

                        int x = (int) dragEvent.getX();
                        int y = (int) dragEvent.getY();

                        int obstacleNum = findObstacleNumber(myImage);
                        System.out.println(obstacleNum);
                        int[] newCoords = {(int) x, (int) y};
                        currentObstacleCoords[obstacleNum-1] = newCoords;
                        printAllObstacleCoords();


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
                        int[] newObstCoord = map.updateObstacleOnBoard(x, y);

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

                        ConstraintLayout myImage = (ConstraintLayout) event.getLocalState();
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
        // save rotation value!! V IMPT
        map.saveFacingWithRotation(rotation);
        //System.out.println("ROTATION");
        //System.out.println(rotation);
        //System.out.println(map.getRobotFacing());

        // Save the ImageView locations
        SharedPreferences preferences = getActivity().getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        for (int i = 0; i < obstacleViews.size(); i++) {
            ConstraintLayout imageView = obstacleViews.get(i);

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

        // put the original coords



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
            ConstraintLayout imageView = obstacleViews.get(i);
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
        }
        // To save the robot image location
        boolean pastDrawRobot = map.getCanDrawRobot();
        if (pastDrawRobot) {
            // NEED TO CLEAR THE MAP ALSO -- ERROR FIX LATER
            map.setCanDrawRobot(true);
            robot.setVisibility(View.VISIBLE);

            //CHANGE THIS EVENTUALLY ALSO
            int[] robotImageCoord = map.getCurCoord();
            int[] robotLocation = map.setRobotImagePosition(robotImageCoord[0] - 4,map.convertRow(robotImageCoord[1]), mapLeft,mapTop); // IDK WHY - 4...
            //System.out.println(robotLocation[0]);
            //System.out.println(robotLocation[1]);
            robot.setX(robotLocation[0] - 5);  // IDK WHY ITS A BIT OFF?? WHEN U PRESS NEXT TWICE
            robot.setY(robotLocation[1]);

            System.out.println(robotImageCoord[0]);
            System.out.println(robotImageCoord[1]);
            //System.out.println(rotation);
            //System.out.println(map.getRobotFacing());

        } else {
            map.setCanDrawRobot(false);
            robot.setVisibility(View.INVISIBLE);
        }

        System.out.println("end resume");



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

    public int findObstacleNumber(ConstraintLayout obstacle) {
        for (int i = 0; i < obstacleViews.size(); i++) {
            if (obstacle == obstacleViews.get(i)) {
                return i+1;
            }
        }
        return -1;
    }

    public void printAllObstacleCoords() {
        System.out.println("Obstacle Coords");
        for (int i = 0; i < currentObstacleCoords.length; i++) {
            System.out.println(i);
            System.out.println(currentObstacleCoords[i][0]);
            System.out.println(currentObstacleCoords[i][1]);
        }
        System.out.println("Obstacle Coords - end");
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