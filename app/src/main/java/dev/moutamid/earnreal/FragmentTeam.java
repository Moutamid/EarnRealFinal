package dev.moutamid.earnreal;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;


public class FragmentTeam extends Fragment {
    private static final String TAG = "FragmentTeam";

    private static final String PAID_STATUS = "paidStatus";
    private static final String USER_ID = "userReferralCode";

//    private ArrayList<refUser> refUsersList = new ArrayList<>();

    private DatabaseReference databaseReference;
    private FirebaseAuth mAuth;

    private Utils utils = new Utils();

    private ProgressDialog dialog;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_team_layout, container, false);


        //
//        // IF USER IS PAID
//        if (utils.getStoredBoolean(getActivity(), PAID_STATUS)) {
//
//            dialog = new ProgressDialog(getActivity());
//            dialog.setMessage("Loading team members...");
//            dialog.show();

//            LinearLayout notPaidLayout = view.findViewById(R.id.not_paid_layout_team);
//            ScrollView paiLayout = view.findViewById(R.id.paid_layout_fragment_team);

//            notPaidLayout.setVisibility(View.GONE);
//            paiLayout.setVisibility(View.VISIBLE);

//            mAuth = FirebaseAuth.getInstance();
//
//            databaseReference = FirebaseDatabase.getInstance().getReference();
//            databaseReference.keepSynced(true);
//
//            getTeamFromDatabase(view);

//            TextView userIdTxt = view.findViewById(R.id.user_id_textView);
        //          userIdTxt.setText(utils.getStoredString(getActivity(), USER_ID));

        //        view.findViewById(R.id.copy_btn_user_id).setOnClickListener(new View.OnClickListener() {
        //    @Override
        //      public void onClick(View view) {


//                    ClipboardManager clipboardManager = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
//                    ClipData clipData = ClipData.newPlainText("User ID", utils.getStoredString(getActivity(), USER_ID));
//                    clipboardManager.setPrimaryClip(clipData);
//
//                    utils.showWorkDoneDialog(getActivity(), "Copied!", "Your referral ID has been copied to clipboard. Now tell others to sign up using your ID and after they upgrade their account, you'll get 15 premium ads and every ad will give you Rs: 5");
        //          }
        //    });

//        }

        ViewPager viewPager = view.findViewById(R.id.view_pager_fragment_team);
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getActivity().getSupportFragmentManager());
        viewPager.setAdapter(viewPagerAdapter);

        TabLayout tabLayout = view.findViewById(R.id.tablayout);
        tabLayout.setupWithViewPager(viewPager);

        //Toast.makeText(getActivity(), utils.getDate(getActivity()), Toast.LENGTH_LONG).show();


        TextView dateTextView = view.findViewById(R.id.current_date_textview);
        dateTextView.setText("Yesterday, (" + utils.getPreviousDate(getActivity()) + ")");

        view.findViewById(R.id.date_picker_layout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showDatePickerDialog(view);

            }
        });

        view.findViewById(R.id.add_referral_detail_btn_fragment_team).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), "Clicked!", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }

    private class ViewPagerAdapter extends FragmentStatePagerAdapter {

        public ViewPagerAdapter(@NonNull FragmentManager fm) {
            super(fm);
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {

            switch (position) {
                case 0:
                    return new FragmentPendingReferrals();
                case 1:
                    return new FragmentApprovedReferrals();
            }

            return new FragmentPendingReferrals();
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "Pending Referrals";
                case 1:
                    return "Approved Referrals";
                default:
                    return null;
            }
        }
    }

    private void showDatePickerDialog(View view) {
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        DialogFragment dialogFragment = new DatePickerFragment(view);
        dialogFragment.show(fragmentManager, "datePicker");
    }

    public static class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {

        Utils utils = new Utils();
        View parentView;

        public DatePickerFragment(View v) {
            parentView = v;
        }

        @NonNull
        @Override
        public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
            //return super.onCreateDialog(savedInstanceState);

            String currentDate = utils.getDate(getActivity());// 06-10-2020

            int date = Integer.parseInt(currentDate.substring(0, 2));
            int month = Integer.parseInt(currentDate.substring(3, 5));
            int year = Integer.parseInt(currentDate.substring(6));

            return new DatePickerDialog(getActivity(), this, year, month - 1, date);

        }

        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

            String y = String.valueOf(year);
            String m = String.valueOf(month + 1);
            String d = String.valueOf(dayOfMonth);

            if (month + 1 < 10)
                m = "0" + m;

            if (dayOfMonth < 10)
                d = "0" + d;

            TextView dateTextView = parentView.findViewById(R.id.current_date_textview);

            dateTextView.setText(d + "-" + m + "-" + y);

        }
    }

//
//    private void getTeamFromDatabase(final View view) {
//        databaseReference.child("teams").addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//
//                if (snapshot.hasChild(mAuth.getCurrentUser().getUid())) {
//
//                    LinearLayout noMemberLayout = view.findViewById(R.id.no_team_member_layout);
//                    noMemberLayout.setVisibility(View.GONE);
//
//                    // CLEARING ALL THE ITEMS
//                    refUsersList.clear();
//
//                    // LOOPING THROUGH ALL THE CHILDREN OF TEAM
//                    for (DataSnapshot dataSnapshot : snapshot.child(mAuth.getCurrentUser().getUid()).child("users").getChildren()) {
//
//                        refUsersList.add(dataSnapshot.getValue(refUser.class));
//
//                    }
//
//                    initRecyclerView(view);
//                } else {
//
//                    dialog.dismiss();
//
//                    // USER HAS NOT INVITED ANYONE
//                    Log.i(TAG, "onDataChange: No child exists");
//
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//                Log.w(TAG, "onCancelled: " + error.toException());
//
//                Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_SHORT).show();
//
//            }
//        });
//    }
//
//    private void initRecyclerView(View view) {
//        Log.d(TAG, "initRecyclerView: ");
//
//        RecyclerView conversationRecyclerView = view.findViewById(R.id.team_recyclerView);
//        RecyclerViewAdapterTeam adapter = new RecyclerViewAdapterTeam();
//
//        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
//
//        conversationRecyclerView.setLayoutManager(linearLayoutManager);
//        conversationRecyclerView.setHasFixedSize(true);
//        conversationRecyclerView.setNestedScrollingEnabled(false);
//
//        conversationRecyclerView.setAdapter(adapter);
//
//        dialog.dismiss();
//    }
//
//    private static class refUser {
//
//        private String email;
//        private boolean paid;
//
//        refUser() {
//
//        }
//
//        public refUser(String email, boolean paid) {
//            this.email = email;
//            this.paid = paid;
//        }
//
//        public boolean isPaid() {
//            return paid;
//        }
//
//        public void setPaid(boolean paid) {
//            this.paid = paid;
//        }
//
//        public String getEmail() {
//            return email;
//        }
//
//        public void setEmail(String email) {
//            this.email = email;
//        }
//
//    }
//
//    private class RecyclerViewAdapterTeam extends RecyclerView.Adapter
//            <RecyclerViewAdapterTeam.ViewHolderTeam> {
//
//        @NonNull
//        @Override
//        public ViewHolderTeam onCreateViewHolder(@NonNull ViewGroup parent, int i) {
//            Log.d(TAG, "onCreateViewHolder: ");
//            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_team_members, parent, false);
//            return new ViewHolderTeam(view);
//        }
//
//        @Override
//        public void onBindViewHolder(@NonNull ViewHolderTeam holder, int position) {
//            Log.d(TAG, "onBindViewHolder: " + position);
//
//            if (refUsersList.get(position).isPaid()) {
//
//                holder.paidStatusImg.setImageResource(R.drawable.ic_done);
//                holder.userEmail.setText(refUsersList.get(position).getEmail());
//                holder.userStatusTxt.setText("PAID");
//
//            } else {
//
//                holder.userEmail.setText(refUsersList.get(position).getEmail());
//
//            }
//        }
//
//        @Override
//        public int getItemCount() {
//            if (refUsersList == null)
//                return 0;
//
//            return refUsersList.size();
//        }
//
//        public class ViewHolderTeam extends RecyclerView.ViewHolder {
//
//            TextView userEmail, userStatusTxt;
//            ImageView paidStatusImg;
//
//            public ViewHolderTeam(@NonNull View v) {
//                super(v);
//
//                userEmail = v.findViewById(R.id.user_email_layout_team);
//                userStatusTxt = v.findViewById(R.id.paid_status_textview_layout_team);
//                paidStatusImg = v.findViewById(R.id.paid_status_image_layout_team);
//            }
//        }
//    }

}
