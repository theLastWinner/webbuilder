package org.webbuilder.web.core;

import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;

import java.io.IOException;
import java.util.Properties;

/**
 * Created by 浩 on 2015-08-17 0017.
 */
public class FullPropertyPlaceholderConfigurer extends PropertyPlaceholderConfigurer {

    @Override
    public void loadProperties(Properties props) throws IOException {
        super.loadProperties(props);
    }
}
