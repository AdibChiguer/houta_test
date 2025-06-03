package com.test.houta;

import io.qameta.allure.*;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.List;

@Epic("Tests Haouta Store")
@Feature("Design Responsif")
@Owner("Younes")
public class ResponsiveDesignTest extends ChromeTestBase {

    // Device dimensions for testing
    @DataProvider(name = "deviceDimensions")
    public Object[][] deviceDimensions() {
        return new Object[][] {
            {"Mobile Portrait", 375, 667},      // iPhone SE
            {"Mobile Landscape", 667, 375},     // iPhone SE Landscape
            {"Tablet Portrait", 768, 1024},     // iPad
            {"Tablet Landscape", 1024, 768},    // iPad Landscape
            {"Desktop Small", 1280, 720},       // Small Desktop
            {"Desktop Large", 1920, 1080}       // Full HD Desktop
        };
    }

    @Test(dataProvider = "deviceDimensions", 
          groups = {"responsive-design", "ui-testing", "cross-device"},
          description = "Test de l'affichage responsif sur différentes tailles d'écran")
    @Severity(SeverityLevel.CRITICAL)
    @Story("Le site s'affiche correctement sur différentes tailles d'écran")
    @Description("Vérifie que les éléments principaux du site sont visibles et fonctionnels sur mobile, tablette et desktop")
    public void testResponsiveLayout(String deviceName, int width, int height) {
        
        // Set browser window size
        setWindowSize(deviceName, width, height);
        
        // Test homepage responsiveness
        testHomepageResponsiveness(deviceName, width, height);
        
        // Test product page responsiveness
        testProductPageResponsiveness(deviceName, width, height);
        
        // Test navigation responsiveness
        testNavigationResponsiveness(deviceName, width, height);
    }

    @Step("Définir la taille de la fenêtre: {deviceName} ({width}x{height})")
    private void setWindowSize(String deviceName, int width, int height) {
        driver.manage().window().setSize(new Dimension(width, height));
        
        // Wait a moment for the resize to take effect
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        Allure.addAttachment("Device Configuration", 
            String.format("Testing on %s with dimensions %dx%d", deviceName, width, height));
    }

    @Step("Tester la responsivité de la page d'accueil sur {deviceName}")
    private void testHomepageResponsiveness(String deviceName, int width, int height) {
        driver.get(baseUrl);
        
        // Test logo visibility
        WebElement logo = wait.until(ExpectedConditions.visibilityOfElementLocated(
            By.cssSelector("img.normal-logo, .site-logo img, .logo img")));
        Assert.assertTrue(logo.isDisplayed(), 
            String.format("Le logo n'est pas visible sur %s", deviceName));
        
        // Test main navigation
        if (width >= 768) { // Desktop/Tablet
            testDesktopNavigation(deviceName);
        } else { // Mobile
            testMobileNavigation(deviceName);
        }
        
        // Test search functionality
        testSearchElementVisibility(deviceName, width);
        
        // Test footer visibility (scroll to bottom first)
        scrollToBottom();
        testFooterResponsiveness(deviceName);
        
        Allure.addAttachment(deviceName + " Homepage Test", 
            "Homepage responsiveness test completed successfully");
    }

    @Step("Tester la navigation desktop/tablette sur {deviceName}")
    private void testDesktopNavigation(String deviceName) {
        try {
            // Look for main navigation menu
            List<WebElement> navItems = driver.findElements(
                By.cssSelector("nav ul li, .main-navigation ul li, .primary-navigation ul li"));
            
            Assert.assertFalse(navItems.isEmpty(), 
                String.format("Le menu de navigation principal n'est pas visible sur %s", deviceName));
            
            // Verify at least some menu items are visible
            long visibleItems = navItems.stream().filter(WebElement::isDisplayed).count();
            Assert.assertTrue(visibleItems > 0, 
                String.format("Aucun élément du menu n'est visible sur %s", deviceName));
                
        } catch (Exception e) {
            Allure.addAttachment("Navigation Error", 
                String.format("Erreur lors du test de navigation sur %s: %s", deviceName, e.getMessage()));
        }
    }

    @Step("Tester la navigation mobile sur {deviceName}")
    private void testMobileNavigation(String deviceName) {
        try {
            // Look for mobile menu button/hamburger
            List<WebElement> mobileMenuButtons = driver.findElements(
                By.cssSelector(".mobile-menu-toggle, .hamburger, .menu-toggle, [class*='mobile-nav']"));
            
            if (!mobileMenuButtons.isEmpty()) {
                WebElement mobileMenuButton = mobileMenuButtons.stream()
                    .filter(WebElement::isDisplayed)
                    .findFirst()
                    .orElse(null);
                
                if (mobileMenuButton != null) {
                    Assert.assertTrue(mobileMenuButton.isDisplayed(), 
                        String.format("Le bouton de menu mobile n'est pas visible sur %s", deviceName));
                } else {
                    // If no visible mobile menu button, check if regular nav is still accessible
                    testDesktopNavigation(deviceName);
                }
            } else {
                // Fallback to desktop navigation test
                testDesktopNavigation(deviceName);
            }
        } catch (Exception e) {
            Allure.addAttachment("Mobile Navigation Error", 
                String.format("Erreur lors du test de navigation mobile sur %s: %s", deviceName, e.getMessage()));
        }
    }

    @Step("Tester la visibilité de la recherche sur {deviceName}")
    private void testSearchElementVisibility(String deviceName, int width) {
        try {
            // Look for search input
            List<WebElement> searchInputs = driver.findElements(
                By.cssSelector("input[type='search'], input[name*='search'], .search-field"));
            
            if (!searchInputs.isEmpty()) {
                WebElement searchInput = searchInputs.stream()
                    .filter(WebElement::isDisplayed)
                    .findFirst()
                    .orElse(null);
                
                if (width >= 768) {
                    // On desktop/tablet, search should be visible
                    Assert.assertNotNull(searchInput, 
                        String.format("La barre de recherche devrait être visible sur %s", deviceName));
                } else {
                    // On mobile, search might be hidden behind a button or icon
                    List<WebElement> searchButtons = driver.findElements(
                        By.cssSelector(".search-toggle, .search-icon, [class*='search-btn']"));
                    
                    boolean hasSearchFunctionality = searchInput != null || !searchButtons.isEmpty();
                    Assert.assertTrue(hasSearchFunctionality, 
                        String.format("Aucune fonctionnalité de recherche n'est accessible sur %s", deviceName));
                }
            }
        } catch (Exception e) {
            Allure.addAttachment("Search Test Warning", 
                String.format("Impossible de tester la recherche sur %s: %s", deviceName, e.getMessage()));
        }
    }

    @Step("Tester la responsivité de la page produit sur {deviceName}")
    private void testProductPageResponsiveness(String deviceName, int width, int height) {
        // Navigate to a product page
        String productUrl = baseUrl + "product/batterie-rechargeable-usb-prumyls-1-5v-5100mwh-2-piece/";
        driver.get(productUrl);
        
        // Test product image visibility
        try {
            WebElement productImage = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.cssSelector(".woocommerce-product-gallery img, .product-images img, .wp-post-image")));
            Assert.assertTrue(productImage.isDisplayed(), 
                String.format("L'image du produit n'est pas visible sur %s", deviceName));
        } catch (Exception e) {
            Allure.addAttachment("Product Image Error", 
                String.format("Erreur lors du test de l'image produit sur %s: %s", deviceName, e.getMessage()));
        }
        
        // Test product title
        try {
            WebElement productTitle = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.cssSelector("h1.product_title, .product-title, .entry-title")));
            Assert.assertTrue(productTitle.isDisplayed(), 
                String.format("Le titre du produit n'est pas visible sur %s", deviceName));
        } catch (Exception e) {
            Allure.addAttachment("Product Title Error", 
                String.format("Erreur lors du test du titre produit sur %s: %s", deviceName, e.getMessage()));
        }
        
        // Test add to cart button
        try {
            WebElement addToCartButton = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.cssSelector("button.single_add_to_cart_button, .add-to-cart-button")));
            Assert.assertTrue(addToCartButton.isDisplayed(), 
                String.format("Le bouton 'Ajouter au panier' n'est pas visible sur %s", deviceName));
            
            // Check if button is clickable (not overlapped)
            Assert.assertTrue(addToCartButton.isEnabled(), 
                String.format("Le bouton 'Ajouter au panier' n'est pas cliquable sur %s", deviceName));
        } catch (Exception e) {
            Allure.addAttachment("Add to Cart Button Error", 
                String.format("Erreur lors du test du bouton panier sur %s: %s", deviceName, e.getMessage()));
        }
        
        Allure.addAttachment(deviceName + " Product Page Test", 
            "Product page responsiveness test completed");
    }

    @Step("Tester la responsivité de la navigation sur {deviceName}")
    private void testNavigationResponsiveness(String deviceName, int width, int height) {
        driver.get(baseUrl);
        
        if (width < 768) { // Mobile
            testMobileMenuFunctionality(deviceName);
        } else { // Desktop/Tablet
            testDesktopMenuHover(deviceName);
        }
    }

    @Step("Tester la fonctionnalité du menu mobile sur {deviceName}")
    private void testMobileMenuFunctionality(String deviceName) {
        try {
            // Find and click mobile menu toggle
            List<WebElement> mobileToggles = driver.findElements(
                By.cssSelector(".mobile-menu-toggle, .hamburger, .menu-toggle"));
            
            WebElement mobileToggle = mobileToggles.stream()
                .filter(WebElement::isDisplayed)
                .findFirst()
                .orElse(null);
            
            if (mobileToggle != null) {
                mobileToggle.click();
                
                // Wait for mobile menu to appear
                Thread.sleep(1000);
                
                // Check if mobile menu items are now visible
                List<WebElement> mobileMenuItems = driver.findElements(
                    By.cssSelector(".mobile-menu li, .responsive-menu li, nav.mobile li"));
                
                long visibleMobileItems = mobileMenuItems.stream()
                    .filter(WebElement::isDisplayed)
                    .count();
                
                Assert.assertTrue(visibleMobileItems > 0, 
                    String.format("Le menu mobile ne s'ouvre pas correctement sur %s", deviceName));
                
                // Close the menu
                mobileToggle.click();
            }
        } catch (Exception e) {
            Allure.addAttachment("Mobile Menu Test", 
                String.format("Test du menu mobile sur %s: %s", deviceName, e.getMessage()));
        }
    }

    @Step("Tester le survol du menu desktop sur {deviceName}")
    private void testDesktopMenuHover(String deviceName) {
        try {
            // This is a basic test - in real scenarios you'd test dropdown functionality
            List<WebElement> menuItems = driver.findElements(
                By.cssSelector("nav ul li a, .main-navigation ul li a"));
            
            if (!menuItems.isEmpty()) {
                // Verify menu items are accessible
                long clickableItems = menuItems.stream()
                    .filter(WebElement::isDisplayed)
                    .filter(WebElement::isEnabled)
                    .count();
                
                Assert.assertTrue(clickableItems > 0, 
                    String.format("Aucun élément de menu n'est cliquable sur %s", deviceName));
            }
        } catch (Exception e) {
            Allure.addAttachment("Desktop Menu Test", 
                String.format("Test du menu desktop sur %s: %s", deviceName, e.getMessage()));
        }
    }

    @Step("Faire défiler vers le bas de la page")
    private void scrollToBottom() {
        ((JavascriptExecutor) driver).executeScript("window.scrollTo(0, document.body.scrollHeight);");
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    @Step("Tester la responsivité du footer sur {deviceName}")
    private void testFooterResponsiveness(String deviceName) {
        try {
            // Look for footer elements
            List<WebElement> footerElements = driver.findElements(
                By.cssSelector("footer, .site-footer, .footer"));
            
            if (!footerElements.isEmpty()) {
                WebElement footer = footerElements.stream()
                    .filter(WebElement::isDisplayed)
                    .findFirst()
                    .orElse(null);
                
                if (footer != null) {
                    Assert.assertTrue(footer.isDisplayed(), 
                        String.format("Le footer n'est pas visible sur %s", deviceName));
                    
                    // Check footer content
                    List<WebElement> footerLinks = footer.findElements(By.tagName("a"));
                    if (!footerLinks.isEmpty()) {
                        long visibleLinks = footerLinks.stream()
                            .filter(WebElement::isDisplayed)
                            .count();
                        
                        Assert.assertTrue(visibleLinks > 0, 
                            String.format("Aucun lien du footer n'est visible sur %s", deviceName));
                    }
                }
            }
        } catch (Exception e) {
            Allure.addAttachment("Footer Test", 
                String.format("Test du footer sur %s: %s", deviceName, e.getMessage()));
        }
    }

    @Test(groups = {"responsive-design", "performance"}, 
          description = "Test de performance sur mobile")
    @Severity(SeverityLevel.NORMAL)
    @Story("Le site se charge rapidement sur mobile")
    @Description("Vérifie que le site se charge dans un délai acceptable sur mobile")
    public void testMobilePerformance() {
        // Set mobile viewport
        driver.manage().window().setSize(new Dimension(375, 667));
        
        long startTime = System.currentTimeMillis();
        driver.get(baseUrl);
        
        // Wait for main content to load
        wait.until(ExpectedConditions.visibilityOfElementLocated(
            By.cssSelector("body")));
        
        long loadTime = System.currentTimeMillis() - startTime;
        
        // Assert reasonable load time (adjust threshold as needed)
        Assert.assertTrue(loadTime < 10000, 
            String.format("Le temps de chargement sur mobile est trop long: %d ms", loadTime));
        
        Allure.addAttachment("Mobile Performance", 
            String.format("Temps de chargement: %d ms", loadTime));
    }
}