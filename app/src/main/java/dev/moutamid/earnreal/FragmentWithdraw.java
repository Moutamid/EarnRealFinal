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
import java.util.Collections;

public class FragmentWithdraw extends Fragment {
    private static final String TAG = "FragmentWithdraw";

    private LinearLayout noDataTextView;

    private ArrayList<withdrawDetails> withdrawDetailsList = new ArrayList<>();

    private DatabaseReference databaseReference;
    private FirebaseAuth mAuth;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_withdraw_layout, container, false);

        databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.keepSynced(true);

        mAuth = FirebaseAuth.getInstance();

        noDataTextView = view.findViewById(R.id.no_data_text_view_withdraw_details);

        RefreshApprovedReferrals(view);

        return view;
    }


    private void RefreshApprovedReferrals(final View view) {

        databaseReference.child("withdraw_details").child(mAuth.getCurrentUser().getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot1) {

                        if (!snapshot1.exists()) {
                            noDataTextView.setVisibility(View.VISIBLE);
                            return;
                        }

                        withdrawDetailsList.clear();

                        // LOOPING THROUGH ALL THE CHILDREN OF TEAM
                        for (DataSnapshot dataSnapshot : snapshot1.getChildren()) {

                            withdrawDetailsList.add(dataSnapshot.getValue(withdrawDetails.class));

                        }

                        Collections.reverse(withdrawDetailsList);

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
