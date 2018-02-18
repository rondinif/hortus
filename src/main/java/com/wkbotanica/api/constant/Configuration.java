package com.wkbotanica.api.constant;

/**
   * The Configuration class provide the constants used in configuration.
   * naming convention: 
   *  - prefixed: 
   * 	- CN_ : short for CONFIGURATION_NAME, is always a {@link java.lang.String}, contains the NAME of a configuration item
   * 	- CD_ : short for CONFIGURATION_DEFAULT, should a @see <a href="https://docs.oracle.com/javase/tutorial/java/nutsandbolts/datatypes.html">java primitive types</a> with the default value of the configuration item
   * - byte	,default: 0
   *        - short	,default:0
   *        - int	,default:0
   *        - long	,default:0L
   *        - float	,default:0.0f
   *        - double	,default:0.0d
   *        - char	,default:'\u0000'
   *        - String (or any object) ,default:   	null
   *        - boolean ,default:	false
   */ 
  public final class Configuration {
	  public static final String ENVIRONMENT = "ENV";
	  public static final String ENVIRONMENT_DEFAULT = "production";
	  
	  public static final String CN_WKBOTANICA_API_CONTEXT_PATH = "context_path";
	  public static final String CD_WKBOTANICA_API_CONTEXT_PATH = "/api/greetings";

	  public static final String CN_DNS = "dns";
	  public static final String CN_DNS_ADD_LOCALS = "add_locals";
	  public static final Boolean CD_DNS_ADD_LOCALS = false;

	  public static final String CN_HTTP = "http";
	  public static final String CN_HTTP_PORT = "port";
	  public static final int    CD_HTTP_PORT = 8080;

	  public static final String CN_PROVIDERS = "providers";
	  public static final String CN_PROVIDERS_FLICKR = "flickr";
	  public static final String CD_FLICKR_API_KEY = "*********";
	  public static final String CN_FLICKR_API_KEY = "api_key";
	  
	  public static final String CD_FLICKR_PHOTOSET_ID = "72157665903273889";
	  public static final String CN_FLICKR_PHOTOSET_ID = "phoset_id";

	  public static final String CD_FLICKR_USER_ID = "77812119%40N02";
	  public static final String CN_FLICKR_USER_ID = "user_id";

/* ... placeholder for future configurations
	public static final String CONTEXT_PATH = "context_path";
	public static final String REFLECTION_PACKAGE_NAME = "reflection_package_name";
	public static final String VERTICLE_PACKAGE_NAME = "verticle_package_name";
	public static final String HTTP_VERTICLE_INSTANCE = "http_server_verticle_instance";
	public static final String DATABSE_VERTICLE_INSTANCE = "http_server_verticle_instance";
	public static final String MONGO_CONFIG = "mongoConfig";
	public static final String RESPONSE_SUCCESS = "success";
	public static final String RESPONSE_FAILED = "fail";
	public static final String DB_EVENT_ADDRESS = "db_queue";
	public static final String MONGO_COLLECTION_NAME = "db_name";
	public static final String REQUEST_HEADER_NAME = "X-CORRELATION-ID";
*/
}