package com.wkbotanica.api.config;

import static org.apache.commons.lang3.StringUtils.isBlank;

import com.wkbotanica.api.constant.Configuration;

import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

public enum WkbConfig {
	INSTANCE;
	private JsonObject config;
	private static final Logger logger = LoggerFactory.getLogger(WkbConfig.class.getName());

	/**
	 * @return a {@link io.vertx.core.json.JsonObject} value from the configuration of the current environment 
	 */	
	public JsonObject getJsonByEnvironment(final String key) {
		logger.debug("getValueByEnvironment::"+key);
		return loadEnvironment().getJsonObject(key.toLowerCase());
	}

	/**
	 * @return a String value from the configuration of the current environment 
	 */	
	public String getStringByEnvironment(final String key, final String defaultValue ) {
	  try {
		String s = loadEnvironment().getString(key.toLowerCase());
		return s == null ? defaultValue : s;		
	  } catch ( Exception e ) {
		logger.warn("getStringByEnvironment return default error because of a missing configuration");
		logger.debug("getStringByEnvironment managed exception:{}",e.getMessage());
		return defaultValue;  
	  }
	}

	/**
	 * @return a String value from base configuration ( same for all environment ) 
	 */
	public String getString(final String key, final String defaultValue ) {
		try {
			String s = this.config.getString(key.toLowerCase());
			return s == null ? defaultValue : s;		
		} catch ( Exception e) {
			logger.warn("getString return default error because of a missing configuration");
			logger.debug("getString managed exception:{}",e.getMessage());
			return defaultValue;  
		}
	}

	/**
	 * return the whole config as {@link io.vertx.core.json.JsonObject}
	 */
	public JsonObject getConfig() {
		logger.debug("WkbConfig::getConfig");
		return config;
	}

	/**
	 * Allow to set the config as {@link io.vertx.core.json.JsonObject}
	 * Primary used by WkbLauncher ( a class that extends VertxCommandLauncher implements VertxLifecycleHooks)
	 * The Launcher in the afterConfigParsed can set the config in WkbConfig
	 */
	public void setConfig(final JsonObject config) {
		logger.debug("WkbConfig::setConfig");
		if ( config == null ) {
			logger.debug("WkbConfig::setConfig::GRAVE! config vale null");
		}
		else {
			this.config = config;
			logger.debug("WkbConfig::setConfig:"+this.config.toString());
		}
	}

	private JsonObject loadEnvironment() {
		logger.debug("WkbConfig::loadEnvironment ...");
		JsonObject jEnv = null; 
		if (this.config != null) {
			jEnv = this.config.getJsonObject(getEnvironment());
		} 
		else {
			logger.debug("WkbConfig::loadEnvironment::GRAVE! this.config vale null");
		}
		if ( jEnv == null ) {
			jEnv = new JsonObject();
		} 
		logger.debug("WkbConfig::loadEnvironment:"+jEnv.toString());
		return jEnv;
	}

	/** 
	 * Gets the environment in a free String form 
	 * if the ENV system enviroment variable is not definet it assumes `Constants.ENVIRONMENT_DEFAULT.toLowerCase()` as the default
	 * Please note that , by design, is not defined an explict enum of possible environment
	 * because of flexibility to choose whatever environment you need, nevertheless there is 
	 * a Constants.ENVIRONMENT_DEFAULT ('production') because of at least one environment 
	 * must exists
	 * @return a String rapresenting the current environment
	*/
	private String getEnvironment() {
		logger.debug("WkbConfig::getEnvironment ...");
		String environment = System.getenv(Configuration.ENVIRONMENT);
		environment = !isBlank(environment) ? environment.trim().toLowerCase()
				: Configuration.ENVIRONMENT_DEFAULT.toLowerCase();
		
		logger.debug("WkbConfig::getEnvironment is:"+environment);
		return environment;
	}
}