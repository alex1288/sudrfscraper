package com.github.courtandrey.sudrfscraper;

import com.github.courtandrey.sudrfscraper.configuration.ApplicationConfiguration;
import com.github.courtandrey.sudrfscraper.service.SeleniumHelper;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;

@SpringBootApplication
public class SUDRFScraperApplication {
    private static final Logger log = LoggerFactory.getLogger(SUDRFScraperApplication.class);
    private static final String OPEN_BROWSER_ON_STARTUP_ENV = "SELENIUM_OPEN_START_PAGE";
    private static final String OPEN_BROWSER_ON_STARTUP_PROPERTY = "selenium.startup.open-browser";

    public static void main(String[] args) {
        if (args.length == 1) ApplicationConfiguration.setUsrDir(args[0]);
        ApplicationConfiguration.getInstance();
        ApplicationConfiguration.getInstance().setProperty("basic.result.path", ApplicationConfiguration.getUsrDir() + "/results/");
        SpringApplication application = new SpringApplication(SUDRFScraperApplication.class);
        application.setLazyInitialization(true);
        application.run(args);
    }

    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationReady() {
        if (!shouldOpenStartPage()) {
            return;
        }
        openStartPage();
    }

    public static void openStartPage() {
        try {
            WebDriver wd = SeleniumHelper.createDriver();
            wd.get("http://localhost:8080/");
            SeleniumHelper.setAppHolder(wd);
        } catch (WebDriverException | IllegalStateException e) {
            log.warn("Selenium startup failed, backend will continue without browser auto-open.", e);
        }
    }

    private boolean shouldOpenStartPage() {
        String envValue = System.getenv(OPEN_BROWSER_ON_STARTUP_ENV);
        if (envValue != null && !envValue.isBlank()) {
            return Boolean.parseBoolean(envValue.trim());
        }

        String propertyValue = ApplicationConfiguration.props.getProperty(OPEN_BROWSER_ON_STARTUP_PROPERTY);
        if (propertyValue != null && !propertyValue.isBlank()) {
            return Boolean.parseBoolean(propertyValue.trim());
        }

        return false;
    }
}
