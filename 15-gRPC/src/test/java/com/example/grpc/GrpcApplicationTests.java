package com.example.grpc;

import com.example.grpc.proto.*;
import io.grpc.ManagedChannel;
import io.grpc.inprocess.InProcessChannelBuilder;
import io.grpc.inprocess.InProcessServerBuilder;
import io.grpc.stub.StreamObserver;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(properties = {
        "grpc.server.inProcessName=test",
        "grpc.server.port=-1",
        "grpc.client.inProcess.address=in-process:test"
})
class GrpcApplicationTests {

    private com.example.grpc.service.ProductGrpcService service;
    private ManagedChannel channel;

    @BeforeEach
    void setup() throws Exception {
        service = new com.example.grpc.service.ProductGrpcService();
        String serverName = InProcessServerBuilder.generateName();
        // Register the service in the in-process server
        io.grpc.Server server = InProcessServerBuilder.forName(serverName)
                .directExecutor()
                .addService(service)
                .build()
                .start();
        
        channel = InProcessChannelBuilder.forName(serverName)
                .directExecutor()
                .build();
    }

    @AfterEach
    void teardown() {
        if (channel != null) {
            channel.shutdownNow();
        }
    }

    @Test
    void testGetProduct() {
        ProductServiceGrpc.ProductServiceBlockingStub stub = ProductServiceGrpc.newBlockingStub(channel);
        ProductResponse response = stub.getProduct(GetProductRequest.newBuilder().setId("1").build());
        
        assertNotNull(response);
        assertEquals("Laptop", response.getName());
        assertEquals(1000.0, response.getPrice());
    }

    @Test
    void testCreateProduct() {
        ProductServiceGrpc.ProductServiceBlockingStub stub = ProductServiceGrpc.newBlockingStub(channel);
        ProductResponse response = stub.createProduct(CreateProductRequest.newBuilder()
                .setName("Mouse")
                .setPrice(50.0)
                .build());
        
        assertNotNull(response.getId());
        assertEquals("Mouse", response.getName());
        assertEquals(50.0, response.getPrice());
    }

    @Test
    void testListProducts() {
        ProductServiceGrpc.ProductServiceBlockingStub stub = ProductServiceGrpc.newBlockingStub(channel);
        java.util.Iterator<ProductResponse> responses = stub.listProducts(
                ListProductsRequest.newBuilder().setMaxResults(10).build());
        
        assertTrue(responses.hasNext());
        ProductResponse p = responses.next();
        assertNotNull(p.getId());
    }

    @Test
    void testBulkCreateProducts() throws Exception {
        ProductServiceGrpc.ProductServiceStub asyncStub = ProductServiceGrpc.newStub(channel);
        CountDownLatch latch = new CountDownLatch(1);
        final int[] count = {0};
        
        StreamObserver<BulkCreateResponse> responseObserver = new StreamObserver<BulkCreateResponse>() {
            @Override
            public void onNext(BulkCreateResponse value) {
                count[0] = value.getCount();
            }
            @Override
            public void onError(Throwable t) {}
            @Override
            public void onCompleted() {
                latch.countDown();
            }
        };

        StreamObserver<CreateProductRequest> requestObserver = asyncStub.bulkCreateProducts(responseObserver);
        requestObserver.onNext(CreateProductRequest.newBuilder().setName("Kbd1").setPrice(100.0).build());
        requestObserver.onNext(CreateProductRequest.newBuilder().setName("Kbd2").setPrice(150.0).build());
        requestObserver.onCompleted();

        assertTrue(latch.await(5, TimeUnit.SECONDS));
        assertEquals(2, count[0]);
    }

    @Test
    void testPriceNegotiation() throws Exception {
        ProductServiceGrpc.ProductServiceStub asyncStub = ProductServiceGrpc.newStub(channel);
        CountDownLatch latch = new CountDownLatch(1);
        List<PriceResponse> responses = new ArrayList<>();

        StreamObserver<PriceResponse> responseObserver = new StreamObserver<PriceResponse>() {
            @Override
            public void onNext(PriceResponse value) {
                responses.add(value);
            }
            @Override
            public void onError(Throwable t) {}
            @Override
            public void onCompleted() {
                latch.countDown();
            }
        };

        StreamObserver<PriceRequest> requestObserver = asyncStub.priceNegotiation(responseObserver);
        
        // Original price is 1000.0 for product "1". 90% is 900.0
        requestObserver.onNext(PriceRequest.newBuilder().setProductId("1").setSuggestedPrice(800.0).build()); // Should be denied
        requestObserver.onNext(PriceRequest.newBuilder().setProductId("1").setSuggestedPrice(950.0).build()); // Should be accepted
        requestObserver.onCompleted();

        assertTrue(latch.await(5, TimeUnit.SECONDS));
        assertEquals(2, responses.size());
        
        assertFalse(responses.get(0).getAccepted());
        assertEquals(1000.0, responses.get(0).getFinalPrice()); // Keep original
        
        assertTrue(responses.get(1).getAccepted());
        assertEquals(950.0, responses.get(1).getFinalPrice()); // New price
    }
}
