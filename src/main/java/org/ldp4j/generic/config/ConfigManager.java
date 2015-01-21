package org.ldp4j.generic.config;

import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.query.ReadWrite;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.tdb.TDBFactory;
import org.apache.jena.riot.RDFDataMgr;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.util.Iterator;

public class ConfigManager implements ServletContextListener {

    private static final Logger logger = LoggerFactory.getLogger(ConfigManager.class);

    private static String DATASET_DIR = "dataset";

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

        return TDBFactory.createDataset(DATASET_DIR);
    }

    private void loadInitDataset(){

        logger.debug("loading the initial dataset ...");

        Dataset initData = RDFDataMgr.loadDataset("data-config.trig") ;
        Dataset dataset = TDBFactory.createDataset(DATASET_DIR);

        Iterator<String> listNames = initData.listNames();
        while (listNames.hasNext()) {
            String name = listNames.next();
            logger.debug("NamedGraph {} loaded ...", name);

            try {
                dataset.begin(ReadWrite.WRITE) ;
                if (!dataset.containsNamedModel(name)) {
                    Model model = initData.getNamedModel(name);

                        dataset.addNamedModel(name, model);
                        dataset.commit() ;
                    }
            } finally {
                dataset.end() ;
            }


        }

    }

}
