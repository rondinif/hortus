package com.wkbotanica.api.vertx;

import static io.vertx.core.http.HttpHeaders.CONTENT_TYPE;

import com.wkbotanica.api.config.Config;
import com.wkbotanica.api.config.Utils;
import com.wkbotanica.api.constant.ReturnCode;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.client.HttpResponse;
// io.vertx.ext.web.client 
import io.vertx.ext.web.client.WebClient;
import io.vertx.ext.web.client.WebClientOptions;
import io.vertx.ext.web.codec.BodyCodec;
import io.vertx.ext.web.handler.StaticHandler;

public class HttpApplication extends AbstractVerticle {
  private static final Logger logger = LoggerFactory.getLogger(HttpApplication.class.getName());

  protected static final String template = "Hello, %s!";

  private WebClient client;

  @Override
  public void start(Future<Void> future) {

    // Create a router object.
    Router router = Router.router(vertx);

    router.get("/api/config/config").handler(this::dumpConfig);
    router.get("/api/config/environment").handler(this::dumpEnv);
    router.get("/api/config/properties").handler(this::dumpProps);
    router.get("/api/images").handler(this::images);
    router.get(Config.getContextPath()).handler(this::greeting); // "/api/greetings"
    router.get("/*").handler(StaticHandler.create());

    // Create the HTTP server and pass the "accept" method to the request handler.
    vertx.createHttpServer().requestHandler(router::accept).listen(
        // Retrieve the port from the configuration, default to 8080. config().getInteger("http.port", 8080)  
        Config.getHttpPort(), ar -> {
          logger.info("Server starting ..");
          if (ar.succeeded()) {
            logger.info("Server started on port: " + ar.result().actualPort());
            logger.info("Found API Key: " + Config.getAPIKey());
          }
          future.handle(ar.mapEmpty());
        });

    client = WebClient.create(vertx,
        new WebClientOptions().setSsl(true).setTrustAll(true).setDefaultPort(443).setKeepAlive(false)
    // .setDefaultHost("api.flickr.com")
    ); // , new WebClientOptions().setFollowRedirects(false));
  }

  private void greeting(RoutingContext rc) {
    String name = rc.request().getParam("name");
    if (name == null) {
      name = "World";
    }

    JsonObject response = new JsonObject().put("content", String.format(template, name));

    rc.response().putHeader(CONTENT_TYPE, "application/json; charset=utf-8").end(response.encodePrettily());
  }

  /**
   * dump configuration to the log and returns ok
   * handler for  "/api/config/config"
   */
  private void dumpConfig(RoutingContext rc) {
    JsonObject response = new JsonObject();
    try {
      Utils.dumpConfig();
      response.put("content", ReturnCode.OK);
    } catch (Exception e) {
      logger.error("There is a problem in dumpConfig", e);
      response.put("error", ReturnCode.KO + ": " + e.getMessage());
    }

    rc.response().putHeader(CONTENT_TYPE, "application/json; charset=utf-8").end(response.encodePrettily());
  }

  /**
   * handler for  "/api/config/environment"
   * dump system environment to the log and returns ok
   */
  private void dumpEnv(RoutingContext rc) {
    JsonObject response = new JsonObject();
    try {
      Utils.dumpSystemEnv();
      response.put("content", ReturnCode.OK);
    } catch (Exception e) {
      logger.error("There is a problem in dumpEnv", e);
      response.put("error", ReturnCode.KO + ": " + e.getMessage());
    }

    rc.response().putHeader(CONTENT_TYPE, "application/json; charset=utf-8").end(response.encodePrettily());
  }

  /**
   * handler for  "/api/config/properties"
   * dump system properties to the log and returns ok
   */
  private void dumpProps(RoutingContext rc) {
    JsonObject response = new JsonObject();
    try {
      Utils.dumpSystemProperties();
      response.put("content", ReturnCode.OK);
    } catch (Exception e) {
      logger.error("There is a problem in dumpProps", e);
      response.put("error", ReturnCode.KO + ": " + e.getMessage());
    }

    rc.response().putHeader(CONTENT_TYPE, "application/json; charset=utf-8").end(response.encodePrettily());
  }

  /**
   * handler dor "/api/images"
   * asynchronously call the Flickr API via dedicated private method invokeFlickrApiAsync
   */
  private void images(RoutingContext rc) {
    logger.trace("exit from void images() !");

    /**
     * Step1: extract and check parameters from the RoutingContext and assume default for missing inputs
     */
    String name = rc.request().getParam("name");
    if (name == null) {
      name = "Ronda"; //TODO define meaningfull arguments and default values
    }

    /**
     * Step2: invokeFlickrApiAsync as a io.vertx.core.Future that returns some JSonObject
     * //TODO passa parmeters defined at previous steps
     */
    Future<JsonObject> future = invokeFlickrApiAsync();
    /**
     * Step3: set the handler that will process the result and prepare a use the RoutingContext to provide the response 
     */
    future.setHandler(flickrApiResult -> {
      if (flickrApiResult.succeeded()) {
        logger.debug("invokeFlickrApiAsync succeeded!");
        rc.response().putHeader(CONTENT_TYPE, "application/json; charset=utf-8")
            .end(flickrApiResult.result().encodePrettily());
      } else {
        logger.debug("invokeFlickrApiAsync not succeeded");
        rc.fail(500);
      }
    });

    logger.trace("exit from void images() !");
  }

  /**
   * invokeFlickrApiAsync is the dedicated private method invokeFlickrApiAsync
   * TODO .. isolate in his own class
   * */
  private Future<JsonObject> invokeFlickrApiAsync() {
    Future<JsonObject> future = Future.future();
    /* 
    Step0: check pre-requisite
    */
    if (client == null) {
      logger.error("Something went wrong: WebClient instance is null yet!");
      JsonObject err = new JsonObject();
      //TODO: maintain a list of ERRORCODE and BUGCODE  
      err.put("error", ReturnCode.KO
          + ": WebClient instance is still null, this is possible BUG code 000000, please report the problem to the com.wkbotanica.api team");
      /* TESTED: response sample:
      {
       "error" : "KO: WebClient instance is still null, this is possible BUG code 000000, please report the problem to the com.wkbotanica.api team"
      }
      */
      future.complete(err);
      return future;
    }

    /*
    Step1: forge a valid String with the URL to call `flickr.photosets.getList`
    client.getAbs("https://api.flickr.com/services/rest/?method=flickr.photosets.getList&api_key="
    +FLICKR_API_KEY
    +"&user_id="+FLICKR_USER_ID+"&format=json&nojsoncallback=1")
    */
    //TODO Use a template instead of concatenating strings
    String sUrl = "https://api.flickr.com/services/rest/?method=flickr.photosets.getPhotos&api_key="
        + Config.getFlickrApiKey() + "&photoset_id=" + Config.getFickrPhotosetId() + "&user_id="
        + Config.getFlickrUserId() + "&format=json&nojsoncallback=1";
    logger.info(sUrl); // flickr.photosets.getPhotos

    /*
    Step2: use WebClient to get a call to the Flickr API
           and registrer a calback
    */
    client.getAbs(sUrl).as(BodyCodec.jsonObject()).send(ar -> {
      if (ar.succeeded()) {
        HttpResponse<JsonObject> response = ar.result();
        JsonObject fickrApiResponseBody = response.body();
        logger.info("Received response with status code:" + response.statusCode() + " with body: " + fickrApiResponseBody);
        /*
          Step3: set the result by trasforming the response of Flickr API to our resonse 
        */
        future.complete(transformFlickrApiResponseToResult(fickrApiResponseBody));
      } else {
        logger.error("Something went wrong " + ar.cause().getMessage());
        ar.cause().printStackTrace();
        JsonObject err = new JsonObject();
        err.put("content", ar.cause().getMessage());
        future.complete(err);
      }
    });
    /*
    Step4: return Future with the asyncResult of call to the Flickr API or the ERROR 
    TESTED: sample problem - Flickr UserId misconfigurated 
    {
       "content" : "ERROR code:2 message:User not found"
    }
    TESTED: sample good: 
    {
      "metadata" : [ {
        "url" : "https://farm5.staticflickr.com/4548/38125301556_5938930a36_b.jpg"
      }, {
        "url" : "https://farm5.staticflickr.com/4560/38149907582_7bab4d0c21_b.jpg"
      }, {
        "url" : "https://farm5.staticflickr.com/4526/37471560534_de0e43c2bf_b.jpg"
      }, {
        "url" : "https://farm5.staticflickr.com/4471/38181978221_0548b0bde3_b.jpg"
      }, {
        "url" : "https://farm5.staticflickr.com/4576/26405557189_675e23e83d_b.jpg"
      }, {
        "url" : "https://farm5.staticflickr.com/4572/38150186852_e99c2f3c48_b.jpg"
      }, {
        "url" : "https://farm5.staticflickr.com/4469/37471791354_caf51d57d6_b.jpg"
      } ],
      "content" : "OK"
    }
    */
    return future;
  }

  /**
   * trasform the json returned by the API call to the FLickr API to a json to return as a response 
   * of our API  
   * TODO .. isolate in his own class
   * TODO: finire implementazione
   */
  private JsonObject transformFlickrApiResponseToResult(JsonObject jsIn) {
    JsonObject jsOut = null;
    final String KEY_CODE = "code";
    final String KEY_STAT = "stat";
    final String KEY_MESSAGE = "message";
    final String JPG_URL_TEMPLATE = "https://farm%s.staticflickr.com/%s/%s_%s_b.jpg";
    final String KEY_FARM = "farm";
    final String KEY_SERVER = "server";
    final String KEY_ID = "id";
    final String KEY_SECRET = "secret";
    // 
    try {
      if (jsIn.containsKey(KEY_CODE) && jsIn.containsKey(KEY_STAT)) {
        if (jsIn.getString(KEY_STAT).equalsIgnoreCase("fail")) {
          JsonObject jsErr = new JsonObject();
          jsErr.put("content", "ERROR code:" + jsIn.getInteger(KEY_CODE) + " message:" + jsIn.getString(KEY_MESSAGE));
          return jsErr;
        }
      }

      jsOut = new JsonObject();

      JsonArray jsArrayPhoto = jsIn.getJsonObject("photoset").getJsonArray("photo");
      if (jsArrayPhoto != null) {
        // for (Map.Entry<String, String> entry : getMetadata().entrySet()) {
        JsonArray jsArrayPhotoOut = new JsonArray();

        for (Object photo : jsArrayPhoto) {
          JsonObject metadata = new JsonObject();
          JsonObject jsPhoto = (JsonObject) photo;
          Integer nFarmId = jsPhoto.getInteger(KEY_FARM);
          String sServerId = jsPhoto.getString(KEY_SERVER);
          String sId = jsPhoto.getString(KEY_ID);
          String sSecret = jsPhoto.getString(KEY_SECRET);
          // https://farm{farm-id}.staticflickr.com/{server-id}/{id}_{secret}_[mstzb].jpg
          String sFormattedURL = String.format(JPG_URL_TEMPLATE, nFarmId.toString(), sServerId, sId, sSecret);
          System.out.println(sFormattedURL);
          metadata.put("url", sFormattedURL);
          jsArrayPhotoOut.add(metadata);
        }
        ;
        jsOut.put("metadata", jsArrayPhotoOut);
      }
      jsOut.put("content", "OK");
    } catch (Exception e) {
      JsonObject jsErr = new JsonObject();
      jsErr.put("content", e.getMessage());
      return jsErr;
    }
    return jsOut;
  }
}