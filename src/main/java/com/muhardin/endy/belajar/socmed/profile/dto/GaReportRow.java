package com.muhardin.endy.belajar.socmed.profile.dto;

import lombok.Builder;
import lombok.Data;

@Data @Builder
public class GaReportRow {
    @Builder.Default
    private String country = "Unknown";
    @Builder.Default
    private String city = "Unknown";
    @Builder.Default
    private Integer pageviews = 0;
    @Builder.Default
    private Integer visitors = 0;
}
