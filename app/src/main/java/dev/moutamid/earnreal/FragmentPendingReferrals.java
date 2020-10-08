package dev.moutamid.earnreal;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class FragmentPendingReferrals extends Fragment {

    private Utils utils = new Utils();
    private DatabaseReference databaseReference;
    private FirebaseAuth mAuth;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pending_referrals, container, false);

        databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.keepSynced(true);

        mAuth = FirebaseAuth.getInstance();
//        if (utils.getStoredBoolean(getActivity(), "first-boolean")) {
//            currentDateString = utils.getDate(getActivity());
//            utils.storeBoolean(getActivity(), "first-boolean", true);
//        } else {
//            currentDateString = utils.getStoredString(getActivity(), CURRENT_DATE_STRING);
//        }
//
//        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot1) {
//
//                if (!snapshot1.hasChild("referrals"))
//                    return;
//                if (!snapshot1.child("referrals").hasChild(mAuth.getCurrentUser().getUid()))
//                    return;
//                if (!snapshot1.child("referrals").child(mAuth.getCurrentUser().getUid()).hasChild(currentDateString))
//                    return;
//
//                DataSnapshot snapshot = snapshot1.child("referrals").child(mAuth.getCurrentUser().getUid()).child(currentDateString);
//
//
////                    utils.storeString(getActivity(), TOTAL_REFERRALS_AMOUNT, totalReferralsStr);
////                    utils.storeString(getActivity(), PAID_REFERRALS_AMOUNT, paidReferralsStr);
//
//
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });

        return view;
    }
}
