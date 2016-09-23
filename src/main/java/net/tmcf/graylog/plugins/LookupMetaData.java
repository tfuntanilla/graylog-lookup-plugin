package net.tmcf.graylog.plugins;

import org.graylog2.plugin.PluginMetaData;
import org.graylog2.plugin.ServerStatus;
import org.graylog2.plugin.Version;

import java.net.URI;
import java.util.EnumSet;
import java.util.Set;

/**
 * Implement the PluginMetaData interface here.
 */
public class LookupMetaData implements PluginMetaData {
    private static final String PLUGIN_PROPERTIES = "net.tmcf.graylog-lookup-plugin/graylog-plugin.properties";

    @Override
    public String getUniqueId() {
        return "net.tmcf.graylog.plugins.LookupPlugin";
    }

    @Override
    public String getName() {
        return "Lookup";
    }

    @Override
    public String getAuthor() {
        return "Trisha Funtanilla <trishafuntanilla@gmail.com>";
    }

    @Override
    public URI getURL() {
        return URI.create("https://github.com/trishafuntanilla/graylog-lookup-plugin.git");
    }

    @Override
    public Version getVersion() {
        return Version.fromPluginProperties(getClass(), PLUGIN_PROPERTIES, "version", Version.from(1, 0, 0, "unknown"));
    }

    @Override
    public String getDescription() {
        // TODO Insert correct plugin description
        return "Description of Lookup plugin";
    }

    @Override
    public Version getRequiredVersion() {
        return Version.fromPluginProperties(getClass(), PLUGIN_PROPERTIES, "graylog.version", Version.from(1, 0, 0, "unknown"));
    }

    @Override
    public Set<ServerStatus.Capability> getRequiredCapabilities() {
    	return EnumSet.of(ServerStatus.Capability.SERVER);
    }
}
