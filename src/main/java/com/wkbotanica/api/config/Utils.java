package com.wkbotanica.api.config;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// import io.vertx.core.logging.Logger;
// import io.vertx.core.logging.LoggerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Utils {
  private static final Logger logger = LoggerFactory.getLogger(Utils.class.getName());  

  /**
   * convenience method to dump the configuration used by this application
   */
  public static void dumpConfig() {
    Config.dumpConfig();
  }

  public static void dumpSystemEnv() {
    logger.info("Dump System.getenv():");
    Utils.dumpVars(System.getenv());
  }
  
  public static void dumpSystemProperties() {
    logger.info("Dump System.getProperties():");
    Utils.dumpVars(new HashMap(System.getProperties()));
  }

  private static void dumpVars(Map<String, ?> m) {
    List<String> keys = new ArrayList<String>(m.keySet());
    Collections.sort(keys);
    for (String k : keys) {
      logger.info(k + " : " + m.get(k));
    }

  }
}
