package com.guo.FlashLightFinal;

import java.util.Timer;
import java.util.TimerTask;
import com.guo.Constant.Constant.ColorMsg;
import com.guo.FlashLightFinal.R;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnTouchListener;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class MainActivity extends Activity  {

	private LinearLayout ll;
	private int i=0;
	private TextView batterLevel;
	private Handler myHandler;
	private RelativeLayout rl;
	private View view;
	private DisplayMetrics dm;
	private int fullwidth;
	private int partwidth;
	private int screencolor;
	private Button colorselect,screenButton,flashLightButton;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//设置全屏
		  getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN); 
		     //去掉标题栏   
		  requestWindowFeature(Window.FEATURE_NO_TITLE);  
		setContentView(R.layout.activity_main);
		colorselect=(Button)findViewById(R.id.colorselect);
		screenButton=(Button)findViewById(R.id.screenbutton);
		flashLightButton=(Button)findViewById(R.id.flashlightbutton);
		batterLevel=(TextView)findViewById(R.id.textView1);
		ll=(LinearLayout)findViewById(R.id.linear222);
		rl=(RelativeLayout)findViewById(R.id.relative);
		view=(View)findViewById(R.id.drawview);
		dm = new DisplayMetrics();   
	    getWindowManager().getDefaultDisplay().getMetrics(dm);
	    fullwidth=dm.widthPixels;
	    partwidth=fullwidth/7;
	    screenButton.setOnClickListener(new ScreenButtonListener());
	    flashLightButton.setOnClickListener(new FlashLightButtonListener());
		view.setOnTouchListener(new ViewChangeListener()); 
		rl=(RelativeLayout)findViewById(R.id.relative);
		//不显示，但占用位置
		ll.setVisibility(View.INVISIBLE);
		/** 设置缩放动画 */ 
		//animation.setStartOffset(long startOffset);//执行前的等待时间
		colorselect.setOnClickListener(new ColorSelectListener());
	   Timer  timer = new Timer();
       timer.schedule(new TimerTask() {			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				Message msg=new Message();
				msg.what=0x1222;
				//当主线程有多个handler的时候，就需要使用dispatch分发message。
				 myHandler.sendMessage(msg);
			}
		 },0, 60000);
      
        myHandler =new Handler(){
			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				super.handleMessage(msg);
				if(msg.what==0x1222){
					 batteryLevel();
					 System.out.println("当前电量");
				}
			}
	};
	}
    	private void batteryLevel() { 
            BroadcastReceiver batteryLevelReceiver = new BroadcastReceiver() { 
            	@Override
    			public void onReceive(Context arg0, Intent intent) {
    				// TODO Auto-generated method stub
            		arg0.unregisterReceiver(this); 
                    int rawlevel = intent.getIntExtra("level", -1);//获得当前电量 
                    int scale = intent.getIntExtra("scale", -1);  //获得总电量 
                    int level = -1; 
                    if (rawlevel >= 0 && scale > 0) { 
                        level = (rawlevel * 100) / scale; 
                    } 
                    batterLevel.setText("当前电量: " + level + "%"); 
                }
            }; 
            IntentFilter batteryLevelFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED); 
            registerReceiver(batteryLevelReceiver, batteryLevelFilter); 
        }
    	@Override
    	public void onConfigurationChanged(Configuration newConfig) {
    		// TODO Auto-generated method stub
    		super.onConfigurationChanged(newConfig);
    	}
    	class ColorSelectListener implements OnClickListener{

    		@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if(i==1){
			
				final ScaleAnimation animation =new ScaleAnimation(1f, 1f, 1f, 0f, 
						Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0f); 
						animation.setDuration(1000);//设置动画持续时间 
						/** 常用方法 */ 
						//animation.setRepeatCount(int repeatCount);//设置重复次数 
						animation.setFillAfter(true);//动画执行完后是否停留在执行完的状态 
				ll.setAnimation(animation); 
				/** 开始动画 */ 
				animation.startNow(); 
				i=2;
				}else{	
					ll.setVisibility(View.VISIBLE);
					//实现
					final ScaleAnimation animation =new ScaleAnimation(1f, 1f, 0f, 1f, 
							Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0f); 
							animation.setDuration(1000);//设置动画持续时间 
							/** 常用方法 */ 
							//animation.setRepeatCount(int repeatCount);//设置重复次数 
							animation.setFillAfter(true);//动画执行完后是否停留在执行完的状态 
					ll.setAnimation(animation); 
					/** 开始动画 */ 
					animation.startNow(); 

				i=1;
				}
			}
		}
    	class FlashLightButtonListener implements OnClickListener{

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent=new Intent();
				intent.setClass(MainActivity.this, FlashLightActivity.class);
				startActivity(intent);
				overridePendingTransition(R.anim.zoomout,R.anim.zoomin);
			}
    		
    	}
    	class ScreenButtonListener implements OnClickListener
    	{

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent=new Intent();
				intent.setClass(MainActivity.this, ScreenActivity.class);
				intent.putExtra("screencolor", screencolor);
				startActivity(intent);
				
			}
    	}
    	
			class ViewChangeListener implements OnTouchListener{

				@Override
				public boolean onTouch(View v, MotionEvent event) {
					// TODO Auto-generated method stub
					int touchwidth=(int) event.getX();
					//System.out.println("event-->"+event.getX());
					if(0<touchwidth&&touchwidth<partwidth){
						screenButton.setBackgroundResource(R.color.red);
						screencolor=ColorMsg.RED;
					}else if(partwidth<touchwidth&&touchwidth<(2*partwidth)){
						screenButton.setBackgroundResource(R.color.orange);
						screencolor=ColorMsg.ORANGE;
					}else if(2*partwidth<touchwidth&&touchwidth<(3*partwidth)){
						screenButton.setBackgroundResource(R.color.green);
						screencolor=ColorMsg.GREEN;
					}else if(3*partwidth<touchwidth&&touchwidth<(4*partwidth)){
						screenButton.setBackgroundResource(R.color.blue);
						screencolor=ColorMsg.BLUE;
				    }else if(4*partwidth<touchwidth&&touchwidth<(5*partwidth)){
				    	screenButton.setBackgroundResource(R.color.yellow);
				    	screencolor=ColorMsg.YELLOW;
				    }else if(5*partwidth<touchwidth&&touchwidth<(6*partwidth)){
				    	screenButton.setBackgroundResource(R.color.purple);
				    	screencolor=ColorMsg.PURPLE;
				    }else if(6*partwidth<touchwidth&&touchwidth<(7*partwidth)){
				    	screenButton.setBackgroundResource(R.color.white);
				    	screencolor=ColorMsg.WHITE;
				    }
					return true;
				}
    	}
} 
        

