package com.redhat.cajun.navy.mission.data;

import org.junit.Test;

import static org.junit.Assert.*;

public class LocationTest {

    @Test
    public void LatLongEquals() {
        Location origin = new Location(31.2254,-77.8974);
        Location wPoint = new Location(34.2114,-77.9210);
        Location dest = new Location(31.2254, -77.8974 );

        assertEquals(true, origin.equals(dest));

    }

    @Test
    public void LatLongNotEquals() {
        Location origin = new Location(31.2254,-77.8974);
        Location wPoint = new Location(34.2114,-77.9210);
        Location dest = new Location(31.2254, -77.8974 );

        assertEquals( false, origin.equals(wPoint));

    }


}