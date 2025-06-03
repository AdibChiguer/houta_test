package com.test.houta;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Owner;

import java.util.List;
import java.util.Map;

@Epic("Website Performance")
@Feature("Performance Testing")
@Owner("Ayoub")
public class PerformanceTest extends ChromeTestBase {

    private static final long ACCEPTABLE_PAGE_LOAD_TIME = 5000; // 5 seconds
    private static final long ACCEPTABLE_DOM_LOAD_TIME = 3000; // 3 seconds
    private static final int ACCEPTABLE_RESOURCE_COUNT = 200; // Maximum resources to load (adjusted for e-commerce site)

    @Test(groups = { "performance", "load-time", "critical" }, 
          description = "Test du temps de chargement de la page d'accueil")
    public void testHomePageLoadTime() {
        long startTime = System.currentTimeMillis();
        
        // Naviguer vers la page d'accueil
        driver.get(baseUrl);
        
        // Attendre que la page soit complètement chargée
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.tagName("body")));
        
        long endTime = System.currentTimeMillis();
        long pageLoadTime = endTime - startTime;
        
        // Vérifier le temps de chargement
        Assert.assertTrue(pageLoadTime <= ACCEPTABLE_PAGE_LOAD_TIME, 
            String.format("La page d'accueil met trop de temps à charger: %d ms (maximum accepté: %d ms)", 
                pageLoadTime, ACCEPTABLE_PAGE_LOAD_TIME));
        
        System.out.println("Temps de chargement de la page d'accueil: " + pageLoadTime + " ms");
    }

    @Test(groups = { "performance", "dom-timing", "critical" }, 
          description = "Test des métriques de performance DOM via Navigation Timing API")
    public void testDOMPerformanceMetrics() {
        driver.get(baseUrl);
        
        // Attendre que la page soit chargée
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.tagName("body")));
        
        JavascriptExecutor js = (JavascriptExecutor) driver;
        
        // Récupérer les métriques de performance
        Long domContentLoadedTime = (Long) js.executeScript(
            "return performance.timing.domContentLoadedEventEnd - performance.timing.navigationStart;");
        
        Long pageLoadTime = (Long) js.executeScript(
            "return performance.timing.loadEventEnd - performance.timing.navigationStart;");
        
        Long domInteractiveTime = (Long) js.executeScript(
            "return performance.timing.domInteractive - performance.timing.navigationStart;");
        
        // Vérifications
        Assert.assertTrue(domContentLoadedTime <= ACCEPTABLE_DOM_LOAD_TIME, 
            String.format("DOM Content Loaded trop lent: %d ms (maximum: %d ms)", 
                domContentLoadedTime, ACCEPTABLE_DOM_LOAD_TIME));
        
        Assert.assertTrue(pageLoadTime <= ACCEPTABLE_PAGE_LOAD_TIME, 
            String.format("Page Load trop lent: %d ms (maximum: %d ms)", 
                pageLoadTime, ACCEPTABLE_PAGE_LOAD_TIME));
        
        // Affichage des métriques
        System.out.println("=== MÉTRIQUES DE PERFORMANCE ===");
        System.out.println("DOM Content Loaded: " + domContentLoadedTime + " ms");
        System.out.println("Page Load Complete: " + pageLoadTime + " ms");
        System.out.println("DOM Interactive: " + domInteractiveTime + " ms");
    }

    @Test(groups = { "performance", "resource-loading", "regression" }, 
          description = "Test du nombre et temps de chargement des ressources")
    public void testResourceLoadingPerformance() {
        driver.get(baseUrl);
        
        // Attendre que la page soit chargée
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.tagName("body")));
        
        JavascriptExecutor js = (JavascriptExecutor) driver;
        
        // Récupérer les informations sur les ressources
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> resources = (List<Map<String, Object>>) js.executeScript(
            "var resources = performance.getEntriesByType('resource');" +
            "return resources.map(function(r) {" +
            "  return {" +
            "    name: r.name," +
            "    duration: r.duration," +
            "    transferSize: r.transferSize || 0," +
            "    type: r.initiatorType" +
            "  };" +
            "});");
        
        // Analyser les ressources
        int totalResources = resources.size();
        double totalLoadTime = 0;
        int slowResources = 0;
        
        for (Map<String, Object> resource : resources) {
            Object durationObj = resource.get("duration");
            if (durationObj != null) {
                double duration = ((Number) durationObj).doubleValue();
                totalLoadTime += duration;
                if (duration > 2000) { // Ressources qui prennent plus de 2 secondes
                    slowResources++;
                    System.out.println("Ressource lente détectée: " + resource.get("name") + 
                        " (" + Math.round(duration) + " ms)");
                }
            }
        }
        
        double averageLoadTime = totalLoadTime / totalResources;
        
        // Vérifications
        Assert.assertTrue(totalResources <= ACCEPTABLE_RESOURCE_COUNT, 
            String.format("Trop de ressources chargées: %d (maximum: %d)", 
                totalResources, ACCEPTABLE_RESOURCE_COUNT));
        
        Assert.assertTrue(slowResources <= 10, 
            String.format("Trop de ressources lentes: %d (maximum: 10)", slowResources));
        
        // Affichage des résultats
        System.out.println("=== ANALYSE DES RESSOURCES ===");
        System.out.println("Nombre total de ressources: " + totalResources);
        System.out.println("Temps moyen de chargement: " + Math.round(averageLoadTime) + " ms");
        System.out.println("Ressources lentes (>2s): " + slowResources);
    }

    @Test(groups = { "performance", "image-optimization", "seo" }, 
          description = "Test de l'optimisation des images sur la page d'accueil")
    public void testImageOptimization() {
        driver.get(baseUrl);
        
        // Attendre que les images soient chargées
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.tagName("body")));
        
        // Trouver toutes les images
        List<WebElement> images = driver.findElements(By.tagName("img"));
        
        int totalImages = images.size();
        int imagesWithoutAlt = 0;
        int imagesWithoutSrc = 0;
        
        for (WebElement img : images) {
            // Vérifier l'attribut alt pour l'accessibilité
            String altText = img.getAttribute("alt");
            if (altText == null || altText.trim().isEmpty()) {
                imagesWithoutAlt++;
            }
            
            // Vérifier l'attribut src
            String src = img.getAttribute("src");
            if (src == null || src.trim().isEmpty()) {
                imagesWithoutSrc++;
            }
        }
        
        // Vérifications SEO et performance
        double altPercentage = (double) imagesWithoutAlt / totalImages * 100;
        Assert.assertTrue(altPercentage <= 50.0, 
            String.format("Trop d'images sans attribut alt: %d/%d (%.1f%% - maximum accepté: 50%%)", 
                imagesWithoutAlt, totalImages, altPercentage));
        
        Assert.assertEquals(imagesWithoutSrc, 0, 
            "Des images sans attribut src ont été trouvées");
        
        System.out.println("=== OPTIMISATION DES IMAGES ===");
        System.out.println("Nombre total d'images: " + totalImages);
        System.out.println("Images sans alt: " + imagesWithoutAlt);
        System.out.println("Images sans src: " + imagesWithoutSrc);
    }

    @Test(groups = { "performance", "responsiveness", "mobile" }, 
          description = "Test de la réactivité de l'interface utilisateur")
    public void testUIResponsiveness() {
        driver.get(baseUrl);
        
        // Attendre que la page soit chargée
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.tagName("body")));
        
        JavascriptExecutor js = (JavascriptExecutor) driver;
        
        // Tester la réactivité en mesurant le temps de réponse aux clics
        try {
            // Trouver un élément cliquable (par exemple, un lien ou bouton)
            WebElement clickableElement = wait.until(ExpectedConditions.elementToBeClickable(
                By.cssSelector("a, button, .clickable")));
            
            long startTime = System.currentTimeMillis();
            clickableElement.click();
            
            // Attendre une réaction (changement d'URL ou d'élément)
            Thread.sleep(100); // Petit délai pour permettre la réaction
            
            long responseTime = System.currentTimeMillis() - startTime;
            
            Assert.assertTrue(responseTime <= 1000, 
                String.format("Interface trop lente à réagir: %d ms (maximum: 1000 ms)", responseTime));
            
            System.out.println("Temps de réponse UI: " + responseTime + " ms");
            
        } catch (Exception e) {
            System.out.println("Aucun élément cliquable trouvé pour le test de réactivité");
        }
    }

    @Test(groups = { "performance", "memory-usage", "stability" }, 
          description = "Test de l'utilisation mémoire du navigateur")
    public void testMemoryUsage() {
        driver.get(baseUrl);
        
        // Attendre que la page soit chargée
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.tagName("body")));
        
        JavascriptExecutor js = (JavascriptExecutor) driver;
        
        try {
            // Vérifier si l'API memory est disponible
            Object memoryInfo = js.executeScript(
                "if (performance.memory) {" +
                "  return {" +
                "    usedJSHeapSize: performance.memory.usedJSHeapSize," +
                "    totalJSHeapSize: performance.memory.totalJSHeapSize," +
                "    jsHeapSizeLimit: performance.memory.jsHeapSizeLimit" +
                "  };" +
                "} else {" +
                "  return null;" +
                "}");
            
            if (memoryInfo != null) {
                @SuppressWarnings("unchecked")
                Map<String, Object> memory = (Map<String, Object>) memoryInfo;
                
                Long usedMemory = ((Number) memory.get("usedJSHeapSize")).longValue();
                Long totalMemory = ((Number) memory.get("totalJSHeapSize")).longValue();
                
                // Convertir en MB
                double usedMB = usedMemory / (1024.0 * 1024.0);
                double totalMB = totalMemory / (1024.0 * 1024.0);
                
                // Vérification (limite raisonnable de 50MB pour une page web)
                Assert.assertTrue(usedMB <= 50, 
                    String.format("Utilisation mémoire trop élevée: %.2f MB (maximum: 50 MB)", usedMB));
                
                System.out.println("=== UTILISATION MÉMOIRE ===");
                System.out.println(String.format("Mémoire utilisée: %.2f MB", usedMB));
                System.out.println(String.format("Mémoire totale: %.2f MB", totalMB));
            } else {
                System.out.println("API memory non disponible dans ce navigateur");
            }
            
        } catch (Exception e) {
            System.out.println("Impossible de mesurer l'utilisation mémoire: " + e.getMessage());
        }
    }

    @AfterMethod
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}