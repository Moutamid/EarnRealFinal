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

public class FragmentDashboard extends Fragment {
    private static final String TAG = "FragmentDashboard";

    private TextView totalBalance_tv, totalWithdraw_tv, currentBalance_tv;
    private TextView totalReferralsSubmitted_tv, paidReferrals_tv;
    private DatabaseReference databaseReference;

    private FirebaseAuth mAuth;
    private Utils utils = new Utils();

    private ProgressDialog dialog;

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

        // GETTING TOTAL BALANCE, TOTAL WITHDRAW, CURRENT BALANCE,
        // ACCOUNT STATUS, TOTAL REFERRALS AMOUNT, PAID REFERRALS AMOUNT
        getDetailsFromDatabase();

        // SETTING INFORMATION DIALOGS ON ALL THE LAYOUTS
        setDialogsOnAllLayouts(view);

        return view;
    }


    private void setDialogsOnAllLayouts(View v) {
        // TODO: Update messages below
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
                        "This field will show you all the money you have earned so far.",
                        "Ok", "", new DialogInterface.OnClickListener() {
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
                        "This field will show you all the money you have received so far.",
                        "Ok", "", new DialogInterface.OnClickListener() {
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
                        "This field will show you the money you have right now in your account. " +
                                "\nYou can withdraw this money at any time.", "Ok", "", new DialogInterface.OnClickListener() {
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
                        "This field will show you your account status. " +
                                "\nYou will reach level 2 after having at least 25 paid referrals. " +
                                "Level 2 will add you to our communicators list and will provide" +
                                "balance facility.", "Ok", "", new DialogInterface.OnClickListener() {
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
                        "This field will show you the amount of all the referrals you've submitted so far.", "Ok", "", new DialogInterface.OnClickListener() {
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
        databaseReference.child("users").child(mAuth.getCurrentUser().getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        if (snapshot.exists()) {

                            AccountDetails accountDetails = snapshot
                                    .getValue(AccountDetails.class);
                            //long counter = (long) snapshot.child("total_referrals").getValue();
                            setValuesToTextViews(
                                    accountDetails.getTotalBalance(),
                                    accountDetails.getTotalWithdraw(),
                                    accountDetails.getCurrentBalance(),
                                    //accountDetails.getAccount_status(),
                                    accountDetails.getPaidReferrals()
                            );

                            getTotalReferralsAmount();

                        } else {

                            setValuesToTextViews(
                                    0,
                                    0,
                                    0,
                                    0
                            );
                            totalReferralsSubmitted_tv.setText("0");

                            dialog.dismiss();

                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.w(TAG, "onCancelled: " + error.toException());

                        Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                        //isDone_getDetailsFromDatabase = true;
                    }
                });

    }

    private void getTotalReferralsAmount() {
        databaseReference.child("referrals").child(mAuth.getCurrentUser().getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {

                            long count = snapshot.getChildrenCount();

                            totalReferralsSubmitted_tv.setText(String.valueOf(count));

                        } else totalReferralsSubmitted_tv.setText("0");

                        dialog.dismiss();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.w(TAG, "onCancelled: database referrals exception", error.toException());

                        Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    }
                });

    }

    private void setValuesToTextViews(long total_balance,
                                      long total_withdraw,
                                      long current_balance,
                                      long paidReferrals) {//String account_status,
        Log.i(TAG, "setValuesToTextViews: ");

        totalBalance_tv.setText(String.valueOf(total_balance));
        totalWithdraw_tv.setText(String.valueOf(total_withdraw));
        currentBalance_tv.setText(String.valueOf(current_balance));
        //accountStatus_tv.setText(account_status);
        paidReferrals_tv.setText(String.valueOf(paidReferrals));

    }

    private void initViews(View v) {
        Log.i(TAG, "initViews: ");

        totalBalance_tv = v.findViewById(R.id.total_balance_tv_dashboard);
        totalWithdraw_tv = v.findViewById(R.id.total_withdraw_tv_dashboard);
        currentBalance_tv = v.findViewById(R.id.current_balance_tv_dashboard);
        //accountStatus_tv = v.findViewById(R.id.account_status_level_tv_dashboard);
        totalReferralsSubmitted_tv = v.findViewById(R.id.total_referrals_tv_dashboard);
        paidReferrals_tv = v.findViewById(R.id.paid_members_tv_dashboard);

    }

    private static class AccountDetails {

        private long totalBalance, currentBalance,
                totalWithdraw, paidReferrals;

        public AccountDetails(long totalBalance, long currentBalance, long totalWithdraw, long paidReferrals) {
            this.totalBalance = totalBalance;
            this.currentBalance = currentBalance;
            this.totalWithdraw = totalWithdraw;
            this.paidReferrals = paidReferrals;
        }

        public long getTotalBalance() {
            return totalBalance;
        }

        public void setTotalBalance(long totalBalance) {
            this.totalBalance = totalBalance;
        }

        public long getCurrentBalance() {
            return currentBalance;
        }

        public void setCurrentBalance(long currentBalance) {
            this.currentBalance = currentBalance;
        }

        public long getTotalWithdraw() {
            return totalWithdraw;
        }

        public void setTotalWithdraw(long totalWithdraw) {
            this.totalWithdraw = totalWithdraw;
        }

        public long getPaidReferrals() {
            return paidReferrals;
        }

        public void setPaidReferrals(long paidReferrals) {
            this.paidReferrals = paidReferrals;
        }

        AccountDetails() {
        }
    }

}
