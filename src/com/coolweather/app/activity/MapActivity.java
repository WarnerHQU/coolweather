package com.coolweather.app.activity;

import android.app.Activity;
import android.os.Bundle;

import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.MapView;
import com.coolweather.app.R;

public class MapActivity extends Activity
{
	private MapView mapView=null;
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		 //��ʹ��SDK�����֮ǰ��ʼ��context��Ϣ������ApplicationContext  
        //ע��÷���Ҫ��setContentView����֮ǰʵ��  
		SDKInitializer.initialize(getApplicationContext());
		setContentView(R.layout.map_layout);
		mapView=(MapView) findViewById(R.id.bmapView);
		//Snackbar.make(mapView, "Snack Bar Started", Snackbar.LENGTH_SHORT).show();
	}
	@Override
	protected void onDestroy()
	{
		// TODO Auto-generated method stub
		super.onDestroy();
		//��activityִ��onDestroyʱִ��mMapView.onDestroy()��ʵ�ֵ�ͼ�������ڹ��� 
		mapView.onDestroy();
	}
	@Override
	protected void onPause()
	{
		// TODO Auto-generated method stub
		super.onPause();
		//��activityִ��onPauseʱִ��mMapView. onPause ()��ʵ�ֵ�ͼ�������ڹ��� 
		mapView.onPause();
		
	}
	@Override
	protected void onResume()
	{
		// TODO Auto-generated method stub
		super.onResume();
		//��activityִ��onResumeʱִ��mMapView. onResume ()��ʵ�ֵ�ͼ�������ڹ��� 
		mapView.onResume();
	}
	
	
}
