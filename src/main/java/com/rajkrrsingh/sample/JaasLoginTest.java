package com.rajkrrsingh.sample;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.security.auth.Subject;
import javax.security.auth.kerberos.KerberosTicket;
import javax.security.auth.login.Configuration;
import javax.security.auth.login.LoginContext;
import java.io.File;
import java.net.URI;
import java.security.URIParameter;
import java.util.Set;

public class JaasLoginTest {
    private static final Logger LOG = LoggerFactory.getLogger(JaasLoginTest.class);

    public static void main(String argv[]) {


        Configuration login_conf = null;
        String loginConfigurationFile = System.getProperty("java.security.auth.login.config");
        if ((loginConfigurationFile != null) && (loginConfigurationFile.length()>0)) {
            File config_file = new File(loginConfigurationFile);
            if (! config_file.canRead()) {
                throw new RuntimeException("File " + loginConfigurationFile +
                        " cannot be read.");
            }
            try {
                URI config_uri = config_file.toURI();
                login_conf = Configuration.getInstance("JavaLoginConfig", new URIParameter(config_uri));
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        }

        try {
            ClientCallbackHandler client_callback_handler = new ClientCallbackHandler(login_conf);
            LoginContext lc = new LoginContext("StormClient", client_callback_handler);
            lc.login();
            Subject subject = lc.getSubject();
            Set<KerberosTicket> tickets = subject.getPrivateCredentials(KerberosTicket.class);
            for(KerberosTicket ticket: tickets) {
                LOG.info("Ticket: " +ticket);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
