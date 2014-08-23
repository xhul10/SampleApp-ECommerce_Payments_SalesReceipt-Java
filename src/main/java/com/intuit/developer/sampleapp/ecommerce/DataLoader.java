package com.intuit.developer.sampleapp.ecommerce;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.intuit.developer.sampleapp.ecommerce.domain.AppInfo;
import com.intuit.developer.sampleapp.ecommerce.domain.Company;
import com.intuit.developer.sampleapp.ecommerce.domain.Customer;
import com.intuit.developer.sampleapp.ecommerce.domain.SalesItem;
import com.intuit.developer.sampleapp.ecommerce.repository.AppInfoRepository;
import com.intuit.developer.sampleapp.ecommerce.repository.CompanyRepository;
import com.intuit.developer.sampleapp.ecommerce.repository.CustomerRepository;
import com.intuit.developer.sampleapp.ecommerce.repository.SalesItemRepository;
import org.apache.commons.io.FileUtils;
import org.joda.money.Money;
import org.springframework.context.ConfigurableApplicationContext;

import java.io.File;
import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: russellb337
 * Date: 8/20/14
 * Time: 3:32 PM
 */
public class DataLoader {

	/**
     * Loads oauth information from oauth.json, which is expected to be in the project root
     *
     * @param context
     */
    public static void initializeData(ConfigurableApplicationContext context) {
        if (oauthInfoNeeded(context)) {
            try {
                final File file = new File("oauth.json");
                final String jsonStr = FileUtils.readFileToString(file);
                ObjectMapper mapper = new ObjectMapper();
                final JsonNode jsonNode = mapper.readTree(jsonStr);

                createAppInfo(jsonNode, context);
                createCompany(context);

            } catch (IOException e) {
                throw new RuntimeException("Failed to read oauth information from oauth.json. Please make sure oauth.json is in the root of the project directory");
            }
        }
    }

	private static void createCompany(ConfigurableApplicationContext springContext) {
        final CompanyRepository repository = springContext.getBean(CompanyRepository.class);

        if (repository.count() == 0) {
	        System.out.println("No company data in the app, creating data");

            Company company = new Company("Your Company eCommerce Storefront");
            repository.save(company);

            createCustomers(company, springContext);
            createSalesItems(company, springContext);
        }
    }

    private static void createSalesItems(Company company, ConfigurableApplicationContext springContext) {
        final SalesItemRepository repository = springContext.getBean(SalesItemRepository.class);

        final SalesItem salesItem1 = new SalesItem("Cotton T-shirt", "Comfy cotton t-shirt", Money.parse("USD 15.00"));
        company.addServiceItem(salesItem1);
	    repository.save(salesItem1);

	    final SalesItem salesItem2 = new SalesItem("Silk Shirt", "Relax in silk", Money.parse("USD 100.00"));
	    company.addServiceItem(salesItem2);
        repository.save(salesItem2);
    }

    private static void createCustomers(Company company, ConfigurableApplicationContext springContext) {
        final CustomerRepository repository = springContext.getBean(CustomerRepository.class);

        final Customer customer1 = new Customer("John", "Defendant", "john.defendant@innocent.com", "916-555-7777");
        company.addCustomer(customer1);

        final Customer customer2 = new Customer("Jane", "Litigious", "jane.litigious@lawsuit.com", "916-777-9999");
        company.addCustomer(customer2);

        repository.save(customer1);
        repository.save(customer2);
    }


    private static boolean oauthInfoNeeded(ConfigurableApplicationContext context) {
        AppInfoRepository appInfoRepository = context.getBean(AppInfoRepository.class);
        return appInfoRepository.count() == 0;
    }


    private static AppInfo createAppInfo(JsonNode jsonNode, ConfigurableApplicationContext context) {
        AppInfoRepository repository = context.getBean(AppInfoRepository.class);

        final JsonNode jsonAppInfo = jsonNode.get("appInfo");

        AppInfo appInfo = new AppInfo(jsonAppInfo.get("appToken").asText(),
                jsonAppInfo.get("consumerKey").asText(),
                jsonAppInfo.get("consumerSecret").asText());

        repository.save(appInfo);

        return appInfo;

    }

}
