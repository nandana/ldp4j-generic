package org.ldp4j.generic.security;

import org.apache.commons.codec.binary.Base64;
import org.ldp4j.generic.core.Handler;
import org.ldp4j.generic.core.HandlerResponse;
import org.ldp4j.generic.core.LDPContext;
import org.ldp4j.generic.core.LDPFault;
import org.ldp4j.generic.http.HttpHeader;
import org.ldp4j.generic.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.StringTokenizer;

public class BasicAuthHandler implements Handler {

    private static final Logger logger = LoggerFactory.getLogger(BasicAuthHandler.class);

    private static final String NAME = "BasicAuthHandler";

    private static String BASIC_AUTH_PREFIX = "Basic ";

    @Override
    public HandlerResponse invoke(LDPContext context) throws LDPFault {

        HttpServletRequest request = context.getServletRequest();
        HttpServletResponse response = context.getServletResponse();

        String authorization = request.getHeader(HttpHeader.AUTHORIZATION.name());

        //No authorization header
        if(authorization == null){
            String msg = "User needs to be authenticated to access this resource.";
            logger.error(msg);
            response.setStatus(HttpStatus.UNAUTHORIZED.code());
            response.setHeader(HttpHeader.WWW_AUTHENTICATE.name(), BASIC_AUTH_PREFIX + "realm=\"LDP4j\"");

            return HandlerResponse.ABORT;
        }

        //Check if value is a basic auth value
        if (!authorization.contains(BASIC_AUTH_PREFIX)) {
            String msg = "User needs to user HTTP Basic Authentication";
            response.setStatus(HttpStatus.UNAUTHORIZED.code());
            response.setHeader(HttpHeader.WWW_AUTHENTICATE.name(), BASIC_AUTH_PREFIX + "realm=\"LDP4j\"");
        }

        // Remove the basic auth prefix
        String basicAuth = authorization.replaceFirst(BASIC_AUTH_PREFIX, "");
        //Decode Base64
        String usernamePassword = new String(Base64.decodeBase64(basicAuth));

        //Split username and password tokens
        StringTokenizer tokenizer = new StringTokenizer(usernamePassword, ":");
        String username = tokenizer.nextToken();
        String password = tokenizer.nextToken();




            return null;
    }

    @Override
    public String getName() {
        return NAME;
    }
}
