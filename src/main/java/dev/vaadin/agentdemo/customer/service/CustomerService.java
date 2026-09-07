package dev.vaadin.agentdemo.customer.service;

import java.time.LocalDate;
import java.time.Month;
import java.util.List;

import org.springframework.stereotype.Service;

import dev.vaadin.agentdemo.customer.domain.Customer;

/**
 * Supplies the customers shown in
 * {@code dev.vaadin.agentdemo.customer.ui.view.CustomerManagementView}.
 *
 * <p>Mock data: the rows and the total count are taken verbatim from the Figma
 * design (node 1:825), so that the view renders the same content as the
 * mockup. Replace this with a repository once customers are persisted.
 */
@Service
public class CustomerService {

    /**
     * The design's card header reads "Customers (42)" while listing twelve
     * sample rows, so the total is kept separate from the sample data.
     */
    private static final int TOTAL_COUNT = 42;

    private static final List<Customer> CUSTOMERS = List.of(
            new Customer("Olivia Reynolds", "olivia.reynolds@example.com",
                    LocalDate.of(2024, Month.JANUARY, 5), "Acme Solutions"),
            new Customer("Ethan Patel", "ethan.patel@example.com",
                    LocalDate.of(2024, Month.FEBRUARY, 18), "TechNova"),
            new Customer("Ava Nguyen", "ava.nguyen@example.com",
                    LocalDate.of(2024, Month.MARCH, 21), "Innovatech"),
            new Customer("Liam Rodriguez", "liam.rodriguez@example.com",
                    LocalDate.of(2024, Month.APRIL, 3), "Digital Dynamics"),
            new Customer("Zoe Kim", "zoe.kim@example.com",
                    LocalDate.of(2024, Month.MAY, 9), "CodeCrafters"),
            new Customer("Mason Singh", "mason.singh@example.com",
                    LocalDate.of(2024, Month.JUNE, 15), "ByteWave"),
            new Customer("Harper Gupta", "harper.gupta@example.com",
                    LocalDate.of(2024, Month.JULY, 22), "WebWise"),
            new Customer("Sebastian Martinez",
                    "sebastian.martinez@example.com",
                    LocalDate.of(2024, Month.AUGUST, 28), "TechTonic"),
            new Customer("Ella Khan", "ella.khan@example.com",
                    LocalDate.of(2024, Month.SEPTEMBER, 4), "InnovaSoft"),
            new Customer("Henry Lopez", "henry.lopez@example.com",
                    LocalDate.of(2023, Month.OCTOBER, 11), "DataDynamix"),
            new Customer("Sophia Chen", "sophia.chen@example.com",
                    LocalDate.of(2023, Month.NOVEMBER, 17), "ByteWave"),
            new Customer("William Ramirez", "william.ramirez@example.com",
                    LocalDate.of(2023, Month.DECEMBER, 24), "WebWise"));

    public List<Customer> findAll() {
        return CUSTOMERS;
    }

    /**
     * The total number of customers, as reported in the card header. Larger
     * than {@link #findAll()} because only the first page is mocked.
     */
    public int count() {
        return TOTAL_COUNT;
    }
}
