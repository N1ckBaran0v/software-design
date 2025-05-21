package traintickets.dataaccess.mongo.repository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.utility.DockerImageName;
import traintickets.dataaccess.mongo.connection.MongoConfig;
import traintickets.dataaccess.mongo.connection.MongoExecutor;

abstract class MongoIT {
    protected MongoDBContainer container;
    protected MongoExecutor mongoExecutor;

    @BeforeEach
    void setUp() {
        container = new MongoDBContainer(DockerImageName.parse("mongo:8.0"));
        container.start();
        var params = new MongoConfig(container.getHost(), container.getFirstMappedPort(), "test", null, null, 1, null);
        mongoExecutor = new MongoExecutor(params);
        insertData();
    }

    abstract protected void insertData();

    @AfterEach
    void tearDown() {
        container.stop();
        container.close();
    }
}
