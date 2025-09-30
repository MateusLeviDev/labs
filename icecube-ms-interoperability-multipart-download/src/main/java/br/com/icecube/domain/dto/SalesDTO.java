package br.com.icecube.domain.dto;

public record SalesDTO(Long saleId,
                       Long productId,
                       Long customerId,
                       String saleDate,
                       Double saleAmount,
                       String location,
                       String country) {
}
