package dev.vaadin.agentdemo.base.ui;

import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Header;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.orderedlayout.Scroller;
import com.vaadin.flow.component.sidenav.SideNav;
import com.vaadin.flow.component.sidenav.SideNavItem;
import com.vaadin.flow.router.AfterNavigationEvent;
import com.vaadin.flow.router.AfterNavigationObserver;
import com.vaadin.flow.router.Layout;
import com.vaadin.flow.server.menu.MenuConfiguration;
import com.vaadin.flow.server.menu.MenuEntry;

/**
 * Application shell. The side navigation is generated from the {@code @Menu}
 * annotations of the views, so a new view needs no change here. The navbar
 * shows the name of the current view.
 */
@Layout
public class MainLayout extends AppLayout implements AfterNavigationObserver {

    static final String APP_NAME = "Agent Demo";

    private final H1 viewTitle;

    public MainLayout() {
        setPrimarySection(Section.DRAWER);

        viewTitle = new H1();
        viewTitle.addClassName("view-title");
        addToNavbar(new DrawerToggle(), viewTitle);

        var appName = new H1(APP_NAME);
        appName.addClassName("app-name");
        var header = new Header(appName);
        header.getElement().setAttribute("slot", "drawer");

        var nav = new SideNav();
        MenuConfiguration.getMenuEntries().stream()
                .map(this::createNavItem)
                .forEach(nav::addItem);

        addToDrawer(header, new Scroller(nav));
    }

    private SideNavItem createNavItem(MenuEntry entry) {
        var item = new SideNavItem(entry.title(), entry.path());
        if (entry.icon() != null) {
            item.setPrefixComponent(new Icon(entry.icon()));
        }
        return item;
    }

    @Override
    public void afterNavigation(AfterNavigationEvent event) {
        viewTitle.setText(currentViewTitle());
    }

    private String currentViewTitle() {
        var content = getContent();
        return MenuConfiguration.getMenuEntries().stream()
                .filter(entry -> content != null
                        && entry.menuClass().equals(content.getClass()))
                .map(MenuEntry::title)
                .findFirst()
                .orElseGet(() -> MenuConfiguration.getPageHeader(content)
                        .orElse(""));
    }
}
