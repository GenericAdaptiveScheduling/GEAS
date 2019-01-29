/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package dataLoader;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 *
 * @author why
 * reading configuration files
 * 
 */
public class Configuration {
//    private static Log logger;
//    private static final Properties systemProperties;
//    public final static String systemProString = "/config/System.properties";
    public final static String constraintRuleString = "../GEAS/config/constraint/consistency_rules.xml";
    public final static String contextString = "../GEAS/config/constraint/context.xml";
    public final static String dataRoot = "../GEAS/config/data/changes/";
    public final static String outRoot = "../GEAS/config/data/out/";

}
