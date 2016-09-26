package net.tmcf.graylog.plugins;

import org.graylog2.plugin.PluginConfigBean;
import org.graylog2.plugin.PluginModule;

import com.google.inject.Scopes;

import java.util.Collections;
import java.util.Set;

/**
 * Extend the PluginModule abstract class here to add you plugin to the system.
 */
public class LookupModule extends PluginModule {
    /**
     * Returns all configuration beans required by this plugin.
     *
     * Implementing this method is optional. The default method returns an empty {@link Set}.
     */
    @Override
    public Set<? extends PluginConfigBean> getConfigBeans() {
        return Collections.emptySet();
    }

    @Override
    protected void configure() {
    	
    	bind(LookupService.class).to(LookupServiceImpl.class).in(Scopes.SINGLETON);
    	
    	addPeriodical(LookupPeriodical.class);
    	addMessageFilter(Lookup.class);
    	addRestResource(LookupPluginResource.class);
    	
    	addConfigBeans();
    	
    }
}
