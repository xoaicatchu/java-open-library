package com.example.grpc.service;

import com.example.grpc.proto.*;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

@GrpcService
public class ProductGrpcService extends ProductServiceGrpc.ProductServiceImplBase {

    private final Map<String, ProductResponse> products = new ConcurrentHashMap<>();

    public ProductGrpcService() {
        // Dummy data
        products.put("1", ProductResponse.newBuilder().setId("1").setName("Laptop").setPrice(1000.0).build());
        products.put("2", ProductResponse.newBuilder().setId("2").setName("Smartphone").setPrice(800.0).build());
    }

    @Override
    public void getProduct(GetProductRequest request, StreamObserver<ProductResponse> responseObserver) {
        ProductResponse product = products.get(request.getId());
        if (product != null) {
            responseObserver.onNext(product);
            responseObserver.onCompleted();
        } else {
            responseObserver.onError(new io.grpc.StatusRuntimeException(io.grpc.Status.NOT_FOUND));
        }
    }

    @Override
    public void createProduct(CreateProductRequest request, StreamObserver<ProductResponse> responseObserver) {
        String id = UUID.randomUUID().toString();
        ProductResponse product = ProductResponse.newBuilder()
                .setId(id)
                .setName(request.getName())
                .setPrice(request.getPrice())
                .build();
        products.put(id, product);
        
        responseObserver.onNext(product);
        responseObserver.onCompleted();
    }

    @Override
    public void listProducts(ListProductsRequest request, StreamObserver<ProductResponse> responseObserver) {
        int count = 0;
        int max = request.getMaxResults() > 0 ? request.getMaxResults() : Integer.MAX_VALUE;
        
        for (ProductResponse product : products.values()) {
            if (count >= max) break;
            responseObserver.onNext(product);
            count++;
        }
        responseObserver.onCompleted();
    }

    @Override
    public StreamObserver<CreateProductRequest> bulkCreateProducts(StreamObserver<BulkCreateResponse> responseObserver) {
        return new StreamObserver<CreateProductRequest>() {
            int count = 0;
            
            @Override
            public void onNext(CreateProductRequest request) {
                String id = UUID.randomUUID().toString();
                products.put(id, ProductResponse.newBuilder()
                        .setId(id)
                        .setName(request.getName())
                        .setPrice(request.getPrice())
                        .build());
                count++;
            }

            @Override
            public void onError(Throwable t) {
                System.err.println("Error in client stream: " + t.getMessage());
            }

            @Override
            public void onCompleted() {
                responseObserver.onNext(BulkCreateResponse.newBuilder().setCount(count).build());
                responseObserver.onCompleted();
            }
        };
    }

    @Override
    public StreamObserver<PriceRequest> priceNegotiation(StreamObserver<PriceResponse> responseObserver) {
        return new StreamObserver<PriceRequest>() {
            @Override
            public void onNext(PriceRequest request) {
                ProductResponse product = products.get(request.getProductId());
                if (product == null) {
                    responseObserver.onNext(PriceResponse.newBuilder()
                            .setProductId(request.getProductId())
                            .setAccepted(false)
                            .setFinalPrice(0)
                            .build());
                    return;
                }
                
                // Chấp nhận nếu giá đề xuất >= 90% giá gốc (Accept if suggested price >= 90% original)
                boolean accepted = request.getSuggestedPrice() >= product.getPrice() * 0.9;
                
                responseObserver.onNext(PriceResponse.newBuilder()
                        .setProductId(request.getProductId())
                        .setFinalPrice(accepted ? request.getSuggestedPrice() : product.getPrice())
                        .setAccepted(accepted)
                        .build());
            }

            @Override
            public void onError(Throwable t) {
                System.err.println("Error in bi-directional stream: " + t.getMessage());
            }

            @Override
            public void onCompleted() {
                responseObserver.onCompleted();
            }
        };
    }
}
