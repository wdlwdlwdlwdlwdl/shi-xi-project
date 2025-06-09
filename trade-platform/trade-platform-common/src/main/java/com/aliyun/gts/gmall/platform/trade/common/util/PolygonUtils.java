package com.aliyun.gts.gmall.platform.trade.common.util;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.List;

/**
 * 说明： TODO
 *
 * @author yangl
 * @version 1.0
 * @date 2025/1/23 16:53
 */
public class PolygonUtils {

    public static boolean isPointInPolygon(double x, double y, String jsonString){
        List<double[]> polygon = getDoubles(jsonString);
        int n = polygon.size();
        boolean inside = false;
        double[] p1 = polygon.get(0);

        for (int i = 1; i <= n; i++) {
            double[] p2 = polygon.get(i % n); // Wrap back to the first point
            if (y > Math.min(p1[1], p2[1])) {
                if (y <= Math.max(p1[1], p2[1])) {
                    if (x <= Math.max(p1[0], p2[0])) {
                        if (p1[1] != p2[1]) {
                            double xinters = (y - p1[1]) * (p2[0] - p1[0]) / (p2[1] - p1[1]) + p1[0];
                            if (p1[0] == p2[0] || x <= xinters) {
                                inside = !inside;
                            }
                        }
                    }
                }
            }
            p1 = p2;
        }
        return inside;
    }

    private static List<double[]> getDoubles(String jsonString) {
        ObjectMapper objectMapper = new ObjectMapper();
        List<List<Double>> tempList = new ArrayList<>();
        try {
            tempList = objectMapper.readValue(jsonString, List.class);
        }catch (Exception e){

        }
        List<double[]> polygon = new ArrayList<>();
        for (List<Double> innerList : tempList) {
            double[] array = new double[innerList.size()];
            for (int i = 0; i < innerList.size(); i++) {
                array[i] = innerList.get(i);
            }
            polygon.add(array);
        }
        return polygon;
    }

}
