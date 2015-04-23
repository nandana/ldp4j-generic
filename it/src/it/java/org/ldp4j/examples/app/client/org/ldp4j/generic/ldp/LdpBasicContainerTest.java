/**
 * Copyright (C) 2014 Ontology Engineering Group, Universidad Politécnica de Madrid
 * (http://www.oeg-upm.net/)
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

package org.ldp4j.examples.app.client.org.ldp4j.generic.ldp;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.sparql.vocabulary.FOAF;
import com.hp.hpl.jena.vocabulary.DCTerms;
import com.hp.hpl.jena.vocabulary.RDF;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.fest.util.Arrays;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.importer.ZipImporter;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.URL;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@RunWith(Arquillian.class)
public class LdpBasicContainerTest {

    private static final Logger logger = LoggerFactory.getLogger(LdpBasicContainerTest.class);

    private static final String WEBAPP_ID = "org.ldp4j.generic:ldp4j-generic-core:war:";

    @Deployment(testable = false)
    public static WebArchive createDeployment() {

        String version = System.getProperty("contactListVersion");
        String mavenCoordinate = WEBAPP_ID + version;

        logger.debug("Creating the deployment of '{}'", mavenCoordinate);

        File warFile = Maven.configureResolver().workOffline().
                resolve(mavenCoordinate).withoutTransitivity().asSingleFile();

        logger.debug("Using the war file in '{}'", warFile.toString());

        WebArchive webapp = ShrinkWrap.create(ZipImporter.class, "ldp4j.war").importFrom(warFile)
                .as(WebArchive.class);

        return webapp;
    }

    @Test
    public void testCreateChildContainer(@ArquillianResource URL contextURL) throws Exception {

        String ldpBcURI = contextURL + "ldp-bc/";

        logger.debug("Context URL : {}", contextURL.toString());
        logger.debug("Basic Container URL : {}", ldpBcURI);

        HttpClient client = new DefaultHttpClient();

        HttpPost post = new HttpPost(ldpBcURI);
        post.setHeader("Link", "<http://www.w3.org/ns/ldp#BasicContainer>; rel=\"type\"");
        post.setEntity(createBasicContainer());

        HttpResponse response = client.execute(post);
        assertThat("successful resource creation",response.getStatusLine().getStatusCode(), equalTo(HttpStatus.SC_CREATED));

        Header location = response.getFirstHeader("Location");
        assertThat("location header present", location, is(notNullValue()));
        EntityUtils.consumeQuietly(response.getEntity());
        //client.getConnectionManager().shutdown();

        // Creating a descendant child
        //client = new DefaultHttpClient();
        post =  new HttpPost(location.getValue());
        post.setHeader("Link", "<http://www.w3.org/ns/ldp#BasicContainer>; rel=\"type\"");
        post.setEntity(createBasicContainer());

        response = client.execute(post);
        assertThat("successful resource creation",response.getStatusLine().getStatusCode(), equalTo(HttpStatus.SC_CREATED));

        location = response.getFirstHeader("Location");
        assertThat("location header present", location, is(notNullValue()));

        client.getConnectionManager().shutdown();

    }


    public void testDirectContainerPOST(@ArquillianResource URL contextURL) throws Exception {

        String ldpDcURI = contextURL.toString() + "ldp-bc/";

        logger.debug("Context URL : {}", contextURL.toString());
        logger.debug("Direct Container URL : {}", ldpDcURI);

        HttpClient client = new DefaultHttpClient();

        HttpGet get = new HttpGet(ldpDcURI);
        get.setHeader("Accept", "text/turtle");
        HttpResponse response = client.execute(get);

        PipedInputStream in = new PipedInputStream();
        PipedOutputStream out = new PipedOutputStream(in);
        response.getEntity().writeTo(out);

        Model containerBefore = ModelFactory.createDefaultModel();
        containerBefore.read(in, null, "Turtle");

        HttpPost post = new HttpPost(ldpDcURI);
        post.setHeader("Link", "<http://www.w3.org/ns/ldp#Resource>; rel=\"type\"");
        post.setEntity(createResource());

        response = client.execute(post);
        assertThat("successful resource creation",response.getStatusLine().getStatusCode(), equalTo(HttpStatus.SC_CREATED));

        Header location = response.getFirstHeader("Location");
        assertThat("location header present", location, is(notNullValue()));
        EntityUtils.consumeQuietly(response.getEntity());

        get = new HttpGet(ldpDcURI);
        get.setHeader("Accept", "text/turtle");
        response = client.execute(get);

        in = new PipedInputStream();
        out = new PipedOutputStream(in);
        response.getEntity().writeTo(out);

        Model containerAfter = ModelFactory.createDefaultModel();
        containerAfter.read(in, null, "Turtle");

        client.getConnectionManager().shutdown();

        Resource container = containerBefore.createResource(ldpDcURI);
        Resource newRessResource = containerBefore.createResource(location.getValue());
        Property ldpContains = containerBefore.createProperty("http://www.w3.org/ns/ldp#contains");
        boolean presentBefore = containerBefore.contains(container, ldpContains, newRessResource);
        boolean presentAfter = containerAfter.contains(container, ldpContains, newRessResource);

        logger.debug("Checking for the triple <{}, {}, {}>", Arrays.array(ldpDcURI, "ldp:contains", location.getValue()));
        assertThat("Not present before creation", presentBefore, is(false));
        assertThat("Present after creation", presentBefore, is(false));



    }

    private StringEntity createBasicContainer() {

        Model model = ModelFactory.createDefaultModel();
        Resource resource = model.createResource("");
        resource.addProperty(RDF.type, "http://www.w3.org/ns/ldp#BasicContainer");
        resource.addProperty(RDF.type, "http://www.w3.org/ns/ldp#Container");
        resource.addProperty(DCTerms.title, "A child container");

        StringWriter writer = new StringWriter();
        model.write(writer, "TURTLE");
        StringEntity entity = new StringEntity(writer.toString(), ContentType.create("text/turtle"));

        return entity;

    }

    private StringEntity createResource() {

        Model model = ModelFactory.createDefaultModel();
        Resource resource = model.createResource("");
        resource.addProperty(RDF.type, FOAF.Person);
        resource.addProperty(FOAF.name, "Nandana Mihindulasooriya");
        resource.addLiteral(FOAF.primaryTopic, "#me");

        StringWriter writer = new StringWriter();
        model.write(writer, "TURTLE");
        StringEntity entity = new StringEntity(writer.toString(), ContentType.create("text/turtle"));

        return entity;

    }

}
