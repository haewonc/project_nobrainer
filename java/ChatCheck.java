
package com.example.nobrainer;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class ChatCheck extends AppCompatActivity {

    TextView textView;
    Button button;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_check);
        Intent intent = getIntent();
        final String chatCode = intent.getStringExtra("chatCode");
        textView=findViewById(R.id.checkCode);
        button=findViewById(R.id.checkbut);
        textView.setText(chatCode);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ClipboardManager clipboardManager = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                ClipData clipData = ClipData.newPlainText("ID",chatCode);
                clipboardManager.setPrimaryClip(clipData);
                Toast.makeText(getApplicationContext(),"코드가 복사되었습니다.",Toast.LENGTH_SHORT).show();
            }
        });
        button.setOnClickListener(new Button.OnClickListener()
        {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }
}
