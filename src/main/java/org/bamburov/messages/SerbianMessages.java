package org.bamburov.messages;

public class SerbianMessages implements Messages {
    @Override
    public String getFirstStartMessageFormat() {
        return "Zdravo, %s! Obavestiću vas kada će proizvodi koji vas zanimaju pojeftiniti. Koristite komandu /help da biste dobili više detalja.";
    }

    @Override
    public String getSetLinksMessage() {
        return "Unesite veze proizvoda za koje želite da budu jeftiniji.\nSvaka veza treba da počinje novom linijom.\nMožete pogledati listu podržanih sajtova pomoću komande /show_acceptable_sites";
    }

    @Override
    public String getLinksAcceptedMessageFormat() {
        return "Super! veze u nastavku su prihvaćene\n%s\nObavestiću vas kada neki od ovih proizvoda pojeftini.";
    }

    @Override
    public String getHelpMessage() {
        return "Obavestiću vas kada cena proizvoda koji vas zanima u onlajn prodavnici pojeftini. Komandom /set_links unesite linkove proizvoda koji vas interesuju. Koristite komandu /show_acceptable_sites da biste videli listu podržanih prodavnica na mreži. Maksimalan dozvoljeni broj linkova je 10. Svaka veza treba da počinje u novom redu.\n\n" +
                "Imam podršku za 3 jezika: srpski, engleski i ruski. Koristite komandu /set_language da izaberete jezik.\n\n" +
                "Moja funkcionalnost je dostupna uz pretplatu. Možete saznati svoj poslednji dan pretplate pomoću komande /get_the_last_day_of_subscription. Prvi put kada stupite u interakciju sa mnom, dočekali ste nedelju dana dobrodošlice. Da biste obnovili pretplatu, koristite komandu /buy_subscription ili unesite promo kod pomoću komande /enter_promo.\n\n" +
                "O promo kodovima. Možete ih sami generisati \ud83d\ude42 pomoću komande /generate_promo. Ali promo kod koji ste generisali mora da koristi drugi korisnik. Čim primeni promo kod, i vi i on dobićete jednu nedelju pretplate. Ne možete koristiti promo kod koji ste sami generisali. Promo kodovi su jednokratni i ne mogu se ponovo koristiti." +
                "Ne možete da primenite novi promo kod drugog korisnika ako ste već uspešno primenili promo kod ovog korisnika. Takođe, ako ste uspešno primenili promo kod drugog korisnika, onda ovaj korisnik ne može primeniti promo kod koji vi generišete.\n\n" +
                "Slobodno pošaljite e-poštu postao.jeftiniji@gmail.com ili koristite /send_feedback za davanje povratnih informacija. Na primer, ako ste pronašli grešku ili ako vaš željeni sajt još uvek nije podržan. Programer će dodati podršku za vašu veb lokaciju \ud83d\ude42";
    }

    @Override
    public String getPromoCreatedMessageFormat() {
        return "%s promo kod je generisan. Dobićete 1 nedelju besplatno kada je vaš prijatelj koristi.";
    }

    @Override
    public String getEnterPromoMessage() {
        return "Molimo unesite promo kod.";
    }

    @Override
    public String getEnteredPromoIsNotValidMessage() {
        return "Uneti promo kod nije važeći.. Pokušajte ponovo da unesete promo kod.";
    }

    @Override
    public String getEnteredPromoDoesNotExistMessage() {
        return "Uneti promo kod ne postoji. Pokušajte ponovo da unesete promo kod.";
    }

    @Override
    public String getYouCantUsePromoThatWasGeneratedByYouMessage() {
        return "Ne možete da koristite promo kod koji ste sami generisali \ud83d\ude42 Pokušajte ponovo da unesete promo kod.";
    }

    @Override
    public String getYouAlreadyUsedPromoFromThatUserMessageFormat() {
        return "Već ste koristili promo kod od %s korisnika. I ne možeš ponovo. Pokušajte ponovo da unesete promo kod.";
    }

    @Override
    public String getYouUsePromoFromThatUserBecauseThatUserUsedThePromoGeneratedByYouMessageFormat() {
        return "Korisnik %s je već primenio promo kod koji ste vi generisali. Zbog toga ne možete da primenite promo kodove od tog korisnika. Pokušajte ponovo da unesete promo kod.";
    }

    @Override
    public String getPromoIsAppliedSuccessfullyMessage() {
        return "Promo kod je uspešno primenjen. Dobili ste još 1 nedelju besplatno! Sada je vaš poslednji dan pretplate %s";
    }

    @Override
    public String getYourFriendSuccessfullyAppliedYourPromoMessageFormat() {
        return "Vaš prijatelj je uspešno primenio vaš promo kod. Dobili ste još 1 nedelju besplatno!";
    }

    @Override
    public String getSelectLanguageMessage() {
        return "Izaberite jezik.";
    }

    @Override
    public String getLanguageIsSelectedMessage() {
        return "Srpski jezik je izabran. Biće primenjeno na moje odgovore. Bot komande ostaju na engleskom.";
    }

    @Override
    public String getFollowingLinksAreInvalidMessageFormat() {
        return "Sledeći veze su nevažeći\n%s\nPonovo unesite veze.";
    }

    @Override
    public String getIsNotAValidLink() {
        return " nije važeća veze.";
    }

    @Override
    public String getLinkOfThatSiteIsNotSupportedMessage() {
        return " veza tog sajta nije podržana. Možete pogledati listu podržanih sajtova pomoću komande /show_acceptable_sites";
    }

    @Override
    public String getTheLastDayOfSubscriptionMessageFormat() {
        return "Poslednji dan pretplate je %s";
    }

    @Override
    public String getMaxCountOfLinksMessage() {
        return "Maksimalni dozvoljeni broj veza je 10.\nPonovo unesite veze.";
    }

    @Override
    public String getOneMonthMessage() {
        return "1 mesec";
    }

    @Override
    public String getThreeMonthsMessage() {
        return "3 meseca";
    }

    @Override
    public String getSixMonthsMessage() {
        return "6 meseci";
    }

    @Override
    public String getBuySubscriptionMessage() {
        return "Za sada je dostupno samo PayPal plaćanje\\.\nMolimo izaberite period plaćanja\\.\n1 mesec \\- €3\\.99\n3 meseca \\- ~€11\\.99~ €9\\.99\n6 meseci \\- ~€23\\.99~ €17\\.99";
    }

    @Override
    public String getSendPaypalEmailMessage() {
        return "Unesite svoju PayPal adresu e-pošte.";
    }

    @Override
    public String getInvoiceIsGeneratedMessage(String invoiceLink) {
        return "Račun se generiše.\n" + invoiceLink + "\nVaša pretplata će biti produžena čim se račun plati.";
    }

    @Override
    public String getYouEnteredInvalidPaymentPeriodMessageFormat() {
        return "Uneti periodi plaćanja su nevažeći. Važeći rokovi plaćanja su %s, %s, i %s.\nPokušajte ponovo da unesete period plaćanja.";
    }

    @Override
    public String getYouEnteredInvalidPaypalEmailMessage() {
        return "Uneta PayPal adresa e-pošte nije važeća.\nPokušajte ponovo da unesete PayPal adresu e-pošte.";
    }

    @Override
    public String getYouEnteredInvalidLanguageMessageFormat() {
        return "Uneti jezik je nevažeći.Važeći jezici su %s, %s, %s.\nPokušajte ponovo da unesete jezik.";
    }

    @Override
    public String getMaxCountOfPromosMessage() {
        return "Maksimalan dozvoljeni broj neiskorišćenih promocija koje ste generisali je 10.\nKoristite komandu /show_my_promos da vidite punu listu neiskorišćenih promocija koje ste generisali.";
    }

    @Override
    public String getYourPromosMessageFormat() {
        return "Lista neiskorišćenih promocija koje ste generisali.\n%s";
    }

    @Override
    public String getRetrievedPriceMessage() {
        return "Preuzeta cena";
    }

    @Override
    public String getCheckingLinksMessage() {
        return "Proveravam veze...";
    }

    @Override
    public String getProvideFeedbackMessage() {
        return "Molimo, dajte povratne informacije";
    }

    @Override
    public String getThanksForFeedbackMessage() {
        return "Hvala na povratnim informacijama!";
    }
}
