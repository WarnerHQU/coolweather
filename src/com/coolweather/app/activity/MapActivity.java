package com.coolweather.app.activity;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.widget.Toast;

import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;
import com.coolweather.app.R;

public class MapActivity extends Activity
{
	private MapView mapView=null;
	
	private BaiduMap baiduMap;
	
	private String provider;
	
	/**
	 * ����isFirstLocate��Ϊ�˷�ֹ��ε���animateMapStatus()����
	 * ��Ϊ����ͼ�ƶ������ǵ�ǰ��λ��ֻ��Ҫ�ڳ����һ�ζ�λ��ʱ����þͿ�����
	 */
	private boolean isFirstLocate=true;
	
	private LocationManager locationManager;
	
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
		baiduMap=mapView.getMap();
		//����������ʾλ��
		baiduMap.setMyLocationEnabled(true);
		
		locationManager=(LocationManager) getSystemService(Context.LOCATION_SERVICE);
		//��ȡ���п��ܵ�λ���ṩ��
		List<String> providerList=locationManager.getProviders(true);
		
		if(providerList.contains(LocationManager.NETWORK_PROVIDER))
		{
			provider=LocationManager.NETWORK_PROVIDER;
			
		}
		else if(providerList.contains(LocationManager.PASSIVE_PROVIDER))
		{
			provider=LocationManager.PASSIVE_PROVIDER;
		}
		else if(providerList.contains(LocationManager.GPS_PROVIDER))
		{
			provider=LocationManager.GPS_PROVIDER;
		}
		
		else
		{
			//��ǰû�п���ʹ�õ�λ���ṩ��������Toast��ʾ�û�
			Toast.makeText(this, "No location provider to use", 
					Toast.LENGTH_SHORT).show();
		}
		
		Location location=locationManager.getLastKnownLocation(provider);
		
		if(location!=null)
		{
			
			navigateTo(location);
		}
		locationManager.requestLocationUpdates(provider, 10, 1, locationListener);
		
		
	}
	@Override
	protected void onDestroy()
	{
		// TODO Auto-generated method stub
		super.onDestroy();
		//��activityִ��onDestroyʱִ��mMapView.onDestroy()��ʵ�ֵ�ͼ�������ڹ��� 
		mapView.onDestroy();
		
		baiduMap.setMyLocationEnabled(false);
		
		if(locationManager!=null)
		{
			locationManager.removeUpdates(locationListener);
		}
		
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
	
	LocationListener locationListener=new LocationListener()
												{

													@Override
													public void onLocationChanged(
															Location location)
													{
														// TODO Auto-generated method stub
														if(location!=null)
															navigateTo(location);
													}

													@Override
													public void onProviderDisabled(
															String provider)
													{
														// TODO Auto-generated method stub
														
													}

													@Override
													public void onProviderEnabled(
															String provider)
													{
														// TODO Auto-generated method stub
														
													}

													@Override
													public void onStatusChanged(
															String provider,
															int status,
															Bundle extras)
													{
														// TODO Auto-generated method stub
														
													}
													
												};
	private  void navigateTo(Location location)
	{
		if(isFirstLocate)
		{
			//���ö�λγ����
			LatLng ll=new LatLng(location.getLatitude(),location.getLongitude());
			MapStatusUpdate update=MapStatusUpdateFactory.newLatLng(ll);
			baiduMap.animateMapStatus(update);
			//���÷Ŵ����
			update=MapStatusUpdateFactory.zoomTo(16f);
			baiduMap.animateMapStatus(update);
			isFirstLocate=false;
		}
		
		MyLocationData.Builder locationBuilder=new MyLocationData.Builder();
		locationBuilder.latitude(location.getLatitude());
		locationBuilder.longitude(location.getLongitude());
		MyLocationData locationData=locationBuilder.build();
		baiduMap.setMyLocationData(locationData);
		
		String message="Latitude is "+location.getLatitude()+
				       "\n"+"Longitude is "+location.getLongitude();
		Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
	}
	@Override
	public void onBackPressed()
	{
		// TODO Auto-generated method stub
		//super.onBackPressed();
		/**
		 * �����ؼ��Ĵ������ص�weatherActivity
		 * ���Է���ͨ����λ��õĵص���Ϣ��ֱ�Ӵ���WeatherActivity���д�������ݵ���ʾ
		 * 
		 */
		if(getIntent().getBooleanExtra("from_weather_to_map",false))
		{
			Intent intent=new Intent(this,WeatherActivity.class);
			intent.putExtra("me", true);
			startActivity(intent);
		}
	}
	
}
