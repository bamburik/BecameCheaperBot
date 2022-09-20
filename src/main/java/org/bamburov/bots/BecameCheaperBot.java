package org.bamburov.bots;

import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import org.bamburov.config.Props;
import org.bamburov.messages.EnglishMessages;
import org.bamburov.messages.Messages;
import org.bamburov.messages.RussianMessages;
import org.bamburov.messages.SerbianMessages;
import org.bamburov.models.Product;
import org.apache.commons.lang3.StringUtils;
import org.bson.Document;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardRemove;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.bamburov.payments.PayPal;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class BecameCheaperBot extends TelegramLongPollingBot {

    private MongoClient client;
    private MongoCollection users;
    private MongoCollection unpaidInvoices;
    private WebDriver driver;
    private Messages messages;

    public BecameCheaperBot(MongoClient client) {
        this.client = client;
    }

    public String getBotUsername() {
        return Props.getBotName();
    }

    public String getBotToken() {
        return Props.getBotToken();
    }

    public void onUpdateReceived(Update update) {
        String chatId = update.getMessage().getChatId().toString();
        BasicDBObject searchQuery = new BasicDBObject();
        searchQuery.put("chatId", chatId);
        if (users == null) {
            users = client.getDatabase("myBotDB").getCollection("users");
        }
        if (unpaidInvoices == null) {
            unpaidInvoices = client.getDatabase("myBotDB").getCollection("unpaidInvoices");
        }
        boolean isNewUsed = users.count(new Document()
                .append("chatId", chatId)) == 0;
        if(isNewUsed) {
            users.insertOne(new Document()
                    .append("chatId", chatId)
                    .append("userFirstName", update.getMessage().getFrom().getFirstName())
                    .append("userLastName", update.getMessage().getFrom().getLastName())
                    .append("userName", update.getMessage().getFrom().getUserName())
                    .append("lastDayOfSubscription", LocalDate.now().plusDays(7).toString())
                    .append("lang", "Srpski"));
        }
        messages = getMessages(((Document)users.find(new Document().append("chatId", chatId)).first()).getString("lang"));
        if (update.getMessage().getText().equals("/start")) {
            onStartCommand(chatId, update.getMessage().getFrom().getFirstName());
        }
        else if (update.getMessage().getText().equals("/set_links")) {
            onSetLinksCommand(chatId);
        }
        else if (update.getMessage().getText().equals("/generate_promo")) {
            onGeneratePromoCommand(chatId);
        }
        else if (update.getMessage().getText().equals("/enter_promo")) {
            onEnterPromoCommand(chatId);
        }
        else if (update.getMessage().getText().equals("/help")) {
            onHelpCommand(chatId);
        }
        else if (update.getMessage().getText().equals("/buy_subscription")) {
            onBuySubscriptionCommand(chatId);
        }
        else if (update.getMessage().getText().equals("/set_language")) {
            onSetLanguageCommand(chatId);
        }
        else if (update.getMessage().getText().equals("/get_the_last_day_of_subscription")) {
            onGetTheLastDayOfSubscriptionCommand(chatId);
        }
        else if (update.getMessage().getText().equals("/show_my_promos")) {
            onShowMyPromosCommand(chatId);
        }
        else if (update.getMessage().getText().equals("/show_acceptable_sites")) {
            sendMessage(chatId, getAcceptableSites());
        }
        else {
            Document user = (Document) users.find(new Document().append("chatId", chatId)).first();
            // TODO check if user is not null
            String userState = user.getString("state");
            if (userState.equals("wait for links")) {
                afterEnterLinks(chatId, update.getMessage().getText());
            }
            else if (userState.equals("wait for promo")) {
                afterPromoEntered(chatId, update.getMessage().getText());
            }
            else if (userState.equals("wait for language")) {
                afterLanguageSelected(chatId, update.getMessage().getText());
            }
            else if (userState.equals("wait for payment duration")) {
                afterPaymentDurationSelected(chatId, update.getMessage().getText());
            }
            else if (userState.equals("wait for paypal email")) {
                afterPaypalEmailEntered(chatId, update.getMessage().getText());
            }
            else {
                // TODO to fulfill
            }
        }
    }

    private void onStartCommand(String chatId, String userFirstName) {
        sendMessage(chatId, String.format(messages.getFirstStartMessageFormat(), userFirstName));
    }

    private void onSetLinksCommand(String chatId) {
        sendMessage(chatId, messages.getSetLinksMessage());
        users.updateOne(new Document().append("chatId", chatId),
                new Document().append("$set", new Document().append("state", "wait for links")));
    }

    private void onEnterPromoCommand(String chatId) {
        sendMessage(chatId, messages.getEnterPromoMessage());
        users.updateOne(new Document().append("chatId", chatId),
                new Document().append("$set", new Document().append("state", "wait for promo")));
    }

    private void onGeneratePromoCommand(String chatId) {
        MongoCollection promos = client.getDatabase("myBotDB").getCollection("promos");
        if (promos.count(new Document().append("from", chatId)) < 10) {
            String promo;
            do {
                promo = generateRandomPromo();
            } while (promos.count(new Document().append("promo", promo)) != 0);
            promos.insertOne(new Document().append("promo", promo).append("from", chatId));
            sendMessage(chatId, String.format(messages.getPromoCreatedMessageFormat(), promo));
        }
        else {
            sendMessage(chatId, messages.getMaxCountOfPromosMessage());
        }
    }

    private void onBuySubscriptionCommand(String chatId) {
        sendMessage(chatId, messages.getBuySubscriptionMessage(), true, getPaymentDurationsReplyKeyboardMarkup());
        users.updateOne(new Document().append("chatId", chatId),
                new Document().append("$set", new Document().append("state", "wait for payment duration")));
    }

    private void onSetLanguageCommand(String chatId) {
        sendMessage(chatId, messages.getSelectLanguageMessage(), false, getLanguagesReplyKeyboardMarkup());
        users.updateOne(new Document().append("chatId", chatId),
                new Document().append("$set", new Document().append("state", "wait for language")));
    }

    private void onGetTheLastDayOfSubscriptionCommand(String chatId) {
        String lastDayOfSubscription = ((Document)users.find(new Document().append("chatId", chatId)).first()).getString("lastDayOfSubscription");
        sendMessage(chatId, String.format(messages.getTheLastDayOfSubscriptionMessageFormat(), lastDayOfSubscription));
    }

    private void onShowMyPromosCommand(String chatId) {
        MongoCollection promos = client.getDatabase("myBotDB").getCollection("promos");
        MongoCursor<Document> cursor = promos.find(new Document().append("from", chatId)).iterator();
        List<String> promosList = new ArrayList<>();
        while (cursor.hasNext()) {
            promosList.add(cursor.next().getString("promo"));
        }
        cursor.close();
        sendMessage(chatId, String.format(messages.getYourPromosMessageFormat(), StringUtils.join(promosList, "\n")));
    }

    private void afterEnterLinks(String chatId, String enteredLinks) {
        sendMessage(chatId, messages.getCheckingLinksMessage());
        String[] links = enteredLinks.split("\\r?\\n|\\r");
        String messageToRespondIfAllLinksValid = "";
        if (links.length > 10) {
            sendMessage(chatId, messages.getMaxCountOfLinksMessage());
            return;
        }
        if (Props.isChromeInContainer()) {
            try {
                driver = new RemoteWebDriver(new URL(Props.getChromeDriverUrl() + ":4444/wd/hub"), DesiredCapabilities.chrome());
            }
            catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }
        else {
            driver = new ChromeDriver();
        }
        driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
        driver.manage().window().maximize();
        boolean allLinksValid = true;
        List<Product> desiredProducts = new ArrayList<>();
        StringBuilder errorMessages = new StringBuilder();
        for (String link : links) {
            if (link.trim().equals("")) {
                continue;
            }
            // Validate link
            try {
                new URL(link).toURI();
                if (!isLinkValid(link)) {
                    allLinksValid = false;
                    errorMessages.append(link + String.format(messages.getLinkOfThatSiteIsNotSupportedMessage(), "Ekupi, Tehnomanija, Gigatron") + "\n");
                    continue;
                }
                driver.get(link);
                String currentPriceStr;
                double currentPrice;
                if (link.startsWith("https://www.ekupi.")) {
                    currentPriceStr = driver.findElement(By.cssSelector(".final-price")).getText();
                    currentPrice = Double.parseDouble(currentPriceStr.trim().replaceAll("[.]","").replace(',', '.').split("[ ]")[0]);
                }
                else if (link.startsWith("https://www.tehnomanija")) {
                    currentPriceStr = driver.findElement(By.cssSelector(".product-price-web-contant .price")).getText();
                    currentPrice = Double.parseDouble(currentPriceStr.trim().replaceAll("[.]","").replace(',', '.').split("[ ]")[0]);
                }
                else if (link.startsWith("https://dijaspora.shop/")) {
                    currentPriceStr = driver.findElement(By.cssSelector(".product_view_row .currency_switch_RSD .regular-price .price , .product_view_row .currency_switch_RSD .special-price .price")).getText();
                    currentPrice = Double.parseDouble(currentPriceStr.trim().replaceAll("[.]","").replace(',', '.').split("[ ]")[0]);
                }
                else if (link.startsWith("https://www.superalati.rs/")) {
                    currentPriceStr = driver.findElement(By.cssSelector(".product-price-and-info .amount")).getText();
                    currentPrice = Double.parseDouble(currentPriceStr.trim().replaceAll("[,]","").split("[ ]")[0]);
                }
                else if (link.startsWith("https://www.winwin.rs/")) {
                    currentPriceStr = driver.findElement(By.cssSelector(".product-shop .special-price [itemprop='price'] , .product-shop .regular-price [itemprop='price']")).getAttribute("content");
                    currentPrice = Double.parseDouble(currentPriceStr.trim());
                }
                else if (link.startsWith("https://www.emmi.rs/")) {
                    currentPriceStr = driver.findElement(By.cssSelector(".product-shop .regular-price .price , .product-shop .special-price .price")).getText();
                    currentPrice = Double.parseDouble(currentPriceStr.trim().replaceAll("[.]","").replace(',', '.').split("[ ]")[0]);
                }
                else if (link.startsWith("https://koncarelektro.com/")) {
                    currentPriceStr = driver.findElement(By.xpath("(//*[contains(@class,'single-product-wrapper')]//*[@class='price']//ins//bdi) | (//*[contains(@class,'single-product-wrapper')]//*[@class='price']/span/span/bdi)")).getText();
                    currentPrice = Double.parseDouble(currentPriceStr.trim().replaceAll("[.]","").replace(',', '.').split("[ ]")[0]);
                }
                else if (link.startsWith("https://www.shoppster.com/sr-RS/")) {
                    currentPriceStr = driver.findElement(By.xpath("//ung-product-details//ung-price-value[not(.//*[contains(@class,'discount')])]//span[contains(@class,'price__value--normal')]")).getText();
                    currentPrice = Double.parseDouble(currentPriceStr.trim().replaceAll("[.]","").replace(',', '.').split("[ ]")[0]);
                }
                else if (link.startsWith("https://eurotehna.rs/")) {
                    currentPriceStr = driver.findElement(By.cssSelector("div.product-info .product-price , div.product-info .product-price-new")).getText();
                    currentPrice = Double.parseDouble(currentPriceStr.trim().replaceAll("[,]","").split("[ ]")[0]);
                }
                else if (link.startsWith("https://www.nitom.rs/")) {
                    currentPriceStr = driver.findElement(By.cssSelector("#product-preview .product-price")).getText();
                    currentPrice = Double.parseDouble(currentPriceStr.trim().replaceAll("[.]","").replace(',', '.').split("[ ]")[0]);
                }
                else if (link.startsWith("https://lirsshop.rs/")) {
                    currentPriceStr = driver.findElement(By.cssSelector(".woo-summary-wrap .price bdi")).getText();
                    currentPrice = Double.parseDouble(currentPriceStr.trim().replaceAll("[,]","").split("[ ]")[0]);
                }
                else if (link.startsWith("https://www.bcgroup-online.com/")) {
                    currentPriceStr = driver.findElement(By.cssSelector("#prodinfo #price")).getText();
                    currentPrice = Double.parseDouble(currentPriceStr.trim().replaceAll("[.]","").replace(',', '.').split("[ ]")[0]);
                }
                else if (link.startsWith("https://crafter.rs/")) {
                    currentPriceStr = driver.findElement(By.cssSelector(".product-essential [itemprop='price'] .price-value")).getText();
                    currentPrice = Double.parseDouble(currentPriceStr.trim().replaceAll("[.]","").replace(',', '.').split("[ ]")[0]);
                }
                else if (link.startsWith("https://www.market.metalac.com/")) {
                    currentPriceStr = driver.findElement(By.cssSelector("form .product-price")).getText();
                    currentPrice = Double.parseDouble(currentPriceStr.trim().replaceAll("[.]","").replace(',', '.').split("[ ]")[0]);
                }
                else if (link.startsWith("https://eplaneta.rs/")) {
                    currentPriceStr = driver.findElement(By.cssSelector(".product-info-main [data-price-type='finalPrice']")).getAttribute("data-price-amount");
                    currentPrice = Double.parseDouble(currentPriceStr.trim());
                }
                else if (link.startsWith("https://www.drtechno.rs/")) {
                    currentPriceStr = driver.findElement(By.cssSelector(".product-info-main [data-price-type='finalPrice']")).getText();
                    currentPrice = Double.parseDouble(currentPriceStr.trim().replaceAll("[.]","").replace(',', '.').split("[ ]")[0]);
                }
                else if (link.startsWith("https://maxshop.rs/")) {
                    currentPriceStr = driver.findElement(By.cssSelector(".product .product__price")).getText();
                    currentPrice = Double.parseDouble(currentPriceStr.trim().split("[ ]")[0]);
                }
                else if (link.startsWith("https://www.tempo-tehnika.rs/")) {
                    currentPriceStr = driver.findElement(By.cssSelector(".product-preview-price .JSAkcCena")).getAttribute("data-akc_cena");
                    currentPrice = Double.parseDouble(currentPriceStr.trim());
                }
                else if (link.startsWith("https://spektar.rs/")) {
                    currentPriceStr = driver.findElement(By.cssSelector(".product-view__info .price")).getText();
                    currentPrice = Double.parseDouble(currentPriceStr.trim().replaceAll("[.]","").replace(',', '.').split("[ ]")[0]);
                }
                else if (link.startsWith("https://www.ctshop.rs/")) {
                    currentPriceStr = driver.findElement(By.cssSelector("#product-page-price .price")).getText();
                    currentPrice = Double.parseDouble(currentPriceStr.trim().replaceAll("[.]","").replace(',', '.').split("[ ]")[0]);
                }
                else if (link.startsWith("https://www.tri-o.rs/")) {
                    currentPriceStr = driver.findElement(By.cssSelector("[itemprop='price']")).getAttribute("content");
                    currentPrice = Double.parseDouble(currentPriceStr.trim());
                }
                else if (link.startsWith("https://www.vmelektronik.com/")) {
                    currentPriceStr = driver.findElement(By.xpath("(//*[contains(@class,'summary')]//*[@class='price']//ins//bdi) | (//*[contains(@class,'summary')]//*[@class='price']/span/bdi)")).getText();
                    currentPrice = Double.parseDouble(currentPriceStr.trim().replaceAll("[.]","").replace(',', '.').split("[ ]")[0]);
                }
                else if (link.startsWith("https://www.elbraco.rs/")) {
                    currentPriceStr = driver.findElement(By.cssSelector(".col-prod-info .prod-price")).getText();
                    currentPrice = Double.parseDouble(currentPriceStr.trim().replaceAll("[.]","").replace(',', '.').split("[ ]")[0]);
                }
                else if (link.startsWith("https://www.gstore.rs/")) {
                    currentPriceStr = driver.findElement(By.cssSelector(".product-preview-info .main-price")).getText();
                    currentPrice = Double.parseDouble(currentPriceStr.trim().replaceAll("[.]","").replace(',', '.').split("[ ]")[0]);
                }
                else if (link.startsWith("https://www.dudico.com/")) {
                    currentPriceStr = driver.findElement(By.xpath("//*[@class='product__price']")).getText();
                    currentPrice = Double.parseDouble(currentPriceStr.trim().replaceAll("[.]","").replace(',', '.').split("[ ]")[0]);
                }
                else if (link.startsWith("https://www.linkplus.rs/")) {
                    currentPriceStr = driver.findElement(By.cssSelector(".product-preview-info .JSweb_price")).getText();
                    currentPrice = Double.parseDouble(currentPriceStr.trim().replaceAll("[.]","").replace(',', '.').split("[ ]")[0]);
                }
                else if (link.startsWith("https://gigatron.rs/")) {
                    currentPriceStr = driver.findElement(By.cssSelector(".main-box .ppra_price-number")).getText();
                    currentPrice = Double.parseDouble(currentPriceStr.trim().replaceAll("[.]","").replace(',', '.').split("[ ]")[0]);
                }
                else {
                    throw new MalformedURLException();
                }
                desiredProducts.add(new Product(link, currentPrice));
                messageToRespondIfAllLinksValid += link + "\n" + messages.getRetrievedPriceMessage() + " - " + currentPrice + "\n";
            }
            catch (MalformedURLException e) {
                allLinksValid = false;
                errorMessages.append(link + messages.getIsNotAValidLink() + "\n");
            }
            catch (URISyntaxException e) {
                allLinksValid = false;
                errorMessages.append(link + messages.getIsNotAValidLink() + "\n");
            }
            catch (WebDriverException e) {
                allLinksValid = false;
                errorMessages.append(link + messages.getIsNotAValidLink() + "\n");
            }
        }
        driver.quit();
        if (allLinksValid) {
            List<Document> productsDocument = new ArrayList<>();
            for(Product product : desiredProducts) {
                productsDocument.add(new Document().append("link", product.getLink()).append("initialPrice", product.getInitialPrice()));
            }
            users.updateOne(new Document().append("chatId", chatId),
                    new Document().append("$set", new Document().append("interestedProducts", productsDocument).append("state", "no pending action")));
            sendMessage(chatId, String.format(messages.getLinksAcceptedMessageFormat(), messageToRespondIfAllLinksValid));
        }
        else {
            sendMessage(chatId, String.format(messages.getFollowingLinksAreInvalidMessageFormat(), errorMessages.toString()));
        }
    }

    private void afterPromoEntered(String chatId, String enteredPromo) {
        // Check if entered promo valid
        if (enteredPromo.length() != 8 && !enteredPromo.chars().allMatch(Character::isLetter)) {
            sendMessage(chatId, messages.getEnteredPromoIsNotValidMessage());
            return;
        }
        MongoCollection promos = client.getDatabase("myBotDB").getCollection("promos");
        if (promos.count(new Document().append("promo", enteredPromo)) == 0) {
            sendMessage(chatId, messages.getEnteredPromoDoesNotExistMessage());
            return;
        }
        String promoFromUserChatId = ((Document)(promos.find(new Document().append("promo", enteredPromo)).first())).getString("from");
        if(promoFromUserChatId.equals(chatId)) {
            sendMessage(chatId, messages.getYouCantUsePromoThatWasGeneratedByYouMessage());
            return;
        }
        MongoCollection usedPromos = client.getDatabase("myBotDB").getCollection("usedPromos");
        if (usedPromos.count(new Document().append("from", promoFromUserChatId).append("for", chatId)) != 0) {
            sendMessage(chatId, messages.getYouAlreadyUsedPromoFromThatUserMessageFormat());
            return;
        }
        if (usedPromos.count(new Document().append("from", chatId).append("for", promoFromUserChatId)) != 0) {
            String promoFromUserName = ((Document)(users.find(new Document().append("chatId", promoFromUserChatId)).first())).getString("userName");
            sendMessage(chatId, String.format(messages.getYouUsePromoFromThatUserBecauseThatUserUsedThePromoGeneratedByYouMessageFormat(), promoFromUserName));
            return;
        }
        promos.deleteOne(new Document().append("promo", enteredPromo));
        usedPromos.insertOne(new Document().append("from", promoFromUserChatId).append("for", chatId));
        addOneWeekSubscriptionFor(chatId);
        addOneWeekSubscriptionFor(promoFromUserChatId);
        String lastDayOfSubscription = ((Document)users.find(new Document().append("chatId", chatId)).first()).getString("lastDayOfSubscription");
        sendMessage(chatId, messages.getPromoIsAppliedSuccessfullyMessage() + "\n" + String.format(messages.getTheLastDayOfSubscriptionMessageFormat(), lastDayOfSubscription));
        lastDayOfSubscription = ((Document)users.find(new Document().append("chatId", promoFromUserChatId)).first()).getString("lastDayOfSubscription");
        sendMessage(promoFromUserChatId, messages.getYourFriendSuccessfullyAppliedYourPromoMessageFormat() + "\n" + String.format(messages.getTheLastDayOfSubscriptionMessageFormat(), lastDayOfSubscription));
        users.updateOne(new Document().append("chatId", chatId),
                new Document().append("$set", new Document().append("state", "no pending action")));
    }

    private void afterLanguageSelected(String chatId, String enteredLanguage) {
        if (enteredLanguage.equals("Srpski")
                || enteredLanguage.equals("English")
                || enteredLanguage.equals("Русский")) {
            messages = getMessages(enteredLanguage);
            sendMessage(chatId, messages.getLanguageIsSelectedMessage(), false, new ReplyKeyboardRemove(true));
            users.updateOne(new Document().append("chatId", chatId),
                    new Document().append("$set", new Document().append("lang", enteredLanguage).append("state", "no pending action")));
        }
        else  {
            sendMessage(chatId, String.format(messages.getYouEnteredInvalidLanguageMessageFormat(), "Srpski", "English", "Русский"));
        }
    }

    private void afterPaymentDurationSelected(String chatId, String enteredPaymentDuration) {
        if (enteredPaymentDuration.startsWith("1")
                || enteredPaymentDuration.startsWith("3")
                || enteredPaymentDuration.startsWith("6")) {
            sendMessage(chatId, messages.getSendPaypalEmailMessage(), false, new ReplyKeyboardRemove(true));
            users.updateOne(new Document().append("chatId", chatId),
                    new Document().append("$set", new Document().append("enteredPaymentDuration", enteredPaymentDuration).append("state", "wait for paypal email")));
        }
        else {
            sendMessage(chatId, String.format(messages.getYouEnteredInvalidPaymentPeriodMessageFormat(), messages.getOneMonthMessage(), messages.getThreeMonthsMessage(), messages.getSixMonthsMessage()));
        }
    }

    private void afterPaypalEmailEntered(String chatId, String enteredPaypalEmail) {
        String totalAmount = "";
        String serviceName = "";
        String paymentDuration = ((Document)users.find(new Document().append("chatId", chatId)).first()).getString("enteredPaymentDuration");
        if (paymentDuration.startsWith("1")) {
            totalAmount = "3.99";
            serviceName = "1 month of bot subscription";
        }
        else if (paymentDuration.startsWith("3")) {
            totalAmount = "9.99";
            serviceName = "3 months of bot subscription";
        }
        else if (paymentDuration.startsWith("6")) {
            totalAmount = "17.99";
            serviceName = "6 months of bot subscription";
        }
        String invoiceLink = "";
        try {
            invoiceLink = PayPal.createInvoiceAndGetItsLink(enteredPaypalEmail, serviceName, totalAmount);
        }
        catch (Exception e) {
            sendMessage(chatId, messages.getYouEnteredInvalidPaypalEmailMessage());
        }
        sendMessage(chatId, messages.getInvoiceIsGeneratedMessage(invoiceLink));
        users.updateOne(new Document().append("chatId", chatId),
                new Document().append("$set", new Document().append("state", "no pending action")));
        unpaidInvoices.insertOne(new Document().append("invoiceLink", invoiceLink).append("chatId", chatId).append("paymentPeriod", paymentDuration.substring(0,1)));
    }

    private void onHelpCommand(String chatId) {
        sendMessage(chatId, messages.getHelpMessage());
    }

    private String generateRandomPromo() {
        int promoLength = 8;
        StringBuilder stringBuilder = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < promoLength; i++) {
            stringBuilder.append((char)(65 + (int) (random.nextFloat() * (90 - 65 + 1))));
        }
        return stringBuilder.toString();
    }

    private void addOneWeekSubscriptionFor(String chatId) {
        LocalDate currentLastDayOfSubscription = LocalDate.parse(((Document)users.find(new Document().append("chatId", chatId)).first()).getString("lastDayOfSubscription"));
        LocalDate newLastDayOfSubscription;
        if (currentLastDayOfSubscription.isAfter(LocalDate.now())) {
            newLastDayOfSubscription = currentLastDayOfSubscription.plusDays(7);
        }
        else {
            newLastDayOfSubscription = LocalDate.now().plusDays(7);
        }
        users.updateOne(new Document().append("chatId", chatId),
                new Document().append("$set", new Document("lastDayOfSubscription", newLastDayOfSubscription.toString())));
    }

    private void sendMessage(String chatId, String message, boolean enableMarkdownV2, ReplyKeyboard replyKeyboard) {
        SendMessage request = new SendMessage();
        request.setChatId(chatId);
        request.enableMarkdownV2(enableMarkdownV2);
        if (replyKeyboard != null) {
            request.setReplyMarkup(replyKeyboard);
        }
        request.setText(message);
        try {
            this.execute(request);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private void sendMessage(String chatId, String message) {
        sendMessage(chatId, message, false, null);
    }

    private ReplyKeyboardMarkup getLanguagesReplyKeyboardMarkup() {
        ReplyKeyboardMarkup result = new ReplyKeyboardMarkup();
        result.setOneTimeKeyboard(true);
        List<KeyboardRow> keyboard = new ArrayList<>();
        KeyboardRow row1 = new KeyboardRow();
        row1.add("Srpski");
        KeyboardRow row2 = new KeyboardRow();
        row2.add("English");
        KeyboardRow row3 = new KeyboardRow();
        row3.add("Русский");
        keyboard.addAll(Arrays.asList(row1, row2, row3));
        result.setKeyboard(keyboard);
        return result;
    }

    private ReplyKeyboardMarkup getPaymentDurationsReplyKeyboardMarkup() {
        ReplyKeyboardMarkup result = new ReplyKeyboardMarkup();
        List<KeyboardRow> keyboard = new ArrayList<>();
        result.setOneTimeKeyboard(true);
        KeyboardRow row1 = new KeyboardRow();
        row1.add(messages.getOneMonthMessage());
        KeyboardRow row2 = new KeyboardRow();
        row2.add(messages.getThreeMonthsMessage());
        KeyboardRow row3 = new KeyboardRow();
        row3.add(messages.getSixMonthsMessage());
        keyboard.addAll(Arrays.asList(row1, row2, row3));
        result.setKeyboard(keyboard);
        return result;
    }

    private Messages getMessages(String lang) {
        switch (lang) {
            case "Srpski":
                return new SerbianMessages();
            case "English":
                return new EnglishMessages();
            case "Русский":
                return new RussianMessages();
            default:
                throw new IllegalArgumentException();
        }
    }

    private String getAcceptableSites(){
        return "https://www.ekupi.rs/\n" +
                "https://www.tehnomanija.rs/\n" +
                "https://gigatron.rs/\n" +
                "https://dijaspora.shop/\n" +
                "https://www.superalati.rs/\n" +
                "https://www.winwin.rs/\n" +
                "https://www.emmi.rs/\n" +
                "https://koncarelektro.com/\n" +
                "https://www.shoppster.com/sr-RS/\n" +
                "https://eurotehna.rs/index.php?route=common/home\n" +
                "https://www.nitom.rs/\n" +
                "https://lirsshop.rs/\n" +
                "https://www.bcgroup-online.com/\n" +
                "https://crafter.rs/\n" +
                "https://www.market.metalac.com/\n" +
                "https://eplaneta.rs/\n" +
                "https://www.drtechno.rs/\n" +
                "https://maxshop.rs/\n" +
                "https://www.tempo-tehnika.rs/\n" +
                "https://spektar.rs/\n" +
                "https://www.ctshop.rs/\n" +
                "https://www.tri-o.rs/\n" +
                "https://www.vmelektronik.com/\n" +
                "https://www.elbraco.rs/\n" +
                "https://www.gstore.rs/\n" +
                "https://www.dudico.com/\n" +
                "https://www.linkplus.rs/";
    }

    private boolean isLinkValid(String link) {
        return link.startsWith("https://www.ekupi.") ||
                link.startsWith("https://www.tehnomanija.") ||
                link.startsWith("https://gigatron.") ||
                link.startsWith("https://dijaspora.shop/") ||
                link.startsWith("https://www.superalati.rs/") ||
                link.startsWith("https://www.winwin.rs/") ||
                link.startsWith("https://www.emmi.rs/") ||
                link.startsWith("https://koncarelektro.com/") ||
                link.startsWith("https://www.shoppster.rs/") ||
                link.startsWith("https://eurotehna.rs/") ||
                link.startsWith("https://www.nitom.rs/") ||
                link.startsWith("https://lirsshop.rs/") ||
                link.startsWith("https://www.bcgroup-online.com/") ||
                link.startsWith("https://crafter.rs/") ||
                link.startsWith("https://www.market.metalac.com/") ||
                link.startsWith("https://eplaneta.rs/") ||
                link.startsWith("https://www.drtechno.rs/") ||
                link.startsWith("https://maxshop.rs/") ||
                link.startsWith("https://www.tempo-tehnika.rs/") ||
                link.startsWith("https://spektar.rs/") ||
                link.startsWith("https://www.ctshop.rs/") ||
                link.startsWith("https://www.tri-o.rs/") ||
                link.startsWith("https://www.vmelektronik.com/") ||
                link.startsWith("https://www.elbraco.rs/") ||
                link.startsWith("https://www.gstore.rs/") ||
                link.startsWith("https://www.dudico.com/") ||
                link.startsWith("https://www.linkplus.rs/");
    }
}
