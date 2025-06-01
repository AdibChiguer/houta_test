package com.test.houta;

import io.qameta.allure.*;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.List;


@Epic("HaoutaStore - Filtrage et Tri")
@Feature("Tests de filtres et de tri")
@Owner("SOUFYANE")
public class ProductFilterAndSortTest extends ChromeTestBase{
    public static final String CATEGORIE_XPATH = "//*[@id=\"menu-item-7760\"]/a/span";
    public static final String CATEGORIE_NAME = "Smartwatches";
    public static final String CATEGORIE_NAME_POSSIBLE = "watche";
    public static final String INPUT_INEXISTANT = "produit_inexistant_test";
    public static final String INPUT_CASE_INSENSITIVE = "BaTteRie";
    public static final String CLASS_SEARCH_NO_RESULTS_WRAPPER = "search-no-results-wrapper";
    public static final String CLASS_PRODUCT = "product";
    public static final String CLASS_PRODUCTS = "products";


    @Test(groups = {"CategorieFilter", "filtrage"})
    @Story("Filtrage par catégorie")
    @Description("Vérifie que les produits affichés appartiennent à la catégorie 'Smartwatches'")
    @Severity(SeverityLevel.CRITICAL)
    public void testFiltrageParCategorie() {
        WebElement categorie = driver.findElement(By.xpath(CATEGORIE_XPATH));
        categorie.click();

        List<WebElement> produits = driver.findElements(By.className(CLASS_PRODUCT));
        Assert.assertFalse(produits.isEmpty(), "Aucun produit affiché");

        for (WebElement produit : produits) {
            String texte = produit.getText().toLowerCase();
            Assert.assertTrue(texte.contains(CATEGORIE_NAME) || texte.contains(CATEGORIE_NAME_POSSIBLE),
                    "Produit ne semble pas appartenir à la catégorie Smartwatches");
        }
    }
}
