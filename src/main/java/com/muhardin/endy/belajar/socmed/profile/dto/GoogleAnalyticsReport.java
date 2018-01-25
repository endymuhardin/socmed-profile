package com.muhardin.endy.belajar.socmed.profile.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;


@Data @Builder
public class GoogleAnalyticsReport {
    private Integer totalPageviews;
    private Integer totalVisitors;
    private List<GaReportRow> rows;
}
