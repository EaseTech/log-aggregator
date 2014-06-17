

package org.easetech.aggregator;

import java.util.Properties;
import org.easetech.easytest.annotation.TestBean;
import org.easetech.easytest.annotation.TestProperties;
import org.grep4j.core.model.Profile;
import org.grep4j.core.model.ProfileBuilder;
import org.junit.Ignore;

/**
 * TODO Describe me
 *
 */
@Ignore
@TestProperties("logging.properties")
public class LogAggregatorConfig {
    
    Properties properties;
    
    @TestBean("circUIProfile") public Profile getCircUIProfile() {
        Profile profile = ProfileBuilder.newBuilder()
            .name("Remote Circ UI Server debug Logs")
            .filePath(properties.getProperty("circui.debug.filePath"))
            .onRemotehost(properties.getProperty("circui.host"))
            .userAuthPrivateKeyLocationAndPassphrase(properties.getProperty("privatekey.location"), properties.getProperty("passphrase"))
            .withUser(properties.getProperty("circui.username"))
            .build();
        return profile;
    }
    
    @TestBean("circBusinessProfile") public Profile getCircBusinessProfile() {
        Profile profile = ProfileBuilder.newBuilder()
            .name("Remote Circ BL Server debug Logs")
            .filePath(properties.getProperty("circbl.debug.filePath"))
            .onRemotehost(properties.getProperty("circbl.host"))
            .userAuthPrivateKeyLocationAndPassphrase(properties.getProperty("privatekey.location"), properties.getProperty("passphrase"))
            .withUser(properties.getProperty("circbl.username"))
            .build();
        return profile;
    }
    
    @TestBean("circUIErrorProfile") public Profile getCircUIErrorProfile() {
        Profile profile = ProfileBuilder.newBuilder()
            .name("Remote Circ UI Server Error Logs")
            .filePath(properties.getProperty("circui.err.filePath"))
            .onRemotehost(properties.getProperty("circui.host"))
            .userAuthPrivateKeyLocationAndPassphrase(properties.getProperty("privatekey.location"), properties.getProperty("passphrase"))
            .withUser(properties.getProperty("circui.username"))
            .build();
        return profile;
    }
    
    @TestBean("circBusinessErrorProfile") public Profile getCircBusinessErrorProfile() {
        Profile profile = ProfileBuilder.newBuilder()
            .name("Remote Circ BL Server Error Logs")
            .filePath(properties.getProperty("circbl.err.filePath"))
            .onRemotehost(properties.getProperty("circbl.host"))
            .userAuthPrivateKeyLocationAndPassphrase(properties.getProperty("privatekey.location"), properties.getProperty("passphrase"))
            .withUser(properties.getProperty("circbl.username"))
            .build();
        return profile;
    }

}
