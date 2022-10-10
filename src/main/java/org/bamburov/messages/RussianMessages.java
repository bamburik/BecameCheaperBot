package org.bamburov.messages;

public class RussianMessages implements Messages {
    @Override
    public String getFirstStartMessageFormat() {
        return "Добрый день, %s! Я сообщу вам, когда интересующий вас товар подешевеет. Чтобы подробнее узнать бот, используйте /help команду.";
    }

    @Override
    public String getSetLinksMessage() {
        return "Введите ссылки на товары, которые вы хотели бы купить дешевле.\nКаждая ссылка должна начинаться с новой строки.\nВы можете просмотреть список поддерживаемых сайтов, используя команду /show_acceptable_sites";
    }

    @Override
    public String getLinksAcceptedMessageFormat() {
        return "Здорово! Перечисленные ниже ссылки приняты.\n%s\nЯ сообщу вам, когда какой-нибудь из этих товаров подешевеет.";
    }

    @Override
    public String getHelpMessage() {
        return "Я сообщаю, когда интересующий вас товар в интернет-магазине подешевеет. Используйте команду /set_links для того, чтобы ввести ссылки на интересующие вас товары. Используйте команду /show_acceptable_sites для того, чтобы увидеть список поддерживаемых интернет-магазинов. Максимально разрешенное количество ссылок - 10. Каждая ссылка должна начинаться с новой строки.\n\n" +
                "У меня есть поддержка 3х языков: русский, сербский и английский. Для выбора языка используйте команду /set_language .\n\n" +
                "Мой функционал доступен по подписке. Узнать свой последний день подписки можно с помощью команды /get_the_last_day_of_subscription . При первом взаимодействии со мной вам дается приветственная неделя. Для продления подписки используйте команду /buy_subscription или введите промокод с помощью команды /enter_promo .\n\n" +
                "О промокодах. Их можно сгенерировать самому \ud83d\ude42 с помощью команды /generate_promo . Но сгенерированный вами прококод должен применить другой пользователь. Как только он применит промокод, и вы, и он получите по одной неделе подписки. Нельзя использовать промокод, который вы сами сгенерировали. Промокоды - одноразовые, " +
                "их нельзя повторно использовать. Нельзя применить и новый промокод от другого пользователя, если вы от этого пользователя уже применяли успешно промокод. Также если вы от другого пользователя применяли успешно промокод, то этот пользователь не может применить промокод, который сгенерируете вы.\n\n" +
                "Не стесняйтесь писать на postao.jeftiniji@gmail.com почту или использовать команду /send_feedback для обратной связи. Например, при обнаружении бага, или если желаемый сайт пока не поддерживается. Разработчик добавит поддержку вашего сайта \ud83d\ude42";
    }

    @Override
    public String getPromoCreatedMessageFormat() {
        return "%s промокод сгенерирован. Вы и ваш друг получите по одной неделе подписки, когда ваш друг применит этот промокод.";
    }

    @Override
    public String getEnterPromoMessage() {
        return "Пожалуйста, введите промокод.";
    }

    @Override
    public String getEnteredPromoIsNotValidMessage() {
        return "Введенный промокод - невалидный. Пожалуйста, попробуйте ввести промокод еще раз.";
    }

    @Override
    public String getEnteredPromoDoesNotExistMessage() {
        return "Введенного вами промокода не существует. Пожалуйста, попробуйте ввести промокод еще раз.";
    }

    @Override
    public String getYouCantUsePromoThatWasGeneratedByYouMessage() {
        return "Вы не можете применить промокод, который сами же и сгенерировали \ud83d\ude42 Пожалуйста, попробуйте ввести промокод еще раз.";
    }

    @Override
    public String getYouAlreadyUsedPromoFromThatUserMessageFormat() {
        return "Вы уже применяли промокод от пользователя %s. И не можете это сделать снова. Пожалуйста, попробуйте ввести промокод еще раз.";
    }

    @Override
    public String getYouUsePromoFromThatUserBecauseThatUserUsedThePromoGeneratedByYouMessageFormat() {
        return "Пользователь %s уже успешно использовал промокод, который был сгенерирован вами. Поэтому вы не можете использовать промокоды, который сгенерировал этот пользователь. Пожалуйста, попробуйте ввести промокод еще раз.";
    }

    @Override
    public String getPromoIsAppliedSuccessfullyMessageFormat() {
        return "Промокод успешно применен! Вы получаете одну бесплатную неделю подписки. Теперь ваш последний день подписки - %s";
    }

    @Override
    public String getYourFriendSuccessfullyAppliedYourPromoMessage() {
        return "Ваш друг успешно применил ваш промокод! Вы получаете одну бесплатную неделю подписки.";
    }

    @Override
    public String getSelectLanguageMessage() {
        return "Выберете язык.";
    }

    @Override
    public String getLanguageIsSelectedMessage() {
        return "Выбран русский язык. Он будет применен для моих ответных сообщений. Команды бота остаются на английском.";
    }

    @Override
    public String getFollowingLinksAreInvalidMessageFormat() {
        return "Ссылки, перечисленные ниже не валидны\n%s\nПожалуйста, введите ссылки на товары еще раз.";
    }

    @Override
    public String getIsNotAValidLink() {
        return " ссылка невалидна.";
    }

    @Override
    public String getLinkOfThatSiteIsNotSupportedMessage() {
        return " ссылки на этот сайт не поддерживаются. Вы можете просмотреть список поддерживаемых сайтов, используя команду /show_acceptable_sites";
    }

    @Override
    public String getTheLastDayOfSubscriptionMessageFormat() {
        return "Ваш последний день подписки - %s";
    }

    @Override
    public String getMaxCountOfLinksMessage() {
        return "Максимально разрешенное количество ссылок - 10.\nПожалуйста, введите ссылки на товары еще раз.";
    }

    @Override
    public String getOneMonthMessage() {
        return "1 месяц";
    }

    @Override
    public String getThreeMonthsMessage() {
        return "3 месяца";
    }

    @Override
    public String getSixMonthsMessage() {
        return "6 месяцев";
    }

    @Override
    public String getBuySubscriptionMessage() {
        return "Пока только PayPal роддерживается для оплаты подписки\\.\nВыберите период оплаты\\.\n1 месяц \\- €4\\.99\n3 месяца \\- ~€14\\.99~ €12\\.49\n6 месяцев \\- ~€29\\.99~ €22\\.49";
    }

    @Override
    public String getSendPaypalEmailMessage() {
        return "Пожалуйста, введите вашу почту от PayPal";
    }

    @Override
    public String getInvoiceIsGeneratedMessage(String invoiceLink) {
        return "Счет сгенерирован.\n" + invoiceLink + "\nВаша подписка будет продлена, как только будет оплачен счет.";
    }

    @Override
    public String getYouEnteredInvalidPaymentPeriodMessageFormat() {
        return "Вы ввели невалидный период оплаты. Валидные периоды : %s, %s, и %s.\n Пожалуйста, попробуйте ввести период оплаты еще раз.";
    }

    @Override
    public String getYouEnteredInvalidPaypalEmailMessage() {
        return "Вы ввели невалидный адрес электронной почты вашего PayPal аккаунта.\nПожалуйста, попробуйте ввести вашу почту от PayPal еще раз.";
    }

    @Override
    public String getYouEnteredInvalidLanguageMessageFormat() {
        return "Вы ввели невалидный язык. Валидные языки: %s, %s, %s.\nПожалуйста, попробуйте ввести язык еще раз.";
    }

    @Override
    public String getMaxCountOfPromosMessage() {
        return "Максимально разрешенное количество сгенерированных вами и неиспользованных промокодов - 10.\nИспрльзуйте команду /show_my_promos для того, чтоюы просмотреть все сгенерированные вами и неиспользованные промокоды.";
    }

    @Override
    public String getYourPromosMessageFormat() {
        return "Список сгенерированных вами и неиспользованных промокодов:\n%s";
    }

    @Override
    public String getRetrievedPriceMessage() {
        return "Считанная цена";
    }

    @Override
    public String getCheckingLinksMessage() {
        return "Проверяю ссылки...";
    }

    @Override
    public String getProvideFeedbackMessage() {
        return "Пожалуйста, введите отзыв";
    }

    @Override
    public String getThanksForFeedbackMessage() {
        return "Спасибо за ваш отзыв!";
    }
}
