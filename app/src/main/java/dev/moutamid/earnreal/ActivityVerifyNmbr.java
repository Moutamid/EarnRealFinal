package dev.moutamid.earnreal;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

public class ActivityVerifyNmbr extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_nmbr);
        findViewById(R.id.verify_btn_otp_activity_verify).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ActivityVerifyNmbr.this, MainActivity.class));
            }
        });
    }
}
