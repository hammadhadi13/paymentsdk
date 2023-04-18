package com.noonpayments.paymentsdk.models;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.noonpayments.paymentsdk.R;
import com.noonpayments.paymentsdk.helpers.Helper;

public class NoonPaymentsUI {

    private Language language = Language.ENGLISH;
    private int backgroundColor = -1;            //dialog background color
    private Drawable logoImage = null;

    private String paymentOptionHeadingText = "";             //top heading for first dialog
    private int paymentOptionHeadingForeground = -1;       //text color of the top heading

    private String paymentOptionText = "";             //text for card displayed on payment mode selection page
    private int paymentOptionForeground = -1;       //foreground color
    private int paymentOptionBackground = -1;       //background color

    private int boxBackgroundColor = -1;      //background color for boxes displayed in dialog
    private int boxForegroundColor = -1;       //foreground color for boxes displayed in dialog
    private int boxBorderColor = -1;         //border color for boxes displayed in dialog

    private String addNewCardText = "";                     //use on card selection page and new card page
    private int addNewCardTextForegroundColor = -1;

    private String paynowTextPrimary = "";                  //text onf the first dialog
    private int paynowForegroundColorPrimary = -1;      //pay now box colors first dialog
    private int paynowBackgroundColorPrimary = -1;       //pay now box colors fist dialog

    private String payableAmountText = "";
    private int payableBackgroundColor = -1;          //payable amount box colors
    private int payableForegroundColor = -1;          //payable amount box colors

    private String paynowText = "";
    private int paynowForegroundColor = -1;              //pay now box colors
    private int paynowBackgroundColor = -1;              //pay now box colors
    private int paynowBackgroundColorHighlight = -1;     //pay now box color when entry is valid

    private String footerText = "";
    private int footerForegroundColor = -1;

    public NoonPaymentsUI() {
    }

    public void setupColors(Context context) {

        if (backgroundColor == -1)
            backgroundColor = Color.WHITE;

        if (boxBorderColor == -1)
            boxBorderColor = ContextCompat.getColor(context, R.color.BoxBorder);

        if (boxBackgroundColor == -1)
            boxBackgroundColor = Color.WHITE;

        if (boxForegroundColor == -1)
            boxForegroundColor = ContextCompat.getColor(context, R.color.Label);

        if (paynowBackgroundColorPrimary == -1)
            paynowBackgroundColorPrimary = ContextCompat.getColor(context, R.color.PinkBox);

        if (paynowForegroundColorPrimary == -1)
            paynowForegroundColorPrimary = ContextCompat.getColor(context, R.color.PaynowText);

        if (paymentOptionHeadingForeground == -1)
            paymentOptionHeadingForeground = ContextCompat.getColor(context, R.color.TopHeading);

        if (paymentOptionForeground == -1)
            paymentOptionForeground = ContextCompat.getColor(context, R.color.Label);

        if (paymentOptionBackground == -1)
            paymentOptionBackground = Color.WHITE;

        if (payableBackgroundColor == -1)
            payableBackgroundColor = ContextCompat.getColor(context, R.color.PayableBox);

        if (payableForegroundColor == -1)
            payableForegroundColor = ContextCompat.getColor(context, R.color.PayableText);

        if (paynowBackgroundColor == -1)
            paynowBackgroundColor = ContextCompat.getColor(context, R.color.PaynowBox);

        if (paynowForegroundColor == -1)
            paynowForegroundColor = ContextCompat.getColor(context, R.color.PaynowText);

        if (paynowBackgroundColorHighlight == -1)
            paynowBackgroundColorHighlight = ContextCompat.getColor(context, R.color.SlateBlue);

        if (addNewCardTextForegroundColor == -1)
            addNewCardTextForegroundColor = R.color.DimGray;

        if (footerForegroundColor == -1)
            footerForegroundColor = ContextCompat.getColor(context, R.color.TopHeading);
    }


    public Language getLanguage() {
        return language;
    }

    public void setLanguage(Language language) {
        this.language = language;
    }

    public int getBackgroundColor() {
        return backgroundColor;
    }

    public void setBackgroundColor(int backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    public Drawable getLogoImage() {
        return logoImage;
    }

    public void setLogoImage(Drawable logoImage) {
        this.logoImage = logoImage;
    }

    public String getPaymentOptionHeadingText() {
        return paymentOptionHeadingText;
    }

    public void setPaymentOptionHeadingText(String paymentOptionHeadingText) {
        this.paymentOptionHeadingText = paymentOptionHeadingText;
    }

    public int getPaymentOptionHeadingForeground() {
        return paymentOptionHeadingForeground;
    }

    public void setPaymentOptionHeadingForeground(int paymentOptionHeadingForeground) {
        this.paymentOptionHeadingForeground = paymentOptionHeadingForeground;
    }

    public String getPaymentOptionText() {
        return paymentOptionText;
    }

    public void setPaymentOptionText(String paymentOptionText) {
        this.paymentOptionText = paymentOptionText;
    }

    public int getPaymentOptionForeground() {
        return paymentOptionForeground;
    }

    public void setPaymentOptionForeground(int paymentOptionForeground) {
        this.paymentOptionForeground = paymentOptionForeground;
    }

    public int getPaymentOptionBackground() {
        return paymentOptionBackground;
    }

    public void setPaymentOptionBackground(int paymentOptionBackground) {
        this.paymentOptionBackground = paymentOptionBackground;
    }

    public int getBoxBackgroundColor() {
        return boxBackgroundColor;
    }

    public void setBoxBackgroundColor(int boxBackgroundColor) {
        this.boxBackgroundColor = boxBackgroundColor;
    }

    public int getBoxForegroundColor() {
        return boxForegroundColor;
    }

    public void setBoxForegroundColor(int boxForegroundColor) {
        this.boxForegroundColor = boxForegroundColor;
    }

    public int getBoxBorderColor() {
        return boxBorderColor;
    }

    public void setBoxBorderColor(int boxBorderColor) {
        this.boxBorderColor = boxBorderColor;
    }

    public String getAddNewCardText() {
        return addNewCardText;
    }

    public void setAddNewCardText(String addNewCardText) {
        this.addNewCardText = addNewCardText;
    }

    public int getAddNewCardTextForegroundColor() {
        return addNewCardTextForegroundColor;
    }

    public void setAddNewCardTextForegroundColor(int addNewCardTextForegroundColor) {
        this.addNewCardTextForegroundColor = addNewCardTextForegroundColor;
    }

    public String getPaynowTextPrimary() {
        return paynowTextPrimary;
    }

    public void setPaynowTextPrimary(String paynowTextPrimary) {
        this.paynowTextPrimary = paynowTextPrimary;
    }

    public int getPaynowForegroundColorPrimary() {
        return paynowForegroundColorPrimary;
    }

    public void setPaynowForegroundColorPrimary(int paynowForegroundColorPrimary) {
        this.paynowForegroundColorPrimary = paynowForegroundColorPrimary;
    }

    public int getPaynowBackgroundColorPrimary() {
        return paynowBackgroundColorPrimary;
    }

    public void setPaynowBackgroundColorPrimary(int paynowBackgroundColorPrimary) {
        this.paynowBackgroundColorPrimary = paynowBackgroundColorPrimary;
    }

    public String getPayableAmountText() {
        return payableAmountText;
    }

    public void setPayableAmountText(String payableAmountText) {
        this.payableAmountText = payableAmountText;
    }

    public int getPayableBackgroundColor() {
        return payableBackgroundColor;
    }

    public void setPayableBackgroundColor(int payableBackgroundColor) {
        this.payableBackgroundColor = payableBackgroundColor;
    }

    public int getPayableForegroundColor() {
        return payableForegroundColor;
    }

    public void setPayableForegroundColor(int payableForegroundColor) {
        this.payableForegroundColor = payableForegroundColor;
    }

    public String getPaynowText() {
        return paynowText;
    }

    public void setPaynowText(String paynowText) {
        this.paynowText = paynowText;
    }

    public int getPaynowForegroundColor() {
        return paynowForegroundColor;
    }

    public void setPaynowForegroundColor(int paynowForegroundColor) {
        this.paynowForegroundColor = paynowForegroundColor;
    }

    public int getPaynowBackgroundColor() {
        return paynowBackgroundColor;
    }

    public void setPaynowBackgroundColor(int paynowBackgroundColor) {
        this.paynowBackgroundColor = paynowBackgroundColor;
    }

    public int getPaynowBackgroundColorHighlight() {
        return paynowBackgroundColorHighlight;
    }

    public void setPaynowBackgroundColorHighlight(int paynowBackgroundColorHighlight) {
        this.paynowBackgroundColorHighlight = paynowBackgroundColorHighlight;
    }


    public String getFooterText() {
        return footerText;
    }

    public void setFooterText(String footerText) {
        this.footerText = footerText;
    }

    public int getFooterForegroundColor() {
        return footerForegroundColor;
    }

    public void setFooterForegroundColor(int footerForegroundColor) {
        this.footerForegroundColor = footerForegroundColor;
    }

    public void setupDialog(Context context, RelativeLayout dialog) {
        GradientDrawable gd = new GradientDrawable();
        gd.setColor(this.backgroundColor);
        int rad = Helper.GetPixelsFromDP(context, 10);
        gd.setCornerRadii(new float[]{rad, rad, rad, rad, 0, 0, 0, 0});
        gd.setStroke(2, Color.BLACK);
        dialog.setBackground(gd);
        dialog.setPadding(0, 0, 0, Helper.GetPixelsFromDP(context, 0));
        dialog.setOutlineProvider(null);
    }

    public void setupBox(Context context, RelativeLayout box, int pageNo) {
        int bgColour = this.backgroundColor;
        int borderColor = this.boxBorderColor;
        GradientDrawable gd = new GradientDrawable();
        gd.setColor(bgColour);
        int rad = Helper.GetPixelsFromDP(context, 8);
        gd.setCornerRadius(rad);
        gd.setStroke(2, borderColor);
        box.setBackground(gd);
        int paddingTB = 0;
        int paddingLR = 0;
        if (pageNo == 2) {
            paddingTB = Helper.GetPixelsFromDP(context, 10);
            paddingLR = Helper.GetPixelsFromDP(context, 20);
            box.setPadding(paddingLR, paddingTB, paddingLR, paddingTB);
        } else {
            paddingTB = Helper.GetPixelsFromDP(context, 6);
            paddingLR = Helper.GetPixelsFromDP(context, 12);
            box.setPadding(paddingLR, paddingTB, paddingLR, 0);
        }

    }

    public void setupPaynow(Context context, RelativeLayout paymentBox, RelativeLayout paynowBox, TextView txtPay, TextView txtAmount, TextView txtPayNow) {
        paymentBox.setBackgroundColor(this.payableBackgroundColor);
        paynowBox.setBackgroundColor(this.paynowBackgroundColor);

        txtPay.setTextColor(this.payableForegroundColor);
        txtAmount.setTextColor(this.payableForegroundColor);
        txtPayNow.setTextColor(this.paynowForegroundColor);
    }

    public void setupLogo(ImageView viewLogo) {
        if (this.logoImage != null)
            viewLogo.setImageDrawable(this.logoImage);
    }
}

