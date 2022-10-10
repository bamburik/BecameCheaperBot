package org.bamburov;

import com.mongodb.*;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import org.bamburov.bots.BecameCheaperBot;
import org.bamburov.config.Props;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;


public class Main {
    public static void main(String[] args) {
        setProperties();
        List<MongoCredential> listOfCredentials = new ArrayList<>();
        listOfCredentials.add(MongoCredential.createCredential(Props.getMongoUsername(), "admin", Props.getMongoPassword().toCharArray()));
        MongoClient mongoClient = new MongoClient(new ServerAddress(Props.getMongoHost(), Props.getMongoPort()), listOfCredentials);
        createDbAndCollectionsIfAbsent(mongoClient);
        try {
            if (!Props.isChromeInContainer()) {
                System.setProperty("webdriver.chrome.driver", "src/main/resources/chromedriver");
            }

            // register bot
            TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
            telegramBotsApi.registerBot(new BecameCheaperBot(mongoClient));
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private static void setProperties() {
        try (InputStream input = new FileInputStream(System.getProperty("propPath"))) {
            Properties prop = new Properties();
            prop.load(input);
            Props.setChromeInContainer(Boolean.parseBoolean(getFilePropertyOrSystemProperty(prop, "chromeInContainer")));
            Props.setChromeDriverUrl(getFilePropertyOrSystemProperty(prop, "chromeDriverUrl"));
            Props.setBotName(getFilePropertyOrSystemProperty(prop, "bot.name"));
            Props.setBotToken(getFilePropertyOrSystemProperty(prop, "bot.token"));
            Props.setChatIdOfRecipient(getFilePropertyOrSystemProperty(prop, "feedbackBot.chatIdOfRecipient"));
            Props.setFeedbackBotName(getFilePropertyOrSystemProperty(prop, "feedbackBot.name"));
            Props.setFeedbackBotToken(getFilePropertyOrSystemProperty(prop, "feedbackBot.token"));
            Props.setMongoHost(getFilePropertyOrSystemProperty(prop, "mongo.host"));
            Props.setMongoPort(Integer.parseInt(getFilePropertyOrSystemProperty(prop, "mongo.port")));
            Props.setMongoUsername(getFilePropertyOrSystemProperty(prop, "mongo.username"));
            Props.setMongoPassword(getFilePropertyOrSystemProperty(prop, "mongo.password"));
            Props.setPaypalUrl(getFilePropertyOrSystemProperty(prop, "paypal.url"));
            Props.setPaypalBusinessClientId(getFilePropertyOrSystemProperty(prop, "paypal.businessClientId"));
            Props.setPaypalClientSecret(getFilePropertyOrSystemProperty(prop, "paypal.clientSecret"));
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private static String getFilePropertyOrSystemProperty(Properties fileProperties, String key) {
        if (System.getProperties().containsKey(key) && !System.getProperty(key).equals("")) {
            return System.getProperty(key);
        }
        return fileProperties.getProperty(key);
    }

    private static void createDbAndCollectionsIfAbsent(MongoClient mongoClient) {
        MongoDatabase database = mongoClient.getDatabase("myBotDB");
        boolean usersCollectionExists = false;
        boolean unpaidInvoicesCollectionExists = false;
        boolean promosCollectionExists = false;
        boolean usedPromosCollectionExists = false;
        MongoCursor<String> collectionsCursor = database.listCollectionNames().iterator();
        while(collectionsCursor.hasNext()) {
            String collectionName = collectionsCursor.next();
            if (collectionName.equals("users")) {
                usersCollectionExists = true;
            } else if (collectionName.equals("promos")) {
                promosCollectionExists = true;
            } else if (collectionName.equals("usedPromos")) {
                usedPromosCollectionExists = true;
            } else if (collectionName.equals("unpaidInvoices")) {
                unpaidInvoicesCollectionExists = true;
            }
        }
        if (!usersCollectionExists) {
            database.createCollection("users");
        }
        if (!promosCollectionExists) {
            database.createCollection("promos");
        }
        if (!usedPromosCollectionExists) {
            database.createCollection("usedPromos");
        }
        if (!unpaidInvoicesCollectionExists) {
            database.createCollection("unpaidInvoices");
        }
    }
}
