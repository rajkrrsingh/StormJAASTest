package com.rajkrrsingh.sample;

import java.io.IOException;
import java.util.Map;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.callback.UnsupportedCallbackException;
import javax.security.sasl.AuthorizeCallback;
import javax.security.sasl.RealmCallback;
import javax.security.auth.login.AppConfigurationEntry;
import javax.security.auth.login.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class ClientCallbackHandler implements CallbackHandler {
    private static final Logger LOG = LoggerFactory.getLogger(ClientCallbackHandler.class);

    /**
     * Constructor based on a JAAS configuration
     * 
     * For digest, you should have a pair of user name and password defined in this figgure.
     * 
     * @param configuration
     * @throws IOException
     */
    public ClientCallbackHandler(Configuration configuration) throws IOException {
        if (configuration == null) return;
        AppConfigurationEntry configurationEntries[] = configuration.getAppConfigurationEntry("StormClient");
        if (configurationEntries == null) {
            String errorMessage = "Could not find a ' StormClient"
                    + "' entry in this configuration: Client cannot start.";
            LOG.error(errorMessage);
            throw new IOException(errorMessage);
        }
    }

    /**
     * This method is invoked by SASL for authentication challenges
     * @param callbacks a collection of challenge callbacks 
     */
    public void handle(Callback[] callbacks) throws IOException, UnsupportedCallbackException {
        for (Callback c : callbacks) {
            if (c instanceof NameCallback) {
                LOG.info("name callback");
            } else if (c instanceof PasswordCallback) {
                LOG.info("password callback");
                LOG.warn("Could not login: the client is being asked for a password, but the " +
                        " client code does not currently support obtaining a password from the user." +
                        " Make sure that the client is configured to use a ticket cache (using" +
                        " the JAAS configuration setting 'useTicketCache=true)' and restart the client. If" +
                        " you still get this message after that, the TGT in the ticket cache has expired and must" +
                        " be manually refreshed. To do so, first determine if you are using a password or a" +
                        " keytab. If the former, run kinit in a Unix shell in the environment of the user who" +
                        " is running this client using the command" +
                        " 'kinit <princ>' (where <princ> is the name of the client's Kerberos principal)." +
                        " If the latter, do" +
                        " 'kinit -k -t <keytab> <princ>' (where <princ> is the name of the Kerberos principal, and" +
                        " <keytab> is the location of the keytab file). After manually refreshing your cache," +
                        " restart this client. If you continue to see this message after manually refreshing" +
                        " your cache, ensure that your KDC host's clock is in sync with this host's clock.");
            } else if (c instanceof AuthorizeCallback) {
                LOG.info("authorization callback");
                AuthorizeCallback ac = (AuthorizeCallback) c;
                String authid = ac.getAuthenticationID();
                String authzid = ac.getAuthorizationID();
                if (authid.equals(authzid)) {
                    ac.setAuthorized(true);
                } else {
                    ac.setAuthorized(false);
                }
                if (ac.isAuthorized()) {
                    ac.setAuthorizedID(authzid);
                }
            }  else {
                throw new UnsupportedCallbackException(c);
            }
        }
    }
}
