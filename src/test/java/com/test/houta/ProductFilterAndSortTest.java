package com.test.houta;

import io.qameta.allure.*;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;


@Epic("HaoutaStore - Filtrage et Tri")
@Feature("Tests de filtres et de tri")
@Owner("SOUFYANE")
public class ProductFilterAndSortTest extends ChromeTestBase{
    public static final String CATEGORIE_XPATH = "//*[@id=\"menu-item-7760\"]/a/span";
    public static final String SORT_BY_XPATH = "//*[@id=\"primary\"]/div[1]/form[2]/select";
    public static final String CATEGORIE_NAME = "Smartwatches";
    public static final String CATEGORIE_NAME_POSSIBLE = "watche";
    public static final String CLASS_PRODUCT = "product";
    public static final String CLASS_PRODUCTS = "products";
    public static final String CLASS_PRICE = "price";


    @Test(groups = {"CategorieFilter", "filtrage"})
    @Story("Filtrage par categorie")
    @Description("Verifie que les produits affiches appartiennent à la categorie 'Smartwatches'")
    @Severity(SeverityLevel.CRITICAL)
    public void testFiltrageParCategorie() {
        WebElement categorie = driver.findElement(By.xpath(CATEGORIE_XPATH));
        categorie.click();

        List<WebElement> produits = driver.findElements(By.className(CLASS_PRODUCT));
        Assert.assertFalse(produits.isEmpty(), "Aucun produit affiche");

        for (WebElement produit : produits) {
            String texte = produit.getText().toLowerCase();
            Assert.assertTrue(texte.contains(CATEGORIE_NAME) || texte.contains(CATEGORIE_NAME_POSSIBLE),
                    "Produit ne semble pas appartenir a la catégorie Smartwatches");
        }
    }


    private Integer extractMainPrice(WebElement priceElement) {
        List<WebElement> priceAmounts = priceElement.findElements(By.className("woocommerce-Price-amount"));
        List<Integer> prices = new ArrayList<>();
        for (WebElement amount : priceAmounts) {
            String text = amount.getText().replaceAll("[^0-9]", "");
            if (!text.isEmpty()) {
                prices.add(Integer.parseInt(text));
            }
        }
        // Si plage de prix, prendre le plus élevé
        return prices.stream().min(Integer::compareTo).orElse(null);
    }

    @Test(groups = {"CombinedFilter", "filtrage"})
    @Story("Filtrage combiné par categorie + tri par prix")
    @Description("Vérifie que les produits affichés appartiennent à la catégorie 'Smartwatches' et sont triés par prix décroissant")
    @Severity(SeverityLevel.CRITICAL)
    public void testFiltrageParCategorieEtPrixDesc() {
        driver.get("https://haoutastore.com/product-category/smartwatches/?orderby=price-desc");
    // Attendre le rechargement des produits
        WebElement primaryDiv = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("primary")));
        wait.until(ExpectedConditions.visibilityOfNestedElementsLocatedBy(primaryDiv, By.className(CLASS_PRICE)));

    // Extraire et vérifier les prix comme avant
        List<WebElement> priceElements = primaryDiv.findElements(By.className(CLASS_PRICE));
        List<Integer> prices = new ArrayList<>();
        for (WebElement priceElement : priceElements) {
            Integer price = extractMainPrice(priceElement);
            if (price != null) {
                prices.add(price);
            }
        }
        Assert.assertFalse(prices.isEmpty(), "Aucun prix trouvé après filtrage et tri.");
        List<Integer> sortedPrices = new ArrayList<>(prices);
        sortedPrices.sort(Comparator.reverseOrder());
        Assert.assertEquals(prices, sortedPrices, "Les prix ne sont pas triés en ordre décroissant");
    }


}