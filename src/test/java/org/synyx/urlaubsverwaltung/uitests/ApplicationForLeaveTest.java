package org.synyx.urlaubsverwaltung.uitests;

import de.retest.recheck.Recheck;
import de.retest.recheck.RecheckImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

class ApplicationForLeaveTest {

  private WebDriver driver;
  private Recheck re;

  @BeforeEach
  void setup() throws MalformedURLException {
    DesiredCapabilities capability = DesiredCapabilities.chrome();
    driver = new RemoteWebDriver(new URL("http://localhost:4444/wd/hub"), capability);

    re = new RecheckImpl();
  }

  @AfterEach
  void tearDown() {
    driver.quit();
    re.cap();
  }

  @Test
  void submitAnApplicationForLeave() {
    re.startTest("submit an application for leave");

    driver.get("http://urlaubsverwaltung:8080");

    // login
    re.check(driver, "1 login");

    findElement(By.id("username")).sendKeys("test");
    findElement(By.id("password")).sendKeys("secret");
    findElement(By.cssSelector("button[type=\"submit\"]")).click();

    // overview
    waitUntil(By.cssSelector("div[id=\"datepicker\"] > button"), 5);

    re.check(driver, "2 overview");
    findElement(By.cssSelector("ul.navbar-right > li:first-child > a")).click();

    // apply an application
    re.check(driver, "3 apply an application");
    String today = getDateToday();
    findElement(By.id("from")).sendKeys(today);
    findElement(By.id("to")).sendKeys(today);
    findElement(By.cssSelector("button[type=\"submit\"]")).click();

    // vacation request
    re.check(driver, "4 application request");
    findElement(By.cssSelector("div[id=\"navbar-collapse\"] > ul:first-child > li:first-child > a")).click();

    // overview
    waitUntil(By.cssSelector("div[id=\"datepicker\"] > button"), 5);
    re.check(driver, "5 overview with new application");
    re.capTest();
  }

  private String getDateToday() {
    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd.MM.yyyy");
    LocalDate now = LocalDate.now();
    return dtf.format(now);
  }

  private WebElement findElement(By by) {
    return driver.findElement(by);
  }

  private void waitUntil(By applyForLeave, int timeOutInSeconds) {
    new WebDriverWait(driver, timeOutInSeconds).until(d -> d.findElement(applyForLeave));
  }
}
