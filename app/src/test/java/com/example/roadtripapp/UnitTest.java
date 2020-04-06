package com.example.roadtripapp;

import org.junit.Test;
import static org.junit.Assert.*;
import com.example.roadtripapp.DistanceCheck;


import java.io.File;
import java.util.Date;

public class UnitTest {

    @Test
    public void DistanceCheckWorking() throws Exception {
        DistanceCheck test = new DistanceCheck();
        Double LatCurr = null;
        Double LongCurr = 100.0;
        Double LatDest = 100.0;
        Double LongDest = 100.0;
        Double ret = test.check_distance(LatCurr, LongCurr, LatDest, LongDest);
        assertEquals(ret, (Double)10000.0);

        //DistanceCheck test = new DistanceCheck();
         LatCurr = 90.0;
         LongCurr = 90.0;
         LatDest = 90.0;
         LongDest = 90.0;
         ret = test.check_distance(LatCurr, LongCurr, LatDest, LongDest);
        assertEquals(ret, (Double)0.0);

        //DistanceCheck test = new DistanceCheck();
        LatCurr = 90.0;
        LongCurr = 90.0;
        LatDest = 80.0;
        LongDest = 80.0;
        ret = test.check_distance(LatCurr, LongCurr, LatDest, LongDest);
        long ret2 = Math.round(ret) -1;
        assertEquals(ret2,(long) 1111.0);

    }
}