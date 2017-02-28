package com.aixinwu.axw.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.PointF;
import android.graphics.Matrix;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.FloatMath;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.view.View.OnTouchListener;

import com.aixinwu.axw.R;

public class RawPictureActivity extends Activity {

    ImageView imageView ;

    int count;
    long firClick, secClick;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_raw_picture);

        imageView = (ImageView) findViewById(R.id.qrImg);

        Intent intent = this.getIntent();
        String imgString = intent.getStringExtra("img");

        if (imgString.equals("APP")){
            imageView.setImageResource(R.drawable.qrcode);
        }
        else if (imgString.equals("wechat")){
            imageView.setImageResource(R.drawable.qrwechat);
        }

        imageView.setOnTouchListener(new TouchListener());

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_raw_picture, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private final class TouchListener implements OnTouchListener {

        /** 记录是拖拉照片模式还是放大缩小照片模式 */
        private int mode = 0;// 初始状态
        /** 拖拉照片模式 */
        private static final int MODE_DRAG = 1;
        /** 放大缩小照片模式 */
        private static final int MODE_ZOOM = 2;
        /**双击放大*/
        private static final int MODE_MAGNIFY = 3;
        /**双击缩小*/
        private static final int MODE_SHRINK = 4;

        private int mode2 = 3;

        /** 用于记录开始时候的坐标位置 */
        private PointF startPoint = new PointF();
        /** 用于记录拖拉图片移动的坐标位置 */
        private Matrix matrix = new Matrix();
        /** 用于记录图片要进行拖拉时候的坐标位置 */
        private Matrix currentMatrix = new Matrix();

        /** 两个手指的开始距离 */
        private float startDis;
        /** 两个手指的中间点 */
        private PointF midPoint;

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            /** 通过与运算保留最后八位 MotionEvent.ACTION_MASK = 255 */
            switch (event.getAction() & MotionEvent.ACTION_MASK) {
                // 手指压下屏幕
                case MotionEvent.ACTION_DOWN:
                    mode = MODE_DRAG;
                    // 记录ImageView当前的移动位置
                    currentMatrix.set(imageView.getImageMatrix());
                    startPoint.set(event.getX(), event.getY());

                    count++;
                    if(count == 1){
                        firClick = System.currentTimeMillis();

                    } else if (count == 2){
                        secClick = System.currentTimeMillis();
                        if(secClick - firClick < 500){
                            //双击事件
                            //if (mode2 == MODE_SHRINK){
                                imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
                             /*   mode2 = MODE_MAGNIFY;
                           }
                            else if (mode2 == MODE_MAGNIFY) {
                                imageView.setScaleType(ImageView.ScaleType.MATRIX);
                                matrix.set(currentMatrix);
                                matrix.postScale((float) 2.0, (float) 2.0, event.getX(), event.getY());
                                mode2 = MODE_SHRINK;
                            }*/
                        }
                        count = 0;
                        firClick = 0;
                        secClick = 0;
                    }

                    break;
                // 手指在屏幕上移动，改事件会被不断触发
                case MotionEvent.ACTION_MOVE:

                    // 拖拉图片
                    if (mode == MODE_DRAG) {
                        float dx = event.getX() - startPoint.x; // 得到x轴的移动距离
                        float dy = event.getY() - startPoint.y; // 得到y轴的移动距离
                        //if (dx > 10 || dy > 10){
                            mode2 = MODE_SHRINK;
                            // 在没有移动之前的位置上进行移动
                        //}
                        matrix.set(currentMatrix);
                        matrix.postTranslate(dx, dy);
                    }
                    // 放大缩小图片
                    else if (mode == MODE_ZOOM) {
                        mode2 = MODE_SHRINK;
                        count = 0;
                        imageView.setScaleType(ImageView.ScaleType.MATRIX);
                        float endDis = distance(event);// 结束距离
                        if (endDis > 10f) { // 两个手指并拢在一起的时候像素大于10
                            float scale = endDis / startDis;// 得到缩放倍数
                            matrix.set(currentMatrix);
                            matrix.postScale(scale, scale,midPoint.x,midPoint.y);
                        }
                    }
                    break;
                // 手指离开屏幕
                case MotionEvent.ACTION_UP:
                    // 当触点离开屏幕，但是屏幕上还有触点(手指)
                case MotionEvent.ACTION_POINTER_UP:
                    mode = 0;
                    break;
                // 当屏幕上已经有触点(手指)，再有一个触点压下屏幕
                case MotionEvent.ACTION_POINTER_DOWN:
                    mode = MODE_ZOOM;
                    /** 计算两个手指间的距离 */
                    startDis = distance(event);
                    /** 计算两个手指间的中间点 */
                    if (startDis > 10f) { // 两个手指并拢在一起的时候像素大于10
                        midPoint = mid(event);
                        //记录当前ImageView的缩放倍数
                        currentMatrix.set(imageView.getImageMatrix());
                    }
                    break;
            }
            imageView.setImageMatrix(matrix);
            return true;
        }

        /** 计算两个手指间的距离 */
        private float distance(MotionEvent event) {
            float dx = event.getX(1) - event.getX(0);
            float dy = event.getY(1) - event.getY(0);
            /** 使用勾股定理返回两点之间的距离 */
            return (float)Math.sqrt(dx * dx + dy * dy);
        }

        /** 计算两个手指间的中间点 */
        private PointF mid(MotionEvent event) {
            float midX = (event.getX(1) + event.getX(0)) / 2;
            float midY = (event.getY(1) + event.getY(0)) / 2;
            return new PointF(midX, midY);
        }

    }

    private class onDoubleClick implements View.OnTouchListener{

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if(MotionEvent.ACTION_DOWN == event.getAction()){
                count++;
                if(count == 1){
                    firClick = System.currentTimeMillis();

                } else if (count == 2){
                    secClick = System.currentTimeMillis();
                    if(secClick - firClick < 1000){
                        //双击事件
                        imageView.setScaleType(ImageView.ScaleType.FIT_XY);

                    }
                    count = 0;
                    firClick = 0;
                    secClick = 0;

                }
            }
            return true;
        }

    }

}
