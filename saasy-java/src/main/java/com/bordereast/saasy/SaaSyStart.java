package com.bordereast.saasy;

import java.io.File;
import java.lang.reflect.Method;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

import com.arangodb.ArangoDB;
import com.arangodb.entity.BaseDocument;
import com.arangodb.entity.DocumentField;
import com.arangodb.entity.DocumentField.Type;
import com.arangodb.velocypack.VPackSlice;
import com.bordereast.saasy.cache.ResourceCache;
import com.bordereast.saasy.file.ResourceFileWatcher;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.json.JsonObject;

public class SaaSyStart {
    private static Vertx vertx;
    private static ResourceFileWatcher watcher;

    public static void main(String args[]) {

        /*
         * final String dbName = "testdb"; final String collName = "testCollection";
         * 
         * ArangoDB arangoDB = new
         * ArangoDB.Builder().user("saasy").password("saasy").build();
         * 
         * // Delete existing database
         * 
         * 
         * 
         * 
         * // Test collection creation try {
         * arangoDB.db(dbName).createCollection(collName);
         * System.out.println("Created collection " + collName); } catch (Exception e) {
         * System.err.println("Did not create collection " + collName); }
         * 
         * // Test custom class document insertion String key1 = null; try { MyObject
         * myObject = new MyObject("Homer", 38); key1 =
         * arangoDB.db(dbName).collection(collName).insertDocument(myObject).getKey();
         * System.out.println("Inserted new document as MyObject. key: " +
         * myObject.getKey() + ", " + key1); } catch (Exception e) {
         * System.err.println("Did not insert new document"); }
         * 
         * // Test BaseDocument class document insertion String key2 = null; try {
         * BaseDocument myBaseDocument = new BaseDocument();
         * myBaseDocument.addAttribute("name", "Paul");
         * myBaseDocument.addAttribute("age", 23); key2 =
         * arangoDB.db(dbName).collection(collName).insertDocument(myBaseDocument).
         * getKey(); System.out.println("Inserted new document as BaseDocument. key: " +
         * myBaseDocument.getKey() + ", " + key2); } catch (Exception e) {
         * System.err.println("Did not insert new document"); }
         * 
         * // Test read as VPackSlice String keyToRead1 = key1; VPackSlice doc1 =
         * arangoDB.db(dbName).collection(collName).getDocument(keyToRead1,
         * VPackSlice.class); if (doc1 != null) System.out.println("Open document " +
         * keyToRead1 + "  VPackSlice: " + doc1.get("name").getAsString() + " " +
         * doc1.get("age").getAsInt()); else
         * System.err.println("Could not open the document " + keyToRead1 +
         * " using VPackSlice");
         * 
         * // Test read as BaseDocument String keyToRead2 = key1; BaseDocument doc2 =
         * arangoDB.db(dbName).collection(collName).getDocument(keyToRead2,
         * BaseDocument.class); if (doc2 != null) System.out.println("Open document " +
         * keyToRead2 + " as BaseDocument: " + doc2.getAttribute("name") + " " +
         * doc2.getAttribute("age")); else
         * System.err.println("Could not open the document " + keyToRead2 +
         * " as BaseDocument");
         * 
         * // Test read as MyObject String keyToRead3 = key1; MyObject doc3 =
         * arangoDB.db(dbName).collection(collName).getDocument(keyToRead3,
         * MyObject.class); if (doc3 != null) System.out.println("Open document " +
         * keyToRead3 + " as MyObject: " + doc3.getName() + " " + doc3.getAge()); else
         * System.err.println("Could not open the document " + keyToRead3 +
         * " as MyObject");
         */

        VertxOptions voptions = new VertxOptions();
        voptions.setMaxEventLoopExecuteTime(Long.MAX_VALUE);

        vertx = Vertx.vertx(voptions);

        // nasty classpath manipulation
        addPath("D:\\apps\\java\\saasy-java\\locales\\");
        reloadResources("D:\\apps\\java\\saasy-java\\locales\\");
        try {
            watcher.start();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        // ensure we stop the watcher
        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                watcher.stop();
            }
        });

        // TODO: get from config file
        JsonObject config = new JsonObject();
        config.put(Constants.CONFIG_IS_TESTING, false);
        config.put(Constants.CONFIG_IS_DEBUG, true);
        config.put(Constants.CONFIG_TEMPLATE_ROOT, "D:\\apps\\java\\saasy-java\\templates\\");
        config.put(Constants.CONFIG_LOCALE_ROOT, "D:\\apps\\java\\saasy-java\\locales\\");
        config.put(Constants.CONFIG_CACHE_TTL, 6000L);
        config.put(Constants.CONFIG_JWT_SECRET, "hgXQqBpW5fZBAN5RW6Gg0OcwMFle8uE8");

        // Bootstrap default tenant
        Bootstrap.init("saasy");

        DeploymentOptions options = new DeploymentOptions().setConfig(config).setInstances(1);

        vertx.deployVerticle(SaaSyServer.class.getName(), options, res -> {
            if (res.succeeded()) {
                System.out.println("Deployment id is: " + res.result());
            } else {
                System.out.println("Deployment failed!");
            }
        });
    }

    private static void reloadResources(String basePath) {
        watcher = new ResourceFileWatcher.Builder().addDirectories(basePath).setPreExistingAsCreated(true)
                .build(new ResourceFileWatcher.Listener() {
                    @Override
                    public void onEvent(ResourceFileWatcher.Event event, Path path) {
                        switch (event) {
                        case ENTRY_CREATE:
                            System.out.println(path + " created.");
                            break;

                        case ENTRY_MODIFY:
                            System.out.println(path + " modified.");
                            if (path.toString().endsWith(".properties")) {
                                String strPath = path.toFile().getName();

                                List<String> args = Arrays.asList(strPath.split("\\."));
                                String key = args.get(0).toLowerCase();
                                if (ResourceCache.Resources.containsKey(key)) {
                                    ResourceCache.Resources.remove(key);
                                    System.out.println("Cleared Resource Cache");
                                }
                            }
                            break;

                        case ENTRY_DELETE:
                            System.out.println(path + " deleted.");
                            break;
                        }
                    }

                });
    }

    private static void addPath(String s) {
        try {
            File f = new File(s);
            URI u = f.toURI();
            URLClassLoader urlClassLoader = (URLClassLoader) ClassLoader.getSystemClassLoader();
            Class<URLClassLoader> urlClass = URLClassLoader.class;
            Method method = urlClass.getDeclaredMethod("addURL", new Class[] { URL.class });
            method.setAccessible(true);

            method.invoke(urlClassLoader, new Object[] { u.toURL() });
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
