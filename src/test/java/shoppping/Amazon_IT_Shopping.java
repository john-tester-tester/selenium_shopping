package shopping;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;
import org.testng.Assert;

import java.time.Duration;
import java.util.List;


public class Amazon_IT_Shopping {

    WebDriver driver;
    String laptop_search_value = "Dell Laptop";
    String laptop_memory = "4 GB";
    String laptop_min_price = "375";
    String laptop_max_price = "385";
    String target_laptop = "'Dell Latitude 3000 3330 Laptop (2022)'";
    String expected_laptop_price = "$379";
    String num_of_items_after_laptop_selection = "1";

    String monitor_search_value = "Dell Monitor";
    String monitor_min_price = "355";
    String monitor_max_price = "365";
    String target_monitor = "'Dell Gaming Monitor 32 Inch'";
    String expected_monitor_price = "$359";
    String num_of_items_after_monitor_selection = "2";

    @BeforeTest
    public void setup() {
        String browserType = "firefox";
        String baseURL = "https://www.amazon.com/";

        switch (browserType) {
            case "firefox":
                FirefoxOptions firefoxOptions = new FirefoxOptions();
                //firefoxOptions.setPageLoadStrategy(PageLoadStrategy.NORMAL);
                //firefoxOptions.addArguments("-headless");
                driver = new FirefoxDriver(firefoxOptions);
                break;
            case "edge":
                EdgeOptions edgeOptions = new EdgeOptions();
                driver = new EdgeDriver(edgeOptions);
                break;
            default:
                Assert.fail("browserType not recognised " + browserType);
        }
        driver.get(baseURL);
        driver.manage().window().maximize();
    }

    @AfterTest
    public void tearDown() {
        driver.quit();
    }

    @Test
    public void Amazon_IT_shopping() {

        WebDriverWait wait =  new WebDriverWait(driver, Duration.ofSeconds(10));

        Find_item(wait, laptop_search_value);

        WebElement select_memory_amount = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//span[contains(@class, 'a-size-base a-color-base') and text()='" + laptop_memory + "']")));
        select_memory_amount.click();

        Price_range(wait, laptop_min_price, laptop_max_price);

        WebElement selected_laptop = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("img[alt*=" + target_laptop + "]")));
        selected_laptop.click();

        Add_item_to_cart(wait, expected_laptop_price, num_of_items_after_laptop_selection);

        Find_item(wait, monitor_search_value);

        Price_range(wait, monitor_min_price, monitor_max_price);

        WebElement selected_monitor = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("img[alt*=" + target_monitor + "]")));
        selected_monitor.click();

        Add_item_to_cart(wait, expected_monitor_price, num_of_items_after_monitor_selection);
    }

    private void Find_item(WebDriverWait wait, String search_value) {

        WebElement search_input = wait.until(ExpectedConditions.presenceOfElementLocated(By.id("twotabsearchtextbox")));
        search_input.sendKeys(search_value);
        wait.until(ExpectedConditions.elementToBeClickable(By.id("nav-search-submit-button"))).click();

        Assert.assertTrue(driver.getCurrentUrl().matches(".*" + search_value.replace(" ", "\\+") + ".*"),
                "Expected url to contain " + search_value + ", actual url " + driver.getCurrentUrl());
    }

    private void Price_range(WebDriverWait wait, String min_price, String  max_price)
    {
        WebElement min_amount = wait.until(ExpectedConditions.presenceOfElementLocated(By.id("low-price")));
        min_amount.sendKeys(min_price);

        WebElement max_amount = wait.until(ExpectedConditions.presenceOfElementLocated(By.id("high-price")));
        max_amount.sendKeys(max_price);

        WebElement price_go_button = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//span[contains(@class, 'a-button-inner') and normalize-space()='Go']")));
        price_go_button.click();
    }

    private void Add_item_to_cart(WebDriverWait wait, String expected_price, String number_of_items_in_cart) {

        //String actual_price = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//table[contains(@class, 'a-lineitem a-align-top') and contains(., 'Price')]"))).getText();

        List<WebElement> actual_price_list = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath("//*[@class='a-offscreen']/following-sibling::span")));

        String actual_price = actual_price_list.getFirst().getText();

        Assert.assertTrue(actual_price.contains(expected_price),
                "Actual laptop price " + actual_price + "does not match expected laptop price " + expected_price);

        wait.until(ExpectedConditions.elementToBeClickable(By.id("add-to-cart-button"))).click();

        String actual_number_of_items_in_cart = wait.until(ExpectedConditions.presenceOfElementLocated(By.id("nav-cart-count-container"))).getText();

        Assert.assertEquals(actual_number_of_items_in_cart, number_of_items_in_cart,
                "Actual number of items in cart " + actual_number_of_items_in_cart + " does not match expected value " + number_of_items_in_cart);
    }
}
