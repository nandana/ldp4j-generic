/**
 * Copyright (C) 2014 Ontology Engineering Group, Universidad Politécnica de Madrid (http://www.oeg-upm.net/)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.ldp4j.generic.core;

import org.ldp4j.generic.handlers.*;
import org.ldp4j.generic.http.HttpMethod;
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

        logger.debug("Started processing the HEAD request on '{}'", req.getRequestURL());

        doGet(req, resp);

    }

    protected void doOptions(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException
    {
        logger.debug("Started processing the OPTIONS request on '{}'", req.getRequestURL());

        LDPContext context = new LDPContext(req,resp);
        LDP4jEngine engine = new LDP4jEngine();

        List<Handler> handlerChain = new ArrayList<Handler>();
        handlerChain.add(new ResourceResolver());
        handlerChain.add(new OptionsHandler());
        handlerChain.add(new RequestPostProcessor());
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
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException
    {

        // In case of the HEAD method, the execution will be delegated to the GET method.
        // The ResponseRDFWriter will do a check to see if the method is GET or HEAD
        if (HttpMethod.GET.name().equals(req.getMethod())) {
            logger.debug("Started processing the GET request on '{}'", req.getRequestURL());
        }
        LDPContext context = new LDPContext(req,resp);
        LDP4jEngine engine = new LDP4jEngine();

        List<Handler> handlerChain = new ArrayList<Handler>();
        handlerChain.add(new ResourceResolver());
        handlerChain.add(new RequestPreProcessor());
        handlerChain.add(new OptionsHandler());
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

        logger.debug("Started processing the POST request on '{}'", req.getRequestURL());

        LDPContext context = new LDPContext(req,resp);
        LDP4jEngine engine = new LDP4jEngine();

        List<Handler> handlerChain = new ArrayList<Handler>();
        handlerChain.add(new ResourceResolver());
        handlerChain.add(new RequestPreProcessor());
        handlerChain.add(new PostHandler());
        handlerChain.add(new RequestPostProcessor());
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

        logger.debug("Started processing the PUT request on '{}'", req.getRequestURL());

        LDPContext context = new LDPContext(req,resp);
        LDP4jEngine engine = new LDP4jEngine();

        List<Handler> handlerChain = new ArrayList<Handler>();
        handlerChain.add(new ResourceResolver());
        handlerChain.add(new PutHandler());
        handlerChain.add(new RequestPostProcessor());
        engine.setHandlerChain(handlerChain);

        execute(engine, context);


    }

    @Override
    protected void doDelete(HttpServletRequest req,
                            HttpServletResponse resp)
            throws ServletException, IOException
    {

        logger.debug("Started processing the DELETE request on '{}'", req.getRequestURL());

        LDPContext context = new LDPContext(req,resp);
        LDP4jEngine engine = new LDP4jEngine();

        List<Handler> handlerChain = new ArrayList<Handler>();
        handlerChain.add(new ResourceResolver());
        handlerChain.add(new DeleteHandler());
        handlerChain.add(new RequestPostProcessor());
        engine.setHandlerChain(handlerChain);

        execute(engine, context);

    }

    private void execute(LDP4jEngine engine, LDPContext context) throws ServletException, IOException {

        try {
            engine.serve(context);
        } catch (LDPFault ldpFault) {
            if(ldpFault.isProcessable()){
                HttpStatus statusCode = ldpFault.getStatusCode();
                context.getServletResponse().setStatus(statusCode.code());
                String msg = ldpFault.getMessage();
                if (msg != null) {
                    context.getServletResponse().getWriter().write(msg);
                }

            } else {
                throw new ServletException(ldpFault);
            }
        }

    }

}
