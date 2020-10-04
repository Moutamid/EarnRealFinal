package dev.moutamid.earnreal;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class FragmentDailyAds extends Fragment {
    private static final String TAG = "FragmentDailyAds";

    private static final String PAID_STATUS = "paidStatus";
    private static final String CURRENT_BALANCE = "currentBalance";
    private static final String DAILY_ADS_QUANTITY = "daily_ads_quantity";

    private ProgressDialog dialog;

    private TextView adsQuantityTxt;
    private Button verifyBtn, showAdBtn;
    private Utils utils = new Utils();
    Runnable runnable = new Runnable() {
        @Override
        public void run() {

            dialog.dismiss();

            utils.showWorkDoneDialog(getActivity(), "Successful", "Your advertisement has been shown!");

            // ON COMPLETING ADVERTISEMENT SHOWING VERIFY BUTTON AND REMOVING SHOW AD BUTTON
            verifyBtn.setVisibility(View.VISIBLE);
            showAdBtn.setVisibility(View.GONE);

        }
    };
    private Handler handler;
    private int quantity;
    private DatabaseReference databaseReference;
    private FirebaseAuth mAuth;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_daily_ads_layout,container,false);

        databaseReference = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();

        showAdBtn = view.findViewById(R.id.show_ad_btn_fragment_daily_ads);
        if (utils.getStoredBoolean(getActivity(), PAID_STATUS)) {

            TextView textView = view.findViewById(R.id.not_paid_textView_fragment_daily_ads);
            textView.setVisibility(View.GONE);

            adsQuantityTxt = view.findViewById(R.id.daily_ads_textView_fragment_daily_ads);
            verifyBtn = view.findViewById(R.id.verify_btn_fragment_daily_ads);

            quantity = utils.getStoredInteger(getActivity(), DAILY_ADS_QUANTITY);

            adsQuantityTxt.setText(String.valueOf(quantity));

            setShowBtnClickListener();

            setVerifyBtnClickListener();

        }else {

            showAdBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(getActivity(), "Advertisement has been shown!", Toast.LENGTH_SHORT).show();
                }
            });

        }

        return view;

    }

    private void setVerifyBtnClickListener() {

        verifyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // IF PREMIUM ADS ARE GREATER OR EQUAL THAN 1
                if (quantity >= 1) {
                    dialog.setMessage("Verifying...");
                    dialog.show();

                    databaseReference
                            .child("users")
                            .child(mAuth.getCurrentUser().getUid())
                            .child("details")
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    // UPDATING TOTAL BALANCE FIELD BY ADDING Rs: 0.20
                                    float totalBalance = Float.parseFloat(snapshot.child("totalBlnc").getValue(String.class));
                                    databaseReference
                                            .child("users")
                                            .child(mAuth.getCurrentUser().getUid())
                                            .child("details")
                                            .child("totalBlnc").setValue(Float.toString(totalBalance + 0.20f));

                                    // UPDATING CURRENT BALANCE FIELD BY ADDING Rs: 0.20
                                    float currentBalance = Float.parseFloat(snapshot.child("cvBlnce").getValue(String.class));
                                    databaseReference
                                            .child("users")
                                            .child(mAuth.getCurrentUser().getUid())
                                            .child("details")
                                            .child("cvBlnce").setValue(Float.toString(currentBalance + 0.20f));

                                    // ADDING Rs: 0.20 IN CURRENT BALANCE IN PREFERENCES
                                    utils.storeFloat(getActivity(), CURRENT_BALANCE, utils.getStoredFloat(getActivity(), CURRENT_BALANCE) + 0.20f);

                                    // REMOVING 1 IN THE PREMIUM ADS QUANTITY
                                    utils.storeInteger(getActivity(), DAILY_ADS_QUANTITY, utils.getStoredInteger(getActivity(), DAILY_ADS_QUANTITY) - 1);
                                    adsQuantityTxt.setText(String.valueOf(quantity - 1));

                                    verifyBtn.setVisibility(View.GONE);
                                    showAdBtn.setVisibility(View.VISIBLE);

                                    dialog.dismiss();

                                    Toast.makeText(getActivity(), "Your ad is verified!", Toast.LENGTH_LONG).show();

                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                    dialog.dismiss();

                                    Log.i(TAG, "onCancelled: " + error.toException());
                                    Toast.makeText(getActivity(), error.toException().getMessage(), Toast.LENGTH_SHORT).show();

                                }
                            });

                } else
                    utils.showOfflineDialog(getActivity(), "Sorry!", "Your request is not verified because premium ads are 0. In order to get more ads please invite people and after they upgrade you'll get 15 ads and every ad will give you Rs: 5");
            }
        });

    }

    private void setShowBtnClickListener() {

        showAdBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                dialog = new ProgressDialog(getActivity());
                dialog.setMessage("Loading ad...");
                dialog.show();

                handler = new Handler();
                handler.postDelayed(runnable, 5000);

            }
        });

    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        handler.removeCallbacks(runnable);
    }

}
