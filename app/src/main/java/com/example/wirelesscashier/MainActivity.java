package com.example.wirelesscashier;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import android.support.v7.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    EditText editTextcard, editTextestablishment, editTextamount;
    Button buttonsend;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        editTextcard = findViewById(R.id.editTextcard);
        editTextestablishment = findViewById(R.id.editTextestablishment);
        editTextamount = findViewById(R.id.editTextamount);
        buttonsend = findViewById(R.id.buttonsend);

        buttonsend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                consumeService();
            }
        });
    }
    public void consumeService() {
        // ahora ejecutaremos el hilo creado
        String card_id= editTextcard.getText().toString();
        String establishment= editTextestablishment.getText().toString();
        String amount= editTextamount.getText().toString();

        ServiceTask serviceTask= new ServiceTask(this,"http://192.168.1.24:8000/transaction",card_id,establishment, amount);
        serviceTask.execute();
    }
}
