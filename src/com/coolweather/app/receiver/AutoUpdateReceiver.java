package com.coolweather.app.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.coolweather.app.service.AutoUpdateService;

public class AutoUpdateReceiver extends BroadcastReceiver
{

	@Override
	public void onReceive(Context arg0, Intent arg1)
	{
		// TODO Auto-generated method stub
		//又调用服务，如此形成循环
		Intent i=new Intent(arg0,AutoUpdateService.class);
		
		arg0.startService(i);
	}

}
