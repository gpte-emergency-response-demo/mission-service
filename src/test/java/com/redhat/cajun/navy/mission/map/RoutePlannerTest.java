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

    // "responderStartLat":"34.22543","responderStartLong":"-77.89744","incidentLat":"-77.9210864462409","incidentLong":"34.21143217819422","destinationLat":"34.05830","destinationLong":"-77.88850"

    @Test
    public void asyncMapboxDirectionsRequestTest() {
        RoutePlanner p = new RoutePlanner(MAP_TOKEN);
        Location origin = new Location(31.2254,-77.8974);
        Location wPoint = new Location(34.2114,-77.9210);
        Location dest = new Location(31.0583, -77.8880 );
        MissionRoute r = p.getMapboxDirectionsRequest(origin, dest, wPoint);
    }





}