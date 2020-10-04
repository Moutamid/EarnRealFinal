package dev.moutamid.earnreal;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class FragmentWithdraw extends Fragment {
    private static final String TAG = "FragmentWithdraw";

    private static final String WITHDRAW_REQUEST_DATE = "withdrawRequestDate";
    private static final String CURRENT_BALANCE = "currentBalance";

    private Utils utils = new Utils();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_withdraw_layout, container, false);

        TextView balance = view.findViewById(R.id.current_balance_textView_withdraw);
        balance.setText(String.valueOf(utils.getStoredInteger(getActivity(), CURRENT_BALANCE)));

        setSubmitBtnClickListener(view);

        return view;
    }

    private void setSubmitBtnClickListener(View view) {
        final RadioGroup radioGroup = (RadioGroup) view.findViewById(R.id.radioGroup_withdraw_layout);

        final EditText accountNameEt = view.findViewById(R.id.accountName_et_withdraw_layout);
        final EditText accountNmbrEt = view.findViewById(R.id.accountNmbr_et_withdraw_layout);
        final EditText amountEt = view.findViewById(R.id.amount_et_withdraw_layout);
        Button submitBtn = view.findViewById(R.id.submit_btn_upgrade_layout);

        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // IF USER HAS ALREADY SUBMITTED ONE REQUEST TODAY THEN STOP REQUESTING AGAIN
                if (utils.getStoredString(getActivity(), WITHDRAW_REQUEST_DATE).equals(utils.getDate(getActivity())) && utils.getStoredBoolean(getActivity(), "isRequested")) {

                    utils.showOfflineDialog(getActivity(), "Request denied!", "You can only do one withdrawal request per day. Come again tomorrow to submit a new request.");
                    return;
                }

                // IF REQUESTED AMOUNT IS LESS THAN 300
                if (Integer.parseInt(amountEt.getText().toString()) < 300) {
                    amountEt.setError("You can only request minimum amount of Rs: 300");
                    amountEt.requestFocus();
                    return;
                }

                // IF AMOUNT IS GREATER THAN THE CURRENT BALANCE AMOUNT
                if (Integer.parseInt(amountEt.getText().toString()) > utils.getStoredInteger(getActivity(), CURRENT_BALANCE)){
                    amountEt.setError("Your amount is greater than your current balance!");
                    amountEt.requestFocus();
                    return;
                }

                final RadioButton radioBtn = radioGroup.findViewById(radioGroup.getCheckedRadioButtonId());

                String details = "Method: " + radioBtn.getText().toString() + "\n\n" +
                        "Account name: " + accountNameEt.getText().toString() + "\n\n" +
                        "Account number: " + accountNmbrEt.getText().toString() + "\n\n" +
                        "Amount: " + amountEt.getText().toString();

                utils.showDialog(getActivity(), "Please confirm your details!", details, "Submit", "Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        uploadWithdrawDetails(radioBtn.getText().toString(), accountNameEt.getText().toString(), accountNmbrEt.getText().toString(), amountEt.getText().toString());

                        dialogInterface.dismiss();
                    }
                }, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Toast.makeText(getActivity(), "Cancel", Toast.LENGTH_SHORT).show();
                        dialogInterface.dismiss();
                    }
                });

            }
        });
    }

    private void uploadWithdrawDetails(String method, String name, String number, String amount) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

        withdrawRequestDetails details = new withdrawRequestDetails(method, name, number, amount);

        databaseReference.child("withdraw_requests").push().setValue(details).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    utils.showWorkDoneDialog(getActivity(), "Successful!", "Your request to withdraw money has been submitted successfully. It will be processed in 12 to 24 business hours.\n\nNote: If we found any suspicious activity in your account, you will be permanently removed and you will lose all of your money!");

                    utils.storeString(getActivity(), WITHDRAW_REQUEST_DATE, utils.getDate(getActivity()));
                    utils.storeBoolean(getActivity(), "isRequested", true);

                } else {
                    //Log.i(TAG, "onComplete: " + task.getException());
                    Toast.makeText(getActivity(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private static class withdrawRequestDetails {

        private String method, name, number, amount;


        public withdrawRequestDetails(String method, String name, String number, String amount) {
            this.method = method;
            this.name = name;
            this.number = number;
            this.amount = amount;
        }

        withdrawRequestDetails() {

        }

        public String getMethod() {
            return method;
        }

        public void setMethod(String method) {
            this.method = method;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getNumber() {
            return number;
        }

        public void setNumber(String number) {
            this.number = number;
        }

        public String getAmount() {
            return amount;
        }

        public void setAmount(String amount) {
            this.amount = amount;
        }

    }
}
