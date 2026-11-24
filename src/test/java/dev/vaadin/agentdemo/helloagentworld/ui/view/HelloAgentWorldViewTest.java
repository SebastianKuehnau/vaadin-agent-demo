package dev.vaadin.agentdemo.helloagentworld.ui.view;

import com.vaadin.browserless.SpringBrowserlessTest;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.server.menu.MenuConfiguration;
import com.vaadin.flow.server.menu.MenuEntry;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class HelloAgentWorldViewTest extends SpringBrowserlessTest {

    @Test
    void view_isPartOfTheNavigationMenu() {
        navigate(HelloAgentWorldView.class);

        Assertions.assertTrue(
                MenuConfiguration.getMenuEntries().stream()
                        .map(MenuEntry::menuClass)
                        .anyMatch(HelloAgentWorldView.class::equals),
                "View should be listed in the navigation menu");
    }

    @Test
    void showButton_isPrimary() {
        var view = navigate(HelloAgentWorldView.class);

        Assertions.assertTrue(
                view.show.getThemeNames()
                        .contains(ButtonVariant.PRIMARY.getVariantName()),
                "Show button should use the primary theme variant");
    }

    @Test
    void enterName_clickShow_greetingShownAsSuccessInBottomRight() {
        var view = navigate(HelloAgentWorldView.class);

        test(view.name).setValue("Agent");
        test(view.show).click();

        var notification = find(Notification.class).single();
        Assertions.assertEquals("Hello Agent!", test(notification).getText(),
                "Notification should greet the entered name");
        Assertions.assertEquals(Notification.Position.BOTTOM_END,
                notification.getPosition(),
                "Notification should appear in the bottom right");
        Assertions.assertTrue(
                notification.getThemeNames()
                        .contains(NotificationVariant.SUCCESS.getVariantName()),
                "Notification should use the success theme variant");
        Assertions.assertFalse(view.name.isInvalid(),
                "Field should not be marked invalid for valid input");
    }

    @Test
    void nameWithSurroundingWhitespace_clickShow_greetingUsesTrimmedName() {
        var view = navigate(HelloAgentWorldView.class);

        test(view.name).setValue("  Agent  ");
        test(view.show).click();

        Assertions.assertEquals("Hello Agent!",
                test(find(Notification.class).single()).getText(),
                "Greeting should use the name without surrounding whitespace");
    }

    @Test
    void emptyInput_clickShow_noNotificationAndFieldInvalid() {
        var view = navigate(HelloAgentWorldView.class);

        test(view.show).click();

        Assertions.assertFalse(find(Notification.class).exists(),
                "No notification should be shown for empty input");
        Assertions.assertTrue(view.name.isInvalid(),
                "Field should be marked invalid for empty input");
        Assertions.assertEquals(HelloAgentWorldView.EMPTY_INPUT_ERROR,
                view.name.getErrorMessage(),
                "Field should explain that a name is required");
    }

    @Test
    void blankInput_clickShow_noNotificationAndFieldInvalid() {
        var view = navigate(HelloAgentWorldView.class);

        test(view.name).setValue("   ");
        test(view.show).click();

        Assertions.assertFalse(find(Notification.class).exists(),
                "No notification should be shown for blank input");
        Assertions.assertTrue(view.name.isInvalid(),
                "Field should be marked invalid for blank input");
    }

    @Test
    void invalidField_typeName_errorCleared() {
        var view = navigate(HelloAgentWorldView.class);
        test(view.show).click();
        Assertions.assertTrue(view.name.isInvalid(),
                "Precondition: field is invalid after empty submit");

        test(view.name).setValue("Agent");

        Assertions.assertFalse(view.name.isInvalid(),
                "Typing a name should clear the error");
    }
}
