package com.example.hd;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.text.InputType;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import com.github.mikephil.charting.charts.*;
import com.github.mikephil.charting.data.*;
import com.github.mikephil.charting.components.*;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.*;

import java.text.SimpleDateFormat;
import java.util.*;

public class CheckInActivity extends AppCompatActivity {

    private ImageView imgAvatar;
    private TextView tvNickname, tvGoalSummary, tvGoalTitle,tvStartDate, tvPercent, tvProgress, btnEdit;
    private PieChart circleChart;
    private LineChart lineChart;
    private Button btnCheckIn, btnShare,btnBack;

    private FirebaseFirestore db;
    private String uid;
    private String username;
    private Map<String, Object> checkinGoal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkin);

        imgAvatar = findViewById(R.id.imgAvatar);
        tvNickname = findViewById(R.id.tvNickname);
        tvGoalSummary = findViewById(R.id.tvGoalSummary);
        tvStartDate = findViewById(R.id.tvStartDate);
        tvPercent = findViewById(R.id.tvPercent);
        tvProgress = findViewById(R.id.tvProgress);
        btnEdit = findViewById(R.id.btnEdit);
        circleChart = findViewById(R.id.circleChart);
        lineChart = findViewById(R.id.lineChart);
        btnCheckIn = findViewById(R.id.btnCheckIn);
        btnShare = findViewById(R.id.btnShare);
        btnBack = findViewById(R.id.btnBack);
        tvGoalTitle = findViewById(R.id.tvGoalTitle);


        db = FirebaseFirestore.getInstance();
        uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        loadUserAndGoal();

        btnEdit.setOnClickListener(v -> showEditDialog());

        btnCheckIn.setOnClickListener(v -> doCheckIn());

        btnShare.setOnClickListener(v -> {
            String shareMsg = tvNickname.getText() + " Successfully " + tvProgress.getText() + " · " + tvGoalSummary.getText();
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_TEXT, shareMsg);
            startActivity(Intent.createChooser(shareIntent, "Share via"));
        });

        btnBack.setOnClickListener(v -> {
            startActivity(new Intent(this, HomeActivity.class));
            finish();
        });
    }

    private void loadUserAndGoal() {
        db.collection("users").document(uid).get()
                .addOnSuccessListener(doc -> {
                    username = doc.getString("username");
                    tvNickname.setText(username != null ? username : "User");

                    Map<String, Object> goal = (Map<String, Object>) doc.get("checkin_goal");
                    if (goal == null) {
                        goal = getDefaultGoal();
                        db.collection("users").document(uid).update("checkin_goal", goal);
                    }
                    checkinGoal = goal;
                    renderGoal();
                });
    }

    private Map<String, Object> getDefaultGoal() {
        Map<String, Object> goal = new HashMap<>();
        goal.put("avatar", "avatar_default");
        goal.put("theme", "30 day running challenge");
        goal.put("totalDays", 30L);
        goal.put("currentDay", 0L);
        goal.put("startDate", new SimpleDateFormat("yyyy-MM-dd", Locale.US).format(new Date()));
        goal.put("entries", new ArrayList<Map<String, Object>>());
        return goal;
    }

    private void renderGoal() {
        String avatar = (String) checkinGoal.get("avatar");
        int resId = getResources().getIdentifier(avatar, "drawable", getPackageName());
        imgAvatar.setImageResource(resId == 0 ? R.drawable.avatar_default : resId);

        String theme = (String) checkinGoal.get("theme");
        tvGoalSummary.setText(theme);
        tvGoalTitle.setText("🏃 " + theme);


        String startDate = (String) checkinGoal.get("startDate");
        tvStartDate.setText("Started: " + (startDate == null ? "-" : startDate));

        int total = ((Number) checkinGoal.get("totalDays")).intValue();
        int done = ((Number) checkinGoal.get("currentDay")).intValue();
        float percent = total > 0 ? done * 100f / total : 0f;
        tvPercent.setText(String.format(Locale.US, "%.0f%%", percent));
        tvProgress.setText(done + " / " + total + " days");

        setCircleChart(done, total);
        setLineChart((List<Map<String, Object>>) checkinGoal.get("entries"));
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> entries = (List<Map<String, Object>>) checkinGoal.get("entries");

        if (isTodayChecked(entries)) {
            btnCheckIn.setEnabled(false);
            btnCheckIn.setText("✔ Checked In");
            btnCheckIn.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#B0BEC5")));
        } else {
            btnCheckIn.setEnabled(true);
            btnCheckIn.setText("Check In");
            btnCheckIn.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#0D3B66")));
        }

    }

    private void doCheckIn() {
        List<Map<String, Object>> entries = (List<Map<String, Object>>) checkinGoal.get("entries");
        String today = new SimpleDateFormat("yyyy-MM-dd", Locale.US).format(new Date());

        if (!isTodayChecked(entries)) {
            Map<String, Object> entry = new HashMap<>();
            entry.put("day", entries.size() + 1);
            entry.put("date", today);
            entry.put("done", true);
            entries.add(entry);

            checkinGoal.put("currentDay", ((Long) checkinGoal.get("currentDay")).intValue() + 1);
            checkinGoal.put("entries", entries);

            db.collection("users").document(uid).update("checkin_goal", checkinGoal)
                    .addOnSuccessListener(unused -> {
                        Toast.makeText(this, "✅ Check-in successful", Toast.LENGTH_SHORT).show();
                        renderGoal();
                    });
        }
    }

    private boolean isTodayChecked(List<Map<String, Object>> entries) {
        String today = new SimpleDateFormat("yyyy-MM-dd", Locale.US).format(new Date());
        for (Map<String, Object> entry : entries) {
            if (today.equals(entry.get("date"))) return true;
        }
        return false;
    }

    private void showEditDialog() {
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(40, 30, 40, 10);

        EditText inputName = new EditText(this);
        inputName.setHint("Nickname");
        inputName.setText(tvNickname.getText());
        layout.addView(inputName);

        EditText inputTheme = new EditText(this);
        inputTheme.setHint("Challenge Theme");
        inputTheme.setText((String) checkinGoal.get("theme"));
        layout.addView(inputTheme);

        EditText inputDays = new EditText(this);
        inputDays.setHint("Total Days");
        inputDays.setInputType(InputType.TYPE_CLASS_NUMBER);
        inputDays.setText(String.valueOf(checkinGoal.get("totalDays")));
        layout.addView(inputDays);

        new AlertDialog.Builder(this)
                .setTitle("Edit Profile & Goal")
                .setView(layout)
                .setPositiveButton("Save", (dialog, which) -> {
                    String newName = inputName.getText().toString().trim();
                    String newTheme = inputTheme.getText().toString().trim();
                    String newDaysStr = inputDays.getText().toString().trim();

                    int newDays = 30;
                    try {
                        newDays = Integer.parseInt(newDaysStr);
                    } catch (Exception e) { }

                    if (!newName.isEmpty()) {
                        db.collection("users").document(uid)
                                .update("username", newName)
                                .addOnSuccessListener(unused -> tvNickname.setText(newName));
                    }

                    if (!newTheme.isEmpty() && newDays > 0) {
                        checkinGoal.put("theme", newTheme);
                        checkinGoal.put("totalDays", newDays);
                        db.collection("users").document(uid)
                                .update("checkin_goal", checkinGoal)
                                .addOnSuccessListener(unused -> {
                                    Toast.makeText(this, "updated", Toast.LENGTH_SHORT).show();
                                    renderGoal();
                                });
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }


    private void setCircleChart(int done, int total) {
        List<PieEntry> entries = new ArrayList<>();
        entries.add(new PieEntry(done, "Done"));
        entries.add(new PieEntry(Math.max(total - done, 0), "Left"));

        PieDataSet ds = new PieDataSet(entries, "");
        ds.setColors(Color.parseColor("#0D3B66"), Color.parseColor("#E4EAF2"));
        ds.setDrawValues(false);

        circleChart.setUsePercentValues(false);
        circleChart.setDrawHoleEnabled(true);
        circleChart.setHoleRadius(72f);
        circleChart.setTransparentCircleRadius(0f);
        circleChart.getDescription().setEnabled(false);
        circleChart.getLegend().setEnabled(false);
        circleChart.setData(new PieData(ds));
        circleChart.invalidate();
    }

    private void setLineChart(List<Map<String, Object>> entries) {
        int[] week = new int[7];
        Calendar cal = Calendar.getInstance();
        for (Map<String, Object> entry : entries) {
            try {
                String dateStr = (String) entry.get("date");
                Date date = new SimpleDateFormat("yyyy-MM-dd", Locale.US).parse(dateStr);
                cal.setTime(date);
                int idx = (cal.get(Calendar.DAY_OF_WEEK) + 5) % 7;
                week[idx]++;
            } catch (Exception ignore) {}
        }

        List<Entry> line = new ArrayList<>();
        for (int i = 0; i < 7; i++) line.add(new Entry(i, week[i]));

        LineDataSet ds = new LineDataSet(line, "");
        ds.setColor(Color.parseColor("#1B7CFF"));
        ds.setCircleColor(Color.parseColor("#1B7CFF"));
        ds.setLineWidth(2f);
        ds.setCircleRadius(4f);
        ds.setValueTextSize(0f);
        ds.setMode(LineDataSet.Mode.CUBIC_BEZIER);

        lineChart.setData(new LineData(ds));
        lineChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(
                Arrays.asList("M", "T", "W", "T", "F", "S", "S")
        ));
        lineChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        lineChart.getXAxis().setGranularity(1f);
        lineChart.getXAxis().setLabelCount(7, true);
        lineChart.getDescription().setEnabled(false);
        lineChart.getLegend().setEnabled(false);
        lineChart.setPinchZoom(true);
        lineChart.setScaleEnabled(true);
        lineChart.invalidate();
    }
}
