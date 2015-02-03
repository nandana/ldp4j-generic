package org.ldp4j.examples.app.client.org.ldp4j.generic.ldp;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.sparql.vocabulary.FOAF;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.StringWriter;
import java.net.URL;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

@RunWith(Arquillian.class)
public class LdprTest {

    private static final Logger logger = LoggerFactory.getLogger(LdprTest.class);

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
    public void testGet(@ArquillianResource URL contextURL) throws Exception {

        String ldprURI = contextURL + "doap";

        logger.debug("Context URL : {}", contextURL.toString());
        logger.debug("Basic Container URL : {}", ldprURI);

        HttpClient client = new DefaultHttpClient();
        HttpGet get = new HttpGet(ldprURI);
        get.setHeader("Accept", "text/turtle");

        HttpResponse response = client.execute(get);

        assertThat("successful retrieval",response.getStatusLine().getStatusCode(), equalTo(HttpStatus.SC_OK));

        HttpEntity entity = response.getEntity();

        assertThat("body shouldn't be empty", entity, is(notNullValue()));

        Model model = ModelFactory.createDefaultModel();
        model.read(entity.getContent(), null, "TURTLE");

        Resource doap = model.createResource("http://localhost:8080/ldp4j/doap/");
        Resource ldp4j = model.createResource("http://github.com/nandana/ldp4j-generic");
        boolean primaryTopicPresent = model.contains(doap, FOAF.primaryTopic, ldp4j);

        assertThat(primaryTopicPresent, is(true));

        StringWriter stringWriter = new StringWriter();
        model.write(stringWriter, "TURTLE");
        logger.debug("DOAP LDPR Content : {} \n", stringWriter.toString());

        client.getConnectionManager().shutdown();

        response.getHeaders("Etag");

    }
}
