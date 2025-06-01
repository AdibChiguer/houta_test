package com.test.houta;

import io.qameta.allure.*;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.*;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.time.Duration;
import java.util.List;

@Epic("Tests Haouta Store")
@Feature("Recherche")
@Owner("SOUFYANE")
public class TestRecherche extends ChromeTestBase {
    public static final String SEARCH_BAR_XPATH = "//*[@id='undefined-sticky-wrapper']/div/div[1]/div/div[2]/div/form/div/div/input[1]";
    public static final String INPUT_TO_SEARCH_FR = "batterie";
    public static final String INPUT_TO_SEARCH_AN = "battery";
    public static final String INPUT_INEXISTANT = "produit_inexistant_test";
    public static final String INPUT_CASE_INSENSITIVE = "BaTteRie";
    public static final String CLASS_SEARCH_NO_RESULTS_WRAPPER = "search-no-results-wrapper";
    public static final String CLASS_PRODUCT = "product";
    public static final String CLASS_PRODUCTS = "products";

    @Test(groups = {"searchBatterie", "search"}, description = "Verifie la recherche de produits par mot-clé")
    @Severity(SeverityLevel.CRITICAL)
    @Story("L'utilisateur recherche un produit par mot-cle")
    public void testRechercheBatterie() {

        WebElement searchInput = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath(SEARCH_BAR_XPATH)));
        searchInput.sendKeys(INPUT_TO_SEARCH_FR);
        searchInput.sendKeys(Keys.ENTER);

        wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.className(CLASS_PRODUCTS)));

        // Find all product elements on the search result page
        List<WebElement> produits = driver.findElements(By.className(CLASS_PRODUCT));
        Assert.assertFalse(produits.isEmpty(), "Aucun produit trouvé pour 'batterie'");

        for (WebElement produit : produits) {
            String texte = produit.getText().toLowerCase();

            // if product text contain batterie skip
            if (texte.contains(INPUT_TO_SEARCH_FR) || texte.contains(INPUT_TO_SEARCH_AN))
                continue;

            // Otherwise, click product link to check details
            try {
                // Assuming the product link is inside 'produit' element
                WebElement lienProduit = produit.findElement(By.tagName("a"));
                lienProduit.click();

                // Wait for product description to be visible
                WebElement description = wait.until(ExpectedConditions.visibilityOfElementLocated(
                        By.id("tab-description")));

                String descTexte = description.getText().toLowerCase();
                Assert.assertTrue(
                        descTexte.contains(INPUT_TO_SEARCH_FR) || descTexte.contains(INPUT_TO_SEARCH_AN),
                        "Produit non pertinent dans la description : " + description.getText()
                );
            } finally {
                driver.navigate().back();

                // Wait for products list to reload before next iteration
                wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.className(CLASS_PRODUCT)));
            }
        }
    }

    @Test(groups={"SearchNotFound", "search"}, description = "Verifie la recherche avec un produit inexistant")
    @Severity(SeverityLevel.MINOR)
    @Story("L'utilisateur recherche un produit qui n'existe pas")
    public void testRechercheProduitInexistant() {

        WebElement searchInput = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//*[@id='undefined-sticky-wrapper']/div/div[1]/div/div[2]/div/form/div/div/input[1]")));
        searchInput.sendKeys(INPUT_INEXISTANT);
        searchInput.sendKeys(Keys.ENTER);

        // Wait for either a 'No products' message or empty product list
        wait.until(ExpectedConditions.presenceOfElementLocated(By.className(CLASS_SEARCH_NO_RESULTS_WRAPPER)));

        // Assert that no products are displayed
        List<WebElement> produits = driver.findElements(By.className(CLASS_PRODUCT));
        Assert.assertTrue(produits.isEmpty(), "Des produits ont été trouvés pour une recherche inexistante.");

        // Assert that a message is displayed to indicate no products were found

        WebElement noResultsMessage = driver.findElement(By.className(CLASS_SEARCH_NO_RESULTS_WRAPPER)); // adjust as needed
        Assert.assertTrue(noResultsMessage.isDisplayed(), "Le message 'aucun résultat' n'est pas affiché.");
    }


    // Search with Along text
    @Severity(SeverityLevel.MINOR)
    @Story("L'utilisateur saisit une chaine longue dans la barre de recherche")
    @Test(groups = {"SearchEdgeCase", "search"}, description = "Verifie la recherche avec une chaîne de caractères très longue")
    public void testRechercheAvecLongTexte() {

        WebElement searchInput = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath(SEARCH_BAR_XPATH)));

        String longText = "rrrr ".repeat(500); // Génère une chaîne de 300 'x'
        searchInput.sendKeys(longText);
        searchInput.sendKeys(Keys.ENTER);

        try{
            wait.until(ExpectedConditions.presenceOfElementLocated(By.className(CLASS_SEARCH_NO_RESULTS_WRAPPER)));
        } catch (Exception e) {
            driver.close();
            Assert.fail("Le test a expiré : un élément attendu n'est pas apparu à temps. Détail : " + e.getMessage());
        }

        // Attendre que la section de résultats (ou non-résultats) apparaisse
        List<WebElement> produits = driver.findElements(By.className(CLASS_PRODUCT));
        Assert.assertTrue(produits.isEmpty(), "Des produits ont ete trouves pour une chaine longue invalide.");

        WebElement noResultsMessage = driver.findElement(By.className(CLASS_SEARCH_NO_RESULTS_WRAPPER));
        Assert.assertTrue(noResultsMessage.isDisplayed(), "Le message 'aucun resultat' n'est pas affiche.");
    }


//    Recherche insensible à la casse
    @Test(groups = {"SearchCaseInsensitive", "search"}, description = "Verifie la recherche insensible a la casse")
    @Severity(SeverityLevel.NORMAL)
    @Story("L'utilisateur recherche un produit avec une casse differente")
    public void testRechercheInsensitiveCasse() {

        WebElement searchInput = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath(SEARCH_BAR_XPATH)));

        searchInput.sendKeys(INPUT_CASE_INSENSITIVE); // casse aléatoire
        searchInput.sendKeys(Keys.ENTER);

        wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.className(CLASS_PRODUCTS)));

        List<WebElement> produits = driver.findElements(By.className(CLASS_PRODUCT));
        Assert.assertTrue(produits.size() > 0, "Aucun produit trouve pour 'BaTTeRy'");
    }

}
