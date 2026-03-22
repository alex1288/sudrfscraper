package com.github.courtandrey.sudrfscraper.service;

import com.github.courtandrey.sudrfscraper.configuration.ApplicationConfiguration;
import com.github.courtandrey.sudrfscraper.service.logger.LoggingLevel;
import com.github.courtandrey.sudrfscraper.service.logger.SimpleLogger;
import org.openqa.selenium.*;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.List;
import java.util.Optional;

public class SeleniumHelper {

    private static final String FIREFOX_BINARY_ENV = "FIREFOX_BINARY";
    private static final String FIREFOX_BINARY_PROPERTY = "selenium.firefox.binary";
    private static final String GECKODRIVER_PATH_ENV = "GECKODRIVER_PATH";
    private static final String GECKODRIVER_PATH_PROPERTY = "selenium.geckodriver.path";
    private static final String SELENIUM_HEADLESS_ENV = "SELENIUM_HEADLESS";
    private static final String SELENIUM_HEADLESS_PROPERTY = "selenium.headless";

    private static WebDriver wd;
    private static SeleniumHelper sh;
    private static WebDriver appHolder;

    public static void setAppHolder(WebDriver appHolder) {
        SeleniumHelper.appHolder = appHolder;
    }

    public static synchronized boolean isActive() {
        return wd != null;
    }

    private SeleniumHelper() {}

    public synchronized void refresh() {
        if (wd == null) reset();
        wd.navigate().refresh();
    }

    public synchronized WebElement findElement(By by) {
        if (wd == null) reset();
        return wd.findElement(by);
    }

    public synchronized List<WebElement> findElements(By by) {
        if (wd == null) reset();
        return wd.findElements(by);
    }

    public static synchronized WebDriver createDriver() {
        return buildDriver();
    }

    public static synchronized SeleniumHelper getInstance() {
        if (sh == null) {
            sh = new SeleniumHelper();
        }
        if (wd == null) {
            reset();
        }
        return sh;
    }

    private static void reset() {
        wd = buildDriver();
    }

    private static WebDriver buildDriver() {
        configureDriverProperties();

        FirefoxOptions options = new FirefoxOptions();
        if (isHeadlessEnabled()) {
            options.addArguments("--headless");
        }
        options.setPageLoadStrategy(PageLoadStrategy.EAGER);
        resolveConfiguredPath(FIREFOX_BINARY_ENV, FIREFOX_BINARY_PROPERTY)
                .filter(path -> !path.isBlank())
                .ifPresent(options::setBinary);

        WebDriver driver = new FirefoxDriver(options);
        driver.manage().timeouts().pageLoadTimeout(Duration.ofMinutes(1));
        driver.manage().timeouts().scriptTimeout(Duration.ofMinutes(1));
        driver.manage().timeouts().implicitlyWait(Duration.ofMinutes(1));
        return driver;
    }

    private static void configureDriverProperties() {
        System.setProperty(FirefoxDriver.SystemProperty.BROWSER_LOGFILE, resolveBrowserLogFile());

        Optional<String> configuredGeckoDriverPath = resolveConfiguredPath(GECKODRIVER_PATH_ENV, GECKODRIVER_PATH_PROPERTY)
                .filter(path -> !path.isBlank());

        if (configuredGeckoDriverPath.isPresent()) {
            System.setProperty("webdriver.gecko.driver", configuredGeckoDriverPath.get());
            return;
        }

        resolveFallbackGeckoDriverPath()
                .filter(Files::exists)
                .map(Path::toString)
                .ifPresent(path -> System.setProperty("webdriver.gecko.driver", path));
    }

    private static Optional<String> resolveConfiguredPath(String envKey, String propertyKey) {
        String envValue = System.getenv(envKey);
        if (envValue != null && !envValue.isBlank()) {
            return Optional.of(envValue.trim());
        }

        String propertyValue = ApplicationConfiguration.props.getProperty(propertyKey);
        if (propertyValue != null && !propertyValue.isBlank()) {
            return Optional.of(propertyValue.trim());
        }

        return Optional.empty();
    }

    private static Optional<Path> resolveFallbackGeckoDriverPath() {
        String os = System.getProperty("os.name", "").toLowerCase();
        if (os.contains("linux")) {
            return Optional.of(Path.of(ApplicationConfiguration.getUsrDir(), "src", "main", "resources", "linux", "geckodriver"));
        }
        if (os.contains("windows")) {
            return Optional.of(Path.of(ApplicationConfiguration.getUsrDir(), "src", "main", "resources", "windows", "geckodriver.exe"));
        }
        if (os.contains("mac")) {
            return Optional.of(Path.of(ApplicationConfiguration.getUsrDir(), "src", "main", "resources", "macOS", "geckodriver"));
        }
        return Optional.empty();
    }

    private static String resolveBrowserLogFile() {
        String os = System.getProperty("os.name", "").toLowerCase();
        return os.contains("windows") ? "nul" : "/dev/null";
    }

    private static boolean isHeadlessEnabled() {
        String envValue = System.getenv(SELENIUM_HEADLESS_ENV);
        if (envValue != null && !envValue.isBlank()) {
            return Boolean.parseBoolean(envValue.trim());
        }

        String propertyValue = ApplicationConfiguration.props.getProperty(SELENIUM_HEADLESS_PROPERTY);
        if (propertyValue != null && !propertyValue.isBlank()) {
            return Boolean.parseBoolean(propertyValue.trim());
        }

        return true;
    }

    public synchronized String getCurrentUrl() {
        if (wd == null) reset();
        if (wd.getCurrentUrl() == null) throw new UnsupportedOperationException();
        return wd.getCurrentUrl();
    }

    public synchronized String getPageSource() {
        if (wd == null) reset();
        if (wd.getCurrentUrl() == null) throw new UnsupportedOperationException();
        return wd.getPageSource();
    }

    public synchronized String getPage(String sourceUrl, Integer waitTime) {
        if (wd == null) reset();

        wd.get(sourceUrl.replaceFirst("http","https"));

        if (waitTime != null) {
            ThreadHelper.sleep(waitTime);
        }

        if (wd.getPageSource() == null) throw new TimeoutException();

        return wd.getPageSource();
    }

    public static synchronized void endSession() {
        if (isActive()) {
            wd.quit();
            wd = null;
        }
        sh = null;
    }

    public static synchronized void killApp() {
        if (appHolder == null) {
            SimpleLogger.log(LoggingLevel.WARNING, "Application browser holder is not initialized, nothing to close.");
            return;
        }
        appHolder.quit();
        appHolder = null;
    }

}
