package org.bamburov.config;

import lombok.Getter;
import lombok.Setter;

public class Props {
    @Getter
    @Setter
    private static boolean chromeInContainer;
    @Getter
    @Setter
    private static String chromeDriverUrl;
    @Getter
    @Setter
    private static String botName;
    @Getter
    @Setter
    private static String botToken;
    @Getter
    @Setter
    private static String mongoHost;
    @Getter
    @Setter
    private static int mongoPort;
    @Getter
    @Setter
    private static String paypalUrl;
    @Getter
    @Setter
    private static String paypalBusinessClientId;
    @Getter
    @Setter
    private static String paypalClientSecret;
}
