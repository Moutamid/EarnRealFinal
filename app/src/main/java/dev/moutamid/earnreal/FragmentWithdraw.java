package dev.moutamid.earnreal;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class FragmentWithdraw extends Fragment {
    private static final String TAG = "FragmentWithdraw";

    private LinearLayout noDataTextView;

    private ArrayList<withdrawDetails> withdrawDetailsList = new ArrayList<>();

    private DatabaseReference databaseReference;
    private FirebaseAuth mAuth;
    private Utils utils = new Utils();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_withdraw_layout, container, false);

//        TextView balance = view.findViewById(R.id.current_balance_textView_withdraw);
//        balance.setText(String.valueOf(utils.getStoredInteger(getActivity(), CURRENT_BALANCE)));
//
//        setSubmitBtnClickListener(view);

        databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.keepSynced(true);

        mAuth = FirebaseAuth.getInstance();

        noDataTextView = view.findViewById(R.id.no_data_text_view_withdraw_details);

        RefreshApprovedReferrals(view);

        //testMethod();

        return view;
    }

    private void testMethod() {

        withdrawDetails withdrawDetailstest = new withdrawDetails(
                "03058853833",
                "90124678573",
                "11/10/2020",
                "500",
                "Easypaisa");

        databaseReference.child("withdraw_details").child(mAuth.getCurrentUser().getUid())
                .push()
                .setValue(withdrawDetailstest);
    }

    private void RefreshApprovedReferrals(final View view) {

        databaseReference.child("withdraw_details").child(mAuth.getCurrentUser().getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot1) {

//                if (!snapshot1.hasChild("withdraw_details")) {
//                    noDataTextView.setVisibility(View.VISIBLE);
//                    return;
//                }
//
//                if (!snapshot1) {
//                    noDataTextView.setVisibility(View.VISIBLE);
//                    return;
//                }
//
//                DataSnapshot snapshot = snapshot1.child("withdraw_details")
//                        .child(mAuth.getCurrentUser().getUid());

                        // CLEARING ALL THE ITEMS

                        if (!snapshot1.exists()) {
                            noDataTextView.setVisibility(View.VISIBLE);
                            return;
                        }

                        withdrawDetailsList.clear();

                        // LOOPING THROUGH ALL THE CHILDREN OF TEAM
                        for (DataSnapshot dataSnapshot : snapshot1.getChildren()) {

                            withdrawDetailsList.add(dataSnapshot.getValue(withdrawDetails.class));

                        }

                        initRecyclerView(view);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.w(TAG, "onCancelled: ", error.toException());
                        Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

    }

    private void initRecyclerView(View view) {
        Log.d("TAG", "initRecyclerView: ");

        RecyclerView conversationRecyclerView = view.findViewById(R.id.recyclerview_withdraw_details);
        RecyclerViewAdapterTeam adapter = new RecyclerViewAdapterTeam();

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());

        conversationRecyclerView.setLayoutManager(linearLayoutManager);
        conversationRecyclerView.setHasFixedSize(true);
        conversationRecyclerView.setNestedScrollingEnabled(false);

        conversationRecyclerView.setAdapter(adapter);

        if (adapter.getItemCount() == 0)
            noDataTextView.setVisibility(View.VISIBLE);
    }

    private class RecyclerViewAdapterTeam extends RecyclerView.Adapter
            <RecyclerViewAdapterTeam.ViewHolderTeam> {

        @NonNull
        @Override
        public RecyclerViewAdapterTeam.ViewHolderTeam onCreateViewHolder(@NonNull ViewGroup parent, int i) {
//            Log.d(TAG, "onCreateViewHolder: ");
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_withdraw_details, parent, false);
            return new RecyclerViewAdapterTeam.ViewHolderTeam(view);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerViewAdapterTeam.ViewHolderTeam holder, int position) {
            Log.d(TAG, "onBindViewHolder: " + position);

            holder.numberTV.setText(withdrawDetailsList.get(position).getNumber());
            holder.dateTV.setText(withdrawDetailsList.get(position).getDate());
            holder.methodTV.setText(withdrawDetailsList.get(position).getMethod());
            holder.tidTV.setText("TID# " + withdrawDetailsList.get(position).getTid());
            holder.amountTV.setText("Rs: " + withdrawDetailsList.get(position).getAmount());

        }

        @Override
        public int getItemCount() {
            if (withdrawDetailsList == null)
                return 0;

            return withdrawDetailsList.size();
        }

        public class ViewHolderTeam extends RecyclerView.ViewHolder {

            TextView numberTV, dateTV, methodTV, tidTV, amountTV;

            public ViewHolderTeam(@NonNull View v) {
                super(v);

                numberTV = v.findViewById(R.id.number_text_view_layout_withdraw_detail);
                dateTV = v.findViewById(R.id.date_text_view_layout_withdraw_detail);
                methodTV = v.findViewById(R.id.method_text_view_layout_withdraw_detail);
                tidTV = v.findViewById(R.id.tid_text_view_layout_withdraw_detail);
                amountTV = v.findViewById(R.id.amount_text_view_layout_withdraw_detail);
            }
        }
    }

//    private void setSubmitBtnClickListener(View view) {
//        final RadioGroup radioGroup = (RadioGroup) view.findViewById(R.id.radioGroup_withdraw_layout);
//
//        final EditText accountNameEt = view.findViewById(R.id.accountName_et_withdraw_layout);
//        final EditText accountNmbrEt = view.findViewById(R.id.accountNmbr_et_withdraw_layout);
//        final EditText amountEt = view.findViewById(R.id.amount_et_withdraw_layout);
//        Button submitBtn = view.findViewById(R.id.submit_btn_upgrade_layout);
//
//        submitBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//                // IF USER HAS ALREADY SUBMITTED ONE REQUEST TODAY THEN STOP REQUESTING AGAIN
//                if (utils.getStoredString(getActivity(), WITHDRAW_REQUEST_DATE).equals(utils.getDate(getActivity())) && utils.getStoredBoolean(getActivity(), "isRequested")) {
//
//                    utils.showOfflineDialog(getActivity(), "Request denied!", "You can only do one withdrawal request per day. Come again tomorrow to submit a new request.");
//                    return;
//                }
//
//                // IF REQUESTED AMOUNT IS LESS THAN 300
//                if (Integer.parseInt(amountEt.getText().toString()) < 300) {
//                    amountEt.setError("You can only request minimum amount of Rs: 300");
//                    amountEt.requestFocus();
//                    return;
//                }
//
//                // IF AMOUNT IS GREATER THAN THE CURRENT BALANCE AMOUNT
//                if (Integer.parseInt(amountEt.getText().toString()) > utils.getStoredInteger(getActivity(), CURRENT_BALANCE)){
//                    amountEt.setError("Your amount is greater than your current balance!");
//                    amountEt.requestFocus();
//                    return;
//                }
//
//                final RadioButton radioBtn = radioGroup.findViewById(radioGroup.getCheckedRadioButtonId());
//
//                String details = "Method: " + radioBtn.getText().toString() + "\n\n" +
//                        "Account name: " + accountNameEt.getText().toString() + "\n\n" +
//                        "Account number: " + accountNmbrEt.getText().toString() + "\n\n" +
//                        "Amount: " + amountEt.getText().toString();
//
//                utils.showDialog(getActivity(), "Please confirm your details!", details, "Submit", "Cancel", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialogInterface, int i) {
//
//                        uploadWithdrawDetails(radioBtn.getText().toString(), accountNameEt.getText().toString(), accountNmbrEt.getText().toString(), amountEt.getText().toString());
//
//                        dialogInterface.dismiss();
//                    }
//                }, new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialogInterface, int i) {
//                        Toast.makeText(getActivity(), "Cancel", Toast.LENGTH_SHORT).show();
//                        dialogInterface.dismiss();
//                    }
//                });
//
//            }
//        });
//    }
//
//    private void uploadWithdrawDetails(String method, String name, String number, String amount) {
//        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
//
//        withdrawRequestDetails details = new withdrawRequestDetails(method, name, number, amount);
//
//        databaseReference.child("withdraw_requests").push().setValue(details).addOnCompleteListener(new OnCompleteListener<Void>() {
//            @Override
//            public void onComplete(@NonNull Task<Void> task) {
//                if (task.isSuccessful()) {
//                    utils.showWorkDoneDialog(getActivity(), "Successful!", "Your request to withdraw money has been submitted successfully. It will be processed in 12 to 24 business hours.\n\nNote: If we found any suspicious activity in your account, you will be permanently removed and you will lose all of your money!");
//
//                    utils.storeString(getActivity(), WITHDRAW_REQUEST_DATE, utils.getDate(getActivity()));
//                    utils.storeBoolean(getActivity(), "isRequested", true);
//
//                } else {
//                    //Log.i(TAG, "onComplete: " + task.getException());
//                    Toast.makeText(getActivity(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
//                }
//            }
//        });
//    }

    private static class withdrawDetails {

        private String number;
        private String tid;
        private String date;
        private String amount;
        private String method;

        public withdrawDetails(String number, String tid, String date, String amount, String method) {
            this.number = number;
            this.tid = tid;
            this.date = date;
            this.amount = amount;
            this.method = method;
        }

        withdrawDetails() {
        }

        public String getNumber() {
            return number;
        }

        public void setNumber(String number) {
            this.number = number;
        }

        public String getTid() {
            return tid;
        }

        public void setTid(String tid) {
            this.tid = tid;
        }

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }

        public String getAmount() {
            return amount;
        }

        public void setAmount(String amount) {
            this.amount = amount;
        }

        public String getMethod() {
            return method;
        }

        public void setMethod(String method) {
            this.method = method;
        }
    }
}
