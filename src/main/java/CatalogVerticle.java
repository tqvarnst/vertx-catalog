import io.vertx.core.AbstractVerticle;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.jdbc.JDBCClient;
import io.vertx.ext.sql.SQLClient;
import io.vertx.ext.sql.SQLConnection;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;

public class CatalogVerticle extends AbstractVerticle {

    private static final String sql = "select itemId, name, description, price from catalog";
    private SQLClient client;

    @Override
    public void start() throws Exception {
        System.out.println("CATALOG VERTICLE IS STARTING");

        JsonObject config = new JsonObject()
                .put("url", "jdbc:hsqldb:mem:test?shutdown=true")
                .put("driver_class", "org.hsqldb.jdbcDriver")
                .put("max_pool_size", 30);

        client = JDBCClient.createShared(vertx, config);
        Router router = Router.router(vertx);

        router.get("/services/catalog").handler(this::getProducts);
        
        vertx.createHttpServer().requestHandler(router::accept).listen(8888);


    }

    private void getProducts(RoutingContext rc) {

        client.getConnection(res ->{
                if(res.succeeded()) {
                    SQLConnection conn = res.result();
                    conn.query(sql,query -> {
                        if(query.succeeded()) {
                            JsonArray products = new JsonArray();
                            query.result().getResults().stream().forEach(rsArr -> {
                                products.add(new JsonObject()
                                    .put("itemId",rsArr.getValue(0))
                                    .put("name",rsArr.getValue(1))
                                    .put("description",rsArr.getValue(2))
                                    .put("price",rsArr.getValue(3))
                                );
                            });
                            rc.response().end(products.encodePrettily());
                        } else {
                            rc.response().end("Failed");
                            System.out.println("query.cause() = " + query.cause());
                        }
                    });
                }
            } );
    }
}
