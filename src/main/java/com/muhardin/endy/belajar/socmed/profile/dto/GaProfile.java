package com.muhardin.endy.belajar.socmed.profile.dto;

import com.google.api.services.analytics.model.Account;
import com.google.api.services.analytics.model.Profile;
import lombok.Data;

@Data
public class GaProfile {
    private Profile profile;
    private Account account;
}
