package com.ulyp.agent.log;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.apache.logging.log4j.core.LifeCycle;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.AbstractConfiguration;
import org.apache.logging.log4j.core.config.composite.CompositeConfiguration;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.ConfigurationFactory;
import org.apache.logging.log4j.core.config.ConfigurationSource;
import org.apache.logging.log4j.core.impl.ContextAnchor;
import org.apache.logging.log4j.core.selector.ClassLoaderContextSelector;
import org.apache.logging.log4j.core.selector.ContextSelector;
import org.apache.logging.log4j.core.util.Cancellable;
import org.apache.logging.log4j.core.util.Constants;
import org.apache.logging.log4j.core.util.DefaultShutdownCallbackRegistry;
import org.apache.logging.log4j.core.util.Loader;
import org.apache.logging.log4j.core.util.ShutdownCallbackRegistry;
import org.apache.logging.log4j.spi.LoggerContextFactory;
import org.apache.logging.log4j.status.StatusLogger;
import org.apache.logging.log4j.util.PropertiesUtil;

/**
 * Factory to locate a ContextSelector and then load a LoggerContext.
 */
public class Log4jContextFactory extends org.apache.logging.log4j.core.impl.Log4jContextFactory {

    public Log4jContextFactory() {
        System.out.println("ULYP HGEL");
    }

    public Log4jContextFactory(ContextSelector selector) {
        super(selector);
        System.out.println("ULYP HGEL");
    }

    public Log4jContextFactory(ShutdownCallbackRegistry shutdownCallbackRegistry) {
        super(shutdownCallbackRegistry);
        System.out.println("ULYP HGEL");
    }

    public Log4jContextFactory(ContextSelector selector, ShutdownCallbackRegistry shutdownCallbackRegistry) {
        super(selector, shutdownCallbackRegistry);
        System.out.println("ULYP HGEL");
    }


}
