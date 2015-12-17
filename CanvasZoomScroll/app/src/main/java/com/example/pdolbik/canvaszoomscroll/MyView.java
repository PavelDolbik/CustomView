package com.example.pdolbik.canvaszoomscroll;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.Toast;

/**
 * Created by p.dolbik on 01.12.2015.
 */


public class MyView extends View {

    //Количество клеточек
    private int   horizontalCountOfCells = 10;
    private int   verticalCountOfCells   = 20;
    private Paint paintGrid;

    //Рисуем Android-а
    private Paint  paintAndroid;
    private Bitmap bitmapAndroid;
    private Matrix matrixAndroid;
    private float  x1, x2;
    private float  y1, y2;
    private boolean moveImg = false;

    //Размеры canvas
    private int canvasWidth;
    private int canvasHeight;

    //Используются для зумирования
    private ScaleGestureDetector scaleGestureDetector;
    //значение зума по умолчанию
    private float mScaleFactor = 1f;

    //Обработка жестов (скролл, касание на canvas)
    private GestureDetector detector;

    public MyView(Context context, AttributeSet attrs) {
        super(context, attrs);

        //Определяем параметры кисти, которой будем рисовать сетку
        paintGrid = new Paint();
        paintGrid.setAntiAlias(true);
        paintGrid.setDither(true);
        paintGrid.setColor(0xffff0505);
        paintGrid.setStrokeWidth(5f);
        paintGrid.setStyle(Paint.Style.STROKE);
        paintGrid.setStrokeJoin(Paint.Join.ROUND);
        paintGrid.setStrokeCap(Paint.Cap.ROUND);

        matrixAndroid = new Matrix();
        paintAndroid  = new Paint(Paint.ANTI_ALIAS_FLAG);
        bitmapAndroid = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);

        //Добавляем скроллбар
        setHorizontalScrollBarEnabled(true);
        setVerticalScrollBarEnabled(true);

        scaleGestureDetector = new ScaleGestureDetector(context, new MyScaleGestureListener());
        detector = new GestureDetector(context, new MyGestureListener());
    }


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        canvasWidth  = w;
        canvasHeight = h;

        x1 = (canvasWidth - bitmapAndroid.getWidth()) / 2;
        y1 = (canvasHeight - bitmapAndroid.getHeight()) / 2;
        x2 = x1 + bitmapAndroid.getWidth();
        y2 = y1 + bitmapAndroid.getHeight();
        matrixAndroid.postTranslate(x1, y1);
    }


    @Override
    protected int computeHorizontalScrollExtent() {
        return canvasWidth; //указываем ширину нашего компонента
    }
    @Override
    protected int computeVerticalScrollExtent() {
        return canvasHeight; //указываем ширину нашего компонента
    }


    @Override
    protected int computeHorizontalScrollOffset() {
        return getScrollX(); //текущий оффсет скроллинга
    }
    @Override
    protected int computeVerticalScrollOffset() {
        return getScrollY(); //текущий оффсет скроллинга
    }


    @Override
    protected int computeHorizontalScrollRange() {
        return (int) (canvasWidth * mScaleFactor); //Размер компонента при зуме
    }
    @Override
    protected int computeVerticalScrollRange() {
        return (int) (canvasHeight * mScaleFactor); //Размер компонента при зуме
    }


    @Override
    protected void onDraw(Canvas canvas) {
        canvas.save();
        //Зумируем канвас
        canvas.scale(mScaleFactor, mScaleFactor);
        //Рисуем етку
        drawGrid(canvas);
        //Рисуем Android-а
        canvas.drawBitmap(bitmapAndroid, matrixAndroid, null);

        canvas.restore();
    }

    //В случае касания пальем передаем обработку Motion Event'а MyGestureListener'у и MyScaleGestureListener'у
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        detector.onTouchEvent(event);
        scaleGestureDetector.onTouchEvent(event);
        return true;
    }

    private void drawGrid(Canvas canvas) {
        for( int x = 0; x <= horizontalCountOfCells; x++)
            canvas.drawLine((float)x* canvas.getWidth() / horizontalCountOfCells, 0, (float)x* canvas.getWidth()  / horizontalCountOfCells, canvas.getHeight() , paintGrid);
        for(int y=0; y < verticalCountOfCells + 1; y++)
            canvas.drawLine(0, (float)y* canvas.getHeight() / verticalCountOfCells, canvas.getWidth(), (float)y* canvas.getHeight() / verticalCountOfCells, paintGrid);
    }

    private class MyScaleGestureListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {

        //Обрабатываем "щипок" пальцами
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            //получаем значение зума относительно предыдущего состояния
            float scaleFactor = scaleGestureDetector.getScaleFactor();

            //получаем координаты фокальной точки - точки между пальцами
            float focusX = scaleGestureDetector.getFocusX();
            float focusY = scaleGestureDetector.getFocusY();

            //следим чтобы канвас не уменьшили меньше исходного размера и не допускаем увеличения больше чем в 3 раза
            if(mScaleFactor*scaleFactor > 1 && mScaleFactor*scaleFactor < 3){
                mScaleFactor *= scaleGestureDetector.getScaleFactor();
                //используется при расчетах
                //по умолчанию после зума канвас отскролит в левый верхний угол.
                //Скролим канвас так, чтобы на экране оставалась
                //область канваса, над которой был жест зума
                //getScrollX()+ focusX - расчитывается координаты фокальной точки относительно начала канваса(верхний левый угол)
                //scaleFactor - умножаются на коэффициент зуммирования
                //focusX(focusY) - вычитаются координаты точки относительно вьюхи
                int scrollX = (int)((getScrollX()+ focusX) * scaleFactor - focusX);
                int scrollY = (int)((getScrollY()+ focusY) * scaleFactor - focusY);
                //берем ближайшее значение из промежутка [0, canvasSize - viewSize]
                // для предотвращения скролла за пределы поля
                scrollX = Math.min( Math.max(scrollX, 0), (int) (canvasWidth * mScaleFactor) - canvasWidth);
                scrollY = Math.min( Math.max(scrollY, 0), (int) (canvasHeight * mScaleFactor) - canvasHeight);
                scrollTo(scrollX, scrollY);
            }
            invalidate(); //Bызываем перерисовку принудительно
            return true;
        }
    }

    private class MyGestureListener extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onDown(MotionEvent e) {

            float X = (e.getX()+ getScrollX()) / mScaleFactor;
            float Y = (e.getY() + getScrollY()) / mScaleFactor;

            if ((X >= x1 && X <= x2) && (Y >= y1 && Y <= y2)) {
                Toast.makeText(getContext(), "Img click", Toast.LENGTH_SHORT).show();
                moveImg = true;
            } else {
                moveImg = false;
            }
            return true;
        }

        //обрабатываем скролл (перемещение пальца по экрану)
        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY)
        {

            if (moveImg) {
                onMove(-distanceX/mScaleFactor, -distanceY/mScaleFactor);
            } else {
                //не даем канвасу показать края по горизонтали
                if( getScrollX() + distanceX < (canvasWidth * mScaleFactor) - canvasWidth && getScrollX() + distanceX > 0){
                    scrollBy((int) distanceX, 0);
                }
                //не даем канвасу показать края по вертикали
                if( getScrollY() + distanceY < (canvasHeight * mScaleFactor) - canvasHeight && getScrollY()+ distanceY > 0){
                    scrollBy(0, (int)distanceY);
                }

                if (!awakenScrollBars()) {
                    invalidate();
                }
            }

            return true;
        }

        //обрабатываем двойной тап
        @Override
        public boolean onDoubleTapEvent(MotionEvent event){
            onResetLocation();
            mScaleFactor = 1f; //Зумируем канвас к первоначальному виду
            scrollTo(0, 0); //Скролим, чтобы не было видно краев канваса.
            invalidate(); //Перерисовываем канвас
            return true;
        }

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            moveImg = false;
            return true;
        }

    }

    public void onMove(float dx, float dy) {
        matrixAndroid.postTranslate(dx, dy);

        float[] values = new float[9];
        matrixAndroid.getValues(values);
        float globalX = values[2];
        float globalY = values[5];

        x1 = globalX;
        y1 = globalY;
        x2 = x1 + bitmapAndroid.getWidth();
        y2 = y1 + bitmapAndroid.getHeight();

        invalidate();
    }

    public void onResetLocation() {
        matrixAndroid.reset();
        x1 = (canvasWidth - bitmapAndroid.getWidth()) / 2;
        y1 = (canvasHeight - bitmapAndroid.getHeight()) / 2;
        x2 = x1 + bitmapAndroid.getWidth();
        y2 = y1 + bitmapAndroid.getHeight();
        matrixAndroid.postTranslate(x1, y1);
        invalidate();
    }

}
