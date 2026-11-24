package dev.vaadin.agentdemo.hello.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import dev.vaadin.agentdemo.Application;
import com.vaadin.browserless.BrowserlessApplicationContext;
import com.vaadin.browserless.SpringBrowserlessApplicationContext;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

@SpringBootTest
class HelloAgentWorldTest {

    @Autowired
    ApplicationContext spring;

    BrowserlessApplicationContext app;
    HelloAgentWorldPage page;

    @BeforeEach
    void openTheGreeter() {
        app = SpringBrowserlessApplicationContext.create(spring, Application.class);
        page = new HelloAgentWorldPage(app.newUser().newWindow());
        page.open();
    }

    @AfterEach
    void closeTheApp() {
        app.close();
    }

    @Test
    void userIsGreetedByName() {
        page.enterName("Agent");
        page.clickShow();

        assertEquals("Hello Agent!", page.notification());
        assertTrue(page.notificationIsSuccessInBottomRight());
    }

    @Test
    void showIsThePrimaryAction() {
        assertTrue(page.showsShowAsPrimaryAction());
    }

    @Test
    void surroundingWhitespaceIsNotPartOfTheGreeting() {
        page.enterName("  Agent  ");
        page.clickShow();

        assertEquals("Hello Agent!", page.notification());
    }

    @Test
    void userWithoutANameIsAskedToEnterOne() {
        page.clickShow();

        assertFalse(page.hasNotification());
        assertEquals("Please enter a name", page.nameFieldError());
    }

    @Test
    void blankNameIsTreatedAsEmpty() {
        page.enterName("   ");
        page.clickShow();

        assertFalse(page.hasNotification());
        assertEquals("Please enter a name", page.nameFieldError());
    }

    @Test
    void errorGoesAwayOnceTheUserTypesAName() {
        page.clickShow();
        page.enterName("Agent");

        assertNull(page.nameFieldError());
        page.clickShow();
        assertEquals("Hello Agent!", page.notification());
    }

    @Test
    void greeterIsReachableFromTheNavigation() {
        assertTrue(page.navigationOffers("Hello Agent World"));
    }
}
