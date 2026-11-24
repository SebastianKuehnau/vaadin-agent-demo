package dev.vaadin.agentdemo.helloagentworld.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.vaadin.browserless.BrowserlessApplicationContext;
import com.vaadin.browserless.SpringBrowserlessApplicationContext;
import dev.vaadin.agentdemo.Application;
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

    @BeforeEach
    void openTheApp() {
        app = SpringBrowserlessApplicationContext.create(spring, Application.class);
    }

    @AfterEach
    void closeTheApp() {
        app.close();
    }

    @Test
    void visitorWhoEntersTheirNameIsGreeted() {
        var visitor = new Visitor();

        visitor.asksToBeGreetedAs("Alice");

        assertTrue(visitor.wasToldThat("Hello Alice!"));
        assertTrue(visitor.sawASuccessMessageInTheBottomRight());
    }

    @Test
    void visitorWhoEntersNoNameIsAskedForOne() {
        var visitor = new Visitor();

        visitor.asksToBeGreetedAs("   ");

        assertFalse(visitor.wasGreeted());
        assertTrue(visitor.isAskedToEnterAName());
    }

    @Test
    void showIsThePrimaryAction() {
        var visitor = new Visitor();

        assertTrue(visitor.seesShowAsThePrimaryAction());
    }

    @Test
    void viewIsReachableFromTheNavigation() {
        var visitor = new Visitor();

        assertTrue(visitor.findsInTheNavigation("Hello Agent World"));
    }

    /** Actor: a visitor of the Hello Agent World view in their own window. */
    class Visitor {

        private final HelloAgentWorldPage page =
                new HelloAgentWorldPage(app.newUser().newWindow());

        Visitor() {
            page.open();
        }

        void asksToBeGreetedAs(String name) {
            page.enterName(name);
            page.clickShow();
        }

        boolean wasToldThat(String message) {
            return message.equals(page.notification());
        }

        boolean wasGreeted() {
            return page.hasNotification();
        }

        boolean sawASuccessMessageInTheBottomRight() {
            return page.notificationIsSuccessInBottomRight();
        }

        boolean isAskedToEnterAName() {
            return page.nameIsMarkedInvalidWith(HelloAgentWorldView.EMPTY_NAME_ERROR);
        }

        boolean seesShowAsThePrimaryAction() {
            return page.showButtonIsPrimary();
        }

        boolean findsInTheNavigation(String title) {
            return page.navigationOffers(title);
        }
    }
}
