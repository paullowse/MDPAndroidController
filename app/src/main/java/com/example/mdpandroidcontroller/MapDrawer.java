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
    private static final int COL = Constants.TWENTY; // LIKE CANT CHANGE THIS SIA
    private static final int ROW = Constants.TWENTY; // LIKE CANT CHANGE THIS WITHOUT ERRORS
    private static float cellSize;   // IDK WHAT THIS SHOULD BE

    private Paint black = new Paint();
    private Paint unexploredCellColor = new Paint();



    public MapDrawer(Context c) {
        super(c);
        // init map???
    }

    public MapDrawer(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        black.setStyle(Paint.Style.FILL_AND_STROKE);
        unexploredCellColor.setColor(Color.LTGRAY);

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
            //this.setEndCoordinate(COL - 1, ROW - 1);    // whats this for the maze?
            mapDrawn = true;
        }

        drawCell(canvas);
        drawHorizontalLines(canvas);
        drawVerticalLines(canvas);
        //drawGridNumber(canvas);
        //if (getCanDrawRobot())
        //    drawRobot(canvas, curCoord);
        //drawArrow(canvas, arrowCoord);
    }

    private void createCell() {
        cells = new Cell[COL + 1][ROW + 1];
        this.calculateDimension();
        cellSize = this.getCellSize();

        for (int x = 0; x <= COL; x++)
            for (int y = 0; y <= ROW; y++)
                cells[x][y] = new Cell(x * cellSize + (cellSize / 30), y * cellSize + (cellSize / 30), (x + 1) * cellSize, (y + 1) * cellSize, unexploredCellColor, "unexplored");
    }



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


    /**
     * Called when create cell called --> to set the size of the cells --> so that it will fit the size?
     * COL+1 to make sure that the cell is full
     */
    private void calculateDimension() {
        this.setCellSize(getWidth()/(COL+1));
    } // removed col +1
    private float getCellSize() { return cellSize; }

    private void setCellSize(float cellSize) {MapDrawer.cellSize = cellSize;
    }
    private int convertRow(int row) {
        return (20 - row);
    }

    private ArrayList<String[]> getArrowCoord() {
        return arrowCoord;
    }

}

