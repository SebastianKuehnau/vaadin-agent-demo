package dev.vaadin.agentdemo.hello.ui;

import com.vaadin.browserless.SpringBrowserlessTest;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.server.menu.MenuConfiguration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class HelloAgentWorldViewTest extends SpringBrowserlessTest {

    private TextField name;
    private Button show;

    @BeforeEach
    void openView() {
        navigate(HelloAgentWorldView.class);
        name = $view(TextField.class).single();
        show = $view(Button.class).withText("Show").single();
    }

    @Test
    void showButtonIsPrimary() {
        assertTrue(show.hasThemeName(ButtonVariant.PRIMARY.getVariantName()));
    }

    @Test
    void clickShowsSuccessNotificationBottomEnd() {
        test(name).setValue("Agent");
        test(show).click();

        var notification = $(Notification.class).single();
        assertEquals("Hello Agent!", test(notification).getText());
        assertEquals(Notification.Position.BOTTOM_END, notification.getPosition());
        assertTrue(notification.hasThemeName(
                NotificationVariant.SUCCESS.getVariantName()));
    }

    @Test
    void inputIsTrimmed() {
        test(name).setValue("  Agent  ");
        test(show).click();

        assertEquals("Hello Agent!",
                test($(Notification.class).single()).getText());
    }

    @Test
    void emptyInputShowsErrorAndNoNotification() {
        test(show).click();

        assertTrue(name.isInvalid());
        assertEquals(HelloAgentWorldView.EMPTY_NAME_ERROR, name.getErrorMessage());
        assertTrue($(Notification.class).all().isEmpty());
    }

    @Test
    void blankInputShowsErrorAndNoNotification() {
        test(name).setValue("   ");
        test(show).click();

        assertTrue(name.isInvalid());
        assertTrue($(Notification.class).all().isEmpty());
    }

    @Test
    void errorIsClearedWhenValueChanges() {
        test(show).click();
        assertTrue(name.isInvalid());

        test(name).setValue("Agent");
        assertFalse(name.isInvalid());
    }

    @Test
    void viewIsInNavigation() {
        assertTrue(MenuConfiguration.getMenuEntries().stream()
                .anyMatch(e -> e.menuClass().equals(HelloAgentWorldView.class)
                        && e.title().equals("Hello Agent World")));
    }
}
