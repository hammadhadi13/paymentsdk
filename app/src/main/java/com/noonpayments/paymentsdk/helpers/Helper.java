package com.noonpayments.paymentsdk.helpers;

import android.content.Context;

import com.noonpayments.paymentsdk.models.Language;
import com.noonpayments.paymentsdk.models.NoonPaymentsSetup;

import java.util.Collections;

public class Helper {

    public static String STATUS_SUCCESS = "success";
    public static String STATUS_FAILURE = "failure";
    public static String STATUS_CANCELLED = "cancelled";

    public static final String CARD_VISA = "VISA";
    public static final String CARD_MC = "MC";
    public static final String CARD_AMEX = "AMEX";
    public static final String CARD_MADA = "MADA";
    public static final String CARD_GENERIC = "GENERIC";

    public static final String CARD_MEEZA = "MEEZA";
    public static final String CARD_MAESTRO = "MAESTRO";
    public static final String CARD_UNIONPAY = "UNIONPAY";
    public static final String CARD_DISCOVER = "DISCOVER";
    public static final String CARD_JCB = "JCB";
    public static final String CARD_DINERS = "DINERS";
    public static final String CARD_OMANNET = "OMANNET";


    public static int GetPixelsFromDP(Context context, int dp) {
        final float scale = context.getResources().getDisplayMetrics().density;
        int px = (int) (dp * scale + 0.5f);

        return px;
    }

    public static String getLanguageTextByUser(Context context, String key, Language language, String userValue) {
        String message = "";

        if (userValue.isEmpty() == false)
            return userValue;

        if (language == Language.ARABIC)
            key = "arb_" + key;
        else
            key = "eng_" + key;
        try {
            int resId = context.getResources().getIdentifier(key, "string", context.getPackageName());
            message = context.getResources().getString(resId);
        } catch (Exception e) {
            message = "";
        }
        return message;
    }

    public static String getLanguageText(Context context, String key, Language language) {
        String message = "";

        if (language == Language.ARABIC)
            key = "arb_" + key;
        else
            key = "eng_" + key;
        try {
            int resId = context.getResources().getIdentifier(key, "string", context.getPackageName());
            message = context.getResources().getString(resId);
        } catch (Exception e) {
            message = "";
        }
        return message;
    }

    public static String getCardType(String s, String currency) {
        String cardType = Helper.CARD_GENERIC;
        s = s.replace(" ", "");
        int len = s.length();
        String jcbRegex = "^(3(?:088|096|112|158|337|5(?:2[89]|[3-8]\\d))\\d{12})$"; //3569 3101 6794 8629
        String dicoverRegex = "^65[4-9]\\d{13}|64[4-9]\\d{13}|6011\\d{12}|(622(?:12[6-9]|1[3-9]\\d|[2-8]\\d\\d|9[01]\\d|92[0-5])\\d{10})$";
        //6555 9000 0047 1950  dicovers
        String dinerRegex = "^3(?:0[0-5]|[68]\\d)\\d{11}"; // 3056 9309 0259 04 / 3852 0000 0232 37
        String maestroRegex = "^(?:50|5[6-9]|6[0-9])\\d+$";
        //6759 0806 7156 5242  / 5610 5910 8101 8250

        if (len > 0) {
            //check if mada card
            boolean isMada = false;
            boolean isMeeza = false;
            boolean isOmannet = false;
            boolean isjscb = false;
            boolean isDiscover = false;
            boolean isDiner = false;
            boolean isMaestro = false;

//            if (len >= 6 && currency.equals("SAD")) {
//                String m = s.substring(0, 6);
//                for (String c : NoonPaymentsSetup.getInstance().getMadaCardList()) {
//                    if (m.equals(c)) {
//                        isMada = true;
//                        break;
//                    }
//                }
//            }
            // check meeza card;
//            else
            if (len >= 6) {
                String m = s.substring(0, 6);
                for (String c : NoonPaymentsSetup.getInstance().getMadaCardList()) {
                    if (m.equals(c)) {
                        isMada = true;
                        break;
                    }
                }
                if (!isMada) {
                    for (String c : NoonPaymentsSetup.getInstance().getMeezaCardList()) {
                        if (m.equals(c)) {
                            isMeeza = true;
                            break;
                        }
                    }
                }
                // check omannet card;
                if (!isMeeza) {
                    for (String c : NoonPaymentsSetup.getInstance().getOmannetCardList()) {
                        if (m.equals(c)) {
                            isOmannet = true;
                            break;
                        }
                    }
                }
            }
            // check jcb card validation
            for (String p : Collections.singletonList(jcbRegex)) {
                if (s.matches(p)) {
                    isjscb = true;
                    break;
                }
            }

            // check discover card  validation;
            if (!isjscb) {
                for (String p : Collections.singletonList(dicoverRegex)) {
                    if (s.matches(p)) {
                        isDiscover = true;
                        break;
                    }
                }
            }
            //check diner Card validation;;
            if (!isjscb && !isDiscover) {
                for (String p : Collections.singletonList(dinerRegex)) {
                    if (s.matches(p)) {
                        isDiner = true;
                        break;
                    }
                }
            }

            if (!isjscb && !isDiscover && !isDiner) {
                if (len == 2) {
                    int ss = Integer.parseInt(s);
                    if (ss == 50 || ss >= 56 && ss <= 59) {
                        isMaestro = true;
                    }

                } else if (len == 4) {
                    for (String p : Collections.singletonList(maestroRegex)) {
                        if (s.matches(p)) {
                            isMaestro = true;
                            break;
                        }
                    }
                }else {
                    for (String p : Collections.singletonList(maestroRegex)) {
                        if (s.matches(p)) {
                            isMaestro = true;
                            break;
                        }
                    }
                }
            }


            if (isMada) {
                cardType = Helper.CARD_MADA;
            } else if (isMeeza && len <= 16) {
                cardType = Helper.CARD_MEEZA;
            } else if (isOmannet && len <= 16) {
                cardType = Helper.CARD_OMANNET;
            } else if (isjscb && len <= 16) {
                cardType = Helper.CARD_JCB;
            } else if (isDiscover && len <= 16) {
                cardType = Helper.CARD_DISCOVER;
            } else if (isDiner && len <= 14) {
                cardType = Helper.CARD_DINERS;
            } else if (isMaestro && len <= 16) {
                cardType = Helper.CARD_MAESTRO;
            } else {
                String c = s.substring(0, 1);
                String c2 = (len > 1) ? s.substring(0, 2) : "";
                if (s.substring(0, 1).equals("4"))
                    cardType = Helper.CARD_VISA;
                else if (len <= 16 && (c2.equals("51") || c2.equals("52") || c2.equals("53") || c2.equals("54") || c2.equals("55")
                        || c2.equals("22") || c2.equals("27")))
                    cardType = Helper.CARD_MC;
                else if (len <= 15 && (c2.equals("37") || c2.equals("34")))
                    cardType = Helper.CARD_AMEX;
                else if (len <= 16 && (c2.equals("62") || c2.equals("88")))
                    cardType = Helper.CARD_UNIONPAY;
                else
                    cardType = Helper.CARD_GENERIC;
            }
        }

        return cardType;
    }


    public static boolean isEmpty(String value) {
        boolean isEmpty = false;
        if (value == null || value.isEmpty())
            isEmpty = true;
        return isEmpty;
    }

}
