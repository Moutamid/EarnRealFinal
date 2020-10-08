package dev.moutamid.earnreal;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class FragmentDashboard extends Fragment {
    private static final String TAG = "FragmentDashboard";

    private static final String TOTAL_REFERRALS_AMOUNT = "total_referrals_amount";
    private static final String PAID_REFERRALS_AMOUNT = "paid_referrals_amount";

    private ArrayList<refUser> refUsersList = new ArrayList<>();
    private ArrayList<String> paid_membersList = new ArrayList<>();
    private TextView totalBalance_tv, totalWithdraw_tv, currentBalance_tv, accountStatus_tv;
    private TextView totalReferralsSubmitted_tv, paidReferrals_tv;
    private DatabaseReference databaseReference;

    private FirebaseAuth mAuth;
    private Utils utils = new Utils();

    private boolean isDone_getDetailsFromDatabase, isDone_getTotalReferralsFromDatabase = false;
    private ProgressDialog dialog;
    private Handler handler;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dashboard_layout, container, false);

        mAuth = FirebaseAuth.getInstance();

        databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.keepSynced(true);

        initViews(view);

        // CHECKING FOR ALL THE METHODS TO COMPLETE AND THEN DISMISSING THE DIALOG
        dialog = new ProgressDialog(getActivity());
        dialog.setMessage("Loading details...");
        dialog.show();
        handler = new Handler();
        startCheckingBooleanValues();

        // GETTING TOTAL BALANCE, TOTAL WITHDRAW, CURRENT BALANCE, ACCOUNT STATUS
        getDetailsFromDatabase();

        // GETTING TOTAL REFERRALS AMOUNT, PAID REFERRALS AMOUNT
        getReferralsAmount();

        // SETTING INFORMATION DIALOGS ON ALL THE LAYOUTS
        setDialogsOnAllLayouts(view);

        return view;
    }

    Runnable booleanCheckerRunnable = new Runnable() {
        @Override
        public void run() {
            try {
                checkBoolean();
            } finally {
                handler.postDelayed(booleanCheckerRunnable, 1000);
            }
        }
    };

    private void startCheckingBooleanValues() {
        booleanCheckerRunnable.run();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        handler.removeCallbacks(booleanCheckerRunnable);
    }

    private void checkBoolean() {
        if (isDone_getDetailsFromDatabase && isDone_getTotalReferralsFromDatabase) {

            handler.removeCallbacks(booleanCheckerRunnable);
            dialog.dismiss();
        }
    }

    private void setDialogsOnAllLayouts(View v) {
        LinearLayout totalbalancelayout = v.findViewById(R.id.total_balance_layout_dashboard);
        LinearLayout totalwithdrawlayout = v.findViewById(R.id.total_withdraw_layout_dashboard);
        LinearLayout currentbalancelayout = v.findViewById(R.id.current_balance_layout_dashboard);
        LinearLayout accountstatuslayout = v.findViewById(R.id.account_status_layout_dashboard);
        LinearLayout totalreferralslayout = v.findViewById(R.id.total_referrals_submitted_layout_dashboard);
        LinearLayout paidreferralslayout = v.findViewById(R.id.paid_referrals_layout_dashboard);

        totalbalancelayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                utils.showDialog(getActivity(), "",
                        "This field will show you all the money you have earned so far.", "Ok", "", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        }, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        });
            }
        });
        totalwithdrawlayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                utils.showDialog(getActivity(), "",
                        "This field will show you all the money you have received so far.", "Ok", "", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        }, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        });
            }
        });
        currentbalancelayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                utils.showDialog(getActivity(), "",
                        "This field will show you the money you have right now in your account. \nYou can withdraw this money at any time.", "Ok", "", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        }, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        });
            }
        });
        accountstatuslayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                utils.showDialog(getActivity(), "",
                        "This field will show you your account status. \nYou will reach level 2 after having at least 25 paid referrals. Level 2 will give you 60% commission on every paid referral.", "Ok", "", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        }, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        });
            }
        });
        totalreferralslayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                utils.showDialog(getActivity(), "",
                        "This field will show you the amount of all the referrals", "Ok", "", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        }, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        });
            }
        });
        paidreferralslayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                utils.showDialog(getActivity(), "",
                        "This field will show you the amount of all paid referrals.", "Ok", "", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        }, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        });
            }
        });
    }

    private void getDetailsFromDatabase() {
        databaseReference.child("users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.hasChild(mAuth.getCurrentUser().getUid())) {

                    AccountDetails accountDetails = snapshot.child(mAuth.getCurrentUser().getUid()).getValue(AccountDetails.class);
                    setValuesToTextViews(accountDetails.getTotal_balance(), accountDetails.getTotal_withdraw(), accountDetails.getCurrent_balance(), accountDetails.getAccount_status());

                }
                isDone_getDetailsFromDatabase = true;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w(TAG, "onCancelled: " + error.toException());

                Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_SHORT).show();

                isDone_getDetailsFromDatabase = true;
            }
        });

    }

    private void getReferralsAmount() {

        totalReferralsSubmitted_tv.setText(utils.getStoredString(getActivity(), TOTAL_REFERRALS_AMOUNT));
        paidReferrals_tv.setText(utils.getStoredString(getActivity(), PAID_REFERRALS_AMOUNT));

        isDone_getTotalReferralsFromDatabase = true;

//        databaseReference.child("referrals").addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {


//                if (snapshot.hasChild(mAuth.getCurrentUser().getUid())) {
//
//                    // CLEARING ALL THE ITEMS
//                    refUsersList.clear();
//                    paid_membersList.clear();
//
//                    // LOOPING THROUGH ALL THE CHILDREN OF TEAM
//                    for (DataSnapshot dataSnapshot : snapshot.child(mAuth.getCurrentUser().getUid()).child("users").getChildren()) {
//
//                        refUsersList.add(dataSnapshot.getValue(refUser.class));
//
//                    }// COUNTING AMOUNT OF TEAM MEMBERS AND SETTING TO TEXT VIEW
//                    totalReferralsSubmitted_tv.setText(String.valueOf(refUsersList.size()));
//
//                    // LOOPING THROUGH THE TEAM LIST AND EXTRACTING OUT PAID MEMBERS
//                    for (int i = 0; i <= refUsersList.size() - 1; i++) {
//                        if (refUsersList.get(i).isPaid()) {
//                            paid_membersList.add(refUsersList.get(i).getEmail());
//                        }
//                    }// COUNTING THE PAID MEMBERS LIST AND SETTING THE SIZE TO TEXT VIEW
//                    paidReferrals_tv.setText(String.valueOf(paid_membersList.size()));
//
//                } else {
//                    Log.i(TAG, "onDataChange: No child exists");
//
//                    totalReferralsSubmitted_tv.setText("0");
//                    paidReferrals_tv.setText("0");
//                }
//
//                isDone_getTotalReferralsFromDatabase = true;
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//                Log.w(TAG, "onCancelled: " + error.toException());
//
//                Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_SHORT).show();
//
//                isDone_getTotalReferralsFromDatabase = true;
//            }
//        });

    }

    private void setValuesToTextViews(String total_balance, String total_withdraw, String current_balance, String account_status) {
        Log.i(TAG, "setValuesToTextViews: ");

        totalBalance_tv.setText(total_balance);
        totalWithdraw_tv.setText(total_withdraw);
        currentBalance_tv.setText(current_balance);
        accountStatus_tv.setText(account_status);
    }

    private void initViews(View v) {
        Log.i(TAG, "initViews: ");

        totalBalance_tv = v.findViewById(R.id.total_balance_tv_dashboard);
        totalWithdraw_tv = v.findViewById(R.id.total_withdraw_tv_dashboard);
        currentBalance_tv = v.findViewById(R.id.current_balance_tv_dashboard);
        accountStatus_tv = v.findViewById(R.id.account_status_tv_dashboard);
        totalReferralsSubmitted_tv = v.findViewById(R.id.team_members_tv_dashboard);
        paidReferrals_tv = v.findViewById(R.id.paid_members_tv_dashboard);

    }

    private static class AccountDetails {

        private String total_balance, current_balance, total_withdraw, account_status;

        public AccountDetails(String total_balance, String current_balance, String total_withdraw, String account_status) {
            this.total_balance = total_balance;
            this.current_balance = current_balance;
            this.total_withdraw = total_withdraw;
            this.account_status = account_status;
        }

        public String getTotal_balance() {
            return total_balance;
        }

        public void setTotal_balance(String total_balance) {
            this.total_balance = total_balance;
        }

        public String getCurrent_balance() {
            return current_balance;
        }

        public void setCurrent_balance(String current_balance) {
            this.current_balance = current_balance;
        }

        public String getTotal_withdraw() {
            return total_withdraw;
        }

        public void setTotal_withdraw(String total_withdraw) {
            this.total_withdraw = total_withdraw;
        }

        public String getAccount_status() {
            return account_status;
        }

        public void setAccount_status(String account_status) {
            this.account_status = account_status;
        }

        AccountDetails() {
        }
    }

    private static class refUser {

        private String email;
        private boolean paid;

        refUser() {

        }

        public refUser(String email, boolean paid) {
            this.email = email;
            this.paid = paid;
        }

        public boolean isPaid() {
            return paid;
        }

        public void setPaid(boolean paid) {
            this.paid = paid;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

    }

}
