package dev.vaadin.agentdemo.views;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import dev.vaadin.agentdemo.views.MessageView;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import com.vaadin.browserless.SpringBrowserlessTest;
import com.vaadin.browserless.ViewPackages;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonTester;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationTester;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.sidenav.SideNav;
import com.vaadin.flow.component.sidenav.SideNavItem;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.textfield.TextFieldTester;

@SpringBootTest
@ViewPackages(classes = MessageView.class)
class MessageViewTest extends SpringBrowserlessTest {

    private TextField messageField;
    private TextFieldTester<TextField, String> messageFieldTester;
    private ButtonTester<Button> showButtonTester;

    @BeforeEach
    void navigateToView() {
        navigate(MessageView.class);
        messageField = $(TextField.class).single();
        messageFieldTester = test(messageField);
        showButtonTester = test($(Button.class).withText("Show notification").single());
    }

    @Test
    @DisplayName("The view is reachable from the navigation")
    void view_isInTheNavigation() {
        SideNav nav = $(SideNav.class).single();
        assertTrue(
                nav.getItems().stream().map(SideNavItem::getLabel)
                        .anyMatch("Message"::equals),
                "Expected a 'Message' item in the side navigation, found: "
                        + nav.getItems().stream().map(SideNavItem::getLabel).toList());
    }

    @Test
    @DisplayName("The button is styled as primary")
    void button_isPrimary() {
        assertTrue(showButtonTester.getComponent().getElement().getThemeList()
                .contains(ButtonVariant.PRIMARY.getVariantName()),
                "Expected the button to use the primary theme variant");
    }

    @Test
    @DisplayName("Clicking shows the field content as a success notification in the bottom right")
    void click_showsFieldContentAsSuccessNotification() {
        messageFieldTester.setValue("Hello Vaadin");
        showButtonTester.click();

        Notification notification = $(Notification.class).single();
        NotificationTester<Notification> tester = test(notification);

        assertEquals("Hello Vaadin", tester.getText());
        assertEquals(Notification.Position.BOTTOM_END, notification.getPosition(),
                "Expected the notification in the bottom right corner");
        assertTrue(notification.getElement().getThemeList()
                .contains(NotificationVariant.SUCCESS.getVariantName()),
                "Expected the notification to use the success theme variant");
        assertFalse(messageField.isInvalid(), "The field should not be invalid");
    }

    @Test
    @DisplayName("Clicking with empty input shows an error instead")
    void click_withEmptyInput_showsError() {
        showButtonTester.click();

        Notification notification = $(Notification.class).single();

        assertEquals(MessageView.EMPTY_MESSAGE_ERROR, test(notification).getText());
        assertTrue(notification.getElement().getThemeList()
                .contains(NotificationVariant.ERROR.getVariantName()),
                "Expected the notification to use the error theme variant");
        assertTrue(messageField.isInvalid(), "The field should be marked invalid");
        assertEquals(MessageView.EMPTY_MESSAGE_ERROR, messageField.getErrorMessage());
    }

    @Test
    @DisplayName("Blank input is treated as empty")
    void click_withBlankInput_showsError() {
        messageFieldTester.setValue("   ");
        showButtonTester.click();

        assertEquals(MessageView.EMPTY_MESSAGE_ERROR,
                test($(Notification.class).single()).getText());
        assertTrue(messageField.isInvalid(), "The field should be marked invalid");
    }

    @Test
    @DisplayName("Typing again clears the error state")
    void typingAfterError_clearsInvalidState() {
        showButtonTester.click();
        assertTrue(messageField.isInvalid());

        messageFieldTester.setValue("Hello again");
        assertFalse(messageField.isInvalid(),
                "Entering a value should clear the error state");
    }
}
