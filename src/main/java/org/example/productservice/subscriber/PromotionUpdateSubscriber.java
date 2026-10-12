package org.example.productservice.subscriber;

import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;

import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;

import org.springframework.stereotype.Service;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.nio.charset.StandardCharsets;

@Service
public class PromotionUpdateSubscriber implements MessageListener {

    private final CacheManager cacheManager;
    private final ObjectMapper objectMapper;

    public PromotionUpdateSubscriber(
            CacheManager cacheManager,
            ObjectMapper objectMapper
    ) {
        this.cacheManager = cacheManager;
        this.objectMapper = objectMapper;
    }

    @Override
    public void onMessage(Message message, byte[] pattern) {

        String json = new String(
                message.getBody(),
                StandardCharsets.UTF_8
        );

        try {

            // Đọc product_id từ JSON
            JsonNode root = objectMapper.readTree(json);
            JsonNode productIdNode = root.get("product_id");

            if (productIdNode == null
                    || !productIdNode.isIntegralNumber()
                    || productIdNode.longValue() <= 0) {

                System.err.println(
                        "Thông báo không có product_id hợp lệ: " + json
                );
                return;
            }

            Long productId = productIdNode.longValue();

            // Lấy cache products đã tạo ở bài trước
            Cache cache = cacheManager.getCache("products");

            if (cache != null) {

                // Xóa cache theo ID sản phẩm
                cache.evict(productId);

                System.out.println();
                System.out.println(
                        "=== NHẬN THÔNG BÁO KHUYẾN MÃI ==="
                );
                System.out.println(
                        "Product ID: " + productId
                );
                System.out.println(
                        "Đã xóa Redis Cache: products::" + productId
                );
                System.out.println(
                        "Lần GET tiếp theo sẽ đọc dữ liệu mới từ PostgreSQL."
                );
                System.out.println(
                        "================================"
                );
                System.out.println();

            } else {
                System.err.println(
                        "Không tìm thấy cache tên products"
                );
            }

        } catch (Exception e) {
            System.err.println(
                    "Lỗi xử lý thông báo Promotion: "
                            + e.getMessage()
            );
        }
    }
}
