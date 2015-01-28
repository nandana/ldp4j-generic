package org.ldp4j.generic.config;

import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.query.ReadWrite;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.tdb.TDBFactory;
import com.hp.hpl.jena.vocabulary.DCTerms;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.XSD;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import org.apache.jena.riot.RDFDataMgr;
import org.ldp4j.generic.rdf.vocab.LDP;
import org.ldp4j.generic.rdf.vocab.LDP4J;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.io.File;
import java.util.Iterator;

public class ConfigManager implements ServletContextListener {

    private static final Logger logger = LoggerFactory.getLogger(ConfigManager.class);

    public static final String META_GRAPH_PREFIX = "ldp4j://";

    public static final String HTTP_PREFIX = "http://";

    private static String datasetPath;

    private static Config appConfig;

    /**
     * Receives notification that the web application initialization
     * process is starting.
     * <p/>
     * <p>All ServletContextListeners are notified of context
     * initialization before any filters or servlets in the web
     * application are initialized.
     *
     * @param sce the ServletContextEvent containing the ServletContext
     *            that is being initialized
     */
    @Override
    public void contextInitialized(ServletContextEvent sce) {

        loadInitDataset();

    }

    /**
     * Receives notification that the ServletContext is about to be
     * shut down.
     * <p/>
     * <p>All servlets and filters will have been destroyed before any
     * ServletContextListeners are notified of context
     * destruction.
     *
     * @param sce the ServletContextEvent containing the ServletContext
     *            that is being destroyed
     */
    @Override
    public void contextDestroyed(ServletContextEvent sce) {

    }


    public static Dataset getDataset(){

        return TDBFactory.createDataset(datasetPath);
    }

    /***
     * Get the application config
     * @param reload a flag to indicate whether the config needed to load again with the latest values
     * @return an app config
     */
    public static Config getAppConfig(boolean reload){
        if (reload) {
            return ConfigFactory.load();
        } else {
            return appConfig;
        }
    }

    private void loadInitDataset(){

        logger.debug("loading the application configuration");
        appConfig = ConfigFactory.load();

        datasetPath = appConfig.getString("dataset.path");
        File datasetDir = new File(datasetPath);
        if (!datasetDir.exists()) {
            datasetDir.mkdirs();
        }
        logger.debug("Dataset path is set to '{}' using the config parameter '{}'", datasetDir.getAbsolutePath(), datasetPath);

        logger.debug("loading the initial dataset ...");

        Dataset initData = RDFDataMgr.loadDataset("data-config.trig") ;
        Dataset dataset = TDBFactory.createDataset(datasetPath);

        Iterator<String> listNames = initData.listNames();
        while (listNames.hasNext()) {
            String name = listNames.next();
            logger.debug("NamedGraph {} loaded ...", name);

            try {
                dataset.begin(ReadWrite.WRITE) ;
                if (!dataset.containsNamedModel(name)) {
                    Model model = initData.getNamedModel(name);
                    addPrefixes(model);
                    dataset.addNamedModel(name, model);
                    dataset.commit() ;
                }
            } finally {
                dataset.end() ;
            }

        }

    }

    private void addPrefixes(Model model){
        model.setNsPrefix(LDP.PREFIX, LDP.NS);
        model.setNsPrefix("dcterms", DCTerms.NS);
        model.setNsPrefix("rdf", RDF.getURI());
        model.setNsPrefix("xsd", XSD.getURI());
    }

}
