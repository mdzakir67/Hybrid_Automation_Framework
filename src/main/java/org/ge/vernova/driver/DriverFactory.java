package org.ge.vernova.driver;

import org.ge.vernova.config.UIConfig;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.net.MalformedURLException;
import java.net.URL;

public final class DriverFactory {

    private DriverFactory(){}

    public static WebDriver createWebDriver(){
        // Allow overriding browser via environment variable (useful in CI/K8s)
        String browserEnv = System.getenv("BROWSER");
        String browser = (browserEnv != null && !browserEnv.isBlank()) ? browserEnv.toLowerCase() : UIConfig.getBrowser();

        String remoteUrl = System.getenv("SELENIUM_REMOTE_URL");
        boolean useRemote = remoteUrl != null && !remoteUrl.isBlank();

        boolean headless = Boolean.parseBoolean(System.getenv().getOrDefault("HEADLESS", "true"));

        if (useRemote) {
            try {
                URL hub = new URL(remoteUrl);
                switch (browser) {
                    case "firefox": {
                        FirefoxOptions opts = new FirefoxOptions();
                        if (headless) opts.addArguments("-headless");
                        return new RemoteWebDriver(hub, opts);
                    }
                    case "edge": {
                        EdgeOptions opts = new EdgeOptions();
                        if (headless) opts.addArguments("--headless=new");
                        return new RemoteWebDriver(hub, opts);
                    }
                    case "chrome":
                    default: {
                        ChromeOptions opts = new ChromeOptions();
                        if (headless) {
                            opts.addArguments("--headless=new", "--no-sandbox", "--disable-dev-shm-usage", "--disable-gpu", "--window-size=1920,1080");
                        }
                        return new RemoteWebDriver(hub, opts);
                    }
                }
            } catch (MalformedURLException e) {
                throw new RuntimeException("Invalid SELENIUM_REMOTE_URL: " + remoteUrl, e);
            }
        }

        switch (browser) {
            case "firefox": {
                FirefoxOptions firefoxOptions = new FirefoxOptions();
                if (headless) firefoxOptions.addArguments("-headless");
                return new FirefoxDriver(firefoxOptions);
            }
            case "edge": {
                EdgeOptions edgeOptions = new EdgeOptions();
                if (headless) edgeOptions.addArguments("--headless=new", "--no-sandbox", "--disable-dev-shm-usage");
                return new EdgeDriver(edgeOptions);
            }
            case "chrome":
            default: {
                ChromeOptions chromeOptions = new ChromeOptions();
                if (headless) {
                    chromeOptions.addArguments("--headless=new", "--no-sandbox", "--disable-dev-shm-usage", "--disable-gpu", "--window-size=1920,1080");
                }
                return new ChromeDriver(chromeOptions);
            }
        }
    }
}
