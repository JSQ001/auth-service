package com.hand.hcf.app.mdata.location.enums;

import java.util.Iterator;
import java.util.LinkedList;

/**
 * Created by vance on 2017/3/7.
 */
public class LocationType {

    public static LinkedList locationStack = new LinkedList();

    static {
        locationStack.add("COUNTRY");
        locationStack.add("STATE");
        locationStack.add("CITY");
        locationStack.add("REGION");
    }

    public static String getNextLocationType(String currentType) {
        Iterator iterator = locationStack.iterator();
        String nextLocationType = "";
        while (iterator.hasNext()) {
            String it = (String) iterator.next();
            if (it.equals(currentType)) {
                nextLocationType = (String) iterator.next();
                return nextLocationType;
            }
        }
        return null;
    }

}
