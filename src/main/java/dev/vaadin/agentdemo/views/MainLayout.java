package dev.vaadin.agentdemo.views;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.dom.Style;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.orderedlayout.Scroller;
import com.vaadin.flow.component.sidenav.SideNav;
import com.vaadin.flow.component.sidenav.SideNavItem;
import com.vaadin.flow.router.Layout;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import com.vaadin.flow.server.menu.MenuConfiguration;
import com.vaadin.flow.server.menu.MenuEntry;

/**
 * The main layout of the application. It is applied automatically to every view
 * and renders the navigation menu built from the {@code @Menu} annotations of
 * the views.
 */
@Layout
@AnonymousAllowed
public class MainLayout extends AppLayout {

    public MainLayout() {
        setPrimarySection(Section.DRAWER);
        addToDrawer(createHeader(), new Scroller(createSideNav()));
        addToNavbar(new DrawerToggle());
    }

    private Component createHeader() {
        var appName = new Span("Vaadin Agent Demo");
        appName.getStyle().setFontWeight(Style.FontWeight.BOLD).setPadding("var(--lumo-space-m)");
        return appName;
    }

    private SideNav createSideNav() {
        var nav = new SideNav();
        MenuConfiguration.getMenuEntries()
                .forEach(entry -> nav.addItem(createSideNavItem(entry)));
        return nav;
    }

    private SideNavItem createSideNavItem(MenuEntry menuEntry) {
        var item = new SideNavItem(menuEntry.title(), menuEntry.menuClass());
        item.setMatchNested(true);
        if (menuEntry.icon() != null) {
            item.setPrefixComponent(new Icon(menuEntry.icon()));
        }
        return item;
    }
}
