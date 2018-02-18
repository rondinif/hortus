package com.wkbotanica.api.config;

import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

import com.wkbotanica.api.config.WkbConfig;
import com.wkbotanica.api.constant.Configuration;

public class Config {
  private static final Logger logger = LoggerFactory.getLogger(Config.class.getName());  

  /**
   * convenience method to dump the configuration used by this application
   */
  protected static void dumpConfig() {
    try {
      logger.info( Configuration.CN_WKBOTANICA_API_CONTEXT_PATH + getContextPath());
      logger.info( Configuration.CN_DNS + " " + Configuration.CN_DNS_ADD_LOCALS + shouldAddLocalDns());
      logger.info( Configuration.CN_HTTP + " " + Configuration.CN_HTTP_PORT + getHttpPort());
      logger.info( Configuration.CN_FLICKR_API_KEY + " " + getFlickrApiKey());
      logger.info( Configuration.CN_FLICKR_USER_ID + " " + getFlickrUserId());
      logger.info( Configuration.CN_FLICKR_PHOTOSET_ID + " " + getFickrPhotosetId());
    }
    catch (Exception e) {
      logger.info("GRAVE:" + e.getMessage());
    }
  }

  public static String getContextPath() {
    return WkbConfig.INSTANCE.getString(Configuration.CN_WKBOTANICA_API_CONTEXT_PATH,Configuration.CD_WKBOTANICA_API_CONTEXT_PATH);
  }  

  public static boolean shouldAddLocalDns() {
    try {
      return WkbConfig.INSTANCE.getJsonByEnvironment(Configuration.CN_DNS).getBoolean(Configuration.CN_DNS_ADD_LOCALS);
    } 
    catch (Exception e) {
      return Configuration.CD_DNS_ADD_LOCALS;
    }
  }  
  
  public static int getHttpPort() {
    try {
      return WkbConfig.INSTANCE.getJsonByEnvironment(Configuration.CN_HTTP).getInteger(Configuration.CN_HTTP_PORT);   
    } 
    catch (Exception e) {
      return Configuration.CD_HTTP_PORT;
    }
  }  

  public static String getAPIKey() {
    try {
      return WkbConfig.INSTANCE.getJsonByEnvironment(Configuration.CN_PROVIDERS)
                               .getJsonObject(Configuration.CN_PROVIDERS_FLICKR)
                               .getString(Configuration.CN_FLICKR_API_KEY);   
    } 
    catch (Exception e) {
      return Configuration.CD_FLICKR_API_KEY;
    }
  }  

  public static String getFlickrApiKey() {
    try {
      return WkbConfig.INSTANCE.getJsonByEnvironment(Configuration.CN_PROVIDERS)
                               .getJsonObject(Configuration.CN_PROVIDERS_FLICKR)
                               .getString(Configuration.CN_FLICKR_API_KEY);   
    } 
    catch (Exception e) {
      return Configuration.CD_FLICKR_API_KEY;
    }
  }  

  public static String getFlickrUserId() {
    try {
      return WkbConfig.INSTANCE.getJsonByEnvironment(Configuration.CN_PROVIDERS)
                               .getJsonObject(Configuration.CN_PROVIDERS_FLICKR)
                               .getString(Configuration.CN_FLICKR_USER_ID);   
    } 
    catch (Exception e) {
      return Configuration.CD_FLICKR_USER_ID;
    }
  }  

  public static String getFickrPhotosetId() {
    try {
      return WkbConfig.INSTANCE.getJsonByEnvironment(Configuration.CN_PROVIDERS)
                               .getJsonObject(Configuration.CN_PROVIDERS_FLICKR)
                               .getString(Configuration.CN_FLICKR_PHOTOSET_ID);   
    } 
    catch (Exception e) {
      return Configuration.CD_FLICKR_PHOTOSET_ID;
    }
  }  
}
