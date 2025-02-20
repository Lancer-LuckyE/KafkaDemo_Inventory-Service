package com.haoyangliu96.kafkademo_inventoryservice.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductDTO {
    private String productId;
    private String name;
    private double price;
    private int stock;
}
