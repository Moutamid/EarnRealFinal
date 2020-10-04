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

public class FragmentPremiumAds extends Fragment {
    private static final String TAG = "FragmentPremiumAds";

    private static final String FIRST_TIME_PREMIUM_ADS_QUANTITY = "first_time_premium_ads_quantity";

    private static final String CURRENT_BALANCE = "currentBalance";
    private static final String PREMIUM_ADS_QUANTITY = "premium_ads_quantity";

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
        View view = inflater.inflate(R.layout.fragment_premium_ads_layout, container, false);

        databaseReference = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();

        adsQuantityTxt = view.findViewById(R.id.premium_ads_textView_fragment_premium);
        verifyBtn = view.findViewById(R.id.verify_btn_fragment_premium);
        showAdBtn = view.findViewById(R.id.show_ad_btn_fragment_premium);

        quantity = utils.getStoredInteger(getActivity(), FIRST_TIME_PREMIUM_ADS_QUANTITY) + utils.getStoredInteger(getActivity(), PREMIUM_ADS_QUANTITY);

        adsQuantityTxt.setText(String.valueOf(quantity));

        setShowBtnClickListener();

        setVerifyBtnClickListener();

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
                                    // UPDATING TOTAL BALANCE FIELD BY ADDING Rs: 5
                                    int totalBalance = Integer.parseInt(snapshot.child("totalBlnc").getValue(String.class));
                                    databaseReference
                                            .child("users")
                                            .child(mAuth.getCurrentUser().getUid())
                                            .child("details")
                                            .child("totalBlnc").setValue(String.valueOf(totalBalance + 5));

                                    // UPDATING CURRENT BALANCE FIELD BY ADDING Rs: 5
                                    int currentBalance = Integer.parseInt(snapshot.child("cvBlnce").getValue(String.class));
                                    databaseReference
                                            .child("users")
                                            .child(mAuth.getCurrentUser().getUid())
                                            .child("details")
                                            .child("cvBlnce").setValue(String.valueOf(currentBalance + 5));

                                    // ADDING Rs: 5 IN CURRENT BALANCE IN PREFERENCES
                                    utils.storeInteger(getActivity(), CURRENT_BALANCE, utils.getStoredInteger(getActivity(), CURRENT_BALANCE) + 5);

                                    // IF FIRST TIME ADS QUANTITY IS NOT 0
                                    if (utils.getStoredInteger(getActivity(), FIRST_TIME_PREMIUM_ADS_QUANTITY) != 0) {

                                        // REMOVING 1 IN FIRST TIME ADS QUANTITY
                                        utils.storeInteger(getActivity(), FIRST_TIME_PREMIUM_ADS_QUANTITY, utils.getStoredInteger(getActivity(), FIRST_TIME_PREMIUM_ADS_QUANTITY) - 1);
                                        adsQuantityTxt.setText(String.valueOf(quantity - 1));
                                        return;
                                    }

                                    // REMOVING 1 IN THE PREMIUM ADS QUANTITY
                                    utils.storeInteger(getActivity(), PREMIUM_ADS_QUANTITY, utils.getStoredInteger(getActivity(), PREMIUM_ADS_QUANTITY) - 1);
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
                    utils.showOfflineDialog(getActivity(), "Sorry!", "Your request is not verified because premium ads are 0. In order to get more ads please invite people and after they upgrade you'll get 15 ads worth and every ad will give you Rs: 5");
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
