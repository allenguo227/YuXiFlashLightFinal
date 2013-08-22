package com.guo.service;

import java.util.Timer;
import java.util.TimerTask;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.widget.Button;
import android.widget.ImageView;

public class FlashLightService extends Service{
    private Camera camera;  
    private int flashTime=0;
    private String msg=null;
	private boolean FlashlightOpening=false;
	private boolean SOSisOpening=false;
	private boolean Flashing=false;
    private Timer timer;
    private  Handler myHandler;
	private IntentFilter intentFilter_flashtime;
	private BroadcastReceiver flashTimeReceiver;
	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onStart(Intent intent, int startId) {
		// TODO Auto-generated method stub
		super.onStart(intent, startId);
		msg=intent.getStringExtra("Flashlight");
		//通过intent传入的值，判断执行哪个动作
		if(msg.equals("FlashlightOpen")){
			//System.out.println("闪光灯已开启");
			 flashlightOpen();
		}else if(msg.equals("FlashlightClose")){
			//System.out.println("闪关灯已关闭");
			flashlightClose();
		}else if(msg.equals("SosLightOpen")){
			//System.out.println("求救信号已开启");
			soslightOpen();
		}else if(msg.equals("SosLightClose")){
			//System.out.println("求救信号已关闭");
			soslightClose();
		}
	}
	
	private void flashlightOpen(){
		//如果求救信号正在开启，先关闭求救信号，再开启手电筒
		soslightClose();
        //获取照相机参数
	    camera = Camera.open(); 
        Parameters params = camera.getParameters();    
      //设置照相机参数，FLASH_MODE_TORCH  持续的亮灯，FLASH_MODE_ON 只闪一下  
        params.setFlashMode(Parameters.FLASH_MODE_TORCH);   
        camera.setParameters(params);  
        camera.startPreview();
        FlashlightOpening=true;
    }
    private void flashlightClose(){
    	if(FlashlightOpening){
    		camera.stopPreview();
    		camera.setPreviewCallback(null);
        //关掉照相机  
        	camera.release();  
            FlashlightOpening=false;
			System.out.println("手电筒已关闭");
    	}
    }
    
	@SuppressLint("HandlerLeak")
	private void soslightOpen(){
		//如果手电筒正在开启的话，先关闭手电筒，再开启求救信号
			flashlightClose();
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
        //打开SOSisOpening
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
								  //通过intent通知广播开启
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
    }
    private void soslightClose(){
    	if(SOSisOpening){
    		unregisterReceiver(flashTimeReceiver);
        	System.out.println("stop flashlight");
        	timer.cancel();
    		camera.stopPreview();
    		camera.setPreviewCallback(null);
        //关掉照相机  
        	camera.release();  
        	SOSisOpening=false;
			System.out.println("求救信号已关闭");
    	}
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
  //闪关灯次数broadcast过滤器
    private IntentFilter getFlashTimerIntentFileter(){
    	if(intentFilter_flashtime==null){
    		intentFilter_flashtime=new IntentFilter();
    		intentFilter_flashtime.addAction("yuxiflashlight.time.com");
    	}
    	return intentFilter_flashtime;
    	}

}
