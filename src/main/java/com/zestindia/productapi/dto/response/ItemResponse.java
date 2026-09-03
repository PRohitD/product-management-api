package com.zestindia.productapi.dto.response;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ItemResponse {

    private Long id;

    private Integer quantity;
}