package com.guo.FlashLightFinal;


import java.util.Timer;
import java.util.TimerTask;

import com.guo.service.FlashLightService;

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
	private Button exitflashlightbutton;
	private ImageView soslightbutton;
	private Button flashlightswitch;
	private boolean FlashlightOpening=true;
	private String FlashlightOpen="FlashlightOpen";
	private String FlashlightClose="FlashlightClose";
	private String SosLightOpen="SosLightOpen";
	private String SosLightClose="SosLightClose";
	private boolean SOSisOpening=false;
	private long firstTime;
	private long secondTime;
	private long spaceTime; 
	private long firstTime_f;
	private long secondTime_f;
	private long spaceTime_f; 

    @Override  
    protected void onCreate(Bundle savedInstanceState) {  
        super.onCreate(savedInstanceState);  
      //设置全屏 
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN); 
     //去掉标题栏   
        requestWindowFeature(Window.FEATURE_NO_TITLE);  
        setContentView(R.layout.activity_flash_light);  
        exitflashlightbutton=(Button)findViewById(R.id.exitflashlightbutton);
        soslightbutton=(ImageView)findViewById(R.id.soslightbutton);
        flashlightswitch=(Button)findViewById(R.id.flashlightswitch);
        exitflashlightbutton.setOnClickListener(new CloseFlashLightClickListener());
        soslightbutton.setOnClickListener(new SOSLightClickListener());
        flashlightswitch.setOnClickListener(new FlashLightSwitchListener());
		System.out.println("onCreate--->");
        notificationOpen();
        //开始亮灯  
        flashlightOpen();
} 
    @Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	      //打开照相机  
   	 //改变背景图片  
    //   view.setBackgroundResource(R.drawable.flashlight_on);  

	}
    
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}
	private void flashlightOpen(){
		Intent intent=new Intent(FlashLightActivity.this,FlashLightService.class);
		intent.putExtra("Flashlight", FlashlightOpen);
		startService(intent);
    }
    private void flashlightClose(){
		Intent intent=new Intent(FlashLightActivity.this,FlashLightService.class);
		intent.putExtra("Flashlight", FlashlightClose);
		startService(intent);   
    }
    private void soslightOpen(){
		Intent intent=new Intent(FlashLightActivity.this,FlashLightService.class);
		intent.putExtra("Flashlight", SosLightOpen);
		startService(intent);   
    }
    private void soslightClose(){
		Intent intent=new Intent(FlashLightActivity.this,FlashLightService.class);
		intent.putExtra("Flashlight", SosLightClose);
		startService(intent);   
    }

class FlashLightSwitchListener implements OnClickListener{
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		firstTime_f = System.currentTimeMillis();
		spaceTime_f = firstTime_f - secondTime_f;
		secondTime_f = firstTime_f;
		if(spaceTime_f > 1000) {
		if(FlashlightOpening){
			flashlightClose();
			notificationCancel();
			FlashlightOpening=false;
		}else{
			flashlightOpen();
			notificationOpen();
			FlashlightOpening=true;
		}
	}
	}
}
class SOSLightClickListener implements OnClickListener{
	@Override
	public void onClick(View v) {
		firstTime = System.currentTimeMillis();
		System.out.println(firstTime);
		spaceTime = firstTime - secondTime;
		secondTime = firstTime;
		if(spaceTime > 1000) {
		if(SOSisOpening){
			soslightClose();
			notificationCancel();
			SOSisOpening=false;
		}else{
			soslightOpen();
			notificationOpen();
			SOSisOpening=true;
		}
		}
}
}

class CloseFlashLightClickListener implements OnClickListener {
	 @Override  
    public void onClick(View v) {
	    	if(SOSisOpening){
				soslightClose();
				SOSisOpening=false;
			}else if(FlashlightOpening){
				flashlightClose();
				FlashlightOpening=false;
			}
			notificationCancel();
	     finish();  
		overridePendingTransition(R.anim.zoomout,R.anim.zoomin);
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
			soslightClose();
			SOSisOpening=false;
		}else if(FlashlightOpening){
			flashlightClose();
			FlashlightOpening=false;
		}
         notificationCancel();
         finish();  
 		overridePendingTransition(R.anim.zoomout,R.anim.zoomin);
    }  
    return super.onKeyDown(keyCode, event);  
}  

}
