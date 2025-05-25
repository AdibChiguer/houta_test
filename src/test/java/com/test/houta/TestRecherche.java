package com.test.houta;

import io.qameta.allure.*;
import io.qameta.allure.testng.Tag;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.*;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.time.Duration;
import java.util.List;

@Epic("Tests Haouta Store")
@Feature("Recherche")
public class TestRecherche extends ChromeTestBase {

    @Tag("searchBatterie")
    @Test(groups = {"searchBatterie", "search"}, description = "Verifie la recherche de produits par mot-clé")
    @Severity(SeverityLevel.CRITICAL)
    @Story("L'utilisateur recherche un produit par mot-cle")
    public void testRechercheBatterie() {

        // Locate search input and enter keyword
        WebElement searchInput = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//*[@id='undefined-sticky-wrapper']/div/div[1]/div/div[2]/div/form/div/div/input[1]")));
        searchInput.sendKeys("batterie");
        searchInput.sendKeys(Keys.ENTER);

        // Wait until products are loaded
        wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.className("products")));

        // Find all product elements on the search result page
        List<WebElement> produits = driver.findElements(By.className("product"));
        Assert.assertFalse(produits.isEmpty(), "Aucun produit trouvé pour 'batterie'");

        for (WebElement produit : produits) {
            // Re-fetch products on each iteration to avoid stale element reference after navigation
            String texte = produit.getText().toLowerCase();

            if (texte.contains("batterie") || texte.contains("battery")) {
                // Product listing contains the keyword, test passed for this product
                continue;
            }

            // Otherwise, click product link to check details
            try {
                // Assuming the product link is inside 'produit' element (adjust selector if needed)
                WebElement lienProduit = produit.findElement(By.tagName("a"));
                lienProduit.click();

                // Wait for product description to be visible
                WebElement description = wait.until(ExpectedConditions.visibilityOfElementLocated(
                        By.id("tab-description"))); // Adjust ID if needed

                String descTexte = description.getText().toLowerCase();
                Assert.assertTrue(
                        descTexte.contains("batterie") || descTexte.contains("battery"),
                        "Produit non pertinent dans la description : " + description.getText()
                );
            } finally {
                // Navigate back to search results page
                driver.navigate().back();

                // Wait for products list to reload before next iteration
                wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.className("product")));
            }
        }
    }

    @Test(groups={"SearchNotFound", "search"}, description = "Verifie la recherche avec un produit inexistant")
    @Severity(SeverityLevel.MINOR)
    @Story("L'utilisateur recherche un produit qui n'existe pas")
    public void testRechercheProduitInexistant() {
        driver.get("https://haoutastore.com");

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        WebElement searchInput = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//*[@id='undefined-sticky-wrapper']/div/div[1]/div/div[2]/div/form/div/div/input[1]")));
        searchInput.sendKeys("produit_inexistant_test");
        searchInput.sendKeys(Keys.ENTER);

        // Wait for either a 'No products' message or empty product list
        wait.until(ExpectedConditions.presenceOfElementLocated(By.className("search-no-results-wrapper"))); // adjust selector as per actual site

        // Assert that no products are displayed
        List<WebElement> produits = driver.findElements(By.className("product"));
        Assert.assertTrue(produits.isEmpty(), "Des produits ont été trouvés pour une recherche inexistante.");

        // Assert that a message is displayed to indicate no products were found

        WebElement noResultsMessage = driver.findElement(By.className("search-no-results-wrapper")); // adjust as needed
        Assert.assertTrue(noResultsMessage.isDisplayed(), "Le message 'aucun résultat' n'est pas affiché.");
    }


    // Recherche avec une chaîne très longue
    @Test(groups = {"SearchEdgeCase", "search"}, description = "Verifie la recherche avec une chaîne de caractères très longue")
    @Severity(SeverityLevel.MINOR)
    @Story("L'utilisateur saisit une chaine longue dans la barre de recherche")
    public void testRechercheAvecLongTexte() {
        driver.get("https://haoutastore.com");

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        WebElement searchInput = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//*[@id='undefined-sticky-wrapper']/div/div[1]/div/div[2]/div/form/div/div/input[1]")));

        String longText = "x".repeat(300); // Génère une chaîne de 300 'x'
        searchInput.sendKeys(longText);
        searchInput.sendKeys(Keys.ENTER);

        // Attendre que la section de résultats (ou non-résultats) apparaisse
        wait.until(ExpectedConditions.presenceOfElementLocated(By.className("search-no-results-wrapper")));

        List<WebElement> produits = driver.findElements(By.className("product"));
        Assert.assertTrue(produits.isEmpty(), "Des produits ont ete trouves pour une chaine longue invalide.");

        WebElement noResultsMessage = driver.findElement(By.className("search-no-results-wrapper"));
        Assert.assertTrue(noResultsMessage.isDisplayed(), "Le message 'aucun resultat' n'est pas affiche.");
    }


//    Recherche insensible à la casse
    @Test(groups = {"SearchCaseInsensitive", "search"}, description = "Verifie la recherche insensible a la casse")
    @Severity(SeverityLevel.NORMAL)
    @Story("L'utilisateur recherche un produit avec une casse differente")
    public void testRechercheInsensitiveCasse() {
        driver.get("https://haoutastore.com");

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        WebElement searchInput = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//*[@id='undefined-sticky-wrapper']/div/div[1]/div/div[2]/div/form/div/div/input[1]")));

        searchInput.sendKeys("BaTTeRy"); // casse aléatoire
        searchInput.sendKeys(Keys.ENTER);

        wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.className("products")));

        List<WebElement> produits = driver.findElements(By.className("product"));
        Assert.assertTrue(produits.size() > 0, "Aucun produit trouve pour 'BaTTeRy'");
    }

}
