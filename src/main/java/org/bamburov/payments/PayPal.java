package org.bamburov.payments;

import org.bamburov.config.Props;
import io.restassured.http.ContentType;
import io.restassured.http.Header;

import static io.restassured.RestAssured.given;

public class PayPal {
    //private static String livePaypalHost = "https://api-m.paypal.com";
    //private static String sandboxPaypalHost = "https://api-m.sandbox.paypal.com/";
    //private static String sandboxBusinessClientId = "AZmt0fSQzaUs3vsr6lSfzP4Mkgcybe7m1xWvyOMnrx_ZxmgI-n87-1M4i8qhXWIw5jjrbAaJYjoGaeLj";
    //private static String sandboxBusinessSecret = "EFcjCmnW0DWvaoezlb0e4-ECmS9sg5Qc6qXe25yv_FGe1vcIfTropnZ9BdeO4F_KDNEfZUazlSa80vrY";
    //private static String paypalBusinessClientId = "AfTFo6zCZbDs59TFQ2WtxJZYlzTVp72rO1eawIaG93YEU7Xe5RuoHyaVYrtz4UAbl4O4FP80zEvexlZS";
    //private static String paypalBusinessSecret = "EFW2pazNffXmOiYAuX63QpVThHO3qr_03KEn0NeRxseD2hbHeYM42rtzvDzcm5Q5sauN9de2KOHKXE-G";

    public static String getToken() {
        return "Bearer " + given()
                .auth()
                .basic(Props.getPaypalBusinessClientId(), Props.getPaypalClientSecret())
                .header(new Header("Authorization", "Basic QVptdDBmU1F6YVVzM3ZzcjZsU2Z6UDRNa2djeWJlN20xeFd2eU9NbnJ4X1p4bWdJLW44Ny0xTTRpOHFoWFdJdzVqanJiQWFKWWpvR2FlTGo6RUZjakNtblcwRFd2YW9lemxiMGU0LUVDbVM5c2c1UWM2cVhlMjV5dl9GR2UxdmNJZlRyb3BuWjlCZGVPNEZfS0RORWZaVWF6bFNhODB2clk="))
                .contentType(ContentType.URLENC)
                .formParam("grant_type", "client_credentials")
                .post(Props.getPaypalUrl() + "v1/oauth2/token").body().jsonPath().getString("access_token");
    }

    public static String createInvoiceAndGetItsLink(String customerEmail, String serviceName, String totalAmount) {
        String apiLink = given()
                .header(new Header("Authorization", PayPal.getToken()))
                .contentType(ContentType.JSON)
                .body(String.format(PayPal.createDraftInvoiceBodyFormat, PayPal.getNextInvoiceNumber(), customerEmail, serviceName, totalAmount))
                .post(Props.getPaypalUrl() + "v2/invoicing/invoices").body().jsonPath().getString("href");
        String[] splittedlink = apiLink.split("/");
        String invoiceId = splittedlink[splittedlink.length - 1];

        return given()
                .header(new Header("Authorization", PayPal.getToken()))
                .contentType(ContentType.JSON)
                .body("{\"send_to_invoicer\": true}")
                .post(Props.getPaypalUrl() + "v2/invoicing/invoices/" + invoiceId + "/send").body().jsonPath().getString("href");
    }

    public static String getNextInvoiceNumber() {
        return given()
                .header(new Header("Authorization", getToken()))
                .post(Props.getPaypalUrl() + "v2/invoicing/generate-next-invoice-number").body().jsonPath().getString("invoice_number");
    }

    public static String createDraftInvoiceBodyFormat = "{\n" +
            "  \"detail\": {\n" +
            "    \"invoice_number\": \"%s\",\n" +
            "    \"currency_code\": \"EUR\",\n" +
            "    \"payment_term\": {\n" +
            "      \"term_type\": \"NO_DUE_DATE\"\n" +
            "    }\n" +
            "  },\n" +
            "  \"primary_recipients\": [\n" +
            "    {\n" +
            "      \"billing_info\": {\n" +
            "        \"email_address\": \"%s\"\n" +
            "      }\n" +
            "    }\n" +
            "  ],\n" +
            "  \"items\": [\n" +
            "    {\n" +
            "      \"name\": \"%s\",\n" +
            "      \"quantity\": \"1\",\n" +
            "      \"unit_amount\": {\n" +
            "        \"currency_code\": \"EUR\",\n" +
            "        \"value\": \"%s\"\n" +
            "      },\n" +
            "      \"unit_of_measure\": \"AMOUNT\"\n" +
            "    }\n" +
            "  ],\n" +
            "  \"configuration\": {\n" +
            "    \"partial_payment\": {\n" +
            "      \"allow_partial_payment\": false\n" +
            "    },\n" +
            "    \"allow_tip\": false\n" +
            "  }\n" +
            "}";
}
