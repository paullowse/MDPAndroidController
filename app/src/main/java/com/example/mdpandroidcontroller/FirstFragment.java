package com.example.mdpandroidcontroller;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.example.mdpandroidcontroller.databinding.FragmentFirstBinding;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FirstFragment extends Fragment {

    private FragmentFirstBinding binding;
    String randomInfo;

    TextView displaytv;

    // grid stuff
    private static Map map;
    private static int mapLeft; // only at robot generate button
    private static int mapTop;
    private static int rotation = 0;

    private static ConstraintLayout obstacle1Grp;
    private static ImageView obstacle1Box;
    private static ImageView obstacle1Face;
    private static TextView obstacle1Id;

    private static ConstraintLayout obstacle2Grp;
    private static ImageView obstacle2Box;
    private static ImageView obstacle2Face;
    private static TextView obstacle2Id;

    private static ConstraintLayout obstacle3Grp;
    private static ImageView obstacle3Box;
    private static ImageView obstacle3Face;
    private static TextView obstacle3Id;

    private static ConstraintLayout obstacle4Grp;
    private static ImageView obstacle4Box;
    private static ImageView obstacle4Face;
    private static TextView obstacle4Id;

    private static ConstraintLayout obstacle5Grp;
    private static ImageView obstacle5Box;
    private static ImageView obstacle5Face;
    private static TextView obstacle5Id;

    private static ConstraintLayout obstacle6Grp;
    private static ImageView obstacle6Box;
    private static ImageView obstacle6Face;
    private static TextView obstacle6Id;

    private static ConstraintLayout obstacle7Grp;
    private static ImageView obstacle7Box;
    private static ImageView obstacle7Face;
    private static TextView obstacle7Id;

    private static ConstraintLayout obstacle8Grp;
    private static ImageView obstacle8Box;
    private static ImageView obstacle8Face;
    private static TextView obstacle8Id;


    private static ImageView obstacleFaceCur;

    private static String obstacleFaceText;
    private static int obstacleFaceNumber;

    private static TextView outputNotifView; // for all the notifications!!
    private static TextView locationNotifView;
    private static TextView facingNotifView;

    private static String outputNotif;
    private static String locationNotif;
    private static String facingNotif;

    private static String instruction = "ROBOT, 3, 14, E";

    private static ConstraintLayout popup;
    private static ConstraintLayout robot_popup;

    private static ImageView robot;
    float pastX, pastY;
    private static String longPress;

    private Runnable runnable;
    private Handler handler;

    TextView incomingMessages;
    StringBuilder messages;


    private static int[][] originalObstacleCoords = new int[8][2];

    private static int[][] currentObstacleCoords = new int[8][2]; // remember to expand this


    // this one is for constraint
    private List<ConstraintLayout> obstacleViews = new ArrayList<>(); // cant be static!! - COS ITS REGENRATED ALL THE TIME - change eventually.

    // for the face views
    private List<ImageView> obstacleFaceViews = new ArrayList<>();
    private List<TextView> obstacleTextViews = new ArrayList<>();
    private List<ImageView> obstacleBoxViews = new ArrayList<>();




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
            map = (Map) savedInstanceState.getSerializable("map");
        } else{
            map = view.findViewById(R.id.gridView);
        }

        for (int i = 0; i < originalObstacleCoords.length; i++) {
            for (int j = 0; j < originalObstacleCoords[i].length; j++) {
                originalObstacleCoords[i][j] = -1;
            }
        }

        incomingMessages = (TextView) view.findViewById(R.id.statusBluetoothTv);
        messages = new StringBuilder();
        LocalBroadcastManager.getInstance(FirstFragment.this.getActivity()).registerReceiver(mReceiver, new IntentFilter("incomingMessage"));

        return view;

    }

    BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String text = intent.getStringExtra("theMessage");

            messages.append(text + "\n");
            incomingMessages.setText(messages);
        }
    };

    @SuppressLint("ClickableViewAccessibility")
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        System.out.println("OnViewCreated");
        super.onViewCreated(view, savedInstanceState);


        //CHECK THIS - MIGHT NOT EVEN NEED THIS!!
        obstacle1Grp = (ConstraintLayout) view.findViewById(R.id.obstacle1Group);
        obstacle1Box = (ImageView) view.findViewById(R.id.obstacle1Box);
        obstacle1Face = (ImageView) view.findViewById(R.id.obstacle1Face);
        obstacle1Id = (TextView) view.findViewById(R.id.obstacle1ID);

        obstacle2Grp = (ConstraintLayout) view.findViewById(R.id.obstacle2Group);
        obstacle2Box = (ImageView) view.findViewById(R.id.obstacle2Box);
        obstacle2Face = (ImageView) view.findViewById(R.id.obstacle2Face);
        obstacle2Id = (TextView) view.findViewById(R.id.obstacle2ID);

        obstacle3Grp = (ConstraintLayout) view.findViewById(R.id.obstacle3Group);
        obstacle3Box = (ImageView) view.findViewById(R.id.obstacle3Box);
        obstacle3Face = (ImageView) view.findViewById(R.id.obstacle3Face);
        obstacle3Id = (TextView) view.findViewById(R.id.obstacle3ID);

        obstacle4Grp = (ConstraintLayout) view.findViewById(R.id.obstacle4Group);
        obstacle4Box = (ImageView) view.findViewById(R.id.obstacle4Box);
        obstacle4Face = (ImageView) view.findViewById(R.id.obstacle4Face);
        obstacle4Id = (TextView) view.findViewById(R.id.obstacle4ID);

        obstacle5Grp = (ConstraintLayout) view.findViewById(R.id.obstacle5Group);
        obstacle5Box = (ImageView) view.findViewById(R.id.obstacle5Box);
        obstacle5Face = (ImageView) view.findViewById(R.id.obstacle5Face);
        obstacle5Id = (TextView) view.findViewById(R.id.obstacle5ID);

        obstacle6Grp = (ConstraintLayout) view.findViewById(R.id.obstacle6Group);
        obstacle6Box = (ImageView) view.findViewById(R.id.obstacle6Box);
        obstacle6Face = (ImageView) view.findViewById(R.id.obstacle6Face);
        obstacle6Id = (TextView) view.findViewById(R.id.obstacle6ID);

        obstacle7Grp = (ConstraintLayout) view.findViewById(R.id.obstacle7Group);
        obstacle7Box = (ImageView) view.findViewById(R.id.obstacle7Box);
        obstacle7Face = (ImageView) view.findViewById(R.id.obstacle7Face);
        obstacle7Id = (TextView) view.findViewById(R.id.obstacle7ID);

        obstacle8Grp = (ConstraintLayout) view.findViewById(R.id.obstacle8Group);
        obstacle8Box = (ImageView) view.findViewById(R.id.obstacle8Box);
        obstacle8Face = (ImageView) view.findViewById(R.id.obstacle8Face);
        obstacle8Id = (TextView) view.findViewById(R.id.obstacle8ID);


        //TEXTVIEWS
        outputNotifView =  (TextView) view.findViewById(R.id.notifications);
        locationNotifView =  (TextView) view.findViewById(R.id.robot_location);
        facingNotifView =  (TextView) view.findViewById(R.id.robot_facing);


        // add to lists
        obstacleViews.add(obstacle1Grp);
        obstacleViews.add(obstacle2Grp);
        obstacleViews.add(obstacle3Grp);
        obstacleViews.add(obstacle4Grp);
        obstacleViews.add(obstacle5Grp);
        obstacleViews.add(obstacle6Grp);
        obstacleViews.add(obstacle7Grp);
        obstacleViews.add(obstacle8Grp);

        obstacleFaceViews.add(obstacle1Face);
        obstacleFaceViews.add(obstacle2Face);
        obstacleFaceViews.add(obstacle3Face);
        obstacleFaceViews.add(obstacle4Face);
        obstacleFaceViews.add(obstacle5Face);
        obstacleFaceViews.add(obstacle6Face);
        obstacleFaceViews.add(obstacle7Face);
        obstacleFaceViews.add(obstacle8Face);

        obstacleTextViews.add(obstacle1Id);
        obstacleTextViews.add(obstacle2Id);
        obstacleTextViews.add(obstacle3Id);
        obstacleTextViews.add(obstacle4Id);
        obstacleTextViews.add(obstacle5Id);
        obstacleTextViews.add(obstacle6Id);
        obstacleTextViews.add(obstacle7Id);
        obstacleTextViews.add(obstacle8Id);

        obstacleBoxViews.add(obstacle1Box);
        obstacleBoxViews.add(obstacle2Box);
        obstacleBoxViews.add(obstacle3Box);
        obstacleBoxViews.add(obstacle4Box);
        obstacleBoxViews.add(obstacle5Box);
        obstacleBoxViews.add(obstacle6Box);
        obstacleBoxViews.add(obstacle7Box);
        obstacleBoxViews.add(obstacle8Box);

        popup = (ConstraintLayout) view.findViewById(R.id.popup_window);
        popup.setVisibility(View.INVISIBLE);

        robot_popup= (ConstraintLayout) view.findViewById(R.id.popup_window_robot);
        robot_popup.setVisibility(View.INVISIBLE);

        //set face views invisible
        for (int i = 0; i < obstacleFaceViews.size(); i++) {
            obstacleFaceViews.get(i).setVisibility(View.INVISIBLE);
        }

        System.out.println("HI - here");
        printAllObstacleCoords();
        printAllObstacleLeftTop();

        obstacle1Grp.post(new Runnable() {
            @Override
            public void run() {
                System.out.println("current coordinates");
                printAllObstacleCoords();
                printAllObstacleLeftTop();

                //SET THE SIZES CORRECTLY JIC - RMB ITS THE BOX NOT THE WHOLE CONSTRAINT

                for (int i = 0; i < obstacleBoxViews.size(); i++) {
                    obstacleBoxViews.get(0).getLayoutParams().height = (int) map.getCellSize();
                    obstacleBoxViews.get(0).getLayoutParams().width = (int) map.getCellSize();
                    obstacleBoxViews.get(0).requestLayout();
                }

                for (int i = 0; i < obstacleFaceViews.size(); i++) {
                    obstacleFaceViews.get(0).getLayoutParams().height = (int) map.getCellSize();
                    obstacleFaceViews.get(0).getLayoutParams().width = (int) map.getCellSize();
                    obstacleFaceViews.get(0).requestLayout();
                }


                robot.getLayoutParams().height = (int) map.getCellSize() * 3;
                robot.getLayoutParams().width = (int) map.getCellSize() * 3;
                robot.requestLayout();





                //MAP coordinates - for saving
                mapLeft = map.getLeft();
                mapTop = map.getTop();

                // save original coords of obstacles
                for (int i = 0; i < obstacleViews.size(); i++) {
                    originalObstacleCoords[i][0] = (int) obstacleViews.get(i).getLeft();
                    originalObstacleCoords[i][1] = (int) obstacleViews.get(i).getTop();
                }
                printOriginalObstacleCoords();

                //SET at correct place
                for (int i = 0; i < obstacleViews.size(); i++) {
                    obstacleViews.get(i).setX(originalObstacleCoords[i][0]);
                    obstacleViews.get(i).setY(originalObstacleCoords[i][1]);
                }


                //System.out.println("obstacle1 dimensions");
                //System.out.println(obstacle1Box.getLayoutParams().height);
                //System.out.println(obstacle1Box.getLayoutParams().width);

            }
        });



        //ROBOT settings - KEEP IT INVISIBLE AT FIRST
        robot = (ImageView) view.findViewById(R.id.robotcar);

        if (map.getCanDrawRobot()) {
            robot.setVisibility(View.VISIBLE);
            rotation = map.convertFacingToRotation(map.getRobotFacing());
            trackRobot();
        } else {
            robot.setVisibility(View.INVISIBLE);
        }


        view.findViewById(R.id.button_first).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*Intent intent = new Intent(getContext(), SecondFragment.class);
                BluetoothServices bluetoothServices = new BluetoothServices();
                intent.putExtra("bluetooth_services", bluetoothServices);
                startActivity(intent);*/

                Intent intent = new Intent(getActivity(), Connect.class);
                startActivity(intent);
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






        Button robotButton = (Button) view.findViewById(R.id.generateRobot);
        view.findViewById(R.id.generateRobot).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean pastDrawRobot = map.getCanDrawRobot();

                if (pastDrawRobot) {
                    // NEED TO CLEAR THE MAP ALSO -- ERROR FIX LATER
                    map.setCanDrawRobot(false);
                    robot.setVisibility(View.INVISIBLE);
                    map.setOldRobotCoord(map.getCurCoord()[0],map.getCurCoord()[1]);
                } else {
                    map.saveFacingWithRotation(rotation); // error: between this button and onresume, map's facing reset to 0
                    map.setCanDrawRobot(true);
                    robot.setVisibility(View.VISIBLE);
                    //rotation = map.convertFacingToRotation(map.getRobotFacing()); - if issue check this
                    trackRobot();

                }
                map.invalidate();
            }
        });





        // NEW Short press and Long Press for ALL BUTTONS
        ImageButton forwardButton = (ImageButton) view.findViewById(R.id.arrowForward);
        ImageButton rightButton = (ImageButton) view.findViewById(R.id.arrowRight);
        ImageButton leftButton = (ImageButton) view.findViewById(R.id.arrowLeft);
        ImageButton backButton = (ImageButton) view.findViewById(R.id.arrowBack);

        View.OnClickListener movementOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (robot.getVisibility() == View.INVISIBLE) {
                    return;
                }
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

        View.OnLongClickListener movementOnLongClickListener = new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if (robot.getVisibility() == View.INVISIBLE) {
                    return false;
                }
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

        View.OnTouchListener movementOnTouchListener = new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (robot.getVisibility() == View.INVISIBLE) {
                    return false;
                }
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    handler.removeCallbacks(runnable);
                }
                return false;
            }
        };

        forwardButton.setOnClickListener(movementOnClickListener);
        forwardButton.setOnLongClickListener(movementOnLongClickListener);
        forwardButton.setOnTouchListener(movementOnTouchListener);

        rightButton.setOnClickListener(movementOnClickListener);
        rightButton.setOnLongClickListener(movementOnLongClickListener);
        rightButton.setOnTouchListener(movementOnTouchListener);

        leftButton.setOnClickListener(movementOnClickListener);
        leftButton.setOnLongClickListener(movementOnLongClickListener);
        leftButton.setOnTouchListener(movementOnTouchListener);

        backButton.setOnClickListener(movementOnClickListener);
        backButton.setOnLongClickListener(movementOnLongClickListener);
        backButton.setOnTouchListener(movementOnTouchListener);

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


        //OBSTACLES


        View.OnTouchListener obstacleOnTouchListener = new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    ClipData data = ClipData.newPlainText("", "");
                    View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(view);
                    view.startDrag(data, shadowBuilder, view, 0);

                    switch (view.getId()) {
                        case R.id.obstacle1Group:
                            pastX = obstacle1Grp.getX();
                            pastY = obstacle1Grp.getY();
                            break;
                        case R.id.obstacle2Group:
                            pastX = obstacle2Grp.getX();
                            pastY = obstacle2Grp.getY();
                            break;
                        case R.id.obstacle3Group:
                            pastX = obstacle3Grp.getX();
                            pastY = obstacle3Grp.getY();
                            break;
                        case R.id.obstacle4Group:
                            pastX = obstacle4Grp.getX();
                            pastY = obstacle4Grp.getY();
                            break;
                        case R.id.obstacle5Group:
                            pastX = obstacle5Grp.getX();
                            pastY = obstacle5Grp.getY();
                            break;
                        case R.id.obstacle6Group:
                            pastX = obstacle6Grp.getX();
                            pastY = obstacle6Grp.getY();
                            break;
                        case R.id.obstacle7Group:
                            pastX = obstacle7Grp.getX();
                            pastY = obstacle7Grp.getY();
                            break;
                        case R.id.obstacle8Group:
                            pastX = obstacle8Grp.getX();
                            pastY = obstacle8Grp.getY();
                            break;
                    }
                    return true;
                } else {
                    return false;
                }
            }
        };

        obstacle1Grp.setOnTouchListener(obstacleOnTouchListener);
        obstacle2Grp.setOnTouchListener(obstacleOnTouchListener);
        obstacle3Grp.setOnTouchListener(obstacleOnTouchListener);
        obstacle4Grp.setOnTouchListener(obstacleOnTouchListener);
        obstacle5Grp.setOnTouchListener(obstacleOnTouchListener);
        obstacle6Grp.setOnTouchListener(obstacleOnTouchListener);
        obstacle7Grp.setOnTouchListener(obstacleOnTouchListener);
        obstacle8Grp.setOnTouchListener(obstacleOnTouchListener);

        /**
         * finally works - resets all obstacles to the original coordinates
         */
        Button resetObstacles = (Button) view.findViewById(R.id.resetObstacles);
        resetObstacles.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (int i = 0; i < obstacleViews.size(); i++) {
                    obstacleViews.get(i).setX(originalObstacleCoords[i][0]);
                    obstacleViews.get(i).setY(originalObstacleCoords[i][1]);
                }
                // make the face side disappear.
                for (int i = 0; i < obstacleFaceViews.size(); i++) {
                    //obstacleFaceViews.get(i).setRotation(0);
                    obstacleFaceViews.get(i).setVisibility(View.INVISIBLE);
                }
                // reset list of current obstacles
                for (int i = 0; i < currentObstacleCoords.length; i++) {
                    currentObstacleCoords[i][0] = 0;
                    currentObstacleCoords[i][1] = 0;
                }
                //map.printObstacleCoord();
                map.removeAllObstacles();
                map.invalidate();
            }
        });



        //POPUP BUTTONS
        //JUST FOR OTHER TESTS rn nothing
        Button test = (Button) view.findViewById(R.id.button_test);
        test.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                executeInstruction();

            }
        });



        /**
         * POPUP disappears when the view clicked is not the popup_window!
         */
        View rootView = view.findViewById(R.id.first_fragment);
        rootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (view.getId() != R.id.popup_window) {
                    popup.setVisibility(View.GONE);
                }
            }
        });


        /**
         * //drop down for the face
         */
        Spinner spinner = view.findViewById(R.id.spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                getActivity(),
                R.array.spinner_array,
                android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                obstacleFaceNumber = Integer.parseInt(parent.getItemAtPosition(position).toString());
                // Do something with the selected item
                System.out.println(obstacleFaceNumber);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });

        Button northFace = (Button) view.findViewById(R.id.face_north);
        Button eastFace = (Button) view.findViewById(R.id.face_east);
        Button southFace = (Button) view.findViewById(R.id.face_south);
        Button westFace = (Button) view.findViewById(R.id.face_west);

        //obstacleFaceCur = obstacle2Face;
        /**
         * Relevant for all obstacles!
         * If u press the option again, the face will be invisible!
         * If its a different orientation, then the view will be rotated.
         */
        View.OnClickListener onClickFaceListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                obstacleFaceCur = obstacleFaceViews.get(obstacleFaceNumber-1);
                ConstraintLayout obstacleGroup = obstacleViews.get(obstacleFaceNumber-1);

                String facing = "error";

                switch (view.getId()) {
                    case R.id.face_north:
                        if (obstacleFaceCur.getRotation() == 0 && obstacleFaceCur.getVisibility() == View.VISIBLE) {
                            obstacleFaceCur.setVisibility(View.INVISIBLE);
                        } else {
                            obstacleFaceCur.setVisibility(View.VISIBLE);
                            obstacleFaceCur.setRotation(0);
                            facing = "N";
                        }
                        break;
                    case R.id.face_east:
                        if (obstacleFaceCur.getRotation() == 90 && obstacleFaceCur.getVisibility() == View.VISIBLE) {
                            obstacleFaceCur.setVisibility(View.INVISIBLE);
                        } else {
                            obstacleFaceCur.setVisibility(View.VISIBLE);
                            obstacleFaceCur.setRotation(90);
                            facing = "E";
                        }
                        break;
                    case R.id.face_south:
                        if (obstacleFaceCur.getRotation() == 180 && obstacleFaceCur.getVisibility() == View.VISIBLE) {
                            obstacleFaceCur.setVisibility(View.INVISIBLE);
                        } else {
                            obstacleFaceCur.setVisibility(View.VISIBLE);
                            obstacleFaceCur.setRotation(180);
                            facing = "S";
                        }
                        break;
                    case R.id.face_west:
                        if (obstacleFaceCur.getRotation() == 270 && obstacleFaceCur.getVisibility() == View.VISIBLE) {
                            obstacleFaceCur.setVisibility(View.INVISIBLE);
                        } else {
                            obstacleFaceCur.setVisibility(View.VISIBLE);
                            obstacleFaceCur.setRotation(270);
                            facing = "W";
                        }
                        break;
                }

                //System.out.printf("Facing: %s, Col: %d, Row: %d, left: %d, top: %d\n", facing, (int) obstacleGroup.getX(), (int) obstacleGroup.getY(), (int) map.getLeft(), (int) map.getTop());
                int[] currentColRow = map.getColRowFromXY(obstacleGroup.getX(), obstacleGroup.getY(), map.getLeft(), map.getTop());
                outputNotif = String.format("Facing: %s, Col: %d, Row: %d\n", facing, currentColRow[0], currentColRow[1]);
                System.out.printf(outputNotif);
                outputNotifView.setText(outputNotif);
            }
        };

        northFace.setOnClickListener(onClickFaceListener);
        eastFace.setOnClickListener(onClickFaceListener);
        southFace.setOnClickListener(onClickFaceListener);
        westFace.setOnClickListener(onClickFaceListener);


        /** WHOLE Dropping segment of the obstacles on the map - Do clean up q hard to understand! lots of considerations.
         *
         */
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
                        ConstraintLayout curObstacleGrp = (ConstraintLayout) dragEvent.getLocalState();

                        int x = (int) dragEvent.getX();
                        int y = (int) dragEvent.getY();


                        // this is the exact location - but we want to snap to grid //myImage.setX(x + map.getX() - map.getCellSize()/2); //myImage.setY(y+ map.getY() - map.getCellSize()/2);
                        // if the past location of obstacle was in the map, u remove the old one.
                        if (pastX >= map.getX() && pastX <= map.getX() + map.getWidth() && pastY >= map.getY() && pastY <= map.getY() + map.getHeight()) {
                            //System.out.println("IN MAP");
                            map.removeObstacleUsingCoord(pastX - map.getX() + map.getCellSize()/2,pastY - map.getY() + map.getCellSize()/2);
                        }
                        // to add the new obstacle black square - returns the coordinates, col and row --> (x, y, col, row)
                        int[] newObstCoordColRow = map.updateObstacleOnBoard(x, y);


                        //getting the notification to print!!
                        System.out.println("Notification values:");
                        int obstacleNum = getObstacleNumber(curObstacleGrp);
                        int col = newObstCoordColRow[2];
                        int row = newObstCoordColRow[3];
                        outputNotif = String.format("Obstacle: %d, Col: %d, Row: %d\n", obstacleNum, col, row);
                        System.out.printf(outputNotif);
                        outputNotifView.setText(outputNotif);


                        int[] newObstacleCoord= {newObstCoordColRow[0], newObstCoordColRow[1]};
                        newObstacleCoord[0] = newObstacleCoord[0] + (int) (map.getX());  // NEW 6 feb
                        newObstacleCoord[1] = newObstacleCoord[1] + (int) (map.getY());

                        //WHEN U JUST CLICK IT ONLY - releases the popupwindow
                        if (currentObstacleCoords[obstacleNum-1][0] == newObstacleCoord[0] && currentObstacleCoords[obstacleNum-1][1] == newObstacleCoord[1]) {
                            if (popup.getVisibility() == View.VISIBLE) {
                                popup.setVisibility(View.INVISIBLE);
                            } else {
                                // Automate the chosen obstacle number first!!
                                spinner.setSelection(obstacleNum-1);
                                popup.setVisibility(View.VISIBLE);
                            }
                        }

                        //saving the current obstacles
                        currentObstacleCoords[obstacleNum-1] = newObstacleCoord;

                        // MUST get from the map class to snap to grid - for the new image
                        curObstacleGrp.setX(newObstacleCoord[0]); //+ map.getX()); // SHOULD BE INBUILT!!
                        curObstacleGrp.setY(newObstacleCoord[1]); // + map.getY());
                        printAllObstacleCoords();

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

        /**
         * when the drop of the obstacle is out of the map, move it to the original starting place
         */
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
                        ConstraintLayout curObstacleGrp = (ConstraintLayout) event.getLocalState();

                        // loop through obstacleviews to find the obstacle name
                        // set according to obstacle coord
                        for (int i = 0; i < obstacleViews.size(); i++) {
                            if (curObstacleGrp == obstacleViews.get(i)) {
                                curObstacleGrp.setX(originalObstacleCoords[i][0]);
                                curObstacleGrp.setY(originalObstacleCoords[i][1]);
                                break; // i just tried adding this
                            }
                        }
                        //reset coordinates of current.
                        int index = getObstacleNumber(curObstacleGrp);
                        currentObstacleCoords[index-1][0] = 0;
                        currentObstacleCoords[index-1][1] = 0;

                        printAllObstacleCoords();

                        // in of map to out of map!!
                        if(pastX >= mapCoord[0] && pastX <= mapCoord[0] + mapWidth && pastY >= mapCoord[1] && pastY <= mapCoord[1] + mapHeight){
                            map.removeObstacleUsingCoord(pastX - map.getX() + map.getCellSize()/2,pastY - map.getY() + map.getCellSize()/2);
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
        map.saveFacingWithRotation(rotation);

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

            //System.out.printf("Obstacle %d |  X: %d, Y: %d, left: %d, top: %d\n", i+1, x,y,left,top);

        }
        editor.apply();

        printAllObstacleCoords();
        System.out.println("END NEXT");
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

            // THIS code just sets it at the top... ERROR

            // somehow now dont rly need
            imageView.setX(x);
            imageView.setY(y);

            //System.out.printf("Obstacle %d |  X: %d, Y: %d, left: %d, top: %d\n", i+1, x,y,left,top);
        }

        // To save the robot image location
        boolean pastDrawRobot = map.getCanDrawRobot();
        if (pastDrawRobot) {
            map.setCanDrawRobot(true);
            robot.setVisibility(View.VISIBLE);

            //CHANGE THIS EVENTUALLY ALSO
            int[] robotImageCoord = map.getCurCoord();
            int[] robotLocation = map.setRobotImagePosition(robotImageCoord[0],map.convertRow(robotImageCoord[1]), mapLeft,mapTop);//mapLeft,mapTop); // ONLY WORKS IF GENERATE WAS DONE BEFORE?

            robot.setX(robotLocation[0]);
            robot.setY(robotLocation[1]);
        } else {
            // NEED TO CLEAR THE MAP ALSO -- ERROR FIX LATER
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
        trackRobot();
    }

    /** RUNS EVERYTIME robot moves!!
     * Purpose is to track the image of the robot to the current coord of the robot in map class. and follows the right rotation
     * The robot will be paired accordingly
     * Does displays as well
     *
     */
    @SuppressLint("DefaultLocale")
    public void trackRobot() {
        //System.out.println("TRACK ROBOT FUNCTION");

        int[] robotImageCoord = map.getCurCoord();
        int[] robotLocation = map.setRobotImagePosition(robotImageCoord[0],map.convertRow(robotImageCoord[1]), map.getLeft(),map.getTop());
        robot.setX(robotLocation[0]);
        robot.setY(robotLocation[1]);
        robot.setRotation(rotation);

        //Setting displays
        locationNotif = String.format("X: %d, Y: %d\n", robotImageCoord[0], robotImageCoord[1]);
        locationNotifView.setText(locationNotif);

        facingNotif = String.format("Facing: %s\n", map.convertRotationToFacing(rotation));
        facingNotifView.setText(facingNotif);
    }

    /**
     * Responding to instructions from external RPI
     */
    public void executeInstruction() {
        String formattedInstruction = instruction.replaceAll("\\s", "");
        List<String> instructionList = Arrays.asList(formattedInstruction.split(","));

        System.out.println(formattedInstruction);
        System.out.println(instructionList.get(0));

        if (instructionList.get(0).equals("TARGET")) {
            // need to add check?
            int targetObst = Integer.parseInt(instructionList.get(1));
            String targetID = instructionList.get(2);
            TextView target = obstacleTextViews.get(targetObst-1);
            target.setText(targetID);

        } else if (instructionList.get(0).equals("ROBOT")) {
            int col = Integer.parseInt(instructionList.get(1));
            int row = Integer.parseInt(instructionList.get(2));
            String face = instructionList.get(3);

            robot.setVisibility(View.VISIBLE);

            map.setOldRobotCoord(map.getCurCoord()[0], map.getCurCoord()[1]); // create tracks
            int[] newCoord = new int[] {col, row};
            map.setCurCoord(newCoord);

            rotation = map.convertFacingToRotation(face);
            map.saveFacingWithRotation(rotation);
            trackRobot();
            map.invalidate();
        }
    }


    /**
     * convert the constraint layout obstacle to an index
     * @param obstacle
     * @return
     */
    public int getObstacleNumber(ConstraintLayout obstacle) {
        for (int i = 0; i < obstacleViews.size(); i++) {
            if (obstacle == obstacleViews.get(i)) {
                return i+1;
            }
        }
        return -1;
    }


    /**
     * HELPER FUNCTIONS TO CHECK
     */

    public void printAllObstacleCoords() {
        System.out.println("Obstacle Coords");
        for (int i = 0; i < currentObstacleCoords.length; i++) {
            System.out.printf("Obstacle %d |  X: %d, Y: %d\n", i+1, currentObstacleCoords[i][0], currentObstacleCoords[i][1]);
        }
    }

    public void printOriginalObstacleCoords() {
        System.out.println("OG obstacle Coords");
        for (int i = 0; i < originalObstacleCoords.length; i++) {
            System.out.printf("Obstacle %d |  X: %d, Y: %d\n", i+1, originalObstacleCoords[i][0], originalObstacleCoords[i][1]);
        }

    }

    public void printAllObstacleLeftTop() {
        System.out.println("Obstacle Left Top");
        for (int i = 0; i < obstacleViews.size(); i++) {
            //System.out.println(i+1);
            System.out.printf("Obstacle %d |  Left: %d, Top: %d\n", i+1, obstacleViews.get(i).getLeft(), obstacleViews.get(i).getTop());
        }
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
        super.onDestroyView();
        binding = null;
    }

}