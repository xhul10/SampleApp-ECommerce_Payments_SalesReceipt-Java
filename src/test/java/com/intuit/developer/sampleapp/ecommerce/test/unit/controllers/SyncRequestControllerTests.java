package com.intuit.developer.sampleapp.ecommerce.test.unit.controllers;

import com.intuit.developer.sampleapp.ecommerce.controllers.SyncRequest;
import com.intuit.developer.sampleapp.ecommerce.controllers.SyncRequestController;
import com.intuit.developer.sampleapp.ecommerce.domain.Company;
import com.intuit.developer.sampleapp.ecommerce.domain.Customer;
import com.intuit.developer.sampleapp.ecommerce.domain.SalesItem;
import com.intuit.developer.sampleapp.ecommerce.qbo.QBOGateway;
import com.intuit.developer.sampleapp.ecommerce.repository.CompanyRepository;
import mockit.*;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static junit.framework.Assert.*;
/**
 * Created by connorm659 on 8/27/14.
 */
public class SyncRequestControllerTests {
    @Tested
    SyncRequestController controller;

    @Injectable
    QBOGateway mockedQBOGateway;

    @Injectable
    CompanyRepository companyRepository;

    @Test
    public void testCustomerSync() {
        final Customer customer = new Customer("firstName", "lastName", "emailAddress", "phoneNumber");
        final Company company = new Company("accessToken", "accessTokenSecret", "1234567");
        company.addCustomer(customer);

        SyncRequest syncRequest = new SyncRequest();
        syncRequest.setCompanyId("1234");
        syncRequest.setType(SyncRequest.EntityType.Customer);

        new NonStrictExpectations(){{
            companyRepository.findOne(anyLong);
            result = company;
        }};

        SyncRequest syncRequestReturn = controller.createSyncRequest(syncRequest);
        assertEquals(true, syncRequestReturn.isSuccessful());

        new Verifications() {{
            mockedQBOGateway.createCustomerInQBO(withSameInstance(customer)); times = 1;
            mockedQBOGateway.createItemInQBO(withInstanceOf(SalesItem.class)); times = 0;
            companyRepository.save(withSameInstance(company)); times = 1;
        }};
    }
}
