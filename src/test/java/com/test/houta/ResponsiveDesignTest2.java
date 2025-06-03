package com.test.houta;

import io.qameta.allure.*;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Epic("Tests Haouta Store")
@Feature("Design Responsif avec Émulation d'Appareils")
@Owner("Younes")
public class ResponsiveDesignTest2 extends ChromeTestBase {

    // Device emulation configurations
    @DataProvider(name = "deviceEmulation")
    public Object[][] deviceEmulation() {
        return new Object[][] {
            {"iPhone 12 Pro", "iPhone 12 Pro", 390, 844, 3.0},
            {"Samsung Galaxy S21", "Samsung Galaxy S21", 384, 854, 2.81},
            {"iPad", "iPad", 768, 1024, 2.0},
            {"iPad Pro", "iPad Pro", 1024, 1366, 2.0},
            {"Desktop HD", "Desktop", 1920, 1080, 1.0}
        };
    }

    @Test(dataProvider = "deviceEmulation", 
          groups = {"responsive-design", "device-emulation", "ui-testing"},
          description = "Test de l'affichage responsif avec émulation d'appareils réels")
    @Severity(SeverityLevel.CRITICAL)
    @Story("Le site s'affiche correctement sur différents appareils émulés")
    @Description("Utilise l'émulation d'appareils Chrome pour tester la responsivité sur des appareils réels")
    public void testDeviceEmulationResponsiveness(String deviceName, String userAgent, 
                                                 int width, int height, double pixelRatio) {
        
        // Configure device emulation
        configureDeviceEmulation(deviceName, userAgent, width, height, pixelRatio);
        
        // Test components with soft assertions for comprehensive reporting
        SoftAssert softAssert = new SoftAssert();
        
        // Test homepage elements
        testHomepageElements(deviceName, softAssert);
        
        // Test navigation functionality
        testNavigationFunctionality(deviceName, width, softAssert);
        
        // Test product page elements
        testProductPageElements(deviceName, softAssert);
        
        // Test responsive images and media
        testResponsiveMedia(deviceName, softAssert);
        
        // Test performance on device
        testDevicePerformance(deviceName, softAssert);
        
        // Assert all collected results
        softAssert.assertAll();
        
        Allure.addAttachment("Test Summary", 
            String.format("Responsive design test completed for %s (%dx%d)", deviceName, width, height));
    }

    @Step("Configurer l'émulation d'appareil: {deviceName}")
    private void configureDeviceEmulation(String deviceName, String userAgent, 
                                        int width, int height, double pixelRatio) {
        try {
            // Setup Chrome with device emulation
            ChromeOptions options = new ChromeOptions();
            
            // Device emulation settings
            Map<String, Object> deviceMetrics = new HashMap<>();
            deviceMetrics.put("width", width);
            deviceMetrics.put("height", height);
            deviceMetrics.put("pixelRatio", pixelRatio);
            
            Map<String, Object> mobileEmulation = new HashMap<>();
            mobileEmulation.put("deviceMetrics", deviceMetrics);
            mobileEmulation.put("userAgent", userAgent);
            
            options.setExperimentalOption("mobileEmulation", mobileEmulation);
            options.addArguments("--disable-blink-features=AutomationControlled");
            options.addArguments("--disable-extensions");
            
            // Reinitialize driver with device emulation
            if (driver != null) {
                driver.quit();
            }
            
            driver = new ChromeDriver(options);
            wait = new WebDriverWait(driver, Duration.ofSeconds(15));
            
            // Navigate to homepage
            driver.get(baseUrl);
            
            // Wait for page load
            wait.until(ExpectedConditions.jsReturnsValue("return document.readyState === 'complete'"));
            
            Allure.addAttachment("Device Configuration", 
                String.format("Émulation configurée: %s (%dx%d, ratio: %.1f)", 
                    deviceName, width, height, pixelRatio));
                    
        } catch (Exception e) {
            takeScreenshot("Device Configuration Error - " + deviceName);
            Assert.fail(String.format("Erreur lors de la configuration de l'émulation pour %s: %s", 
                deviceName, e.getMessage()));
        }
    }

    @Step("Tester les éléments de la page d'accueil sur {deviceName}")
    private void testHomepageElements(String deviceName, SoftAssert softAssert) {
        try {
            // Test logo visibility with multiple selectors
            boolean logoVisible = testElementVisibility(
                new String[]{"img.normal-logo", ".site-logo img", ".logo img", "img[alt*='logo']"},
                "Logo", deviceName, softAssert);
            
            if (!logoVisible) {
                takeScreenshot("Logo Not Found - " + deviceName);
            }
            
            // Test main navigation
            boolean navVisible = testElementVisibility(
                new String[]{".main-navigation", "nav", ".primary-menu", ".navigation"},
                "Navigation principale", deviceName, softAssert);
            
            // Test search functionality
            boolean searchVisible = testElementVisibility(
                new String[]{"input[type='search']", ".search-field", ".search-form", "[class*='search']"},
                "Barre de recherche", deviceName, softAssert);
            
            // Test hero section or main content
            boolean heroVisible = testElementVisibility(
                new String[]{".hero", ".banner", ".main-content", ".content", "main"},
                "Contenu principal", deviceName, softAssert);
            
            // Test footer
            scrollToBottom();
            boolean footerVisible = testElementVisibility(
                new String[]{"footer", ".site-footer", ".footer", ".page-footer"},
                "Footer", deviceName, softAssert);
            
            Allure.addAttachment("Homepage Elements Test", 
                String.format("Résultats pour %s - Logo: %s, Nav: %s, Search: %s, Hero: %s, Footer: %s",
                    deviceName, logoVisible, navVisible, searchVisible, heroVisible, footerVisible));
                    
        } catch (Exception e) {
            takeScreenshot("Homepage Elements Error - " + deviceName);
            softAssert.fail(String.format("Erreur lors du test des éléments homepage sur %s: %s", 
                deviceName, e.getMessage()));
        }
    }

    @Step("Tester la fonctionnalité de navigation sur {deviceName}")
    private void testNavigationFunctionality(String deviceName, int width, SoftAssert softAssert) {
        try {
            if (width < 768) {
                // Test mobile navigation
                testMobileNavigationFunctionality(deviceName, softAssert);
            } else {
                // Test desktop navigation
                testDesktopNavigationFunctionality(deviceName, softAssert);
            }
            
        } catch (Exception e) {
            takeScreenshot("Navigation Error - " + deviceName);
            softAssert.fail(String.format("Erreur lors du test de navigation sur %s: %s", 
                deviceName, e.getMessage()));
        }
    }

    @Step("Tester la navigation mobile sur {deviceName}")
    private void testMobileNavigationFunctionality(String deviceName, SoftAssert softAssert) {
        // Look for mobile menu button
        List<WebElement> mobileMenuButtons = driver.findElements(
            By.cssSelector(".mobile-menu-toggle, .hamburger, .menu-toggle, .navbar-toggler"));
        
        if (!mobileMenuButtons.isEmpty()) {
            WebElement menuButton = mobileMenuButtons.get(0);
            if (menuButton.isDisplayed()) {
                try {
                    menuButton.click();
                    Thread.sleep(1000); // Wait for animation
                    
                    // Check if menu opened
                    List<WebElement> mobileMenuItems = driver.findElements(
                        By.cssSelector(".mobile-menu a, .responsive-menu a, .navbar-collapse a"));
                    
                    long visibleItems = mobileMenuItems.stream()
                        .filter(WebElement::isDisplayed)
                        .count();
                    
                    softAssert.assertTrue(visibleItems > 0, 
                        String.format("Le menu mobile ne s'ouvre pas correctement sur %s", deviceName));
                    
                    // Close menu
                    menuButton.click();
                    
                } catch (Exception e) {
                    softAssert.fail(String.format("Erreur lors du test du menu mobile sur %s: %s", 
                        deviceName, e.getMessage()));
                }
            }
        } else {
            // If no mobile menu found, check if regular nav is accessible
            List<WebElement> navItems = driver.findElements(By.cssSelector("nav a, .navigation a"));
            softAssert.assertFalse(navItems.isEmpty(), 
                String.format("Aucune navigation accessible trouvée sur %s", deviceName));
        }
    }

    @Step("Tester la navigation desktop sur {deviceName}")
    private void testDesktopNavigationFunctionality(String deviceName, SoftAssert softAssert) {
        List<WebElement> navItems = driver.findElements(
            By.cssSelector("nav ul li a, .main-navigation ul li a, .primary-menu li a"));
        
        if (!navItems.isEmpty()) {
            long clickableItems = navItems.stream()
                .filter(WebElement::isDisplayed)
                .filter(WebElement::isEnabled)
                .count();
            
            softAssert.assertTrue(clickableItems > 0, 
                String.format("Aucun élément de navigation cliquable sur %s", deviceName));
                
            // Test first menu item click
            try {
                WebElement firstItem = navItems.stream()
                    .filter(WebElement::isDisplayed)
                    .filter(WebElement::isEnabled)
                    .findFirst()
                    .orElse(null);
                
                if (firstItem != null) {
                    String originalUrl = driver.getCurrentUrl();
                    firstItem.click();
                    Thread.sleep(2000);
                    
                    String newUrl = driver.getCurrentUrl();
                    softAssert.assertNotEquals(newUrl, originalUrl, 
                        String.format("La navigation ne fonctionne pas sur %s", deviceName));
                    
                    // Return to homepage
                    driver.get(baseUrl);
                }
            } catch (Exception e) {
                softAssert.fail(String.format("Erreur lors du test de clic navigation sur %s: %s", 
                    deviceName, e.getMessage()));
            }
        } else {
            softAssert.fail(String.format("Aucun élément de navigation trouvé sur %s", deviceName));
        }
    }

    @Step("Tester les éléments de la page produit sur {deviceName}")
    private void testProductPageElements(String deviceName, SoftAssert softAssert) {
        try {
            // Navigate to product page
            String productUrl = baseUrl + "product/batterie-rechargeable-usb-prumyls-1-5v-5100mwh-2-piece/";
            driver.get(productUrl);
            
            wait.until(ExpectedConditions.jsReturnsValue("return document.readyState === 'complete'"));
            
            // Test product image
            boolean productImageVisible = testElementVisibility(
                new String[]{".woocommerce-product-gallery img", ".product-images img", 
                           ".wp-post-image", ".product-image img"},
                "Image produit", deviceName, softAssert);
            
            // Test product title
            boolean titleVisible = testElementVisibility(
                new String[]{"h1.product_title", ".product-title", ".entry-title", "h1"},
                "Titre produit", deviceName, softAssert);
            
            // Test product price
            boolean priceVisible = testElementVisibility(
                new String[]{".price", ".woocommerce-price-amount", ".product-price", "[class*='price']"},
                "Prix produit", deviceName, softAssert);
            
            // Test add to cart button
            boolean cartButtonVisible = testElementVisibility(
                new String[]{"button.single_add_to_cart_button", ".add-to-cart-button", 
                           "button[name='add-to-cart']", ".cart-button"},
                "Bouton ajouter au panier", deviceName, softAssert);
            
            Allure.addAttachment("Product Page Test", 
                String.format("Résultats pour %s - Image: %s, Titre: %s, Prix: %s, Panier: %s",
                    deviceName, productImageVisible, titleVisible, priceVisible, cartButtonVisible));
                    
        } catch (Exception e) {
            takeScreenshot("Product Page Error - " + deviceName);
            softAssert.fail(String.format("Erreur lors du test de la page produit sur %s: %s", 
                deviceName, e.getMessage()));
        }
    }

    @Step("Tester les médias responsifs sur {deviceName}")
    private void testResponsiveMedia(String deviceName, SoftAssert softAssert) {
        try {
            driver.get(baseUrl);
            
            // Test images are loaded and have proper dimensions
            List<WebElement> images = driver.findElements(By.tagName("img"));
            
            int loadedImages = 0;
            int totalImages = images.size();
            
            for (WebElement img : images) {
                try {
                    if (img.isDisplayed()) {
                        String naturalWidth = img.getAttribute("naturalWidth");
                        String naturalHeight = img.getAttribute("naturalHeight");
                        
                        if (naturalWidth != null && !naturalWidth.equals("0") && 
                            naturalHeight != null && !naturalHeight.equals("0")) {
                            loadedImages++;
                        }
                    }
                } catch (Exception e) {
                    // Continue checking other images
                }
            }
            
            if (totalImages > 0) {
                double loadRatio = (double) loadedImages / totalImages;
                softAssert.assertTrue(loadRatio > 0.7, 
                    String.format("Trop d'images non chargées sur %s: %d/%d (%.1f%%)", 
                        deviceName, loadedImages, totalImages, loadRatio * 100));
            }
            
            Allure.addAttachment("Media Test", 
                String.format("Images chargées sur %s: %d/%d", deviceName, loadedImages, totalImages));
                
        } catch (Exception e) {
            softAssert.fail(String.format("Erreur lors du test des médias sur %s: %s", 
                deviceName, e.getMessage()));
        }
    }

    @Step("Tester les performances sur {deviceName}")
    private void testDevicePerformance(String deviceName, SoftAssert softAssert) {
        try {
            long startTime = System.currentTimeMillis();
            driver.get(baseUrl);
            
            // Wait for complete page load
            wait.until(ExpectedConditions.jsReturnsValue("return document.readyState === 'complete'"));
            
            long loadTime = System.currentTimeMillis() - startTime;
            
            // Performance thresholds based on device type
            long maxLoadTime = deviceName.toLowerCase().contains("iphone") || 
                              deviceName.toLowerCase().contains("samsung") ? 8000 : 6000;
            
            softAssert.assertTrue(loadTime < maxLoadTime, 
                String.format("Temps de chargement trop long sur %s: %d ms (max: %d ms)", 
                    deviceName, loadTime, maxLoadTime));
            
            Allure.addAttachment("Performance Test", 
                String.format("Temps de chargement sur %s: %d ms", deviceName, loadTime));
                
        } catch (Exception e) {
            softAssert.fail(String.format("Erreur lors du test de performance sur %s: %s", 
                deviceName, e.getMessage()));
        }
    }

    // Helper method to test element visibility with multiple selectors
    private boolean testElementVisibility(String[] selectors, String elementName, 
                                        String deviceName, SoftAssert softAssert) {
        for (String selector : selectors) {
            try {
                List<WebElement> elements = driver.findElements(By.cssSelector(selector));
                if (!elements.isEmpty() && elements.get(0).isDisplayed()) {
                    return true;
                }
            } catch (Exception e) {
                // Continue to next selector
            }
        }
        
        softAssert.fail(String.format("%s non visible sur %s (sélecteurs testés: %s)", 
            elementName, deviceName, String.join(", ", selectors)));
        return false;
    }

    @Step("Faire défiler vers le bas")
    private void scrollToBottom() {
        try {
            ((JavascriptExecutor) driver).executeScript("window.scrollTo(0, document.body.scrollHeight);");
            Thread.sleep(1000);
        } catch (Exception e) {
            // Ignore scroll errors
        }
    }

    @Attachment(value = "Screenshot - {screenshotName}", type = "image/png")
    private byte[] takeScreenshot(String screenshotName) {
        try {
            return ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
        } catch (Exception e) {
            return new byte[0];
        }
    }

    @Test(groups = {"responsive-design", "accessibility"}, 
          description = "Test d'accessibilité responsive")
    @Severity(SeverityLevel.NORMAL)
    @Story("Le site reste accessible sur tous les appareils")
    @Description("Vérifie que les éléments d'accessibilité fonctionnent sur différents appareils")
    public void testResponsiveAccessibility() {
        // Test with mobile device
        configureDeviceEmulation("iPhone 12 Pro", "iPhone 12 Pro", 390, 844, 3.0);
        
        SoftAssert softAssert = new SoftAssert();
        
        try {
            // Test keyboard navigation
            testKeyboardNavigation(softAssert);
            
            // Test alt texts on images
            testImageAltTexts(softAssert);
            
            // Test form labels
            testFormAccessibility(softAssert);
            
            softAssert.assertAll();
            
        } catch (Exception e) {
            takeScreenshot("Accessibility Test Error");
            Assert.fail("Erreur lors du test d'accessibilité: " + e.getMessage());
        }
    }

    @Step("Tester la navigation au clavier")
    private void testKeyboardNavigation(SoftAssert softAssert) {
        try {
            List<WebElement> focusableElements = driver.findElements(
                By.cssSelector("a, button, input, select, textarea, [tabindex]"));
            
            long focusableCount = focusableElements.stream()
                .filter(WebElement::isDisplayed)
                .filter(WebElement::isEnabled)
                .count();
            
            softAssert.assertTrue(focusableCount > 0, 
                "Aucun élément focusable trouvé pour la navigation au clavier");
                
        } catch (Exception e) {
            softAssert.fail("Erreur lors du test de navigation clavier: " + e.getMessage());
        }
    }

    @Step("Tester les textes alternatifs des images")
    private void testImageAltTexts(SoftAssert softAssert) {
        try {
            List<WebElement> images = driver.findElements(By.tagName("img"));
            
            long imagesWithAlt = images.stream()
                .filter(WebElement::isDisplayed)
                .filter(img -> {
                    String alt = img.getAttribute("alt");
                    return alt != null && !alt.trim().isEmpty();
                })
                .count();
            
            long totalVisibleImages = images.stream()
                .filter(WebElement::isDisplayed)
                .count();
            
            if (totalVisibleImages > 0) {
                double altRatio = (double) imagesWithAlt / totalVisibleImages;
                softAssert.assertTrue(altRatio > 0.8, 
                    String.format("Trop d'images sans texte alternatif: %d/%d (%.1f%%)", 
                        imagesWithAlt, totalVisibleImages, altRatio * 100));
            }
            
        } catch (Exception e) {
            softAssert.fail("Erreur lors du test des textes alternatifs: " + e.getMessage());
        }
    }

    @Step("Tester l'accessibilité des formulaires")
    private void testFormAccessibility(SoftAssert softAssert) {
        try {
            List<WebElement> inputs = driver.findElements(
                By.cssSelector("input[type='text'], input[type='email'], textarea"));
            
            for (WebElement input : inputs) {
                if (input.isDisplayed()) {
                    String id = input.getAttribute("id");
                    String name = input.getAttribute("name");
                    String placeholder = input.getAttribute("placeholder");
                    
                    // Check if input has associated label
                    boolean hasLabel = false;
                    if (id != null && !id.isEmpty()) {
                        List<WebElement> labels = driver.findElements(
                            By.cssSelector("label[for='" + id + "']"));
                        hasLabel = !labels.isEmpty();
                    }
                    
                    boolean hasAccessibility = hasLabel || 
                        (placeholder != null && !placeholder.trim().isEmpty()) ||
                        (name != null && !name.trim().isEmpty());
                    
                    if (!hasAccessibility) {
                        softAssert.fail("Champ de formulaire sans label accessible trouvé");
                        break;
                    }
                }
            }
            
        } catch (Exception e) {
            softAssert.fail("Erreur lors du test d'accessibilité des formulaires: " + e.getMessage());
        }
    }
}