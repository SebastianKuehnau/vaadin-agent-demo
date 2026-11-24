package dev.vaadin.agentdemo.hello.ui.view;

import com.vaadin.browserless.SpringBrowserlessTest;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.server.menu.MenuConfiguration;
import com.vaadin.flow.server.menu.MenuEntry;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class HelloAgentWorldViewTest extends SpringBrowserlessTest {

    private TextField name;
    private Button show;

    @BeforeEach
    void openView() {
        navigate(HelloAgentWorldView.class);
        name = find(TextField.class).single();
        // MainLayout's DrawerToggle is a Button too, so narrow by text.
        show = find(Button.class).withText("Show").single();
    }

    @Test
    void view_isPartOfTheNavigationMenu() {
        Assertions.assertTrue(
                MenuConfiguration.getMenuEntries().stream()
                        .map(MenuEntry::menuClass)
                        .anyMatch(HelloAgentWorldView.class::equals),
                "View should be listed in the navigation menu");
    }

    @Test
    void showButton_isPrimary() {
        Assertions.assertTrue(
                show.getThemeNames()
                        .contains(ButtonVariant.PRIMARY.getVariantName()),
                "Show button should use the primary theme variant");
    }

    @Test
    void enterName_clickShow_successGreetingShownInBottomRight() {
        test(name).setValue("Agent");
        test(show).click();

        var notification = find(Notification.class).single();
        Assertions.assertEquals(HelloAgentWorldView.greeting("Agent"),
                test(notification).getText(),
                "Notification should greet the entered name");
        Assertions.assertEquals("Hello Agent!", test(notification).getText(),
                "Greeting should be prefixed with 'Hello ' and end with '!'");
        Assertions.assertEquals(Notification.Position.BOTTOM_END,
                notification.getPosition(),
                "Notification should appear in the bottom right");
        Assertions.assertTrue(
                notification.getThemeNames()
                        .contains(NotificationVariant.SUCCESS.getVariantName()),
                "Notification should use the success theme variant");
        Assertions.assertFalse(name.isInvalid(),
                "Field should not be marked invalid for valid input");
    }

    @Test
    void nameWithSurroundingWhitespace_clickShow_greetingIsTrimmed() {
        test(name).setValue("  Agent  ");
        test(show).click();

        Assertions.assertEquals("Hello Agent!",
                test(find(Notification.class).single()).getText(),
                "Surrounding whitespace should be trimmed from the name");
    }

    @Test
    void emptyInput_clickShow_noNotificationAndFieldInvalid() {
        test(show).click();

        Assertions.assertFalse(find(Notification.class).exists(),
                "No notification should be shown for empty input");
        Assertions.assertTrue(name.isInvalid(),
                "Field should be marked invalid for empty input");
        Assertions.assertEquals(HelloAgentWorldView.EMPTY_INPUT_ERROR,
                name.getErrorMessage(),
                "Field should explain that a name is required");
    }

    @Test
    void blankInput_clickShow_noNotificationAndFieldInvalid() {
        test(name).setValue("   ");
        test(show).click();

        Assertions.assertFalse(find(Notification.class).exists(),
                "No notification should be shown for blank input");
        Assertions.assertTrue(name.isInvalid(),
                "Field should be marked invalid for blank input");
    }

    @Test
    void invalidField_typeName_errorCleared() {
        test(show).click();
        Assertions.assertTrue(name.isInvalid(),
                "Precondition: empty input marks the field invalid");

        test(name).setValue("Agent");

        Assertions.assertFalse(name.isInvalid(),
                "Typing should clear the error");
    }
}
