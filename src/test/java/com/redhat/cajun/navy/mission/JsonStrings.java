package com.redhat.cajun.navy.mission;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class JsonStrings {


    private static String MissionCommandsFile = "src/test/resources/CreateMissionCommand.json";
    private static String ResponderSimEventsFile = "src/test/resources/ResponderSim.json";

    public List<String> getMissionsCommands(){
        return Collections.unmodifiableList(getMissionCommandsFromFile(MissionCommandsFile));
    }

    public List<String> getResponderSimEvents(){
        return Collections.unmodifiableList(getMissionCommandsFromFile(ResponderSimEventsFile));
    }


    private List<String> getMissionCommandsFromFile(String fileName) {
        List<String> list = new ArrayList<>();
        try {
            try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
                for (String line; (line = br.readLine()) != null; ) {
                    list.add(line);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

}
