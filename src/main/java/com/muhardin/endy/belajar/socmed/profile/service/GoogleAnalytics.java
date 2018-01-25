package com.muhardin.endy.belajar.socmed.profile.service;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.analytics.Analytics;
import com.google.api.services.analytics.model.*;
import com.muhardin.endy.belajar.socmed.profile.dto.GoogleAnalyticsReport;
import com.muhardin.endy.belajar.socmed.profile.entity.Website;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;

@Service
public class GoogleAnalytics {

    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
    @Value("${spring.application.name}") private String appname;

    public List<Profile> getProfiles(Website website) {
        List<Profile> hasil = new ArrayList<>();

        try {
            Analytics analytics = getAnalytics(website);
            for (Account acc : analytics.management().accounts().list().execute().getItems()) {
                for (Webproperty prop : analytics.management().webproperties()
                        .list(acc.getId()).execute().getItems()) {
                    Profiles profiles = analytics.management().profiles().list(acc.getId(), prop.getId()).execute();
                    System.out.println("Jumlah Profile : "+profiles.getTotalResults());
                    for (Profile p : profiles.getItems()) {
                        System.out.println("Profile ID : "+p.getId());
                        System.out.println("Profile Name : "+p.getName());
                        System.out.println("Profile Account ID : "+p.getAccountId());
                        System.out.println("Profile Web Property ID : "+p.getWebPropertyId());
                        System.out.println("Profile Internal Web Property ID : "+p.getInternalWebPropertyId());
                        hasil.add(p);
                    }
                }
            }
        } catch (Exception err) {
            err.printStackTrace();
        }

        return hasil;
    }

    public GoogleAnalyticsReport generateReport(Website website) {
        try {
            Analytics analytics = getAnalytics(website);
            printResults(analytics.data().ga()
                    .get("ga:" + website.getProfileId(), "7daysAgo", "today", "ga:sessions")
                    .execute());
            return GoogleAnalyticsReport.builder().build();
        } catch (Exception err) {
            err.printStackTrace();
            return GoogleAnalyticsReport.builder().build();
        }
    }

    private Analytics getAnalytics(Website website) throws GeneralSecurityException, IOException {
        GoogleCredential credential = new GoogleCredential()
                .setAccessToken(website.getAccessToken());

        HttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
        return new Analytics.Builder(httpTransport, JSON_FACTORY, credential)
                .setApplicationName(appname)
                .build();
    }

    private static void printResults(GaData results) {
        // Parse the response from the Core Reporting API for
        // the profile name and number of sessions.
        if (results != null && !results.getRows().isEmpty()) {
            System.out.println("View (Profile) Name: "
                    + results.getProfileInfo().getProfileName());
            System.out.println("Total Sessions: " + results.getRows().get(0).get(0));
        } else {
            System.out.println("No results found");
        }
    }

}
