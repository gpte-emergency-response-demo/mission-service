package com.redhat.cajun.navy.mission;

import io.vertx.core.DeploymentOptions;
import io.vertx.core.json.JsonObject;
import org.apache.commons.lang.StringUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class DeployOptions extends DeploymentOptions{


    public DeploymentOptions getDeployOptions(String fileName) throws Exception{
        return new DeploymentOptions().setConfig(propsToJson(getProperties(fileName)));
    }


    public Properties getProperties (String filename) throws IOException {
        Properties props = new Properties();
        props.load(new FileInputStream(new File(filename)));
        return props;
    }

    public JsonObject propsToJson(Properties props){
        JsonObject obj = new JsonObject();
        for(String s: props.stringPropertyNames()){
            String prop = props.getProperty(s);
            if(StringUtils.isNumeric(prop))
                obj.put(s, Integer.valueOf(prop));
            else if(prop.equalsIgnoreCase("true") || prop.equalsIgnoreCase("false"))
                obj.put(s, Boolean.valueOf(prop));
            else
                obj.put(s, prop);
        }
        return obj;
    }

}
