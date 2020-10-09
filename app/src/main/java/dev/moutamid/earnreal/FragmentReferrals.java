package dev.moutamid.earnreal;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class FragmentReferrals extends Fragment {
    private static final String TAG = "FragmentReferrals";

    private static final String PAID_STATUS = "paidStatus";
    private static final String USER_ID = "userReferralCode";
    private static final String TOTAL_REFERRALS_AMOUNT = "total_referrals_amount";
    private static final String PAID_REFERRALS_AMOUNT = "paid_referrals_amount";
    private static final String CURRENT_DATE_STRING = "current_date_string";

    private String currentDateString;
    private boolean isOnline = false;
    private boolean isNmbrValid = true;

//    private ArrayList<refUser> refUsersList = new ArrayList<>();

    private DatabaseReference databaseReference;
    private FirebaseAuth mAuth;

    private Utils utils = new Utils();

    private ProgressDialog dialog;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_referrals_layout, container, false);

        checkOnlineStatus();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.keepSynced(true);

        mAuth = FirebaseAuth.getInstance();

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

        utils.storeString(getActivity(), CURRENT_DATE_STRING, utils.getPreviousDate(getActivity()));

        ViewPager viewPager = view.findViewById(R.id.view_pager_fragment_team);
        final ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getActivity().getSupportFragmentManager());
        viewPager.setAdapter(viewPagerAdapter);

        TabLayout tabLayout = view.findViewById(R.id.tablayout);
        tabLayout.setupWithViewPager(viewPager);

        TextView dateTextView = view.findViewById(R.id.current_date_textview);
        dateTextView.setText("Yesterday, (" + utils.getPreviousDate(getActivity()) + ")");

        view.findViewById(R.id.date_picker_layout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showDatePickerDialog(view, viewPagerAdapter);

            }
        });

        view.findViewById(R.id.add_referral_detail_btn_fragment_team).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showAddReferralDetailsDialog();
            }
        });

        return view;
    }

    private void showAddReferralDetailsDialog() {
        final Dialog dialog = new Dialog(getActivity());
        dialog.setContentView(R.layout.dialog_ad_referral_details);

        TextView closeBtn = dialog.findViewById(R.id.close_text_view_btn_dialog_ad_referral);
        final EditText nameTv = (EditText) dialog.findViewById(R.id.name_edit_text_dialog_ad_referral);
        final EditText numberTv = (EditText) dialog.findViewById(R.id.number_edit_text_dialog_ad_referral);
        final EditText cityTv = (EditText) dialog.findViewById(R.id.city_name_edit_text_dialog_ad_referral);
        Button submitBtn = dialog.findViewById(R.id.submit_btn_dialog_ad_referral);

        numberTv.addTextChangedListener(phoneNmbrEditTextWatcherListener(numberTv));

        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (nameTv.getText().toString().isEmpty()) {
                    nameTv.setError("Name is empty!");
                    nameTv.requestFocus();
                    return;
                }

                if (numberTv.getText().toString().isEmpty()) {
                    numberTv.setError("Number is empty!");
                    numberTv.requestFocus();
                    return;
                }

                if (cityTv.getText().toString().isEmpty()) {
                    cityTv.setError("City name is empty!");
                    cityTv.requestFocus();
                    return;
                }

                if (!isNmbrValid) {
                    numberTv.setError("Number is invalid!");
                    numberTv.requestFocus();
                    return;
                }

                if (numberTv.getText().toString().length() != 11) {
                    numberTv.setError("Minimum length should be 11!");
                    numberTv.requestFocus();
                    return;
                }

                if (!isOnline) {
                    utils.showOfflineDialog(getActivity(), "", "");
                    return;
                }

                //--- TO-DO CHECK IF NUMBER ALREADY EXISTS IN DATABASE

                ProgressDialog dialog1 = new ProgressDialog(getActivity());
                dialog1.setMessage("Uploading...");
                dialog1.show();

                uploadReferralDetailToDataBase(dialog, dialog1, nameTv, numberTv, cityTv);

            }
        });

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
    }

    private static class ReferralDetail {

        private String name, number, city, remarks;

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

    private void uploadReferralDetailToDataBase(final Dialog referralDialog, final ProgressDialog dialog1, EditText nameTv, EditText numberTv, EditText cityTv) {

        String name = nameTv.getText().toString().trim();
        String number = numberTv.getText().toString().trim();
        String city = cityTv.getText().toString().trim();
        ReferralDetail details = new ReferralDetail(name, number, city, "pg");

        databaseReference
                .child("referrals")
                .child(mAuth.getCurrentUser().getUid())
                .child(utils.getDate(getActivity()))
                .push()
                .setValue(details).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    dialog1.dismiss();
                    referralDialog.dismiss();
                    utils.showWorkDoneDialog(getActivity(), "Detail uploaded!", "Your referral detail is successfully submitted and you wil get a response feedback in approx 28 hours.");
                } else {
                    dialog1.dismiss();
                    Toast.makeText(getActivity(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    Log.i(TAG, "onComplete: task failed " + task.getException());
                }
            }
        });

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

    }

    private TextWatcher phoneNmbrEditTextWatcherListener(final EditText phoneNmbrEditText) {
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                String nmbr = charSequence.toString();

                if (nmbr.length() == 0)
                    return;

                // FIRST CHARACTER OF THE NUMBER IS NOT 0

                if (nmbr.length() == 1)
                    if (!nmbr.substring(0, 1).equals("0")) {
                        phoneNmbrEditText.setError("Number should start from 0!");
                        isNmbrValid = false;
                        return;
                    }
                // SECOND CHARACTER OF THE NUMBER IS NOT 3
                if (nmbr.length() == 2)
                    if (!nmbr.substring(0, 2).equals("03")) {
                        phoneNmbrEditText.setError("Number should start like 03...!");
                        isNmbrValid = false;
                        return;
                    }

                // THIRD CHARACTER OF THE NUMBER IS 6, 7, 8 OR 9 WHICH ARE INVALID
                if (nmbr.length() >= 3)
                    if (nmbr.substring(0, 3).equals("036")
                            || nmbr.substring(0, 3).equals("037")
                            || nmbr.substring(0, 3).equals("038")
                            || nmbr.substring(0, 3).equals("039")

                    ) {
                        phoneNmbrEditText.setError("Number is invalid!");
                        isNmbrValid = false;
                        return;
                    }

                isNmbrValid = true;
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        };

    }

    private void checkOnlineStatus() {
        Log.i(TAG, "checkOnlineStatus: ");

        DatabaseReference connectedRef = FirebaseDatabase.getInstance().getReference(".info/connected");
        connectedRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Log.i(TAG, "onDataChange: online status");

                isOnline = snapshot.getValue(Boolean.class);

//                if (isOnline != null)
//                    signUpBtn.setText(isOnline.toString());
//                else signUpBtn.setText("NULL");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w(TAG, "onCancelled: " + error.getMessage());
            }
        });
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
                case 2:
                    return new FragmentPaidReferrals();
            }

            return new FragmentPendingReferrals();
        }

        @Override
        public int getCount() {
            return 3;
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "Pending";
                case 1:
                    return "Approved";
                case 2:
                    return "Paid";
                default:
                    return null;
            }
        }
    }

    private void showDatePickerDialog(View view, ViewPagerAdapter viewAdapter) {
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        DialogFragment dialogFragment = new DatePickerFragment(view, viewAdapter);
        dialogFragment.show(fragmentManager, "datePicker");
    }

    public static class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {

        private static final String CURRENT_DATE_STRING = "current_date_string";
        Utils utils = new Utils();
        View parentView;
        ViewPagerAdapter pagerAdapter;

        public DatePickerFragment(View v, ViewPagerAdapter adapter) {
            parentView = v;
            pagerAdapter = adapter;
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

            String currentDateStr = d + "-" + m + "-" + y;

            if (currentDateStr.equals(utils.getPreviousDate(getActivity())))
            dateTextView.setText("Yesterday, (" + currentDateStr + ")");

            else if (currentDateStr.equals(utils.getDate(getActivity())))
                dateTextView.setText("Today, (" + currentDateStr + ")");

            else dateTextView.setText(currentDateStr);

            utils.storeString(getActivity(), CURRENT_DATE_STRING, currentDateStr);

//            Intent intent = new Intent(getActivity(), MainActivity.class);
//            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
//            getActivity().finish();
//            getActivity().startActivity(intent);

            pagerAdapter.notifyDataSetChanged();

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
