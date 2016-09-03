package com.coolweather.app.activity;

import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
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
	
	
	/**
	 * ����������
	 */
	private GeoCoder mSearch =null;
	
	/**
	 * �洢��λ��λ�õص�
	 */
	private String myPosition=null;
	
	
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
		

		// ������ʼ��
		
		mSearch = GeoCoder.newInstance();
		mSearch.setOnGetGeoCodeResultListener(new OnGetGeoCoderResultListener() {
			// �������
			@Override
			public void onGetGeoCodeResult(GeoCodeResult result)
			{
				if (result == null
						|| result.error != SearchResult.ERRORNO.NO_ERROR)
				{
					Toast.makeText(MapActivity.this, "��Ǹ��δ���ҵ����",
							Toast.LENGTH_LONG).show();
					return;
				}
				baiduMap.setMapStatus(MapStatusUpdateFactory.newLatLng(result
						.getLocation()));
				// ��λ
				String strInfo = String.format("γ�ȣ�%f ���ȣ�%f",
						result.getLocation().latitude,
						result.getLocation().longitude);
				Toast.makeText(MapActivity.this, strInfo, Toast.LENGTH_LONG)
						.show();
				// result����������Ľ�� ����-->����

			}

			// �������
			@Override
			public void onGetReverseGeoCodeResult(ReverseGeoCodeResult result)
			{
				if (result == null
						|| result.error != SearchResult.ERRORNO.NO_ERROR)
				{
					Toast.makeText(MapActivity.this, "��Ǹ��δ���ҵ����",
							Toast.LENGTH_LONG).show();
					return;
				}
				baiduMap.setMapStatus(MapStatusUpdateFactory.newLatLng(result
						.getLocation()));
				// ��λ
				//Toast.makeText(MapActivity.this, result.getAddress(),
				//		Toast.LENGTH_LONG).show();
				// result���淭�������Ľ�� ����-->����
				myPosition=result.getAddress();
				showAlertDialog(myPosition+"?");
			}

			

			

		});
		
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
			
			if (mSearch != null)
			{
				mSearch.reverseGeoCode(new ReverseGeoCodeOption().location(ll));
			}
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
		CallBackMess();
	}
	
	
	
	private void showAlertDialog(String tips)
	{
		AlertDialog.Builder dialog=new AlertDialog.Builder(MapActivity.this);
		dialog.setTitle("This is a location dialog");
		dialog.setMessage(tips);
		dialog.setCancelable(false);
		dialog.setPositiveButton("ȷ��", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which)
			{
				CallBackMess();
				
			}
		});
		dialog.setNegativeButton("ȡ��", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which)
			{
				// TODO Auto-generated method stub
				
			}
		});
		dialog.show();
		
	}
	
	public void CallBackMess()
	{
		if (getIntent().getBooleanExtra("from_weather_to_map", false))
		{
			Intent intent = new Intent(this, WeatherActivity.class);
			intent.putExtra("self_position", myPosition);
			startActivity(intent);
		}
	}
}
