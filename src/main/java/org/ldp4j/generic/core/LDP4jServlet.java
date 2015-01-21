package org.ldp4j.generic.core;

import org.ldp4j.generic.handlers.*;
import org.ldp4j.generic.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class LDP4jServlet extends HttpServlet {

    private static final Logger logger = LoggerFactory.getLogger(LDP4jServlet.class);

    @Override
    protected void doHead(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException
    {



    }


    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException
    {

        logger.debug("Started processing the GET request ...");

        LDPContext context = new LDPContext(req,resp);
        LDP4jEngine engine = new LDP4jEngine();

        List<Handler> handlerChain = new ArrayList<Handler>();
        handlerChain.add(new ResourceResolver());
        handlerChain.add(new RequestPreProcessor());
        handlerChain.add(new RequestPostProcessor());
        handlerChain.add(new ResponseRDFWriter());
        engine.setHandlerChain(handlerChain);

        try {
            engine.serve(context);
        } catch (LDPFault ldpFault) {
            if(ldpFault.isProcessable()){
                HttpStatus statusCode = ldpFault.getStatusCode();
                resp.sendError(statusCode.code());
            } else {
                throw new ServletException(ldpFault);
            }
        }

    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException
    {

        LDPContext context = new LDPContext(req,resp);
        LDP4jEngine engine = new LDP4jEngine();

        List<Handler> handlerChain = new ArrayList<Handler>();
        handlerChain.add(new ResourceResolver());
        handlerChain.add(new BasicContainerCreateHandler());
        engine.setHandlerChain(handlerChain);

        try {
            engine.serve(context);
        } catch (LDPFault ldpFault) {
            if(ldpFault.isProcessable()){
                HttpStatus statusCode = ldpFault.getStatusCode();
                resp.sendError(statusCode.code());
            } else {
                throw new ServletException(ldpFault);
            }
        }

    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException
    {

    }

    @Override
    protected void doDelete(HttpServletRequest req,
                            HttpServletResponse resp)
            throws ServletException, IOException
    {

    }


}
