package com.hfut.navigation.config;

import com.baidu.location.LocationClientOption;


public class LocationConfig {
    private static final int REFRESH_TIME = 1000 * 2;//2s刷新一次
    private static final String COORDINATE_TYPE = "bd09ll";// 设置坐标类型,此坐标类型可无偏差的叠加在百度地图上
    private static final boolean OPEN_GPS = true;// 打开GPS
    private static final boolean REFRESH_GPS = true;// 设置是否当GPS有效时按照1S/1次频率输出GPS结果

    public static LocationClientOption getLocationConfig() {
        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(OPEN_GPS); // 是否打开gps
        option.setCoorType(COORDINATE_TYPE); // 设置坐标类型
        option.setScanSpan(REFRESH_TIME);//定位刷新频率
        option.setLocationNotify(REFRESH_GPS);//设置是否当GPS有效时按照1S/1次频率输出GPS结果
        return option;
    }

}
