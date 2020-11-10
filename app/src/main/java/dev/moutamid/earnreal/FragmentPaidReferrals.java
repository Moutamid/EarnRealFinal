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

public class FragmentPaidReferrals extends Fragment {
    private static final String TAG = "FragmentPaidReferrals";

    private DatabaseReference databaseReference;
    private FirebaseAuth mAuth;

    private LinearLayout noDataTextView;

    private ArrayList<ReferralDetail> allReferralDetailsList = new ArrayList<>();
    private ArrayList<ReferralDetail> paidReferralDetailsList = new ArrayList<>();

    private static class ReferralDetail {

        private String name, number, city, remarks;

        ReferralDetail() {
        }

        ReferralDetail(String name, String number, String city, String remarks) {
            this.name = name;
            this.number = number;
            this.city = city;
            this.remarks = remarks;
        }

        public String getRemarks() {
            return remarks;
        }

        public void setRemarks(String remarks) {
            this.remarks = remarks;
        }

        public String getCity() {
            return city;
        }

        public void setCity(String city) {
            this.city = city;
        }

        public String getNumber() {
            return number;
        }

        public void setNumber(String number) {
            this.number = number;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_paid_referrals, container, false);

        databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.keepSynced(true);

        mAuth = FirebaseAuth.getInstance();

        noDataTextView = view.findViewById(R.id.no_data_text_view_paid_referrals);

        RefreshPaidReferrals(view);


        return view;
    }

    private void RefreshPaidReferrals(final View view) {

        databaseReference.child("referrals").child(mAuth.getCurrentUser().getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot1) {

                        if (!snapshot1.exists()) {
                            noDataTextView.setVisibility(View.VISIBLE);
                            return;
                        }

                        allReferralDetailsList.clear();
                        paidReferralDetailsList.clear();

                        // LOOPING THROUGH ALL THE CHILDREN OF TEAM
                        for (DataSnapshot dataSnapshot : snapshot1.getChildren()) {

                            allReferralDetailsList.add(dataSnapshot.getValue(ReferralDetail.class));

                        }

                        // EXTRACTING OUT ONLY THE PENDING REFERRALS AND SHOWING THE RECYCLER VIEW
                        for (int i = 0; i <= allReferralDetailsList.size() - 1; i++) {

                            if (allReferralDetailsList.get(i).getRemarks().equals("paid"))
                                paidReferralDetailsList.add(allReferralDetailsList.get(i));

                        }

                        Collections.reverse(paidReferralDetailsList);

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

        RecyclerView conversationRecyclerView = view.findViewById(R.id.recyclerview_paid_referrals);
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
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_referrals_details, parent, false);
            return new RecyclerViewAdapterTeam.ViewHolderTeam(view);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerViewAdapterTeam.ViewHolderTeam holder, int position) {
            Log.d(TAG, "onBindViewHolder: " + position);

            holder.nameTV.setText(paidReferralDetailsList.get(position).getName());
            holder.numberTV.setText(paidReferralDetailsList.get(position).getNumber());
            holder.cityTV.setText(paidReferralDetailsList.get(position).getCity());

            holder.remarksTV.setVisibility(View.VISIBLE);
            holder.remarksTV.setText(paidReferralDetailsList.get(position).getRemarks());

        }

        @Override
        public int getItemCount() {
            if (paidReferralDetailsList == null)
                return 0;

            return paidReferralDetailsList.size();
        }

        public class ViewHolderTeam extends RecyclerView.ViewHolder {

            TextView nameTV, numberTV, cityTV, remarksTV;

            public ViewHolderTeam(@NonNull View v) {
                super(v);

                nameTV = v.findViewById(R.id.name_text_view_layout_referral_details);
                numberTV = v.findViewById(R.id.number_text_view_layout_referral_details);
                cityTV = v.findViewById(R.id.city_text_view_layout_referral_details);
                remarksTV = v.findViewById(R.id.remarks_text_view_layout_referral_details);
            }
        }
    }

}
