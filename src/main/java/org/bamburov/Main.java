package org.bamburov;

import org.bamburov.bots.BecameCheaperBot;
import com.mongodb.MongoClient;
import org.bamburov.config.Props;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;


public class Main {
    public static void main(String[] args) {
        setProperties();
        MongoClient mongoClient = new MongoClient(Props.getMongoHost(), Props.getMongoPort());;
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
            Props.setChromeInContainer(Boolean.parseBoolean(prop.getProperty("chromeInContainer")));
            Props.setBotName(prop.getProperty("bot.name"));
            Props.setBotToken(prop.getProperty("bot.token"));
            Props.setMongoHost(prop.getProperty("mongo.host"));
            Props.setMongoPort(Integer.parseInt(prop.getProperty("mongo.port")));
            Props.setPaypalUrl(prop.getProperty("paypal.url"));
            Props.setPaypalBusinessClientId(prop.getProperty("paypal.businessClientId"));
            Props.setPaypalClientSecret(prop.getProperty("paypal.clientSecret"));
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
