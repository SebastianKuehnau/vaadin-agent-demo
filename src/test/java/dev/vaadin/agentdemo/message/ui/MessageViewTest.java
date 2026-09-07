package dev.vaadin.agentdemo.message.ui;

import com.vaadin.browserless.SpringBrowserlessTest;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class MessageViewTest extends SpringBrowserlessTest {

    @Test
    void enterMessage_clickButton_successNotificationShown() {
        var view = navigate(MessageView.class);

        test(view.message).setValue("Hello Vaadin");
        test(view.showMessage).click();

        var notification = find(Notification.class).single();
        Assertions.assertEquals("Hello Vaadin", test(notification).getText());
        Assertions.assertEquals(Notification.Position.BOTTOM_END,
                notification.getPosition());
        Assertions.assertTrue(hasVariant(notification, NotificationVariant.SUCCESS),
                "Notification should use the success variant");
        Assertions.assertFalse(view.message.isInvalid());
    }

    @Test
    void whitespaceIsTrimmedFromTheMessage() {
        var view = navigate(MessageView.class);

        test(view.message).setValue("  Hello Vaadin  ");
        test(view.showMessage).click();

        Assertions.assertEquals("Hello Vaadin",
                test(find(Notification.class).single()).getText());
    }

    @Test
    void emptyMessage_clickButton_errorNotificationAndFieldMarkedInvalid() {
        var view = navigate(MessageView.class);

        test(view.showMessage).click();

        var notification = find(Notification.class).single();
        Assertions.assertEquals(MessageView.EMPTY_MESSAGE_ERROR,
                test(notification).getText());
        Assertions.assertTrue(hasVariant(notification, NotificationVariant.ERROR),
                "Notification should use the error variant");
        Assertions.assertTrue(view.message.isInvalid(),
                "Field should be marked invalid for empty input");
    }

    @Test
    void blankMessage_clickButton_treatedAsEmpty() {
        var view = navigate(MessageView.class);

        test(view.message).setValue("   ");
        test(view.showMessage).click();

        Assertions.assertEquals(MessageView.EMPTY_MESSAGE_ERROR,
                test(find(Notification.class).single()).getText());
        Assertions.assertTrue(view.message.isInvalid());
    }

    @Test
    void typingAfterAnError_clearsTheInvalidState() {
        var view = navigate(MessageView.class);

        test(view.showMessage).click();
        Assertions.assertTrue(view.message.isInvalid());

        test(view.message).setValue("Hello again");

        Assertions.assertFalse(view.message.isInvalid(),
                "Invalid state should clear as soon as the user types");
    }

    private static boolean hasVariant(Notification notification,
            NotificationVariant variant) {
        return notification.getThemeNames().contains(variant.getVariantName());
    }
}
