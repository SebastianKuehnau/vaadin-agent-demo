package dev.vaadin.agentdemo.customer.ui.view;

import com.vaadin.browserless.SpringBrowserlessTest;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.server.menu.MenuConfiguration;
import com.vaadin.flow.server.menu.MenuEntry;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import dev.vaadin.agentdemo.customer.service.CustomerService;

@SpringBootTest
class CustomerManagementViewTest extends SpringBrowserlessTest {

    @Autowired
    CustomerService customerService;

    @Test
    void view_isPartOfTheNavigationMenu() {
        navigate(CustomerManagementView.class);

        Assertions.assertTrue(
                MenuConfiguration.getMenuEntries().stream()
                        .map(MenuEntry::menuClass)
                        .anyMatch(CustomerManagementView.class::equals),
                "View should be listed in the navigation menu");
    }

    @Test
    void view_showsDescriptionAndLeavesTheTitleToTheNavbar() {
        navigate(CustomerManagementView.class);

        Span description = find(Span.class)
                .withClassName("customer-management-view__description")
                .single();
        Assertions.assertEquals(CustomerManagementView.DESCRIPTION,
                description.getText());
        Assertions.assertFalse(find(H2.class).exists(),
                "View should not add a heading of its own; MainLayout already "
                        + "shows the view name in the navbar");
    }

    @Test
    void grid_showsTheFourColumnsOfTheDesign() {
        CustomerManagementView view = navigate(CustomerManagementView.class);

        Assertions.assertEquals(4, view.grid.getColumns().size(),
                "Grid should show name, email, last purchase and organization");
    }

    @Test
    void grid_showsTheCustomersFromTheService() {
        CustomerManagementView view = navigate(CustomerManagementView.class);
        var firstCustomer = customerService.findAll().getFirst();

        Assertions.assertEquals(customerService.findAll().size(),
                test(view.grid).size(),
                "Grid should show every customer the service returns");
        Assertions.assertEquals(firstCustomer.name(),
                test(view.grid).getCellText(0, 0));
        Assertions.assertEquals(firstCustomer.email(),
                test(view.grid).getCellText(0, 1));
        Assertions.assertEquals(firstCustomer.organization(),
                test(view.grid).getCellText(0, 3));
    }

    @Test
    void grid_spellsOutTheLastPurchaseDateAsInTheDesign() {
        CustomerManagementView view = navigate(CustomerManagementView.class);

        Assertions.assertEquals("January 5, 2024",
                test(view.grid).getCellText(0, 2),
                "Last purchase should be spelled out, as in the design");
    }

    @Test
    void card_titleReportsTheTotalCustomerCount() {
        CustomerManagementView view = navigate(CustomerManagementView.class);

        Assertions.assertEquals(
                "Customers (%d)".formatted(customerService.count()),
                view.customers.getTitleAsText(),
                "Card title should report the total number of customers");
    }

    @Test
    void scopeTabs_showTheDesignScopesWithTheFirstOneSelected() {
        CustomerManagementView view = navigate(CustomerManagementView.class);

        Assertions.assertEquals(3, view.scopes.getTabCount());
        Assertions.assertEquals("All", view.scopes.getTabAt(0).getLabel());
        Assertions.assertEquals("Recent", view.scopes.getTabAt(1).getLabel());
        Assertions.assertEquals("Favourite",
                view.scopes.getTabAt(2).getLabel());
        Assertions.assertSame(view.scopes.getTabAt(0),
                view.scopes.getSelectedTab(),
                "The first scope should be selected initially");
    }

    @Test
    void headerAndToolbarCarryTheActionLabelsOfTheDesign() {
        CustomerManagementView view = navigate(CustomerManagementView.class);

        Assertions.assertEquals("Create", view.create.getText());
        Assertions.assertEquals("Customer", view.addCustomer.getText());
        Assertions.assertEquals("Status", view.status.getText());
        Assertions.assertEquals("Advanced", view.advanced.getText());
    }

    @Test
    void addCustomerIsThePrimaryActionOfTheHeader() {
        CustomerManagementView view = navigate(CustomerManagementView.class);

        Assertions.assertTrue(
                view.addCustomer.getThemeNames()
                        .contains(ButtonVariant.PRIMARY.getVariantName()),
                "Adding a customer should be the primary header action");
        Assertions.assertFalse(
                view.create.getThemeNames()
                        .contains(ButtonVariant.PRIMARY.getVariantName()),
                "Only one header action should be primary");
    }

    @Test
    void iconOnlyCardMenuIsTertiaryAndHasAnAccessibleName() {
        CustomerManagementView view = navigate(CustomerManagementView.class);

        Assertions.assertTrue(
                view.customersMenu.getThemeNames()
                        .contains(ButtonVariant.TERTIARY.getVariantName()),
                "The card's overflow menu should be a tertiary button");
        Assertions.assertNotNull(view.customersMenu.getAriaLabel().orElse(null),
                "An icon-only button needs a label for screen readers");
    }

    @Test
    void searchFieldHasNoVisibleLabelButAnAccessibleName() {
        CustomerManagementView view = navigate(CustomerManagementView.class);

        Assertions.assertNull(view.search.getLabel(),
                "The design shows the search field without a visible label");
        Assertions.assertEquals("Search", view.search.getPlaceholder());
        Assertions.assertNotNull(view.search.getAriaLabel().orElse(null),
                "A field without a visible label needs an accessible name");
    }

}
