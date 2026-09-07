package dev.vaadin.agentdemo.customer.ui.view;

import java.time.format.DateTimeFormatter;
import java.util.Locale;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.card.Card;
import com.vaadin.flow.component.card.CardVariant;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import dev.vaadin.agentdemo.customer.domain.Customer;
import dev.vaadin.agentdemo.customer.service.CustomerService;

/**
 * The customer list from the Figma design "Grid view" (node 1:825): a
 * description with the two page-level actions, a scope tab bar, a search and
 * filter toolbar, and the customer grid inside an outlined card.
 *
 * <p>The design's own "Customer Management" heading is deliberately not
 * rendered. {@code MainLayout} already shows the view name in the navbar,
 * taken from the {@code @Menu} entry below, so repeating it here would show
 * the title twice.
 *
 * <p>This view is presentational. The actions, the tabs and the search field
 * carry no behaviour yet, matching the scope of the design hand-off.
 */
@Route("customers")
@PageTitle("Customer Management")
@Menu(title = "Customer Management", order = 2, icon = "vaadin:users")
public class CustomerManagementView extends VerticalLayout {

    static final String DESCRIPTION = "View and manage customer information, "
            + "including contact details and purchase history.";

    /**
     * The design spells the last purchase out as "January 5, 2024". The locale
     * is fixed so the column reads the same regardless of the server default.
     */
    private static final DateTimeFormatter LAST_PURCHASE_FORMAT =
            DateTimeFormatter.ofPattern("MMMM d, yyyy", Locale.ENGLISH);

    final Button create;
    final Button addCustomer;
    final Tabs scopes;
    final TextField search;
    final Button status;
    final Button advanced;
    final Card customers;
    final Button customersMenu;
    final Grid<Customer> grid;

    public CustomerManagementView(CustomerService customerService) {
        addClassName("customer-management-view");
        setSizeFull();
        setPadding(false);
        setSpacing(false);

        var description = new Span(DESCRIPTION);
        description.addClassName("customer-management-view__description");

        create = new Button("Create", VaadinIcon.PLAY.create());

        addCustomer = new Button("Customer", VaadinIcon.PLUS.create());
        addCustomer.addThemeVariants(ButtonVariant.PRIMARY);

        var actions = new HorizontalLayout(create, addCustomer);
        actions.setAlignItems(Alignment.CENTER);

        var header = new HorizontalLayout(description, actions);
        header.addClassName("customer-management-view__header");
        header.setWidthFull();
        header.setAlignItems(Alignment.CENTER);
        header.setFlexGrow(1, description);

        scopes = new Tabs(new Tab("All"), new Tab("Recent"),
                new Tab("Favourite"));
        scopes.addClassName("customer-management-view__tabs");
        scopes.setWidthFull();

        search = new TextField();
        search.setPlaceholder("Search");
        search.setPrefixComponent(VaadinIcon.SEARCH.create());
        search.setClearButtonVisible(true);
        // The design shows no visible label, so screen readers get one here.
        search.setAriaLabel("Search customers");
        search.setWidthFull();

        status = new Button("Status");
        advanced = new Button("Advanced");

        var filters = new HorizontalLayout(status, advanced);
        filters.setAlignItems(Alignment.CENTER);
        filters.setJustifyContentMode(JustifyContentMode.END);

        var toolbar = new HorizontalLayout(search, filters);
        toolbar.setWidthFull();
        toolbar.setAlignItems(Alignment.END);
        toolbar.setFlexGrow(1, search, filters);

        grid = new Grid<>();
        grid.addColumn(Customer::name).setHeader("Name");
        grid.addColumn(Customer::email).setHeader("Email");
        grid.addColumn(customer -> LAST_PURCHASE_FORMAT
                .format(customer.lastPurchase())).setHeader("Last purchase");
        grid.addColumn(Customer::organization).setHeader("Organization");
        grid.setItems(customerService.findAll());
        grid.setSizeFull();

        customersMenu = new Button(VaadinIcon.ELLIPSIS_DOTS_V.create());
        customersMenu.addThemeVariants(ButtonVariant.TERTIARY);
        customersMenu.setAriaLabel("Customer list options");

        customers = new Card();
        customers.addClassName("customer-management-view__card");
        customers.addThemeVariants(CardVariant.OUTLINED);
        customers.setTitle("Customers (%d)".formatted(customerService.count()));
        customers.setHeaderSuffix(customersMenu);
        customers.add(grid);
        customers.setSizeFull();

        var content = new VerticalLayout(toolbar, customers);
        content.addClassName("customer-management-view__content");
        content.setPadding(false);
        content.setSpacing(false);
        content.setFlexGrow(1, customers);

        add(header, scopes, content);
        setFlexGrow(1, content);
    }
}
