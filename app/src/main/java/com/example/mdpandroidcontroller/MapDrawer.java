package com.example.mdpandroidcontroller;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.annotation.Nullable;

import java.util.ArrayList;

import com.example.mdpandroidcontroller.Constants;


public class MapDrawer extends View {

    private boolean mapDrawn = false;
    private static ArrayList<String[]> arrowCoord = new ArrayList<>();
    private static Cell[][] cells;
    private static final int COL = Constants.TWENTY;
    private static final int ROW = Constants.TWENTY;
    private static float cellSize;   // IDK WHAT THIS SHOULD BE
    private static boolean canDrawRobot = false;  // why false?
    private static String robotDirection = Constants.NONE;
    private static int[] curCoord = new int[]{4, 6};     // IF CHANGE THIS THEN HOW

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

    public MapDrawer(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

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

        drawCell(canvas);
        drawHorizontalLines(canvas);
        drawVerticalLines(canvas);
        drawGridNumber(canvas);
        //if (getCanDrawRobot())   // USED TO HAVE THIS
        drawRobot(canvas, curCoord);
        drawArrow(canvas, arrowCoord);
        drawCell(canvas);
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
    private void drawCell(Canvas canvas) {
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


    /**
     * DELETE THIS LATER - irrelevant
     * @param col
     * @param row
     */
    public void setEndCoordinate(int col, int row) {
        row = this.convertRow(row);
        for (int x = col - 1; x <= col + 1; x++)
            for (int y = row - 1; y <= row + 1; y++)
                cells[x][y].setType("end");
    }

    private float getCellSize() { return cellSize; }
    public String getRobotDirection() {
        return robotDirection;
    }
    public boolean getCanDrawRobot() {
        return canDrawRobot;
    }
    private ArrayList<String[]> getArrowCoord() {
        return arrowCoord;
    }

}

