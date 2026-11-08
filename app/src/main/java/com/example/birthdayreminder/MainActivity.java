package com.example.birthdayreminder;

import android.os.Bundle;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.birthdayreminder.R;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    private PrefsManager prefsManager;
    private EditText etName;
    private DatePicker datePicker;
    private RadioGroup rgRemindTime;
    private RadioButton rbSameDay, rbOneDayBefore, rbThreeDaysBefore;
    private Switch switchSound;
    private Button btnSave, btnClear;
    private TextView tvStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        prefsManager = new PrefsManager(this);
        etName = findViewById(R.id.etName);
        datePicker = findViewById(R.id.datePicker);
        rgRemindTime = findViewById(R.id.rgRemindTime);
        rbSameDay = findViewById(R.id.rbSameDay);
        rbOneDayBefore = findViewById(R.id.rbOneDayBefore);
        rbThreeDaysBefore = findViewById(R.id.rbThreeDaysBefore);
        switchSound = findViewById(R.id.switchSound);
        btnSave = findViewById(R.id.btnSave);
        btnClear = findViewById(R.id.btnClear);
        tvStatus = findViewById(R.id.tvStatus);

        rbOneDayBefore.setChecked(true);

        btnSave.setOnClickListener(v -> {
            String name = etName.getText().toString().trim();
            if (name.isEmpty()) {
                Toast.makeText(this, "Harap masukkan nama terlebih dahulu!", Toast.LENGTH_SHORT).show();
                return;
            }

            int day = datePicker.getDayOfMonth();
            int month = datePicker.getMonth();
            int year = datePicker.getYear();

            int daysBefore = 1;
            int checkedId = rgRemindTime.getCheckedRadioButtonId();

            if (checkedId == R.id.rbSameDay) daysBefore = 0;
            else if (checkedId == R.id.rbOneDayBefore) daysBefore = 1;
            else if (checkedId == R.id.rbThreeDaysBefore) daysBefore = 3;

            boolean isSoundOn = switchSound.isChecked();

            prefsManager.addBirthdayItem(name, day, month, year, daysBefore, isSoundOn);
            etName.setText(""); // Reset form nama setelah disimpan
            updateUI();
            Toast.makeText(this, "Berhasil menambah " + name + " ke daftar!", Toast.LENGTH_SHORT).show();
        });

        btnClear.setOnClickListener(v -> {
            prefsManager.clearAll();
            updateUI();
            Toast.makeText(this, "Semua daftar berhasil dihapus!", Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateUI();
    }

    private void updateUI() {
        JSONArray list = prefsManager.getBirthdayListJson();
        if (list.length() == 0) {
            tvStatus.setText("Belum ada data pengingat tersimpan.");
            return;
        }

        StringBuilder builder = new StringBuilder();
        try {
            for (int i = 0; i < list.length(); i++) {
                JSONObject item = list.getJSONObject(i);
                String name = item.getString("name");
                int day = item.getInt("day");
                int month = item.getInt("month") + 1;
                int year = item.getInt("year");
                int days = item.getInt("daysBefore");
                boolean isSoundOn = item.getBoolean("isSoundEnabled");

                String timeText = (days == 0) ? "Hari H" : "H-" + days;
                String soundText = isSoundOn ? "Bersuara" : "Tanpa Suara";

                builder.append(i + 1).append(". ").append(name)
                        .append("\n   Tgl: ").append(day).append("/").append(month).append("/").append(year)
                        .append(" | ").append(timeText)
                        .append(" | ").append(soundText)
                        .append("\n\n");
            }
            tvStatus.setText(builder.toString());
        } catch (JSONException e) {
            e.printStackTrace();
            tvStatus.setText("Gagal memuat daftar pengingat.");
        }
    }
}