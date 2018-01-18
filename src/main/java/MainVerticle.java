import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.jdbc.JDBCClient;
import io.vertx.ext.sql.SQLClient;
import io.vertx.ext.sql.SQLConnection;
import io.vertx.ext.web.Router;

public class MainVerticle extends AbstractVerticle{
    private static final String CREATE_TABLE_SQL = "DROP TABLE IF EXISTS catalog;\n" +
            "\n" +
            "CREATE TABLE catalog (\n" +
            "  itemId VARCHAR(256) NOT NULL PRIMARY KEY,\n" +
            "  name VARCHAR(256),\n" +
            "  description VARCHAR(2560),\n" +
            "  price DOUBLE PRECISION\n" +
            ");";

    private static final String INIT_SQL = "insert into catalog (itemId, name, description, price) values ('329299', 'Red Fedora', 'Official Red Hat Fedora', 34.99);\n" +
            "insert into catalog (itemId, name, description, price) values ('329199', 'Forge Laptop Sticker', 'JBoss Community Forge Project Sticker', 8.50);\n" +
            "insert into catalog (itemId, name, description, price) values ('165613', 'Solid Performance Polo', 'Moisture-wicking, antimicrobial 100% polyester design wicks for life of garment. No-curl, rib-knit collar; special collar band maintains crisp fold; three-button placket with dyed-to-match buttons; hemmed sleeves; even bottom with side vents; Import. Embroidery. Red Pepper.',17.80);\n" +
            "insert into catalog (itemId, name, description, price) values ('165614', 'Ogio Caliber Polo', 'Moisture-wicking 100% polyester. Rib-knit collar and cuffs; Ogio jacquard tape inside neck; bar-tacked three-button placket with Ogio dyed-to-match buttons; side vents; tagless; Ogio badge on left sleeve. Import. Embroidery. Black.', 28.75);\n" +
            "insert into catalog (itemId, name, description, price) values ('165954', '16 oz. Vortex Tumbler', 'Double-wall insulated, BPA-free, acrylic cup. Push-on lid with thumb-slide closure; for hot and cold beverages. Holds 16 oz. Hand wash only. Imprint. Clear.', 6.00);\n" +
            "insert into catalog (itemId, name, description, price) values ('444434', 'Pebble Smart Watch', 'Smart glasses and smart watches are perhaps two of the most exciting developments in recent years.', 24.00);\n" +
            "insert into catalog (itemId, name, description, price) values ('444435', 'Oculus Rift', 'The world of gaming has also undergone some very unique and compelling tech advances in recent years. Virtual reality, the concept of complete immersion into a digital universe through a special headset, has been the white whale of gaming and digital technology ever since Geekstakes Oculus Rift GiveawayNintendo marketed its Virtual Boy gaming system in 1995.Lytro',106.00 );\n" +
            "insert into catalog (itemId, name, description, price) values ('444436', 'Lytro Camera', 'Consumers who want to up their photography game are looking at newfangled cameras like the Lytro Field camera, designed to take photos with infinite focus, so you can decide later exactly where you want the focus of each image to be.', 44.30);";
    @Override
    public void start(Future<Void> fut) throws Exception {
        JsonObject config = new JsonObject()
                .put("url", "jdbc:hsqldb:mem:test?shutdown=true")
                .put("driver_class", "org.hsqldb.jdbcDriver")
                .put("max_pool_size", 30);

        SQLClient client = JDBCClient.createShared(vertx, config);

        client.getConnection(res -> {
            if(res.succeeded()) {
                SQLConnection connection = res.result();
                connection.execute(CREATE_TABLE_SQL, res2 -> {
                    if (res2.succeeded()) {
                        connection.execute(INIT_SQL, res3 -> {
                            if(res3.succeeded()) {
                                System.out.println("INITIATED DB");
                                vertx.deployVerticle(CatalogVerticle.class.getName());
                                fut.complete();
                            } else {
                                fut.fail(res3.cause());
                            }
                        });
                    } else {
                        fut.fail(res2.cause());
                    }
                } );

            } else {
                fut.fail(res.cause());
            }
        });



     /*   Router router = Router.router(vertx);
        router.get("/hello").handler(req -> {
            req.response().end("Hello World");
        });
        vertx.createHttpServer().requestHandler(router::accept).listen(8080);
        fut.complete(); */
    }

    @Override
    public void stop() throws Exception {

    }
}
