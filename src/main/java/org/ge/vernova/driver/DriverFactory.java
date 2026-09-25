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
import java.util.Map;
import java.util.function.Supplier;

public final class DriverFactory {

    private DriverFactory(){}

    private static final Map<String,Supplier<WebDriver>> DriverCreators = Map.of(
            "chrome", ChromeDriver::new,
            "firefox", FirefoxDriver::new,
            "edge", EdgeDriver::new
    );

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
                        if (headless) opts.addArguments("--headless=new", "--no-sandbox", "--disable-dev-shm-usage");
                        return new RemoteWebDriver(hub, opts);
                    }
                }
            } catch (MalformedURLException e) {
                throw new RuntimeException("Invalid SELENIUM_REMOTE_URL: " + remoteUrl, e);
            }
        }

        // Local driver creation (fallback)
        return DriverCreators.getOrDefault(browser, ChromeDriver::new).get();
    }
}
