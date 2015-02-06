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
package org.ldp4j.generic.config;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableSet;
import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.query.ReadWrite;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.tdb.TDBFactory;
import com.hp.hpl.jena.vocabulary.DCTerms;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import org.apache.jena.riot.RDFDataMgr;
import org.ldp4j.generic.util.RdfUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.io.File;
import java.util.*;

import static org.ldp4j.generic.util.RdfUtils.resource;

public class ConfigManager implements ServletContextListener {

    private static final Logger logger = LoggerFactory.getLogger(ConfigManager.class);

    public static final String META_GRAPH_PREFIX = "ldp4j://";

    public static final String HTTP_PREFIX = "http://";

    private static String datasetPath;

    private static Config appConfig;

    private static final Set<Property> READ_ONLY_PROPS = new HashSet<Property>();

    private static final Set<Property> RESTRICTED_PROPS = new HashSet<Property>();

    private static DefaultPropertyConfig defaultPropertyConfig;

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

        defaultPropertyConfig = new DefaultPropertyConfig();
        defaultPropertyConfig.setCreated(appConfig.getBoolean("default-props.created"));

        List<String> readOnlyProps = appConfig.getStringList("read-only-props");
        logger.debug("Read-only properties: {}", Joiner.on(", ").join(readOnlyProps));
        for (String prop : readOnlyProps) {
            READ_ONLY_PROPS.add(RdfUtils.property(prop));
        }

        List<String> restrictedProps = appConfig.getStringList("restricted-props");
        logger.debug("Restricted properties: {}", Joiner.on(", ").join(restrictedProps));
        for (String prop : restrictedProps) {
            RESTRICTED_PROPS.add(RdfUtils.property(prop));
        }

        logger.debug("loading the initial dataset ...");

        Dataset initData = RDFDataMgr.loadDataset("data-config.trig") ;
        Dataset dataset = TDBFactory.createDataset(datasetPath);

        Map<String, String> nsPrefixMap = initData.getDefaultModel().getNsPrefixMap();

        Iterator<String> listNames = initData.listNames();
        while (listNames.hasNext()) {
            String name = listNames.next();
            logger.debug("NamedGraph {} loaded ...", name);

            try {
                dataset.begin(ReadWrite.WRITE) ;
                if (!dataset.containsNamedModel(name)) {
                    Model model = initData.getNamedModel(name);
                    model.setNsPrefixes(nsPrefixMap);
                    if (!name.startsWith("ldp4j://")) {

                        if (defaultPropertyConfig.isCreated()) {
                            Calendar cal = Calendar.getInstance();
                            cal.setTime(new Date());
                            model.add(resource(name), DCTerms.created, model.createTypedLiteral(cal));
                        }
                    }
                    dataset.addNamedModel(name, model);
                    dataset.commit() ;
                } else {
                    dataset.abort();
                }
            } finally {
                dataset.end() ;
            }

        }

    }

    public static DefaultPropertyConfig getDefaultPropertyConfig() {
        return defaultPropertyConfig;
    }

    public static Set<Property> getReadOnlyProps() {
        return READ_ONLY_PROPS;
    }

    public static Set<Property> getRestrictedProps() {
        return RESTRICTED_PROPS;
    }
}
