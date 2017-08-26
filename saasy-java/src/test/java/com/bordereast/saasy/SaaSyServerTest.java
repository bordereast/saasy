package com.bordereast.saasy;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;


@RunWith(VertxUnitRunner.class)
public class SaaSyServerTest {

  private Vertx vertx;

  @Before
  public void setUp(TestContext tc) {
    vertx = Vertx.vertx();
    //vertx.deployVerticle(SaaSyServer.class.getName(), tc.asyncAssertSuccess());
    JsonObject config = new JsonObject();
    config.put(Constants.CONFIG_IS_TESTING, true);
    DeploymentOptions options = new DeploymentOptions().setConfig(config);
	vertx.deployVerticle(SaaSyServer.class.getName(), options); 
  }

  @After
  public void tearDown(TestContext tc) {
    vertx.close(tc.asyncAssertSuccess());
  }

  @Test
  public void testThatTheServerIsStarted(TestContext tc) {
    Async async = tc.async();
    vertx.createHttpClient().getNow(8080, "localhost", "/", response -> {
      tc.assertEquals(response.statusCode(), 200);
      response.bodyHandler(body -> {
        tc.assertTrue(body.length() > 0);
        async.complete();
      });
    });
  }

}