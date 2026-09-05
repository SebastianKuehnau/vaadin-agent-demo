package dev.vaadin.agentdemo.views;

import com.vaadin.browserless.SpringBrowserlessTest;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class MessageViewTest extends SpringBrowserlessTest {

    @Test
    void enterText_clickButton_successNotificationIsShown() {
        MessageView view = navigate(MessageView.class);

        test(view.message).setValue("Hello Vaadin");
        test(view.showButton).click();

        Notification notification = find(Notification.class).single();
        Assertions.assertEquals("Hello Vaadin", test(notification).getText());
        Assertions.assertTrue(
                notification.getThemeNames().contains(NotificationVariant.SUCCESS.getVariantName()),
                "Notification should use the success theme variant");
        Assertions.assertEquals(Notification.Position.BOTTOM_END, notification.getPosition());
        Assertions.assertFalse(view.message.isInvalid(), "Field should not be marked invalid");
    }

    @Test
    void emptyInput_clickButton_errorNotificationIsShown() {
        MessageView view = navigate(MessageView.class);

        test(view.showButton).click();

        Notification notification = find(Notification.class).single();
        Assertions.assertEquals("Please enter a message", test(notification).getText());
        Assertions.assertTrue(
                notification.getThemeNames().contains(NotificationVariant.ERROR.getVariantName()),
                "Notification should use the error theme variant");
        Assertions.assertTrue(view.message.isInvalid(), "Field should be marked invalid");
    }

    @Test
    void blankInput_clickButton_errorNotificationIsShown() {
        MessageView view = navigate(MessageView.class);

        test(view.message).setValue("   ");
        test(view.showButton).click();

        Notification notification = find(Notification.class).single();
        Assertions.assertEquals("Please enter a message", test(notification).getText());
        Assertions.assertTrue(view.message.isInvalid(), "Field should be marked invalid");
    }
}
