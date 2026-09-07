package dev.vaadin.agentdemo.base.ui;

import com.vaadin.browserless.SpringBrowserlessTest;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.sidenav.SideNavItem;
import com.vaadin.flow.server.menu.MenuConfiguration;
import com.vaadin.flow.server.menu.MenuEntry;
import dev.vaadin.agentdemo.message.ui.view.MessageView;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class MainLayoutTest extends SpringBrowserlessTest {

    @Test
    void navigateToView_navbarShowsTheNameFromTheSideNavigation() {
        navigate(MessageView.class);

        String menuName = MenuConfiguration.getMenuEntries().stream()
                .filter(entry -> entry.menuClass().equals(MessageView.class))
                .map(MenuEntry::title).findFirst().orElseThrow();

        Assertions.assertEquals(menuName, find(H1.class).single().getText(),
                "Navbar title should be the view's name from the side navigation");
    }

    @Test
    void navigateToView_navbarTitleMatchesTheSideNavItem() {
        navigate(MessageView.class);

        SideNavItem item = find(SideNavItem.class).single();

        Assertions.assertEquals(item.getLabel(),
                find(H1.class).single().getText(),
                "Navbar title and side navigation label should be the same text");
    }

}
