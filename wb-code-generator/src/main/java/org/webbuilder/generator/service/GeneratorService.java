package org.webbuilder.generator.service;

import org.webbuilder.generator.bean.GeneratorConfig;

/**
 * Created by 浩 on 2015-07-27 0027.
 */
public interface GeneratorService {
    void generate(GeneratorConfig config) throws Exception;
}
