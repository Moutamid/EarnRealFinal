package dev.moutamid.earnreal;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.BaseInputConnection;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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

    private boolean isOnline = false;

    private DatabaseReference databaseReference;
    private FirebaseAuth mAuth;

    private Utils utils = new Utils();

    private ViewPagerAdapter viewPagerAdapter;
    private ViewPager viewPager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_referrals_layout, container, false);

        checkOnlineStatus();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.keepSynced(true);

        mAuth = FirebaseAuth.getInstance();

        viewPager = view.findViewById(R.id.view_pager_fragment_team);
        viewPagerAdapter = new ViewPagerAdapter(getActivity().getSupportFragmentManager());
        viewPager.setAdapter(viewPagerAdapter);

        TabLayout tabLayout = view.findViewById(R.id.tablayout);
        tabLayout.setSelectedTabIndicatorColor(Color.parseColor("#ffffff"));
        tabLayout.setTabTextColors(Color.parseColor("#A9FFFFFF"), Color.parseColor("#ffffff"));
        tabLayout.setupWithViewPager(viewPager);

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
        final EditText numbereditText = (EditText) dialog.findViewById(R.id.number_edit_text_dialog_ad_referral);
        final EditText cityTv = (EditText) dialog.findViewById(R.id.city_name_edit_text_dialog_ad_referral);
        Button submitBtn = dialog.findViewById(R.id.submit_btn_dialog_ad_referral);

        numbereditText.addTextChangedListener(phoneNmbrEditTextWatcherListener(numbereditText));

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

                if (numbereditText.getText().toString().isEmpty()) {
                    numbereditText.setError("Number is empty!");
                    numbereditText.requestFocus();
                    return;
                }

                if (cityTv.getText().toString().isEmpty()) {
                    cityTv.setError("City name is empty!");
                    cityTv.requestFocus();
                    return;
                }

                if (numbereditText.getText().toString().length() != 11) {
                    numbereditText.setError("Minimum length should be 11!");
                    numbereditText.requestFocus();
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

                uploadReferralDetailToDataBase(dialog, dialog1, nameTv, numbereditText, cityTv);

            }
        });

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
    }

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

    private void uploadReferralDetailToDataBase(final Dialog referralDialog, final ProgressDialog dialog1, EditText nameTv, EditText numberTv, EditText cityTv) {

        String name = nameTv.getText().toString().trim();
        String number = numberTv.getText().toString().trim();
        String city = cityTv.getText().toString().trim();
        ReferralDetail details = new ReferralDetail(name, number, city, "pg");

        databaseReference
                .child("referrals")
                .child(mAuth.getCurrentUser().getUid())
//                .child(utils.getDate(getActivity()))
                .push()
                .setValue(details).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    dialog1.dismiss();
                    referralDialog.dismiss();
                    //updateCounter(dialog1, referralDialog);
                    utils.showWorkDoneDialog(getActivity(), "Detail uploaded!", "Your referral detail is successfully submitted and you wil get a response feedback in approx 28 hours.");
                    updateViewPagerWithCurrentDate();
                } else {
                    dialog1.dismiss();
                    Toast.makeText(getActivity(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    Log.i(TAG, "onComplete: task failed " + task.getException().getMessage());

                }
            }
        });

    }

    private void updateViewPagerWithCurrentDate() {

        viewPager.setAdapter(viewPagerAdapter);

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

                        removeCharacter(phoneNmbrEditText);
                        //phoneNmbrEditText.setError("Number should start from 0!");
                        Toast.makeText(getActivity(), "Number should start from 0!", Toast.LENGTH_SHORT).show();
                        //isNmbrValid = false;
                        return;
                    }
                // SECOND CHARACTER OF THE NUMBER IS NOT 3
                if (nmbr.length() == 2)
                    if (!nmbr.substring(0, 2).equals("03")) {

                        removeCharacter(phoneNmbrEditText);
                        Toast.makeText(getActivity(), "Number should start like 03...!", Toast.LENGTH_SHORT).show();
                        //phoneNmbrEditText.setError("Number should start like 03...!");
//                        isNmbrValid = false;
                        return;
                    }

                // THIRD CHARACTER OF THE NUMBER IS 6, 7, 8, 9 WHICH ARE INVALID
                if (nmbr.length() >= 3) {
                    if (nmbr.substring(0, 3).equals("036")
                            || nmbr.substring(0, 3).equals("037")
                            || nmbr.substring(0, 3).equals("038")
                            || nmbr.substring(0, 3).equals("039")
                    ) {

                        removeCharacter(phoneNmbrEditText);
                        Toast.makeText(getActivity(), "Number is invalid!", Toast.LENGTH_SHORT).show();
                        //phoneNmbrEditText.setError("Number is invalid!");
                        //isNmbrValid = false;
                        //return;
                    }

                }
                //isNmbrValid = true;

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        };

    }

    private void removeCharacter(EditText edittext) {

        BaseInputConnection textFieldConnection = new BaseInputConnection(edittext, true);
        textFieldConnection.sendKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DEL));

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

}
