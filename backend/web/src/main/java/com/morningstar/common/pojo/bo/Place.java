package com.morningstar.common.pojo.bo;

import lombok.Getter;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Random;

/**
 * 三国时期地名
 * 参考地图: <a href="https://upload.wikimedia.org/wikipedia/commons/0/07/三国行政区划%28简%29.png">三国行政区划</a>
 */
@Getter
public enum Place {
    // 司州
    洛阳(34.6181, 112.4540, "河南-洛阳"),
    // 豫州
    颍川(34.0357, 113.8523, "河南-许昌"),
    // 冀州
    邺城(36.3348, 114.6197, "河北-邯郸-临漳"),
    // 兖州
    济南(36.6518, 117.1201, "山东-济南"), 曲阜(35.4747, 117.2454, "山东-曲阜"), 泰山(36.1999, 117.0884, "山东-泰安"),
    临沂(35.1046, 118.3565, "山东-临沂"), 东平(35.9371, 116.4702, "山东-东平"), 陈留(34.7973, 114.3073, "河南-开封"),
    // 徐州
    彭城(34.2044, 117.2858, "江苏-徐州"), 沛县(34.7217, 116.9375, "江苏-徐州-沛县"), 下邳(34.3352, 118.0125, "江苏-邳州"),
    // 青州
    临淄(36.7994, 118.3891, "山东-淄博-临淄"),
    // 并州
    上党(36.0531, 113.0513, "山西-长治-上党"),
    // 幽州
    北平(39.9042, 116.4074, "北京"),
    // 雍州
    武都(33.3919, 104.9267, "甘肃-陇南-武都"), 阴平(32.8028, 105.4171, "甘肃-陇南-文县"), 天水(34.5808, 105.7249, "甘肃-天水"),
    长安(34.2658, 108.9541, "陕西-西安"), 扶风(34.3753, 107.9002, "陕西-宝鸡-扶风"), 咸阳(34.3293, 108.7093, "陕西-咸阳"),
    // 凉州
    敦煌(40.1422, 94.6620, "甘肃-敦煌"), 酒泉(39.7325, 98.4939, "甘肃-酒泉"),
    // 益州
    成都(30.5723, 104.0665, "四川-成都"), 汉中(33.0676, 107.0238, "陕西-汉中"), 江州(29.5657, 106.5512, "重庆"),
    // 荆州
    江陵(30.0418, 112.4247, "湖北-荆州-江陵"), 长沙(28.2278, 112.9389, "湖南-长沙"), 零陵(26.2225, 111.6311, "湖南-永州-零陵"),
    襄阳(32.0090, 112.1226, "河北-襄阳"), 南阳(32.9907, 112.5285, "河南-南阳"), 武昌(30.5539, 114.3160, "湖北-武汉-武昌"),
    // 扬州
    建业(32.0584, 118.7965, "江苏-南京"), 吴郡(31.2983, 120.5832, "江苏-苏州"), 会稽(29.9958, 120.5861, "浙江-绍兴"),
    丹阳(32.0094, 119.6069, "江苏-镇江-丹阳"), 豫章(28.6850, 115.8992, "江西-南昌-东湖"), 庐江(31.3247, 117.0930, "安徽-合肥-庐江"),
    广陵(32.3946, 119.4316, "江苏-扬州-广陵"), 寿春(32.1534, 116.7450, "安徽-淮南-寿县"),
    // 交州
    苍梧(23.7517, 111.1139, "广西-梧州-苍梧"),
    ;

    private final Double latitude; // 纬度
    private final Double longitude; // 经度
    private final String currentLocation;

    Place(Double latitude, Double longitude, String currentLocation) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.currentLocation = currentLocation;
    }

    public static Place getRandomPlace() {
        Random random = new Random();
        Place[] places = Place.values();
        return places[random.nextInt(places.length)];
    }

    public static Place getNearestPlace(double latitude, double longitude) {
        return Arrays.stream(Place.values()).sequential()
                .min(Comparator.comparingDouble(p -> Math.pow(p.getLatitude() - latitude, 2) + Math.pow(p.getLongitude() - longitude, 2)))
                .orElse(null);
    }
}
