package com.wkbotanica.api.vertx;

import io.vertx.core.Vertx;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import io.vertx.ext.web.client.WebClient;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.core.logging.Logger;
// import org.slf4j.Logger;

import static com.wkbotanica.api.vertx.HttpApplication.template;
import com.wkbotanica.api.config.Config;

@RunWith(VertxUnitRunner.class)
public class HttpApplicationTest {

    private Vertx vertx;
    private WebClient client;
    private static final Logger logger = LoggerFactory.getLogger(HttpApplicationTest.class.getName());
    
    @Before
    public void before(TestContext context) {
        System.setProperty("vertx.disableFileCPResolving","true");
        System.setProperty("vertx.disableFileCaching", "true");
        logger.info("before beging...");
        try {
            logger.info("step-1: try to instantiate vertx");
            vertx = Vertx.vertx();
            vertx.exceptionHandler(context.exceptionHandler());
            logger.info("step-2: try to deploy verticle");
            vertx.deployVerticle(HttpApplication.class.getName(), context.asyncAssertSuccess());
        } catch (Exception e)
        {
            logger.error("impossibile instanziare vertx");            
            logger.error(e.getMessage());
        }
        if (vertx == null) {
            logger.error("impossibile proseguire con il test");            
        } else {
            try {
                    logger.info("step-3: try to create the client");
                    client = WebClient.create(vertx);
                    logger.info("step-3: created client:" + client.toString());
                } catch (Exception ec) {
                    logger.error("impossibile instanziare il client");            
                    logger.error(ec.getMessage());
                }
        }
        logger.info("before completed");
    }

    @After
    public void after(TestContext context) {
        logger.info("after beging...");
        vertx.close(context.asyncAssertSuccess());
        logger.info("after completed");
    }

    @Test public void callGreetingTest(final TestContext context) {
        try {
            logger.info("callGreetingTest: ContestPath={0}; HttpPort={1}",Config.getContextPath(),Config.getHttpPort());
        } catch (Exception eLog) {
            System.out.println("########callGreetingTest::GRAVE:"+eLog.getMessage());            
        }
        try {
            // Send a request and get a response
            Async async = context.async();
            client.get(Config.getHttpPort(), "localhost", Config.getContextPath())
                .send(resp -> {
                    try {
                        logger.info("processing response");
                        context.assertTrue(resp.succeeded());
                        context.assertEquals(resp.result().statusCode(), 200);
                        String content = resp.result().bodyAsJsonObject().getString("content");
                        context.assertEquals(content, String.format(template, "World"));
                        async.complete();
                    } catch (Exception  e) {
                        logger.error(e.getMessage());
                    }
                });
            } catch (Exception eTest) {
                System.out.println("########callGreetingTest::GRAVE:"+eTest.getMessage());
            }
    }

    @Test
    public void callGreetingWithParamTest(TestContext context) {
        System.out.println("########callGreetingWithParamTest");
        logger.info("callGreetingWithParamTest: ContestPath={0}; HttpPort={1}",Config.getContextPath(),Config.getHttpPort());         // Send a request and get a response
        Async async = context.async();
        client.get(Config.getHttpPort(), "localhost", Config.getContextPath()+"?name=Charles")
            .send(resp -> {
                context.assertTrue(resp.succeeded());
                context.assertEquals(resp.result().statusCode(), 200);
                String content = resp.result().bodyAsJsonObject().getString("content");
                context.assertEquals(content, String.format(template, "Charles"));
                async.complete();
            });
    }

}
