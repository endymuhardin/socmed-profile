package com.muhardin.endy.belajar.socmed.profile.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;


@Data @Builder
public class GoogleAnalyticsReport {
    private String accountId;
    private String profileId;
    private String profileName;
    private String viewId;
    private String tableId;
    private String trackingId;

    private String startDate;
    private String endDate;

    private Integer totalPageviews;
    private Integer totalVisitors;
    private List<GaReportRow> rows;
}
