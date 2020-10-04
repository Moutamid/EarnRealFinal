package dev.moutamid.earnreal;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class FragmentUpgrade extends Fragment {
    private static final String TAG = "FragmentUpgrade";

    private static final String PAID_STATUS = "paidStatus";
    private static final String REQUESTS_QUANTITY = "requestQuantity";
    private static final String PAID_EXPIRE_DATE = "paidExpireDate";
    private static final String USER_EMAIL = "userEmail";
    private static final String UPGRADE_REQUEST_DATE = "upgradeRequestDate";

    private DatabaseReference databaseReference;

    private LinearLayout methodSelectionLayout;

    private Boolean isOnline = false;
    private Utils utils = new Utils();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_upgrade_layout, container, false);

        databaseReference = FirebaseDatabase.getInstance().getReference();

        checkOnlineStatus();

        LinearLayout paidAccountLayout = (LinearLayout) view.findViewById(R.id.paid_layout_fragment_upgrade);
        methodSelectionLayout = (LinearLayout) view.findViewById(R.id.method_selection_layout_upgrade);

        if (utils.getStoredBoolean(getActivity(), PAID_STATUS)) {

            methodSelectionLayout.setVisibility(View.GONE);
            paidAccountLayout.setVisibility(View.VISIBLE);

            TextView expireDate = view.findViewById(R.id.paid_expire_date_fragment_upgrade);
            expireDate.setText("You are a paid member until " + utils.getStoredString(getActivity(), PAID_EXPIRE_DATE));

        } else {

            setNextBtnMethods(view);

            setEasyPaisaBtnMethod(view);

            setJazzcashBtnMethod(view);
        }

        return view;
    }

    private void setJazzcashBtnMethod(View view) {
        final EditText jazzCashEt = view.findViewById(R.id.jazzcash_edittext_layout_upgrade);
        Button jazzcashBtn = view.findViewById(R.id.jazzcash_submitBtn_layout_upgrade);
        jazzcashBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!isOnline) {
                    utils.showOfflineDialog(getActivity(), "", "");
                    return;
                }

                if (utils.getStoredInteger(getActivity(), REQUESTS_QUANTITY) == 3
                        && utils.getDate(getActivity())
                        .equals(utils.getStoredString(getActivity(), UPGRADE_REQUEST_DATE))) {

                    utils.showOfflineDialog(getActivity(), "NOTICE!", "You have already submitted many requests. Your account can be permanently banned if we found you spamming ID's!");

                    return;
                }

                String trxID = jazzCashEt.getText().toString().trim();
                String email = utils.getStoredString(getActivity(), USER_EMAIL);

                upgradeRequestDetails details = new upgradeRequestDetails(trxID, email);

                databaseReference.child("upgrade_requests").push().setValue(details).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            // TASK SUCCESSFUL

                            utils.showWorkDoneDialog(getActivity(), "TRX ID Sent", "Your request to upgrade your account has been submitted. It will be processed in 12 to 24 business hours. Check in everyday to make sure you don't lose daily ads after account confirmation.\n\nNote: If your requested Trx ID is wrong, your account can be permanently removed!");

                            utils.storeInteger(getActivity(), REQUESTS_QUANTITY, utils.getStoredInteger(getActivity(), REQUESTS_QUANTITY) + 1);
                            utils.storeString(getActivity(), UPGRADE_REQUEST_DATE, utils.getDate(getActivity()));

                        } else {
                            Log.i(TAG, "onComplete: " + task.getException());
                            Toast.makeText(getActivity(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });


    }

    private void setEasyPaisaBtnMethod(View view) {
        final EditText easyPaisaEt = view.findViewById(R.id.easypaisa_edittext_layout_upgrade);
        Button easypaisaBtn = view.findViewById(R.id.easypaisa_submitBtn_layout_upgrade);
        easypaisaBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!isOnline) {
                    utils.showOfflineDialog(getActivity(), "", "");
                    return;
                }

                if (utils.getStoredInteger(getActivity(), REQUESTS_QUANTITY) == 3
                        && utils.getDate(getActivity())
                        .equals(utils.getStoredString(getActivity(), UPGRADE_REQUEST_DATE))) {

                    utils.showOfflineDialog(getActivity(), "NOTICE!", "You have already submitted many requests. Your account can be permanently banned if we found you spamming ID's!");

                    return;
                }

                String trxID = easyPaisaEt.getText().toString().trim();
                String email = utils.getStoredString(getActivity(), USER_EMAIL);

                upgradeRequestDetails details = new upgradeRequestDetails(trxID, email);

                databaseReference.child("upgrade_requests").push().setValue(details).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            // TASK SUCCESSFUL

                            utils.showWorkDoneDialog(getActivity(), "TRX ID Sent", "Your request to upgrade your account has been submitted. It will be processed in 12 to 24 business hours. Check in everyday to make sure you don't lose daily ads after account confirmation.\n\nNote: If your requested Trx ID is wrong, your account can be permanently removed!");

                            utils.storeInteger(getActivity(), REQUESTS_QUANTITY, utils.getStoredInteger(getActivity(), REQUESTS_QUANTITY) + 1);
                            utils.storeString(getActivity(), UPGRADE_REQUEST_DATE, utils.getDate(getActivity()));

                        } else {
                            Log.i(TAG, "onComplete: " + task.getException());
                            Toast.makeText(getActivity(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

    }

    private void setNextBtnMethods(View view) {

        final RadioGroup radioGroup = (RadioGroup) view.findViewById(R.id.payment_methods_radioGroup_upgrade_layout);

        final ScrollView easypaisaLayout = (ScrollView) view.findViewById(R.id.easypaisa_instructions_layout_upgrade);
        final ScrollView jazzcashLayout = (ScrollView) view.findViewById(R.id.jazzcash_instructions_layout_upgrade);

        view.findViewById(R.id.nextBtn_upgrade_layout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int radioButtonId = radioGroup.getCheckedRadioButtonId();

                if (radioButtonId == R.id.easypaisa_radioBtn_upgrade_layout) {
                    methodSelectionLayout.setVisibility(View.GONE);
                    easypaisaLayout.setVisibility(View.VISIBLE);

                } else if (radioButtonId == R.id.jazzcash_radioBtn_upgrade_layout) {
                    methodSelectionLayout.setVisibility(View.GONE);
                    jazzcashLayout.setVisibility(View.VISIBLE);

                } else
                    Toast.makeText(getActivity(), "Error occurred", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void checkOnlineStatus() {
        Log.i(TAG, "checkOnlineStatus: ");

        DatabaseReference connectedRef = FirebaseDatabase.getInstance().getReference(".info/connected");
        connectedRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Log.i(TAG, "onDataChange: online status");

                isOnline = snapshot.getValue(Boolean.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w(TAG, "onCancelled: " + error.getMessage());
            }
        });

    }

    private static class upgradeRequestDetails {

        private String email, trx;

        public upgradeRequestDetails(String trx, String email) {
            this.email = email;
            this.trx = trx;
        }

        upgradeRequestDetails() {

        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getTrx() {
            return trx;
        }

        public void setTrx(String trx) {
            this.trx = trx;
        }

    }
}
