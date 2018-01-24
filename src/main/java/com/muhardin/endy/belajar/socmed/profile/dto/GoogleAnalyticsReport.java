package com.muhardin.endy.belajar.socmed.profile.dto;

import lombok.Builder;
import lombok.Data;

import java.util.Map;

@Data @Builder
public class GoogleAnalyticsReport {
    private String userId;
    private Map<String, Integer> metrics;
}
