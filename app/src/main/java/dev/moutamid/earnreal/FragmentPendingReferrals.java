package dev.moutamid.earnreal;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

public class FragmentPendingReferrals extends Fragment {
    private static final String TAG = "FragmentPendingReferral";

    private static final String CURRENT_DATE_STRING = "current_date_string";
    private Utils utils = new Utils();
    private DatabaseReference databaseReference;
    private FirebaseAuth mAuth;
    private String currentDateString;
    private TextView noDataTextView;

    private ArrayList<ReferralDetail> allReferralDetailsList = new ArrayList<>();
    private ArrayList<ReferralDetail> pendingReferralDetailsList = new ArrayList<>();

    private static class ReferralDetail {

        private String name, number, city, remarks;

        ReferralDetail() {}

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
        final View view = inflater.inflate(R.layout.fragment_pending_referrals, container, false);

        databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.keepSynced(true);

        mAuth = FirebaseAuth.getInstance();

        currentDateString = utils.getStoredString(getActivity(), CURRENT_DATE_STRING);

        noDataTextView = view.findViewById(R.id.no_data_text_view_pending_referrals);

        if (currentDateString.equals("Error"))
            currentDateString = utils.getPreviousDate(getActivity());

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot1) {

                if (!snapshot1.hasChild("referrals")) {
                    noDataTextView.setVisibility(View.VISIBLE);
                    return;
                }

                if (!snapshot1.child("referrals").hasChild(mAuth.getCurrentUser().getUid())) {
                    noDataTextView.setVisibility(View.VISIBLE);
                    return;
                }

                if (!snapshot1.child("referrals").child(mAuth.getCurrentUser().getUid()).hasChild(currentDateString)) {
                    noDataTextView.setVisibility(View.VISIBLE);
                    return;
                }

                DataSnapshot snapshot = snapshot1.child("referrals").child(mAuth.getCurrentUser().getUid()).child(currentDateString);


                // CLEARING ALL THE ITEMS
                allReferralDetailsList.clear();

                // LOOPING THROUGH ALL THE CHILDREN OF TEAM
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {

                    allReferralDetailsList.add(dataSnapshot.getValue(ReferralDetail.class));

                }

                // EXTRACTING OUT ONLY THE PENDING REFERRALS AND SHOWING THE RECYCLER VIEW
                for (int i = 0; i<=allReferralDetailsList.size() - 1; i++){

                    if (allReferralDetailsList.get(i).getRemarks().equals("pg"))
                        pendingReferralDetailsList.add(allReferralDetailsList.get(i));

                }

                initRecyclerView(view);

//                    utils.storeString(getActivity(), TOTAL_REFERRALS_AMOUNT, totalReferralsStr);
//                    utils.storeString(getActivity(), PAID_REFERRALS_AMOUNT, paidReferralsStr);


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w(TAG, "onCancelled: ", error.toException());
                Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }

    private void initRecyclerView(View view) {
        Log.d("TAG", "initRecyclerView: ");

        RecyclerView conversationRecyclerView = view.findViewById(R.id.recyclerview_pending_referrals);
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
        public ViewHolderTeam onCreateViewHolder(@NonNull ViewGroup parent, int i) {
//            Log.d(TAG, "onCreateViewHolder: ");
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_referrals_details, parent, false);
            return new ViewHolderTeam(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolderTeam holder, int position) {
            Log.d(TAG, "onBindViewHolder: " + position);

            holder.nameTV.setText(pendingReferralDetailsList.get(position).getName());
            holder.numberTV.setText(pendingReferralDetailsList.get(position).getNumber());
            holder.cityTV.setText(pendingReferralDetailsList.get(position).getCity());
            holder.remarksTV.setText("");

        }

        @Override
        public int getItemCount() {
            if (pendingReferralDetailsList == null)
                return 0;

            return pendingReferralDetailsList.size();
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
