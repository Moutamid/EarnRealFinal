package dev.moutamid.earnreal;

import android.app.ProgressDialog;
import android.content.DialogInterface;
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

    //private ArrayList<refUser> refUsersList = new ArrayList<>();
    private ArrayList<String> paid_membersList = new ArrayList<>();
    private TextView totalBalance_tv, totalWithdraw_tv, currentBalance_tv, accountStatus_tv;
    private TextView totalReferralsSubmitted_tv, paidReferrals_tv;
    private DatabaseReference databaseReference;

    private FirebaseAuth mAuth;
    private Utils utils = new Utils();

    //private boolean isDone_getDetailsFromDatabase = false;
    private ProgressDialog dialog;
    //private Handler handler;

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
        //handler = new Handler();
        //startCheckingBooleanValues();

        // GETTING TOTAL BALANCE, TOTAL WITHDRAW, CURRENT BALANCE,
        // ACCOUNT STATUS, TOTAL REFERRALS AMOUNT, PAID REFERRALS AMOUNT
        getDetailsFromDatabase();

        // GETTING
        //getReferralsAmount();

        // SETTING INFORMATION DIALOGS ON ALL THE LAYOUTS
        setDialogsOnAllLayouts(view);

//        // TEST DATA
//        AccountDetails details = new AccountDetails(
//                "0",
//                "0",
//                "0",
//                "Level 1",
//                "0"
//        );
//        databaseReference.child("users")
//                .child(mAuth.getCurrentUser().getUid()).setValue(details);

        return view;
    }

//    Runnable booleanCheckerRunnable = new Runnable() {
//        @Override
//        public void run() {
//            try {
//                checkBoolean();
//            } finally {
//                handler.postDelayed(booleanCheckerRunnable, 1000);
//            }
//        }
//    };

//    private void startCheckingBooleanValues() {
//        booleanCheckerRunnable.run();
//    }

//    @Override
//    public void onDestroy() {
//        super.onDestroy();
//
//        handler.removeCallbacks(booleanCheckerRunnable);
//    }

//    private void checkBoolean() {
//        if (isDone_getDetailsFromDatabase && isDone_getTotalReferralsFromDatabase) {
//
//            handler.removeCallbacks(booleanCheckerRunnable);
//            dialog.dismiss();
//        }
//    }

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
                        }, true);
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
                        }, true);
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
                        }, true);
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
                        }, true);
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
                        }, true);
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
                        }, true);
            }
        });
    }

    private void getDetailsFromDatabase() {
        databaseReference.child("users").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.hasChild(mAuth.getCurrentUser().getUid())) {

                    AccountDetails accountDetails = snapshot
                            .child(mAuth.getCurrentUser().getUid())
                            .getValue(AccountDetails.class);

                    setValuesToTextViews(
                            accountDetails.getTotal_balance(),
                            accountDetails.getTotal_withdraw(),
                            accountDetails.getCurrent_balance(),
                            accountDetails.getAccount_status(),
                            accountDetails.getPaid_referrals(),
                            accountDetails.getTotal_referrals()
                    );

                    dialog.dismiss();

                } else {

                    setValuesToTextViews(
                            "0.00",
                            "0.00",
                            "0.00",
                            "Level 1",
                            "0",
                            "0"
                    );

                    dialog.dismiss();
                }
                //isDone_getDetailsFromDatabase = true;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w(TAG, "onCancelled: " + error.toException());

                Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_SHORT).show();

                //isDone_getDetailsFromDatabase = true;
            }
        });

    }

    //private void getReferralsAmount() {

//        String totalReferrals = utils.getStoredString(getActivity(), TOTAL_REFERRALS_AMOUNT);
//        //String paidReferrals = utils.getStoredString(getActivity(), PAID_REFERRALS_AMOUNT);
//
//        if (totalReferrals.equals("Error"))
//            totalReferralsSubmitted_tv.setText("0");
//        else totalReferralsSubmitted_tv.setText(totalReferrals);
//
//        isDone_getTotalReferralsFromDatabase = true;
//
//    }

    private void setValuesToTextViews(String total_balance,
                                      String total_withdraw,
                                      String current_balance,
                                      String account_status,
                                      String paidReferrals,
                                      String totalReferrals) {
        Log.i(TAG, "setValuesToTextViews: ");

        totalBalance_tv.setText(total_balance);
        totalWithdraw_tv.setText(total_withdraw);
        currentBalance_tv.setText(current_balance);
        accountStatus_tv.setText(account_status);
        paidReferrals_tv.setText(paidReferrals);
        totalReferralsSubmitted_tv.setText(totalReferrals);
    }

    private void initViews(View v) {
        Log.i(TAG, "initViews: ");

        totalBalance_tv = v.findViewById(R.id.total_balance_tv_dashboard);
        totalWithdraw_tv = v.findViewById(R.id.total_withdraw_tv_dashboard);
        currentBalance_tv = v.findViewById(R.id.current_balance_tv_dashboard);
        accountStatus_tv = v.findViewById(R.id.account_status_tv_dashboard);
        totalReferralsSubmitted_tv = v.findViewById(R.id.total_referrals_tv_dashboard);
        paidReferrals_tv = v.findViewById(R.id.paid_members_tv_dashboard);

    }

    private static class AccountDetails {

        private String total_balance, current_balance,
                total_withdraw, account_status, paid_referrals, total_referrals;

        public AccountDetails(String totalBalance, String currentBalance, String totalWithdraw,
                              String accountStatus,
                              String paidReferrals, String totalReferrals) {

            this.total_balance = totalBalance;
            this.current_balance = currentBalance;
            this.total_withdraw = totalWithdraw;
            this.account_status = accountStatus;
            this.paid_referrals = paidReferrals;
            this.total_referrals = totalReferrals;
        }

        AccountDetails() {
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

        public String getPaid_referrals() {
            return paid_referrals;
        }

        public void setPaid_referrals(String paid_referrals) {
            this.paid_referrals = paid_referrals;
        }

        public String getTotal_referrals() {
            return total_referrals;
        }

        public void setTotal_referrals(String total_referrals) {
            this.total_referrals = total_referrals;
        }
    }

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

}
