package com.redhat.cajun.navy.mission.map;

import com.redhat.cajun.navy.mission.data.Location;
import com.redhat.cajun.navy.mission.data.MissionRoute;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.FileReader;
import java.util.Properties;

import static org.junit.Assert.*;

public class RoutePlannerTest {


    private String MAP_TOKEN = null;

    @Before
    public void setUp() throws Exception {
        MAP_TOKEN = getMapToken();
    }


    private String getMapToken() throws Exception{
        Properties p = new Properties();
        p.load(new FileReader(new File("src/main/resources/local-app-config.properties")));
        if(p.getProperty("map.token") != null)
            return p.getProperty("map.token");
        else throw new Exception("Map token not found");


    }

    @Test
    public void asyncMapboxDirectionsRequestHasSteps() throws Exception{
        RoutePlanner p = new RoutePlanner(MAP_TOKEN);
        Location origin = new Location(29.7890,-95.6332);
        Location wPoint = new Location(29.7476, -95.3691);
        Location dest = new Location( 29.7576,-95.3591);
        MissionRoute r = p.getMapboxDirectionsRequest(origin, dest, wPoint);
        assertEquals(r.getSteps().size(), 23);

    }

    @Test
    public void asyncMapboxDirectionsRequestHasNoSteps() {
        RoutePlanner p = new RoutePlanner(MAP_TOKEN);
        Location origin = new Location(31.12345,-70.98765);
        Location wPoint = origin;
        Location dest = origin;
        MissionRoute r = p.getMapboxDirectionsRequest(origin, dest, wPoint);
        assertEquals(r.getSteps().size(), 4);
    }


}