package com.muhardin.endy.belajar.socmed.profile.service;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.analytics.Analytics;
import com.google.api.services.analytics.model.GaData;
import com.muhardin.endy.belajar.socmed.profile.dto.GoogleAnalyticsReport;
import com.muhardin.endy.belajar.socmed.profile.entity.Website;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class GoogleAnalytics {

    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
    @Value("${spring.application.name}") private String appname;

    public GoogleAnalyticsReport generateReport(Website website) {
        try {
            GoogleCredential credential = new GoogleCredential()
                    .setAccessToken(website.getAccessToken());

            HttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
            Analytics analytics = new Analytics.Builder(httpTransport, JSON_FACTORY, credential)
                    .setApplicationName(appname)
                    .build();
            System.out.println("Mengakses Google API");
            GaData data = analytics.data().ga().get("ga:65778193", // Table Id.
                    "2017-12-01", // Start date.
                    "2018-01-01", // End date.
                    "ga:visits") // Metrics.
                    .setDimensions("ga:source,ga:keyword")
                    .setSort("-ga:visits,ga:source")
                    .setFilters("ga:medium==organic")
                    .setMaxResults(25)
                    .execute();

            GaData.ProfileInfo profileInfo = data.getProfileInfo();

            System.out.println("Profile Info");
            System.out.println("Account ID: " + profileInfo.getAccountId());
            System.out.println("Web Property ID: " + profileInfo.getWebPropertyId());
            System.out.println("Internal Web Property ID: " + profileInfo.getInternalWebPropertyId());
            System.out.println("Profile ID: " + profileInfo.getProfileId());
            System.out.println("Profile Name: " + profileInfo.getProfileName());
            System.out.println("Table ID: " + profileInfo.getTableId());

            return GoogleAnalyticsReport.builder().build();
        } catch (Exception err) {
            err.printStackTrace();
            return GoogleAnalyticsReport.builder().build();
        }
    }
}
