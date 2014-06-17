

package org.easetech.aggregator;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.Address;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import org.easetech.easytest.io.Resource;
import org.easetech.easytest.io.ResourceLoader;
import org.easetech.easytest.io.ResourceLoaderStrategy;
import org.junit.Assert;

/**
 * TODO Describe me
 * 
 */
public class EmailReport {
    
    private static final String MAIL_PROPERTIES_FILE_LOCATION = "mailProperties";
    
    private final Session session;

    public EmailReport(Properties mailProperties) {
        
        session = Session.getDefaultInstance(mailProperties);
    }

    public void send(String[] filesToSend , String from , List<String> mailTo) {
        Properties properties = getProperties();
        if(properties != null) {
            for(String filePath : filesToSend) {
                sendMail(filePath , from, mailTo);
            }
            
        }
    }

    private Properties getProperties() {
        Properties properties = null;
        ResourceLoader resourceLoader = new ResourceLoaderStrategy(this.getClass());
        String propertyFileLocation = System.getProperty(MAIL_PROPERTIES_FILE_LOCATION);
        if(propertyFileLocation != null) {
            Resource fileResource = resourceLoader.getResource(propertyFileLocation);
            if (fileResource.exists()) {

                try {
                    properties = new Properties();
                    properties.load(fileResource.getInputStream());
                } catch (IOException e) {
                    throw new RuntimeException("IOException occured while trying to load the properties from file : "
                        + propertyFileLocation, e);
                }
            } else {
                Assert.fail("Properties file with path " + propertyFileLocation + " does not exist.");
            }

            
        }
        return properties;
        
    }

    public void sendMail(String filename , String from , List<String> mailTo) {

       

        // Get the default Session object.
        

        try {
            // Create a default MimeMessage object.
            MimeMessage message = new MimeMessage(session);

            // Set From: header field of the header.
            message.setFrom(new InternetAddress(from));

            List<Address> to = new ArrayList<Address>();
            for(String mailAddr : mailTo) {           
                to.add(new InternetAddress(mailAddr));
            }
            
            
            // Set To: header field of the header.
            message.addRecipients(Message.RecipientType.TO, to.toArray(new Address[0]));

            // Set Subject: header field
            message.setSubject("This is the Subject Line!");

            // Create the message part
            BodyPart messageBodyPart = new MimeBodyPart();

            // Fill the message
            messageBodyPart.setText("This is message body");

            // Create a multipar message
            Multipart multipart = new MimeMultipart();

            // Set text message part
            multipart.addBodyPart(messageBodyPart);

            // Part two is attachment
            messageBodyPart = new MimeBodyPart();
            DataSource source = new FileDataSource(filename);
            messageBodyPart.setDataHandler(new DataHandler(source));
            messageBodyPart.setFileName(filename);
            multipart.addBodyPart(messageBodyPart);

            // Send the complete message parts
            message.setContent(multipart);

            // Send message
            Transport.send(message);
            System.out.println("Message sent successfully....");
        } catch (MessagingException mex) {
            mex.printStackTrace();
        }
    }

}
