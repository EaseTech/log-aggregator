package org.easetech.aggregator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import java.util.List;
import org.oclc.log.internal.xml.Application;
import org.oclc.log.internal.xml.Env;
import org.oclc.log.internal.xml.Profile;

import org.oclc.log.internal.xml.ObjectFactory;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import org.oclc.log.internal.xml.Logging;

import java.io.IOException;
import java.io.InputStream;
import org.easetech.easytest.io.Resource;
import org.easetech.easytest.io.ResourceLoader;
import org.easetech.easytest.io.ResourceLoaderStrategy;
import org.junit.Assert;

public class LogUtil {
    
    private static final String LOCATION_SUFFIX_OUT_LOG = "/logs/webapp-out.log";

    private static final String LOCATION_SUFFIX_ERR_LOG = "/logs/webapp-err.log";

    private static final String COLON = ":";

    private static final String DEFAULT_PORT = "8081";

    private static final String HTTP = "http://";

    private static final List<String> RESOURCE_URLS = new ArrayList<String>();


    
    public static InputStream getFileInputStream(String propertyFileLocation) {
        InputStream result = null;
        ResourceLoader resourceLoader = new ResourceLoaderStrategy(LogUtil.class);
        Resource fileResource = resourceLoader.getResource(propertyFileLocation);
        if(fileResource.exists()) {
            try {
                result = fileResource.getInputStream();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } else {
            Assert.fail("Properties file with path " + propertyFileLocation + " does not exist."); 
        }
        return result;
    }
    
    public static Logging getProperties(InputStream fileInputStream) {

        JAXBContext context = getJAXBContext();
        Logging logProperties = null;
        try {
            if (context != null) {
                Unmarshaller unmarshaller = context.createUnmarshaller();
                logProperties = (Logging) unmarshaller.unmarshal(fileInputStream);
            }
        } catch (JAXBException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return logProperties;
    }


    /**
     * Get the JAXBContext
     * 
     * @return an instance of {@link JAXBContext}
     */
    private static JAXBContext getJAXBContext() {
        JAXBContext context = null;
        try {
            context = JAXBContext.newInstance(ObjectFactory.class);
        } catch (JAXBException e) {
            throw new RuntimeException("Error occurred while creating JAXB Context.", e);
        }
        return context;
    }
    
    public static Map<String , String> getDestinationToSourceMap(Logging logProperties) {
        Map<String, String> destinationToSourceMap = new HashMap<String, String>();
        String baseDir = logProperties.getBaseDirForLogFiles();
        List<Env> environments = logProperties.getEnv();
        for (Env env : environments) {
            List<Application> apps = env.getApplication();
            for (Application app : apps) {
                List<Profile> profiles = app.getProfile();
                for (Profile profile : profiles) {
                    String host = profile.getHost();
                    String port = profile.getPort() == null ? DEFAULT_PORT : profile.getPort();
                    
                    String resourceUrlPrefix = HTTP.concat(host).concat(COLON).concat(port);
                    String outResource = resourceUrlPrefix.concat(LOCATION_SUFFIX_OUT_LOG);
                    String errResource = resourceUrlPrefix.concat(LOCATION_SUFFIX_ERR_LOG);
                    RESOURCE_URLS.add(outResource);
                    RESOURCE_URLS.add(errResource);
                    String destinationPrefix = baseDir.concat("/").concat(env.getName()).concat("/")
                        .concat(app.getName()).concat("/").concat(profile.getName());
                    String outDestination = destinationPrefix.concat(LOCATION_SUFFIX_OUT_LOG);
                    String errDestination = destinationPrefix.concat(LOCATION_SUFFIX_ERR_LOG);
                    outDestination = outDestination.replace(" ", "_");
                    errDestination = errDestination.replace(" ", "_");
                    destinationToSourceMap.put(outDestination, outResource);
                    destinationToSourceMap.put(errDestination, errResource);
                }
            }
        }
        return destinationToSourceMap;
    }
    
    public static Map<String, List<String>> getProfileNameToFileMap(Logging logProperties) {
        Map<String, List<String>> profileNameToFileMap = new HashMap<String, List<String>>();
        String baseDir = logProperties.getBaseDirForLogFiles();
        List<Env> environments = logProperties.getEnv();
        
        for (Env env : environments) {
            List<Application> apps = env.getApplication();
            for (Application app : apps) {
                List<Profile> profiles = app.getProfile();
                for (Profile profile : profiles) {
                    List<String> fileNames = new ArrayList<String>();
                    String destinationPrefix = baseDir.concat("/").concat(env.getName()).concat("/")
                        .concat(app.getName()).concat("/").concat(profile.getName());
                    String outDestination = destinationPrefix.concat(LOCATION_SUFFIX_OUT_LOG);
                    String errDestination = destinationPrefix.concat(LOCATION_SUFFIX_ERR_LOG);
                    outDestination = outDestination.replace(" ", "_");
                    errDestination = errDestination.replace(" ", "_");
                    fileNames.add(outDestination);
                    fileNames.add(errDestination);
                    profileNameToFileMap.put(profile.getName() , fileNames);
                }
            }
        }
        return profileNameToFileMap;
    }
}
