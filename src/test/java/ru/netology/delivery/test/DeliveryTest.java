package ru.netology.delivery.test;

import com.codeborne.selenide.Condition;
import com.github.javafaker.Faker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;

import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Random;

import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.open;

public class DeliveryTest {
    @BeforeEach
    void setup() {
        open("http://localhost:9999");
    }

    String city = generateCity();
    String name = generateName();
    String phone = generatePhone();

    public static String generateDate(int shift) {
        LocalDate date = LocalDate.now().plusDays(shift);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        String formattedDate = date.format(formatter);
        return formattedDate;
    }

    public static String generateCity() {
        String[] cities = {"Москва", "Калуга", "Казань", "Пермь", "Ростов-на-Дону", "Мурманск", "Волгоград", "Санкт-Петербург", "Нижний Новгород", "Владивосток", "Екатеринбург"};
        Random random = new Random();
        int randomIndex = random.nextInt(cities.length);
        return cities[randomIndex];
    }

    public static String generateName() {
        Faker faker = new Faker(new Locale("ru"));
        return faker.name().fullName();
    }

    public static String generatePhone() {
        Faker faker = new Faker(new Locale("ru"));
        return faker.phoneNumber().subscriberNumber(11);

    }


    @Test
    public void shouldFillRegistrationForm() {
        String formattedDate1 = generateDate(4);
        String formattedDate2 = generateDate(7);
        $(By.cssSelector("[data-test-id=city] input")).sendKeys(city);
        $("[data-test-id=date] input").doubleClick().sendKeys(formattedDate1);
        $(By.cssSelector("[data-test-id=name] input")).sendKeys(name);
        $(By.cssSelector("[data-test-id=phone] input")).sendKeys(phone);
        $(By.cssSelector("[data-test-id=agreement]")).click();
        $(By.cssSelector(".button")).click();
        $(".notification__content").shouldHave((Condition.text("Встреча успешно запланирована на " + formattedDate1)), Duration.ofSeconds(15)).shouldBe(Condition.visible);
        $("[data-test-id=date] input").doubleClick().sendKeys(formattedDate2);
        $(By.cssSelector(".button")).click();
        $("[data-test-id=replan-notification] .notification__title").shouldHave((Condition.text("Необходимо подтверждение")), Duration.ofSeconds(15)).shouldBe(Condition.visible);
        $(By.cssSelector("[data-test-id=replan-notification] .button")).click();
        $("[data-test-id=success-notification] .notification__content").shouldHave((Condition.text("Встреча успешно запланирована на " + formattedDate2)), Duration.ofSeconds(15)).shouldBe(Condition.visible);
    }

    @Test
    public void shouldSelectIncorrectCity() {
        String formattedDate1 = generateDate(4);
        $(By.cssSelector("[data-test-id=city] input")).sendKeys("Серпухов");
        $("[data-test-id=date] input").doubleClick().sendKeys(formattedDate1);
        $(By.cssSelector("[data-test-id=name] input")).sendKeys(name);
        $(By.cssSelector("[data-test-id=phone] input")).sendKeys(phone);
        $(By.cssSelector("[data-test-id=agreement]")).click();
        $(By.cssSelector(".button")).click();
        $(".input__sub").shouldHave((Condition.text("Доставка в выбранный город недоступна")));
    }

    @Test
    public void shouldSendUnfilledCity() {
        String formattedDate1 = generateDate(4);
        $(By.cssSelector("[data-test-id=city] input")).sendKeys("");
        $("[data-test-id=date] input").doubleClick().sendKeys(formattedDate1);
        $(By.cssSelector("[data-test-id=name] input")).sendKeys(name);
        $(By.cssSelector("[data-test-id=phone] input")).sendKeys(phone);
        $(By.cssSelector("[data-test-id=agreement]")).click();
        $(By.cssSelector(".button")).click();
        $(".input__sub").shouldHave((Condition.text("Поле обязательно для заполнения")));
    }

    @Test
    public void shouldSendIncorrectDate() {
        $(By.cssSelector("[data-test-id=city] input")).sendKeys(city);
        $("[data-test-id=date] input").doubleClick().sendKeys("27.06.2023");
        $(By.cssSelector("[data-test-id=name] input")).sendKeys(name);
        $(By.cssSelector("[data-test-id=phone] input")).sendKeys(phone);
        $(By.cssSelector("[data-test-id=agreement]")).click();
        $(By.cssSelector(".button")).click();
        $(".calendar-input .input__sub").shouldHave((Condition.text("Заказ на выбранную дату невозможен")));
    }

    @Test
    public void shouldSendUnfilledDate() {
        $(By.cssSelector("[data-test-id=city] input")).sendKeys(city);
        $("[data-test-id=date] input").doubleClick().sendKeys(Keys.BACK_SPACE);
        $(By.cssSelector("[data-test-id=name] input")).sendKeys(name);
        $(By.cssSelector("[data-test-id=phone] input")).sendKeys(phone);
        $(By.cssSelector("[data-test-id=agreement]")).click();
        $(By.cssSelector(".button")).click();
        $(".calendar-input .input__sub").shouldHave((Condition.text("Неверно введена дата")));
    }

    @Test
    public void shouldSendIncorrectName() {
        String formattedDate1 = generateDate(4);
        $(By.cssSelector("[data-test-id=city] input")).sendKeys(city);
        $("[data-test-id=date] input").doubleClick().sendKeys(formattedDate1);
        $(By.cssSelector("[data-test-id=name] input")).sendKeys("Bakulina Natalia");
        $(By.cssSelector("[data-test-id=phone] input")).sendKeys(phone);
        $(By.cssSelector("[data-test-id=agreement]")).click();
        $(By.cssSelector(".button")).click();
        $("[data-test-id=name].input_invalid .input__sub").shouldHave((Condition.text("Имя и Фамилия указаные неверно. Допустимы только русские буквы, пробелы и дефисы.")));
    }

    @Test
    public void shouldSendUnfilledName() {
        String formattedDate1 = generateDate(4);
        $(By.cssSelector("[data-test-id=city] input")).sendKeys(city);
        $("[data-test-id=date] input").doubleClick().sendKeys(formattedDate1);
        $(By.cssSelector("[data-test-id=name] input")).sendKeys("");
        $(By.cssSelector("[data-test-id=phone] input")).sendKeys(phone);
        $(By.cssSelector("[data-test-id=agreement]")).click();
        $(By.cssSelector(".button")).click();
        $("[data-test-id=name].input_invalid .input__sub").shouldHave((Condition.text("Поле обязательно для заполнения")));
    }


    @Test
    public void shouldEnterUnfilledPhone() {
        String formattedDate1 = generateDate(4);
        $(By.cssSelector("[data-test-id=city] input")).sendKeys(city);
        $("[data-test-id=date] input").doubleClick().sendKeys(formattedDate1);
        $(By.cssSelector("[data-test-id=name] input")).sendKeys(name);
        $(By.cssSelector("[data-test-id=phone] input")).sendKeys("");
        $(By.cssSelector("[data-test-id=agreement]")).click();
        $(By.cssSelector(".button")).click();
        $(By.cssSelector("[data-test-id=phone].input_invalid .input__sub")).shouldHave(Condition.text("Поле обязательно для заполнения"));
    }

    @Test
    public void shouldDoNotClickCheckbox() {
        String formattedDate1 = generateDate(4);
        String formattedDate2 = generateDate(7);
        $(By.cssSelector("[data-test-id=city] input")).sendKeys(city);
        $("[data-test-id=date] input").doubleClick().sendKeys(formattedDate1);
        $(By.cssSelector("[data-test-id=name] input")).sendKeys(name);
        $(By.cssSelector("[data-test-id=phone] input")).sendKeys(phone);
        $(By.cssSelector("[data-test-id=agreement]"));
        $(By.cssSelector(".button")).click();
        $(By.cssSelector("[data-test-id=agreement].input_invalid")).shouldBe(Condition.visible).shouldHave(Condition.text("Я соглашаюсь с условиями обработки и использования моих персональных данных"));
    }

}

