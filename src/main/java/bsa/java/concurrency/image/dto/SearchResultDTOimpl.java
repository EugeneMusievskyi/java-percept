package bsa.java.concurrency.image.dto;

import lombok.Getter;

import java.util.UUID;

@Getter
public class SearchResultDTOimpl implements SearchResultDTO {
    private UUID imageId;
    private Double matchPercent;
    private String imageUrl;
}
