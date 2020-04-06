package com.example.roadtripapp;

public class DistanceCheck {
    public Double check_distance(Double LatCurr, Double LongCurr, Double LatDest, Double LongDest){
        if ((LatCurr != null) && (LongCurr != null)) {
            // The math module contains a function
            // named toRadians which converts from
            // degrees to radians.
            Double lon1 = Math.toRadians(LongCurr);
            Double lon2 = Math.toRadians(LongDest);
            Double lat1 = Math.toRadians(LatCurr);
            Double lat2 = Math.toRadians(LatDest);

            // Haversine formula
            double dlon = lon2 - lon1;
            double dlat = lat2 - lat1;
            double a = Math.pow(Math.sin(dlat / 2), 2)
                    + Math.cos(lat1) * Math.cos(lat2)
                    * Math.pow(Math.sin(dlon / 2), 2);

            double c = 2 * Math.asin(Math.sqrt(a));

            // Radius of earth in kilometers. Use 3956
            // for miles
            double r = 6371;

            // calculate the result
            return (c * r);
        }
        else{
            return 10000.0;
        }
    }
}
