package com.wkbotanica.api.vertx;
import java.util.ArrayList;

import com.wkbotanica.api.config.WkbConfig;
import com.wkbotanica.api.constant.Configuration;

import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.dns.AddressResolverOptions;
import io.vertx.core.impl.launcher.VertxCommandLauncher;
import io.vertx.core.impl.launcher.VertxLifecycleHooks;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.core.logging.SLF4JLogDelegateFactory;


public class WkbLauncher extends VertxCommandLauncher implements VertxLifecycleHooks {
	private static final Logger logger = LoggerFactory.getLogger(HttpApplication.class.getName());

	// make sure you have the logger
	static {
		System.out.println("VertxCommandLauncher::static");

		if (System.getProperty("vertx.logger-delegate-factory-class-name") == null) {
			System.setProperty("vertx.logger-delegate-factory-class-name",
					SLF4JLogDelegateFactory.class.getCanonicalName());
		}
		System.out.println("VertxCommandLauncher::static::"+System.getenv(Configuration.ENVIRONMENT));

		logger.info("testing logger");
	}

	@Override
	public void afterConfigParsed(JsonObject config) {
        System.out.println("WkbLauncher::afterConfigParsed");
		WkbConfig.INSTANCE.setConfig(config);
	}

	@Override
	public void beforeStartingVertx(VertxOptions options) {
	System.out.println("WkbLauncher::beforeStartingVertx start[");

	if ( shouldAddLocalDns() ) {
		System.out.println("WkbLauncher::beforeStartingVertx add options by wkbotanica_add_local_dns setting");
		options.setWarningExceptionTime(1500000000)
			.setAddressResolverOptions(new AddressResolverOptions()
			.addServer("192.168.0.1")
			.addServer("192.168.2.1")
			.setMaxQueries(5)
			.setCacheNegativeTimeToLive(0) // discard failed DNS lookup results immediately
			.setCacheMaxTimeToLive(0) // support DNS based service resolution
			.setRotateServers(true)
			.setQueryTimeout(8000));
	}

	System.out.println("WkbLauncher::beforeStartingVertx end ]");

	}

	@Override
	public void afterStartingVertx(Vertx vertx) {
        System.out.println("WkbLauncher::afterStartingVertx");
	}

	@Override
	public void beforeDeployingVerticle(DeploymentOptions deploymentOptions) {
        System.out.println("WkbLauncher::beforeDeployingVerticle");
	}

	@Override
	public void beforeStoppingVertx(Vertx vertx) {
        System.out.println("WkbLauncher::beforeStoppingVertx");
	}

	@Override
	public void afterStoppingVertx() {
        System.out.println("WkbLauncher::afterStoppingVertx");
	}

	@Override
	public void handleDeployFailed(Vertx vertx, String mainVerticle, DeploymentOptions deploymentOptions,
			Throwable cause) {
        System.out.println("WkbLauncher::handleDeployFailed");
		vertx.close();
	}

	public static void main(String[] args) {
		final String ARG_TO_MOVE="redeploy-termination-period";
		String argToMove = null;
		System.out.println("WkbLauncher main starting.");
		if (args == null) {
			System.out.println("WkbLauncher main without args dispatch defaults args.");
			String[] newArgs = {"run", HttpApplication.class.getName(), "-conf", "booster-rest-http.json"};
			new WkbLauncher().dispatch(newArgs);			
		}
		else { // MUST CHECK THE ORDER OF ARGS AND THE PRESENCE OF ARGS TO MOVE AT THE END OF THE LIST
			System.out.println("WkbLauncher::main::"+args.length+" arguments received");
			ArrayList<String> newArgsList = new ArrayList<String>();
			int inx;
			for (inx = 0; inx < args.length; ++inx) {
				final String ARG_VALUE = args[inx];
				if (ARG_VALUE.startsWith(ARG_TO_MOVE)) {
					System.out.println("WkbLauncher::main::"+ARG_VALUE+ " ==>this arg was buffered for moving it at the end of the argument list");
					argToMove = ARG_VALUE; 
				} 
				else {
					System.out.println("WkbLauncher::main::"+ARG_VALUE);
					newArgsList.add(ARG_VALUE);
				} 
			}
			if (argToMove != null) {
				System.out.println("WkbLauncher::main dispatch with new list of args");
				newArgsList.add(argToMove);
				String[] newArgs = new String[newArgsList.size()];
				newArgs = newArgsList.toArray(newArgs);
				new WkbLauncher().dispatch(newArgs);
			}
			else {
				System.out.println("WkbLauncher::main dispatch with originals args");
				new WkbLauncher().dispatch(args);			
			}
		}
	}

/* NOT JET USED
	public static void executeCommand(String cmd, String... args) {
		System.out.println("WkbLauncher::executeCommand");
		new WkbLauncher().execute(cmd, args);
	}
*/




	/* 
	CLASS CONFIGURATION 
	*/
	private static boolean shouldAddLocalDns() {
		try {
		  return WkbConfig.INSTANCE.getJsonByEnvironment(Configuration.CN_DNS).getBoolean(Configuration.CN_DNS_ADD_LOCALS);
		} 
		catch (Exception e) {
		  return Configuration.CD_DNS_ADD_LOCALS;
		}
	  }  
		
}