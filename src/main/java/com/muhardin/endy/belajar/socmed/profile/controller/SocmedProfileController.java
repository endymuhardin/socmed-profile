package com.muhardin.endy.belajar.socmed.profile.controller;

import com.muhardin.endy.belajar.socmed.profile.dao.WebsiteDao;
import com.muhardin.endy.belajar.socmed.profile.entity.Website;
import com.muhardin.endy.belajar.socmed.profile.service.GoogleAnalytics;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class SocmedProfileController {

    // harusnya ini diambil dari user yang sedang login
    private static final String user = "endy";

    @Autowired private GoogleAnalytics googleAnalytics;
    @Autowired private WebsiteDao websiteDao;

    @GetMapping("/")
    public String indexPage() {
        return "redirect:/website/form";
    }

    @GetMapping("/website/form")
    public void displayWebsiteForm() { }

    @PostMapping("/website/form")
    public String processWebsiteForm() {
        Website website = websiteDao.findByUser(user);
        if (website == null) {
            website = new Website();
            website.setUser(user);
            websiteDao.save(website);
            return "redirect:/google/auth/start";
        }

        if (website.getAccessToken() == null) {
            return "redirect:/google/auth/start";
        }

        return "redirect:analytics";
    }

    @GetMapping("/website/select")
    public void displayFormSelectView(Model model) {
        Website website = websiteDao.findByUser(user);
        model.addAttribute("daftarProfil", googleAnalytics.getProfiles(website));
    }

    @PostMapping("/website/select")
    public String processFormSelectView(@RequestParam String profile) {
        Website website = websiteDao.findByUser(user);
        website.setProfileId(profile);
        websiteDao.save(website);
        return "redirect:analytics";
    }

    @GetMapping("/website/analytics")
    public void displayWebsiteAnalytics(Model model) {
        Website website = websiteDao.findByUser(user);
        model.addAttribute("gaReport", googleAnalytics.generateReport(website));
    }

}
