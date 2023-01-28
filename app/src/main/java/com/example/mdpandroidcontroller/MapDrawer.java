package com.example.mdpandroidcontroller;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.Nullable;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;


public class MapDrawer extends View implements Serializable {

    private static final long serialVersionUID = 1L;
    private Context context;
    private AttributeSet attrs;
    private boolean mapDrawn = false;
    private static ArrayList<String[]> arrowCoord = new ArrayList<>();
    private static Cell[][] cells;
    private static final int COL = Constants.TWENTY;
    private static final int ROW = Constants.TWENTY;
    private static float cellSize;   // IDK WHAT THIS SHOULD BE
    private static boolean canDrawRobot = false;  // why false?
    private static String robotDirection = Constants.NONE;
    private static int[] curCoord = new int[]{4, 6};     // CHANGE THIS WAY OF IMPLEMENTATION... - when u drag the robot thing

    //private ArrayList<ArrayList<Integer>> obstacleCoord =  new ArrayList<ArrayList<Integer>>();

    private static ArrayList<int[]> obstacleCoord = new ArrayList<>();

    private static int[] oldCoord = new int[]{-1, -1};
    private Bitmap arrowBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.arrow_error); // WHAT IS THIS??

    private Paint black = new Paint();
    private Paint unexploredCellColor = new Paint();
    private Paint robotColor = new Paint();


    public MapDrawer(Context c) {
        super(c);
        // init map???
        black.setStyle(Paint.Style.FILL_AND_STROKE);
        unexploredCellColor.setColor(Color.LTGRAY);
        robotColor.setColor(Color.GREEN);
    }

    public MapDrawer (Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        this.attrs = attrs;

        black.setStyle(Paint.Style.FILL_AND_STROKE);
        unexploredCellColor.setColor(Color.LTGRAY);
        robotColor.setColor(Color.GREEN);

    }


    /**
     * where u start everything?
     * @param canvas
     */
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // on first time drawing?
        if (!mapDrawn) {
            String[] dummyArrowCoord = new String[3];
            dummyArrowCoord[0] = "1";
            dummyArrowCoord[1] = "1";
            dummyArrowCoord[2] = "dummy";
            arrowCoord.add(dummyArrowCoord);
            this.createCell();
            //this.setEndCoordinate(10, 8);    // not needed anymore - use this to test functionality
            mapDrawn = true;

        }

        drawGridNumber(canvas);
        drawObstacles(canvas, obstacleCoord);
        //if (getCanDrawRobot())   // USED TO HAVE THIS

        drawRobot(canvas, curCoord);
        drawArrow(canvas, arrowCoord);
        drawCell(canvas);
        drawHorizontalLines(canvas);
        drawVerticalLines(canvas);
    }

    private void createCell() {
        cells = new Cell[COL + 1][ROW + 1];
        this.calculateDimension();
        cellSize = this.getCellSize();

        for (int x = 0; x <= COL; x++)
            for (int y = 0; y <= ROW; y++)
                cells[x][y] = new Cell(x * cellSize + (cellSize / 30), y * cellSize + (cellSize / 30), (x + 1) * cellSize, (y + 1) * cellSize, unexploredCellColor, "unexplored");
    }


    /**
     * I dont understand the image part,
     * Believe that if: is for empty cells, so you just draw the rectangle.
     * However for else: when there is an image (or obstacle?), then there is white text on it.
     * @param canvas
     */
    public void drawCell(Canvas canvas) {
        for (int x = 1; x <= COL; x++)
            for (int y = 0; y < ROW; y++)
                for (int i = 0; i < this.getArrowCoord().size(); i++)
                    if (!cells[x][y].type.equals("image") && cells[x][y].getId() == -1) {
                        canvas.drawRect(cells[x][y].startX, cells[x][y].startY, cells[x][y].endX, cells[x][y].endY, cells[x][y].paint);
                    } else {
                        Paint textPaint = new Paint();
                        textPaint.setTextSize(20);
                        textPaint.setColor(Color.WHITE);
                        textPaint.setTextAlign(Paint.Align.CENTER);
                        canvas.drawRect(cells[x][y].startX, cells[x][y].startY, cells[x][y].endX, cells[x][y].endY, cells[x][y].paint);
                        canvas.drawText(String.valueOf(cells[x][y].getId()),(cells[x][y].startX+cells[x][y].endX)/2, cells[x][y].endY + (cells[x][y].startY-cells[x][y].endY)/4, textPaint);
                    }


    }

    /**
     * Draws horizontal lines for each of the cells
     * @param canvas
     */
    private void drawHorizontalLines(Canvas canvas) {
        for (int y = 0; y <= ROW; y++)
            canvas.drawLine(cells[1][y].startX, cells[1][y].startY - (cellSize / 30), cells[ROW][y].endX, cells[15][y].startY - (cellSize / 30), black);
    }

    /**
     * Draws vertical lines for each of the cells
     * @param canvas
     */
    private void drawVerticalLines(Canvas canvas) {
        for (int x = 0; x <= COL; x++)
            canvas.drawLine(cells[x][0].startX - (cellSize / 30) + cellSize, cells[x][0].startY - (cellSize / 30), cells[x][0].startX - (cellSize / 30) + cellSize, cells[x][19].endY + (cellSize / 30), black);
    }


    private void drawGridNumber(Canvas canvas) {
        for (int x = 1; x <= COL; x++) {
            if (x > 9)
                canvas.drawText(Integer.toString(x-1), cells[x][20].startX + (cellSize / 5), cells[x][20].startY + (cellSize / 3), black);
            else
                canvas.drawText(Integer.toString(x-1), cells[x][20].startX + (cellSize / 3), cells[x][20].startY + (cellSize / 3), black);
        }
        for (int y = 0; y < ROW; y++) {
            if ((20 - y) > 9)
                canvas.drawText(Integer.toString(19 - y), cells[0][y].startX + (cellSize / 2), cells[0][y].startY + (cellSize / 1.5f), black);
            else
                canvas.drawText(Integer.toString(19 - y), cells[0][y].startX + (cellSize / 1.5f), cells[0][y].startY + (cellSize / 1.5f), black);
        }
    }

    // DOESNT WORK
    private void drawArrow(Canvas canvas, ArrayList<String[]> arrowCoord) {
        RectF rect;

        for (int i = 0; i < arrowCoord.size(); i++) {
            if (!arrowCoord.get(i)[2].equals("dummy")) {
                int col = Integer.parseInt(arrowCoord.get(i)[0]);
                int row = convertRow(Integer.parseInt(arrowCoord.get(i)[1]));
                rect = new RectF(col * cellSize, row * cellSize, (col + 1) * cellSize, (row + 1) * cellSize);
                switch (arrowCoord.get(i)[2]) {
                    case Constants.UP:
                        arrowBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.arrow_forward);
                        break;
                    case Constants.DOWN:
                        arrowBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.arrow_back);
                        break;
                    case Constants.LEFT:
                        arrowBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.arrow_left);
                        break;
                    case Constants.RIGHT:
                        arrowBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.arrow_right);
                        break;
                    default:
                        break;
                }
                canvas.drawBitmap(arrowBitmap, null, rect, null);
            }
        }
    }


    // DOESNT WORK
    public void drawRobot(Canvas canvas, int[] curCoord) {
        int androidRowCoord = this.convertRow(curCoord[1]);

        // for the shading of square
        for (int x = curCoord[0] - 1; x <= curCoord[0] + 1; x++)
            for (int y = androidRowCoord - 1; y <= androidRowCoord + 1; y++)
                cells[x][y].setType("robot");

        // For the line part
        for (int y = androidRowCoord; y <= androidRowCoord + 1; y++)
            canvas.drawLine(cells[curCoord[0] - 1][y].startX, cells[curCoord[0] - 1][y].startY - (cellSize / 30), cells[curCoord[0] + 1][y].endX, cells[curCoord[0] + 1][y].startY - (cellSize / 30), robotColor);
        for (int x = curCoord[0] - 1; x < curCoord[0] + 1; x++)
            canvas.drawLine(cells[x][androidRowCoord - 1].startX - (cellSize / 30) + cellSize, cells[x][androidRowCoord - 1].startY, cells[x][androidRowCoord + 1].startX - (cellSize / 30) + cellSize, cells[x][androidRowCoord + 1].endY, robotColor);

        // redrawing the old one black
        switch (this.getRobotDirection()) {
            case Constants.UP:
                canvas.drawLine(cells[curCoord[0] - 1][androidRowCoord + 1].startX, cells[curCoord[0] - 1][androidRowCoord + 1].endY, (cells[curCoord[0]][androidRowCoord - 1].startX + cells[curCoord[0]][androidRowCoord - 1].endX) / 2, cells[curCoord[0]][androidRowCoord - 1].startY, black);
                canvas.drawLine((cells[curCoord[0]][androidRowCoord - 1].startX + cells[curCoord[0]][androidRowCoord - 1].endX) / 2, cells[curCoord[0]][androidRowCoord - 1].startY, cells[curCoord[0] + 1][androidRowCoord + 1].endX, cells[curCoord[0] + 1][androidRowCoord + 1].endY, black);
                break;
            case Constants.DOWN:
                canvas.drawLine(cells[curCoord[0] - 1][androidRowCoord - 1].startX, cells[curCoord[0] - 1][androidRowCoord - 1].startY, (cells[curCoord[0]][androidRowCoord + 1].startX + cells[curCoord[0]][androidRowCoord + 1].endX) / 2, cells[curCoord[0]][androidRowCoord + 1].endY, black);
                canvas.drawLine((cells[curCoord[0]][androidRowCoord + 1].startX + cells[curCoord[0]][androidRowCoord + 1].endX) / 2, cells[curCoord[0]][androidRowCoord + 1].endY, cells[curCoord[0] + 1][androidRowCoord - 1].endX, cells[curCoord[0] + 1][androidRowCoord - 1].startY, black);
                break;
            case Constants.RIGHT:
                canvas.drawLine(cells[curCoord[0] - 1][androidRowCoord - 1].startX, cells[curCoord[0] - 1][androidRowCoord - 1].startY, cells[curCoord[0] + 1][androidRowCoord].endX, cells[curCoord[0] + 1][androidRowCoord - 1].endY + (cells[curCoord[0] + 1][androidRowCoord].endY - cells[curCoord[0] + 1][androidRowCoord - 1].endY) / 2, black);
                canvas.drawLine(cells[curCoord[0] + 1][androidRowCoord].endX, cells[curCoord[0] + 1][androidRowCoord - 1].endY + (cells[curCoord[0] + 1][androidRowCoord].endY - cells[curCoord[0] + 1][androidRowCoord - 1].endY) / 2, cells[curCoord[0] - 1][androidRowCoord + 1].startX, cells[curCoord[0] - 1][androidRowCoord + 1].endY, black);
                break;
            case Constants.LEFT:
                canvas.drawLine(cells[curCoord[0] + 1][androidRowCoord - 1].endX, cells[curCoord[0] + 1][androidRowCoord - 1].startY, cells[curCoord[0] - 1][androidRowCoord].startX, cells[curCoord[0] - 1][androidRowCoord - 1].endY + (cells[curCoord[0] - 1][androidRowCoord].endY - cells[curCoord[0] - 1][androidRowCoord - 1].endY) / 2, black);
                canvas.drawLine(cells[curCoord[0] - 1][androidRowCoord].startX, cells[curCoord[0] - 1][androidRowCoord - 1].endY + (cells[curCoord[0] - 1][androidRowCoord].endY - cells[curCoord[0] - 1][androidRowCoord - 1].endY) / 2, cells[curCoord[0] + 1][androidRowCoord + 1].endX, cells[curCoord[0] + 1][androidRowCoord + 1].endY, black);
                break;
            default:
                break;
        }
    }

    public void drawObstacles(Canvas canvas, ArrayList<int[]> obstacles) {
        //ArrayList<int[]> obstacles = getObstacleCoord();
        for (int i = 0; i < obstacles.size(); i++) {
            cells[obstacles.get(i)[0]][obstacles.get(i)[1]].setType("obstacle");
        }
    }

    /**
     * have smt to move the robot
     * HOW TO MAKE THE OLD ONE NOT COUNT
     */
    public void moveRobot() {
        int[] tempCoord = this.getCurCoord();
        this.setOldRobotCoord(tempCoord[0], tempCoord[1]);
        int[] oldCoord = this.getOldRobotCoord();
        boolean isWithinGrid = true;


        switch( this.getRobotDirection()) {
            case Constants.UP:
                // Includes check for end of the grid
                tempCoord[1] = Math.min(tempCoord[1] + 1,ROW-1);
                break;
            case Constants.DOWN:
                tempCoord[1] = Math.max(tempCoord[1] - 1, 2);
                break;
            case Constants.LEFT:
                tempCoord[0] = Math.max(tempCoord[0] - 1,2);
                break;
            case Constants.RIGHT:
                tempCoord[0] = Math.min(tempCoord[0] + 1,COL-1);
                break;
            default:
                System.out.println("Error in moveRobot() direction input");
                break;
        }

        // set oldcoord wont happen as of now - useless btw
        if (isWithinGrid) {
            setCurCoord(tempCoord);
        } else {
            setCurCoord(oldCoord);
        }

    }


    public int[] updateObstacleOnBoard(int x, int y, ImageView obstacle) {

        // NOTES: one cell size worth is the grid...
        //System.out.println(cells[1][0].startX + cellSize / 2);
        //System.out.println(cells[1][0].startY + cellSize / 2);

        int column = (int) Math.floor(x / cellSize);
        int row = (int) Math.floor(y / cellSize);

        //System.out.println("add obstacle");
        //System.out.println(x);
        //System.out.println(y);
        //System.out.println(column);
        //System.out.println(row);
        cells[column][row].setType("obstacle");

        setObstacleCoord(new int[] {column, row});

        int[] newObstacleDrag= {(int) (column * cellSize), (int) (row * cellSize)};

        return newObstacleDrag;

    }

    public void removeObstacleOnBoard(float originalX, float originalY) {
        int column = (int) Math.floor(originalX / cellSize);
        int row = (int) Math.floor(originalY / cellSize);

        System.out.println("removing obstacle");
        //System.out.println(originalX);
        //System.out.println(originalY);
        //System.out.println(column);
        //System.out.println(row);
        cells[column][row].setType("unexplored");
        removeObstacleCoord(new int[] {column, row});

        //printObstacleCoord();
    }





    /**
     * Saves the old robot coords and also resets the cell to the old one
     * (a little inefficient as most of the robot cells will still be robot)
     */
    private void setOldRobotCoord(int oldCol, int oldRow) {
        this.oldCoord[0] = oldCol;
        this.oldCoord[1] = oldRow;
        oldRow = this.convertRow(oldRow);
        for (int x = oldCol - 1; x <= oldCol + 1; x++)
            for (int y = oldRow - 1; y <= oldRow + 1; y++)
                cells[x][y].setType("explored");
    }




    /**
     * Called when create cell called --> to set the size of the cells --> so that it will fit the size?
     * COL+1 to make sure that the cell is full
     */
    private void calculateDimension() {
        this.setCellSize(getWidth()/(COL+1));

    } // removed col +1

    /**
     * cos row 5 is array[][15]
     * @param row
     * @return
     */
    private int convertRow(int row) {
        return (20 - row);
    }


    private void setCellSize(float cellSize) {MapDrawer.cellSize = cellSize;
    }

    public void setObstacleCoord(int[] coordinates) {
        obstacleCoord.add(coordinates);
    }

    public void removeObstacleCoord(int[] coordinates) {
        //printObstacleCoord();

        for (int i = 0; i < obstacleCoord.size(); i++) {
            if (Arrays.equals(obstacleCoord.get(i), coordinates)) {
                obstacleCoord.remove(i);
                break;
            }
        }
        //printObstacleCoord();
    }

    public void printObstacleCoord() {
        System.out.printf("total number of obstacles: %d \n", obstacleCoord.size());
        for (int x = 0; x < obstacleCoord.size(); x++) {
            System.out.println(obstacleCoord.get(x)[0]);
            System.out.println(obstacleCoord.get(x)[1]);
        }
    }

    public ArrayList<int[]> getObstacleCoord() {
        return obstacleCoord;
    }

    public float getCellSize() { return cellSize; }
    public String getRobotDirection() {
        return robotDirection;
    }


    public boolean getCanDrawRobot() {
        return canDrawRobot;
    }
    private ArrayList<String[]> getArrowCoord() {
        return arrowCoord;
    }

    public void setRobotDirection(String direction) {robotDirection = direction;}

    public void setCurCoord(int[] coordinates) {curCoord = coordinates;}

    public int[] getCurCoord() {
        return curCoord;
    }

    public int[] getOldRobotCoord() {
        return oldCoord;
    }

    /**
     * can remove this
     * @param out
     * @throws IOException
     */
    //to try serializable...
    private void writeObject(ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
        out.writeObject(context);
        out.writeObject(attrs);
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        context = (Context) in.readObject();
        attrs = (AttributeSet) in.readObject();
    }


}

