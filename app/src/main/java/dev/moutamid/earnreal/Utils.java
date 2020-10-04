package dev.moutamid.earnreal;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import com.google.android.material.snackbar.Snackbar;
import com.krishna.securetimer.SecureTimer;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;

public class Utils {

    private static final String USER_EMAIL = "userEmail";
    private static final String USER_ID = "userReferralCode";
    private static final String USER_GENDER = "userGender";
    private static final String USER_NUMBER = "userNumber";
    private static final String REFERRED_BY = "referredBy";
    private static final String PACKAGE_NAME = "dev.moutamid.earnreal";
    private static final String PAID_STATUS = "paidStatus";

    private SharedPreferences sharedPreferences;

    public String getStoredString(Context context, String name) {
        sharedPreferences = context.getSharedPreferences(PACKAGE_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(name, "Error");
    }

    public void storeString(Context context, String name, String object) {
        sharedPreferences = context.getSharedPreferences(PACKAGE_NAME, Context.MODE_PRIVATE);
        sharedPreferences.edit().putString(name, object).apply();
    }

    public void storeBoolean(Context context1, String name, boolean value) {
        sharedPreferences = context1.getSharedPreferences(PACKAGE_NAME, Context.MODE_PRIVATE);
        sharedPreferences.edit().putBoolean(name, value).apply();
    }

    public boolean getStoredBoolean(Context context1, String name) {
        sharedPreferences = context1.getSharedPreferences(PACKAGE_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean(name, false);
    }

    public void storeInteger(Context context1, String name, int value) {
        sharedPreferences = context1.getSharedPreferences(PACKAGE_NAME, Context.MODE_PRIVATE);
        sharedPreferences.edit().putInt(name, value).apply();
    }

    public int getStoredInteger(Context context1, String name) {
        sharedPreferences = context1.getSharedPreferences(PACKAGE_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getInt(name, 0);
    }

    public void storeFloat(Context context1, String name, float value) {
        sharedPreferences = context1.getSharedPreferences(PACKAGE_NAME, Context.MODE_PRIVATE);
        sharedPreferences.edit().putFloat(name, value).apply();
    }

    public float getStoredFloat(Context context1, String name) {
        sharedPreferences = context1.getSharedPreferences(PACKAGE_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getFloat(name, 0);
    }

    public String getRandomNmbr(int length) {
        return String.valueOf(new Random().nextInt(length) + 1);
    }

    public void showOfflineDialog(Context context, String title, String desc) {

        Button okayBtn;

        final Dialog dialogOffline = new Dialog(context);
        dialogOffline.setContentView(R.layout.dialog_offline);

        okayBtn = dialogOffline.findViewById(R.id.okay_btn_offline_dialog);
        TextView titleTv = dialogOffline.findViewById(R.id.title_offline_dialog);
        TextView descTv = dialogOffline.findViewById(R.id.desc_offline_dialog);

        if (!TextUtils.isEmpty(title))
            titleTv.setText(title);

        if (!TextUtils.isEmpty(desc))
            descTv.setText(desc);

        okayBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogOffline.dismiss();
            }
        });

        dialogOffline.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogOffline.show();

    }

    public void showWorkDoneDialog(Context context, String title, String desc) {

        final Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.dialog_work_done);

        Button okayBtn = dialog.findViewById(R.id.okay_btn_work_done_dialog);
        TextView titleTv = dialog.findViewById(R.id.title_work_done_dialog);
        TextView descTv = dialog.findViewById(R.id.desc_work_done_dialog);

        if (!TextUtils.isEmpty(title))
            titleTv.setText(title);

        if (!TextUtils.isEmpty(desc))
            descTv.setText(desc);

        okayBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();

    }

    public String getDate(Context context) {

        try {

            Date date = SecureTimer.with(context).getCurrentDate();
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
            return sdf.format(date);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return "Error";

    }

    public String getNextDate(Context context){

        try {
            Date date = SecureTimer.with(context).getCurrentDate();
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");

            Calendar c = Calendar.getInstance();

            c.setTime(sdf.parse(sdf.format(date)));
            c.add(Calendar.DATE, 1);
            return sdf.format(c.getTime());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "Error";
    }

    public void showSnackBar(View view, String msg) {
        Snackbar.make(view, msg, Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
    }

    public void showDialog(Context context, String title, String message, String positiveBtnName, String negativeBtnName, DialogInterface.OnClickListener positiveListener, DialogInterface.OnClickListener negativeListener) {
        new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(positiveBtnName, positiveListener)
                .setNegativeButton(negativeBtnName, negativeListener)
                .show();
    }
}
