package dev.vaadin.agentdemo.base.ui;

import com.vaadin.browserless.SpringBrowserlessTest;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.sidenav.SideNavItem;
import com.vaadin.flow.server.menu.MenuConfiguration;
import com.vaadin.flow.server.menu.MenuEntry;
import dev.vaadin.agentdemo.helloagentworld.ui.view.HelloAgentWorldView;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class MainLayoutTest extends SpringBrowserlessTest {

    private static MenuEntry menuEntryOf(Class<?> viewClass) {
        return MenuConfiguration.getMenuEntries().stream()
                .filter(entry -> viewClass.equals(entry.menuClass()))
                .findFirst()
                .orElseThrow();
    }

    private static String stripLeadingSlash(String path) {
        return path.startsWith("/") ? path.substring(1) : path;
    }

    @Test
    void sideNav_containsItemForHelloAgentWorldView() {
        navigate(HelloAgentWorldView.class);
        var entry = menuEntryOf(HelloAgentWorldView.class);

        var item = find(SideNavItem.class)
                .withCondition(i -> entry.title().equals(i.getLabel()))
                .single();

        // MenuEntry paths start with "/", SideNavItem paths are relative.
        Assertions.assertEquals(stripLeadingSlash(entry.path()),
                stripLeadingSlash(item.getPath()),
                "Side-nav item should link to the route of the view");
    }

    @Test
    void navbar_showsTitleOfCurrentView() {
        navigate(HelloAgentWorldView.class);

        var title = find(H1.class).withClassName("view-title").single();

        Assertions.assertEquals(menuEntryOf(HelloAgentWorldView.class).title(),
                title.getText(),
                "Navbar should show the menu title of the current view");
    }
}
