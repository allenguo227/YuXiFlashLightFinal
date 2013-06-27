package com.guo.FlashLightFinal;


import java.util.Timer;
import java.util.TimerTask;

import com.guo.FlashLightFinal.R.layout;
import com.guo.FlashLightFinal.R.menu;

import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;

public class FlashLightActivity extends Activity {
	private ImageView exitflashlightbutton;
	private ImageView soslightbutton;
	private ImageView flashlightswitch;
    private Camera camera;  
    private int flashTime=0;
	private boolean Flashing=false;
	private boolean FlashlightOpening=true;
	private boolean SOSisOpening=false;
    private Timer timer;
    private  Handler myHandler;
	private IntentFilter intentFilter_flashtime;
	private BroadcastReceiver flashTimeReceiver;
    @Override  
    protected void onCreate(Bundle savedInstanceState) {  
        super.onCreate(savedInstanceState);  
      //设置全屏 
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN); 
     //去掉标题栏   
        requestWindowFeature(Window.FEATURE_NO_TITLE);  
        setContentView(R.layout.activity_flash_light);  
        exitflashlightbutton=(ImageView)findViewById(R.id.exitflashlightbutton);
        soslightbutton=(ImageView)findViewById(R.id.soslightbutton);
        flashlightswitch=(ImageView)findViewById(R.id.flashlightswitch);
        exitflashlightbutton.setOnClickListener(new CloseFlashLightClickListener());
        soslightbutton.setOnClickListener(new SOSLightClickListener());
        flashlightswitch.setOnClickListener(new FlashLightSwitchListener());
		System.out.println("onCreate--->");

} 
    
    
    @Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	      //打开照相机  
   	 //改变背景图片  
    //   view.setBackgroundResource(R.drawable.flashlight_on);  
       //开始亮灯  
        flashlightOpen();
        notificationOpen();
	}


	private void flashlightOpen(){
        //获取照相机参数
	    camera = Camera.open(); 
        Parameters params = camera.getParameters();    
      //设置照相机参数，FLASH_MODE_TORCH  持续的亮灯，FLASH_MODE_ON 只闪一下  
        params.setFlashMode(Parameters.FLASH_MODE_TORCH);   
        camera.setParameters(params);  
        camera.startPreview();   
        //打开notification
    }
    private void flashlightClose(){
    		camera.stopPreview();   
        //关掉照相机  
        	camera.release();  
    }

class FlashLightSwitchListener implements OnClickListener{
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if(SOSisOpening){
			  unregisterReceiver(flashTimeReceiver);
			if(!Flashing){
				 Parameters params = camera.getParameters();    
			      //设置照相机参数，FLASH_MODE_TORCH  持续的亮灯，FLASH_MODE_ON 只闪一下  
			        params.setFlashMode(Parameters.FLASH_MODE_TORCH);   
			        camera.setParameters(params);  
			        camera.startPreview();   
		 		FlashlightOpening=true;
			}
	      	 timer.cancel();
	      	SOSisOpening = false;  
		}else if(FlashlightOpening){
			flashlightClose();
			FlashlightOpening=false;
		}else if(!FlashlightOpening){
	 		flashlightOpen();
	 		FlashlightOpening=true;
	 	}
	}
}
class SOSLightClickListener implements OnClickListener{
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if(FlashlightOpening){
			flashlightClose();
			System.out.println("FlashlightOpening---close--->");
			FlashlightOpening=false;
		}
		if(!SOSisOpening){
	        //注册broadcast,用以接收从handler发出的信息
		    flashTimeReceiver=new FlashLightBroadcast();
			registerReceiver(flashTimeReceiver,getFlashTimerIntentFileter());
			 //获取照相机参数
		    camera = Camera.open(); 
			System.out.println("SOSisOpening----->");
           timer = new Timer(true);
           timer.schedule(new TimerTask() {			
				@Override
				public void run() {
					// TODO Auto-generated method stub
					Message msg=new Message();
					msg.what=0x1222;
					//当主线程有多个handler的时候，就需要使用dispatch分发message。
					 myHandler.dispatchMessage(msg);
				}
			 },0, 200);
           SOSisOpening=true;
           //handler,用以更新摄像头闪关灯闪烁开关。
           myHandler =new Handler(){
				@Override
				public void handleMessage(Message msg) {
					// TODO Auto-generated method stub
					super.handleMessage(msg);
					if(msg.what==0x1222){
						if(Flashing==false){
					        Parameters params = camera.getParameters();    
					        //设置照相机参数，FLASH_MODE_TORCH  持续的亮灯，FLASH_MODE_ON 只闪一下  
					          params.setFlashMode(Parameters.FLASH_MODE_TORCH);   
					          camera.setParameters(params);  
					          camera.startPreview();   
				             Flashing=true;		            
						}else if(Flashing==true){
							 Flashing=false;
							 //flashTime用以记录闪关灯次数
							 ++flashTime;
				           //关掉亮灯 
							camera.stopPreview();
				             //当闪关灯闪了三次，发送广播，通知timer更改循环等待时间（由0.5秒改为1.5秒）
							  if(flashTime==3) {
								  try {Thread.sleep(600);
								} catch (InterruptedException e){
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								  Intent intent=new Intent();
								  intent.putExtra("flashTime_Three", flashTime);
								  intent.setAction("yuxiflashlight.time.com");
								  sendBroadcast(intent);
						             //当闪关灯共闪了6次，发送广播，通知timer更改循环等待时间（由三秒改为一秒，并把闪关灯次数重置为0）
							    }else if(flashTime==6) {
								  try {
									Thread.sleep(200);
								}catch (InterruptedException e){
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								  Intent intent=new Intent();
								  intent.putExtra("flashTime_Three", flashTime);
								  intent.setAction("yuxiflashlight.time.com");
								  sendBroadcast(intent);
							  }
						}
					}
				}	 
           };
       }else{ 
    	   //0626
     	unregisterReceiver(flashTimeReceiver);
    	System.out.println("stop flashlight");
    	timer.cancel();
    	 if(Flashing==true){
    		 Flashing=false;
    		 System.out.println("stop flashlight------Flashing");
    		camera.stopPreview();
    	 }
    	 camera.release();
    	 System.out.println("stop flashlight------release");
    	 flashTime=0;
      	SOSisOpening = false;  
	}
}
}
class CloseFlashLightClickListener implements OnClickListener {
	 @Override  
    public void onClick(View v) {
	  
	    if(SOSisOpening){
		 	timer.cancel();
		    flashlightClose();
	        unregisterReceiver(flashTimeReceiver);
	    }
	  	if(FlashlightOpening){
	    flashlightClose();
	  	}
        notificationCancel();
	     finish();  
    }  
}
private void notificationOpen(){
	Intent intent=new Intent(this,FlashLightActivity.class);
	//在创建PendingIntent的时候需要注意参数PendingIntent.FLAG_CANCEL_CURRENT
	//这个标志位用来指示：如果当前的Activity和PendingIntent中设置的intent一样，那么就先取消当前的Activity，用PendingIntent中指定的Activity取代之。
	PendingIntent pi=PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
	Notification notify=new Notification();
	notify.icon=R.drawable.flashlighticon;
	//点击后自动消失
	//notify.flags |= Notification.FLAG_AUTO_CANCEL;
	notify.when=System.currentTimeMillis();
	//notify.defaults=Notification.DEFAULT_ALL;
	notify.setLatestEventInfo(this, "手电筒", "正在开启",pi);
	NotificationManager notifyManager=(NotificationManager)getSystemService(NOTIFICATION_SERVICE);
	notifyManager.notify(0x0123, notify);
}
private void notificationCancel(){
	NotificationManager notifyManager=(NotificationManager)getSystemService(NOTIFICATION_SERVICE);
	notifyManager.cancel(0x0123);
}
@Override
public void onConfigurationChanged(Configuration newConfig) {
	// TODO Auto-generated method stub
	super.onConfigurationChanged(newConfig);
}

@Override  
public boolean onKeyDown(int keyCode, KeyEvent event) {  
    if(keyCode == KeyEvent.KEYCODE_BACK){       //按back键的时候 释放照相机  
    	if(SOSisOpening){
	 		timer.cancel();
		    flashlightClose();
	        unregisterReceiver(flashTimeReceiver);
    	}
	  	if(FlashlightOpening){
		    flashlightClose();
		  	}
         notificationCancel();
         finish();  
    }  
    return super.onKeyDown(keyCode, event);  
}  
//闪关灯次数broadcast过滤器
private IntentFilter getFlashTimerIntentFileter(){
	if(intentFilter_flashtime==null){
		intentFilter_flashtime=new IntentFilter();
		intentFilter_flashtime.addAction("yuxiflashlight.time.com");
	}
	return intentFilter_flashtime;
	}
//接收广播，通知运行哪个timer
class FlashLightBroadcast extends BroadcastReceiver{
	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		flashTime=intent.getIntExtra("flashTime_Three", 0);
		if(flashTime==3){	
		   timer.cancel();
	       timer = new Timer(true);
          timer.schedule(new TimerTask() {			
				@Override
				public void run() {
					// TODO Auto-generated method stub
					Message msg=new Message();
					msg.what=0x1222;
					 myHandler.dispatchMessage(msg);
				}
			},0, 600);
		}else if(flashTime==6){
			//当闪光灯次数为6时，重置该次数为0
			flashTime=0;
			timer.cancel();
           timer = new Timer(true);
           timer.schedule(new TimerTask() {			
				@Override
				public void run() {
					// TODO Auto-generated method stub
					Message msg=new Message();
					msg.what=0x1222;
					 myHandler.dispatchMessage(msg);
				}
			},0, 200);
		
		}
		
	}
	}
}
