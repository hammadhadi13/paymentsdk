package com.noonpayments.paymentsdk.models;

import java.util.ArrayList;
import java.util.Arrays;

public class NoonPaymentsSetup {

    private static NoonPaymentsSetup setupInstance = null;
    private static NoonPaymentsUI noonPaymentsUI;
    private static NoonPaymentsData noonPaymentsData;
    private static ArrayList<NoonPaymentsCard> noonPaymentsCards;
    private static ArrayList<String> madaCardList;
    private static ArrayList<String> meezaCardList;
    private static ArrayList<String> omannetCardList;
    private static ArrayList<String> maestroCardList;


    private NoonPaymentsSetup() {
    }

    public static NoonPaymentsSetup getInstance() {

        if (setupInstance == null) {
            setupInstance = new NoonPaymentsSetup();
            noonPaymentsCards = new ArrayList<>();
            madaCardList = new ArrayList<>();
            meezaCardList = new ArrayList<>();
            omannetCardList = new ArrayList<>();
            maestroCardList = new ArrayList<>();
            initializeMadaCardList();
        }
        return setupInstance;
    }

    public void Setup(NoonPaymentsData noonPaymentsData, NoonPaymentsUI noonPaymentsUI, ArrayList<NoonPaymentsCard> noonPaymentsCards) {
        NoonPaymentsSetup.noonPaymentsData = noonPaymentsData;
        NoonPaymentsSetup.noonPaymentsUI = noonPaymentsUI;
        NoonPaymentsSetup.noonPaymentsCards = noonPaymentsCards;
    }

    public NoonPaymentsUI getNoonUI() {
        return noonPaymentsUI;
    }

    public NoonPaymentsData getNoonData() {
        return noonPaymentsData;
    }

    public ArrayList<NoonPaymentsCard> getNoonSavedCards() {
        return noonPaymentsCards;
    }

    public ArrayList<String> getMadaCardList() {
        return madaCardList;
    }

    public ArrayList<String> getMeezaCardList() {
        return meezaCardList;
    }

    public ArrayList<String> getOmannetCardList() {
        return omannetCardList;
    }

    public ArrayList<String> getMaestroCardList(){
        return maestroCardList;
    }

    public void setMadaCardList(ArrayList<String> cardList) {
        this.madaCardList = cardList;
    }

    private static void initializeMadaCardList() {
        String[] cards = new String[]{
                "588845", "440647", "440795", "446404", "457865", "968208", "457997", "474491", "636120", "417633",
                "468540", "468541", "468542", "468543", "968201", "446393", "409201", "458456", "484783", "968205",
                "462220", "455708", "410621", "588848", "455036", "968203", "486094", "486095", "486096", "504300",
                "440533", "489318", "489319", "445564", "968211", "410685", "406996", "432328", "428671", "428672",
                "428673", "968206", "446672", "543357", "434107", "407197", "407395", "412565", "431361", "604906",
                "521076", "588850", "968202", "529415", "535825", "543085", "524130", "554180", "549760", "968209",
                "524514", "529741", "537767", "535989", "536023", "513213", "520058", "558563", "585265", "588983",
                "588982", "589005", "508160", "531095", "530906", "532013", "605141", "968204", "422817", "422818",
                "422819", "428331", "483010", "483011", "483012", "589206", "968207", "419593", "439954", "530060",
                "531196", "420132"
        };

        madaCardList.addAll(Arrays.asList(cards));

        String[] meezaCards = new String[]{
                "507803", "507809", "507810", "507811"
        };

        meezaCardList.addAll(Arrays.asList(meezaCards));


        String[] omannetCards = new String[]{
                "419244", "426371", "426372", "433829", "464426", "489091", "484172", "433236", "490907",
                "428257", "439357", "402004", "437569", "483791", "422821", "422822", "422820", "447168",
                "407545", "435949", "464156", "464157", "456595", "437425", "437426", "437427", "403803",
                "484130", "484131", "528647", "559406", "533117", "524278", "559071", "559407", "542160",
                "400282", "588855", "432410", "432415", "433084", "422610", "510723", "515722", "523672",
                "534417", "539150", "549184", "528669", "535981", "419291", "467362", "413298", "410469",
                "536028", "559753", "473820", "426681", "422823", "464175", "422681", "601722"
        };
        omannetCardList.addAll(Arrays.asList(omannetCards));


        String[] maestroCards = new String[]{
                "6304", "6759", "6761", "6762", "6763", "6771"
        };
        maestroCardList.addAll(Arrays.asList(maestroCards));

    }

}