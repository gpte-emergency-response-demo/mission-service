package com.redhat.cajun.navy.mission.map;

import com.mapbox.api.directions.v5.DirectionsCriteria;
import com.mapbox.api.directions.v5.MapboxDirections;
import com.mapbox.api.directions.v5.models.DirectionsResponse;
import com.mapbox.api.directions.v5.models.DirectionsRoute;
import com.mapbox.api.directions.v5.models.LegStep;
import com.mapbox.api.directions.v5.models.RouteLeg;
import com.mapbox.geojson.Point;
import com.redhat.cajun.navy.mission.data.Location;
import com.redhat.cajun.navy.mission.data.MissionRoute;
import com.redhat.cajun.navy.mission.data.MissionStep;
import retrofit2.Response;
import rx.Observable;

import java.io.IOException;
import java.util.List;


public class RoutePlanner {

    private String MAPBOX_ACCESS_TOKEN = null;

    public RoutePlanner(String MAPBOX_ACCESS_TOKEN) {
        this.MAPBOX_ACCESS_TOKEN = MAPBOX_ACCESS_TOKEN;
    }

    public MissionRoute getMapboxDirectionsRequest(Location origin, Location destination, Location waypoint) {

        MapboxDirections request = MapboxDirections.builder()
                .accessToken(MAPBOX_ACCESS_TOKEN)
                .origin(Point.fromLngLat(origin.getLong(), origin.getLat()))
                .destination(Point.fromLngLat(destination.getLong(), destination.getLat()))
                .addWaypoint(Point.fromLngLat(waypoint.getLong(), waypoint.getLat()))
                .overview(DirectionsCriteria.OVERVIEW_FULL)
                .profile(DirectionsCriteria.PROFILE_DRIVING)
                .steps(true)
                .build();

        MissionRoute mRoute = new MissionRoute();
        try {

            Response<DirectionsResponse> response = request.executeCall();

            if (response.body() == null) {
                System.err.println("No routes found check access token, rights and coordinates");
            } else if (response.body().routes().size() < 1) {
                System.err.println("No routes found!");
            } else {

                if (response.body().routes().size() > 0) {
                    DirectionsRoute route = response.body().routes().get(0);


                    mRoute.setDistance(route.distance());
                    mRoute.setDuration(route.duration());
                    List<RouteLeg> legs = route.legs();

                    Observable.from(legs).map(l -> {
                        List<LegStep> steps = l.steps();
                        Observable.from(steps).map(s -> {
                            MissionStep step = new MissionStep(s.distance(), s.duration(), s.name(), s.maneuver().instruction(), s.weight(), s.maneuver().location());
                            if(s.maneuver().type().equalsIgnoreCase("arrive"))
                                if(s.maneuver().modifier() == null)
                                    step.setDestination(true);
                                else
                                    step.setWayPoint(true);
                            mRoute.addMissionStep(step);
                            //System.out.println(s.maneuver().type()+" , "+s.maneuver().modifier());
                            //System.out.println(step);
                            return step;
                        }).subscribe();
                        return steps;
                    }).subscribe();
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        // System.out.println(mRoute);
        return mRoute;
    }

}
