package com.example.jack.hw6;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.os.CountDownTimer;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Random;


public class MainActivity extends Activity {

    RelativeLayout gameboard;
    RelativeLayout root;
    final Context thisContext = this;
    Random cR = new Random();
    int userPoint = 0;
    int stspeed = 5, stamount = 5, sttime = 3000;
    int minspeed = 5, minamount = 5, mintime = 3000;

    SeekBar seekbar_speed, seekbar_amount, seekbar_time;
    TextView textview_speed, textview_amount, textview_time, textview_score, textview_gamescore;

    private class Dot extends View {

        RelativeLayout.LayoutParams lr;
        int radious, speed;
        double direction, x, y;

        public Dot(Context context, int x, int y, int color, int speed, double dir, int radious){
            super(context);
            this.radious = radious;
            this.x = (double)x;
            this.y = (double)y;
            this.speed = speed;
            this.direction = dir;
            ShapeDrawable circle = new ShapeDrawable(new OvalShape());
            circle.getPaint().setColor(color);
            setBackground(circle);
            lr = new RelativeLayout.LayoutParams(radious, radious);
            setLayoutParams(lr);
            lr.setMargins(x, y, 0, 0);

            this.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent e) {
                    if(e.getAction() == MotionEvent.ACTION_DOWN){
                        userPoint++;
                        String tmpstr = (String) thisContext.getResources().getText(R.string.score_text);
                        textview_gamescore.setText(tmpstr + userPoint);
                        //Toast.makeText(getApplicationContext(), "GET POINT!! Your score now: " + userPoint, Toast.LENGTH_SHORT).show();

                        ShapeDrawable circle = new ShapeDrawable(new OvalShape());
                        circle.getPaint().setColor(Color.argb(0, 0, 0, 0));
                        setBackground(circle);

                        int tmpx = cR.nextInt() % (gameboard.getRight() - 70*2 - root.getPaddingRight() - root.getPaddingLeft()) + (70 + root.getPaddingLeft()), tmpy = cR.nextInt() % (gameboard.getBottom() - 70*2 - root.getPaddingBottom() - root.getPaddingTop()) + (70 + root.getPaddingTop());
                        int tmpr = cR.nextInt() % 256, tmpg = cR.nextInt() % 256, tmpb = cR.nextInt() % 256;
                        setPosition(tmpx, tmpy);
                        setXYD(tmpx, tmpy, 0);
                        circle.getPaint().setColor(Color.argb(255, tmpr, tmpg, tmpb));
                        setBackground(circle);
                    }
                    return false;
                }
            });
        }

        public void setXYD(double x, double y, double dir){
            this.x = x;
            this.y = y;
            this.direction = dir;
        }

        public int setPosition(int x,int y){
            Point p = new Point(gameboard.getRight(), gameboard.getBottom());
            int result = 0;

            if(x<0) {
                x = 0;
                result += 1;
            }else if(x >=  p.x - radious - root.getPaddingRight()) {
                x = p.x - radious - root.getPaddingRight();
                result += 2;
            }
            if(y<0) {
                y = 0;
                result += 4;
            }else if(y >= p.y-radious-root.getPaddingBottom()) {
                y = p.y - radious - root.getPaddingBottom();
                result  += 8;
            }

            setTop(y);
            setBottom(y + radious);
            setLeft(x);
            setRight(x + radious);
            return result;
        }

        public int moveTo(double x,double y){
            this.x += x;
            this.y += y;
            return setPosition((int)this.x,(int)this.y);
        }

        synchronized  public void move(){
            double movex = Math.cos(direction) * speed, movey = Math.sin(direction) * speed;
            int result = moveTo(movex, movey);

            if(result != 0){
                Random rand = new Random();
                double newDir = rand.nextDouble() * Math.PI * 2;
                if((result & 1) != 0){
                    if(newDir >= Math.PI / 2 && newDir <= Math.PI) {
                        newDir -= Math.PI / 2;
                    }else if(newDir >= Math.PI && newDir <= Math.PI * 3f / 2f){
                        newDir += Math.PI / 2;
                    }
                }else if((result & 2) != 0){
                    if(newDir >= 0 && newDir <= Math.PI / 2) {
                        newDir += Math.PI / 2;
                    }else if(newDir >= Math.PI * 3f / 2f && newDir <= Math.PI * 2) {
                        newDir -= Math.PI / 2;
                    }
                }
                if((result & 4) != 0) {
                    if (newDir >= Math.PI && newDir <= Math.PI * 3f / 2f) {
                        newDir -= Math.PI / 2;
                    }else if (newDir >= Math.PI * 3f / 2f && newDir <= Math.PI * 2){
                        newDir += Math.PI / 2 - Math.PI * 2;
                    }
                }else if((result & 8) != 0){
                    if(newDir >= 0 && newDir <= Math.PI / 2) {
                        newDir += Math.PI * 2 - Math.PI / 2;
                    }else if(newDir >= Math.PI / 2 && newDir <= Math.PI) {
                        newDir += Math.PI / 2;
                    }
                }
                direction = newDir;
                moveTo(Math.cos(direction) * speed, Math.sin(direction) * speed);
            }
        }
    }

    public void jumpToStart(){
        setContentView(R.layout.start);
        Button startButton = (Button)findViewById(R.id.button_start);
        startButton.setOnClickListener(new Button.OnClickListener(){
            public void onClick(View v){
                jumpToGame();
            }
        });

        seekbar_speed = (SeekBar)findViewById(R.id.seekbar_speed);
        seekbar_amount = (SeekBar)findViewById(R.id.seekbar_amount);
        seekbar_time = (SeekBar)findViewById(R.id.seekbar_time);
        textview_speed = (TextView)findViewById(R.id.textview_speed);
        textview_amount = (TextView)findViewById(R.id.textview_amount);
        textview_time = (TextView)findViewById(R.id.textview_time);
        textview_score = (TextView)findViewById(R.id.textview_score);

        seekbar_speed.setProgress(stspeed-minspeed);
        seekbar_amount.setProgress(stamount-minamount);
        seekbar_time.setProgress((sttime-mintime)/1000);

        String tmpstr = (String)thisContext.getResources().getText(R.string.speed_text);
        int tmpnow = seekbar_speed.getProgress() + minspeed, tmpmax = seekbar_speed.getMax() + minspeed;
        textview_speed.setText(tmpstr + " " + tmpnow + "/" + tmpmax);
        tmpnow = seekbar_amount.getProgress() + minamount;
        tmpmax = seekbar_amount.getMax() + minamount;
        tmpstr = (String)thisContext.getResources().getText(R.string.amount_text);
        textview_amount.setText(tmpstr + " " + tmpnow + "/" + tmpmax);
        tmpnow = seekbar_time.getProgress() + mintime/1000;
        tmpmax = seekbar_time.getMax() + mintime/1000;
        tmpstr = (String)thisContext.getResources().getText(R.string.time_text);
        textview_time.setText(tmpstr + " " + tmpnow + "/" + tmpmax);
        tmpstr = (String)thisContext.getResources().getText(R.string.score_text);
        textview_score.setText(tmpstr + "" + userPoint);

        seekbar_speed.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int p = stspeed;

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                p = progress + minspeed;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                int tmpmax = seekbar_speed.getMax() + minspeed;
                String tmpstr = (String)thisContext.getResources().getText(R.string.speed_text);
                textview_speed.setText(tmpstr + " " + p + "/" + tmpmax);
                stspeed = p;
            }
        });
        seekbar_amount.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int p = stamount;
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                p = progress + minamount;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                int tmpmax = seekbar_amount.getMax() + minamount;
                String tmpstr = (String)thisContext.getResources().getText(R.string.amount_text);
                textview_amount.setText(tmpstr + " " + p + "/" + tmpmax);
                stamount = p;
            }
        });
        seekbar_time.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int p = sttime;
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                p = progress + mintime/1000;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                int tmpmax = seekbar_time.getMax() + mintime/1000;
                String tmpstr = (String)thisContext.getResources().getText(R.string.time_text);
                textview_time.setText(tmpstr + " " + p + "/" + tmpmax);
                sttime = p * 1000;
            }
        });

    }

    public void jumpToGame(){
        setContentView(R.layout.activity_main);
        gameboard = (RelativeLayout)findViewById(R.id.gameboard);
        root = (RelativeLayout)findViewById(R.id.root);

        userPoint = 0;
        textview_gamescore = (TextView)findViewById(R.id.textview_gamescore);
        String tmpstr = (String)thisContext.getResources().getText(R.string.score_text);
        textview_gamescore.setText(tmpstr + userPoint);
        textview_gamescore.setText(tmpstr + root.getRight());
        for(int ii = 0; ii < stamount; ii++) {
            int tmpx = cR.nextInt() % (gameboard.getRight() - 70*2 - root.getPaddingRight() - root.getPaddingLeft()) + (70 + root.getPaddingLeft()), tmpy = cR.nextInt() % (gameboard.getBottom() - 70*2 - root.getPaddingBottom() - root.getPaddingTop()) + (70 + root.getPaddingTop());
            int tmpr = cR.nextInt() % 256, tmpg = cR.nextInt() % 256, tmpb = cR.nextInt() % 256;
            gameboard.addView(new Dot(this, tmpx, tmpy, Color.argb(255, tmpr, tmpg, tmpb), stspeed, 0, 70));
        }

        new CountDownTimer(sttime,1000){

            @Override
            public void onFinish() {
                // TODO Auto-generated method stub
                Toast.makeText(getApplicationContext(), "TIME'S UP!!", Toast.LENGTH_SHORT).show();
                jumpToStart();
            }
            @Override
            public void onTick(long millisUntilFinished) {
                // TODO Auto-generated method stub
            }

        }.start();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        gameboard=(RelativeLayout)findViewById(R.id.gameboard);
        root=(RelativeLayout)findViewById(R.id.root);

        userPoint = 0;

        stspeed = minspeed;
        stamount = minamount;
        sttime = mintime;
        jumpToStart();
    }

    protected void onResume (){
        super.onResume();

        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            for (int i = 0; i < gameboard.getChildCount(); i++) {
                                Dot cur = (Dot) gameboard.getChildAt(i);
                                cur.move();
                            }
                        }
                    });
                    try {
                        Thread.sleep(10);
                    } catch (Exception e) {
                    }
                }
            }
        }).start();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
}