package com.hfut.navigation.frame.main;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Point;
import android.support.v7.app.AlertDialog;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationConfiguration.LocationMode;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;
import com.hfut.navigation.R;
import com.hfut.navigation.base.BaseActivity;
import com.hfut.navigation.config.LocationConfig;
import com.hfut.navigation.config.LocationInfo;
import com.hfut.navigation.listener.MyOrientationListener;
import com.hfut.navigation.util.ConstantUtil;
import com.hfut.navigation.util.ToolUtil;

import butterknife.BindView;
import butterknife.OnClick;
import butterknife.OnLongClick;


public class App_Main extends BaseActivity implements BaiduMap.OnMapLoadedCallback, BDLocationListener, BaiduMap.OnMapTouchListener {
    @BindView(R.id.rv_Title)
    RelativeLayout rv_Title;
    @BindView(R.id.tv_SearchContent)
    TextView tv_SearchContent;
    @BindView(R.id.tv_showInfo)
    TextView tv_showInfo;
    @BindView(R.id.mMapView)
    MapView mMapView;
    @BindView(R.id.iv_ModeState)
    ImageView iv_ModeState;


    private BaiduMap mBaiduMap;
    private boolean isFirstLoc = true;//是否是第一次定位
    private LocationClient mLocClient;//定位服务
    private LocationMode mCurrentMode = LocationMode.NORMAL;//当前定位模式
    private MyOrientationListener myOrientationListener;//陀螺仪监听器
    private double mCurrentLatitude, mCurrentLongitude;//经纬度
    private float mCurrentAccuracy;//精确度
    private float mXDirection;//方向
    private MapStatus mMapStatus;

    @Override
    protected void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    protected void onDestroy() {
        // 关闭定位服务
        mLocClient.stop();
        // 关闭定位图层
        mBaiduMap.setMyLocationEnabled(false);
        //关闭陀螺仪监听器
        myOrientationListener.stop();
        //销毁地图
        mMapView.onDestroy();
        mMapView = null;
        super.onDestroy();
    }

    @Override
    protected void onStart() {
        // 开启方向传感器
        myOrientationListener.start();
        super.onStart();
    }

    @Override
    protected void init() {

        initOrientation();
        initMap();
        initLocation();
        mMapStatus = new MapStatus.Builder().build();
        mLocClient.start();//启动定位服务
    }

    /**
     * 初始化陀螺仪监听器
     */
    private void initOrientation() {
        myOrientationListener = new MyOrientationListener(this);
        myOrientationListener.setOnOrientationListener(new MyOrientationListener.OnOrientationListener() {
            @Override
            public void onOrientationChanged(float x) {
                mXDirection = x;
                // 构造定位数据
                MyLocationData locData = new MyLocationData.Builder()
                        .accuracy(mCurrentAccuracy)
                        // 此处设置开发者获取到的方向信息，顺时针0-360
                        .direction(mXDirection)
                        .latitude(mCurrentLatitude)
                        .longitude(mCurrentLongitude).build();
                // 设置定位数据
                mBaiduMap.setMyLocationData(locData);
                mBaiduMap.setMyLocationConfigeration(new MyLocationConfiguration(
                        mCurrentMode, true, null));

            }
        });
    }

    /**
     * 初始化定位服务
     */
    private void initLocation() {
        mLocClient = new LocationClient(getApplicationContext()); // 定位初始化
        mLocClient.registerLocationListener(this);
        mLocClient.setLocOption(LocationConfig.getLocationConfig());
    }

    /**
     * 初始化地图
     */
    private void initMap() {
        mBaiduMap = mMapView.getMap();
        mBaiduMap.setMyLocationEnabled(true);// 开启定位图层
        mBaiduMap.setOnMapTouchListener(this);
        mBaiduMap.setCompassPosition(new Point(ToolUtil.dp2px(this, 30), ToolUtil.dp2px(this, 80)));//左上角为原点,此点为icon的中心点
        mBaiduMap.setViewPadding(ToolUtil.dp2px(this, 60), 0, 0, ToolUtil.dp2px(this, 15));//icon的左、上、右、下边距
    }


    @OnLongClick(R.id.iv_ModeState)
    public boolean onLongClick(View v) {
        switch (v.getId()) {
            case R.id.iv_ModeState:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("选择地图模式");
                String[] modes = {"普通地图", "卫星地图"};
                builder.setItems(modes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mBaiduMap.setMapType(which + 1);
                    }
                });
                builder.show();
                break;
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case 0:
                if (data != null) //用于接受搜索输入的地点
                    tv_SearchContent.setText(data.getStringExtra(ConstantUtil.CODE_SearchContent));
                break;
        }
    }

    @OnClick({R.id.rv_Title, R.id.iv_ModeState})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rv_Title:
                startActivityForResult(new Intent(this, App_Search.class), 0);
                break;
            case R.id.iv_ModeState:
                float r = mMapStatus.rotate;
                switch (mCurrentMode) {
                    case NORMAL://正常模式，点击变为跟随
                        mCurrentMode = LocationMode.FOLLOWING;
                        iv_ModeState.setImageResource(R.drawable.icon_mode_normal);

                        mMapStatus = new MapStatus.Builder().rotate(r).build();
                        mMapStatus = new MapStatus.Builder().overlook(45).build();

                        MapStatusUpdate mMapStatusUpdate = MapStatusUpdateFactory.newMapStatus(mMapStatus);
                        mBaiduMap.animateMapStatus(mMapStatusUpdate);
                        break;
                    case FOLLOWING://跟随模式，点击变为罗盘
                        mCurrentMode = LocationMode.COMPASS;
                        iv_ModeState.setImageResource(R.drawable.icon_mode_compass);
                        break;
                    case COMPASS://罗盘模式，点击变为跟随
                        mCurrentMode = LocationMode.FOLLOWING;
                        iv_ModeState.setImageResource(R.drawable.icon_mode_normal);

                        mMapStatus = new MapStatus.Builder().rotate(r).build();
                        mMapStatus = new MapStatus.Builder().overlook(45).build();

                        MapStatusUpdate mMapStatusUpdate2 = MapStatusUpdateFactory.newMapStatus(mMapStatus);
                        mBaiduMap.animateMapStatus(mMapStatusUpdate2);
                        break;
                }
                mBaiduMap
                        .setMyLocationConfigeration(new MyLocationConfiguration(
                                mCurrentMode, true, null));
                mLocClient.start();
                LatLng ll = new LatLng(mCurrentLatitude, mCurrentLongitude);
                MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(ll);
                mBaiduMap.animateMapStatus(u);
                break;
        }
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.app_map;
    }

    @Override
    public void onMapLoaded() {
//        mMapView.getMap().setMapStatusLimits(new LatLngBounds.Builder().include(northeast).include(southwest).build());
    }

    @Override
    public void onReceiveLocation(BDLocation location) {

        // map view 销毁后不在处理新接收的位置
        if (location == null || mMapView == null) {
            return;
        }
        tv_showInfo.setText(LocationInfo.getLocationInfo(location));
        setLocationToMap(location);


    }

    private void setLocationToMap(BDLocation location) {

        mCurrentLatitude = location.getLatitude();
        mCurrentLongitude = location.getLongitude();

        // 构造定位数据
        MyLocationData locData = new MyLocationData.Builder()
                .accuracy(location.getRadius())
                // 此处设置开发者获取到的方向信息，顺时针0-360
                .direction(mXDirection).latitude(location.getLatitude())
                .longitude(location.getLongitude()).build();
        // 设置定位数据
        mBaiduMap.setMyLocationData(locData);

        mCurrentAccuracy = location.getRadius();
        mCurrentLatitude = location.getLatitude();
        mCurrentLongitude = location.getLongitude();

        MyLocationConfiguration config = new MyLocationConfiguration(
                mCurrentMode, true, null);
        mBaiduMap.setMyLocationConfigeration(config);

        // 第一次定位时，将地图位置移动到当前位置
        if (isFirstLoc) {
            isFirstLoc = false;
            LatLng ll = new LatLng(location.getLatitude(),
                    location.getLongitude());
            MapStatus.Builder builder = new MapStatus.Builder();
            builder.target(ll).zoom(18.0f);
            mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
        }
    }

    @Override
    public void onConnectHotSpotMessage(String s, int i) {

    }

    @Override
    public void onTouch(MotionEvent motionEvent) {
        mCurrentMode = LocationMode.NORMAL;
        iv_ModeState.setImageResource(R.drawable.icon_get_location);
        mBaiduMap
                .setMyLocationConfigeration(new MyLocationConfiguration(
                        mCurrentMode, true, null));
    }
}
