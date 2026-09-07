package dev.vaadin.agentdemo.message.ui.view;

import com.vaadin.browserless.SpringBrowserlessTest;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.server.menu.MenuConfiguration;
import com.vaadin.flow.server.menu.MenuEntry;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class MessageViewTest extends SpringBrowserlessTest {

    @Test
    void view_isPartOfTheNavigationMenu() {
        navigate(MessageView.class);

        Assertions.assertTrue(
                MenuConfiguration.getMenuEntries().stream()
                        .map(MenuEntry::menuClass)
                        .anyMatch(MessageView.class::equals),
                "View should be listed in the navigation menu");
    }

    @Test
    void enterText_clickButton_successNotificationShownInBottomRight() {
        MessageView view = navigate(MessageView.class);

        test(view.message).setValue("Hello Vaadin");
        test(view.show).click();

        Notification notification = find(Notification.class).single();
        Assertions.assertEquals("Hello Vaadin", test(notification).getText());
        Assertions.assertEquals(Notification.Position.BOTTOM_END,
                notification.getPosition());
        Assertions.assertTrue(
                notification.getThemeNames()
                        .contains(NotificationVariant.SUCCESS.getVariantName()),
                "Notification should use the success theme variant");
        Assertions.assertFalse(view.message.isInvalid(),
                "Field should not be marked invalid for valid input");
    }

    @Test
    void emptyInput_clickButton_noNotificationAndFieldInvalid() {
        MessageView view = navigate(MessageView.class);

        test(view.show).click();

        Assertions.assertFalse(find(Notification.class).exists(),
                "No notification should be shown for empty input");
        Assertions.assertTrue(view.message.isInvalid(),
                "Field should be marked invalid for empty input");
        Assertions.assertEquals(MessageView.EMPTY_INPUT_ERROR,
                view.message.getErrorMessage());
    }

    @Test
    void blankInput_clickButton_noNotificationAndFieldInvalid() {
        MessageView view = navigate(MessageView.class);

        test(view.message).setValue("   ");
        test(view.show).click();

        Assertions.assertFalse(find(Notification.class).exists(),
                "No notification should be shown for blank input");
        Assertions.assertTrue(view.message.isInvalid(),
                "Field should be marked invalid for blank input");
    }

    @Test
    void invalidField_typeText_errorCleared() {
        MessageView view = navigate(MessageView.class);
        test(view.show).click();
        Assertions.assertTrue(view.message.isInvalid());

        test(view.message).setValue("Hello again");

        Assertions.assertFalse(view.message.isInvalid(),
                "Typing should clear the error");
    }

}
