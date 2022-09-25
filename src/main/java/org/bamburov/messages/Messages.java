package org.bamburov.messages;

public interface Messages {
    String getFirstStartMessageFormat();
    String getSetLinksMessage();
    String getLinksAcceptedMessageFormat();
    String getHelpMessage();
    String getPromoCreatedMessageFormat();
    String getEnterPromoMessage();
    String getEnteredPromoIsNotValidMessage();
    String getEnteredPromoDoesNotExistMessage();
    String getYouCantUsePromoThatWasGeneratedByYouMessage();
    String getYouAlreadyUsedPromoFromThatUserMessageFormat();
    String getYouUsePromoFromThatUserBecauseThatUserUsedThePromoGeneratedByYouMessageFormat();
    String getPromoIsAppliedSuccessfullyMessage();
    String getYourFriendSuccessfullyAppliedYourPromoMessageFormat();
    String getSelectLanguageMessage();
    String getLanguageIsSelectedMessage();
    String getFollowingLinksAreInvalidMessageFormat();
    String getIsNotAValidLink();
    String getLinkOfThatSiteIsNotSupportedMessage();
    String getTheLastDayOfSubscriptionMessageFormat();
    String getMaxCountOfLinksMessage();
    String getOneMonthMessage();
    String getThreeMonthsMessage();
    String getSixMonthsMessage();
    String getBuySubscriptionMessage();
    String getSendPaypalEmailMessage();
    String getInvoiceIsGeneratedMessage(String invoiceLink);
    String getYouEnteredInvalidPaymentPeriodMessageFormat();
    String getYouEnteredInvalidPaypalEmailMessage();
    String getYouEnteredInvalidLanguageMessageFormat();
    String getMaxCountOfPromosMessage();
    String getYourPromosMessageFormat();
    String getRetrievedPriceMessage();
    String getCheckingLinksMessage();
    String getProvideFeedbackMessage();
    String getThanksForFeedbackMessage();
}
