package com.krrtvl.court;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputLayout;
import com.krrtvl.court.domain.Visits;

import java.sql.Time;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VisitsActivity extends AppCompatActivity {

    boolean FirstTime = true; // маячок для попередження наступних поновлень данних
    EditText date1,btime,etime,name1;
    TextInputLayout layoutDate,layoutBtime,layoutEtime,layoutName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visits);
        setTitle(R.string.visits_title);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        date1 = findViewById(R.id.mDate);
        btime = findViewById(R.id.mBtime);
        etime = findViewById(R.id.mEtime);
        name1 = findViewById(R.id.mName);
        layoutDate = findViewById(R.id.layoutDate);
        layoutBtime = findViewById(R.id.layoutBtime);
        layoutEtime = findViewById(R.id.layoutEtime);
        layoutName = findViewById(R.id.layoutName);

        date1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            public void afterTextChanged(Editable s) {
                layoutDate.setErrorEnabled(false);
            }
        });
        btime.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            public void afterTextChanged(Editable s) {
                layoutBtime.setErrorEnabled(false);
            }
        });
        etime.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            public void afterTextChanged(Editable s) {
                layoutEtime.setErrorEnabled(false);
            }
        });
        name1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            public void afterTextChanged(Editable s) {
                layoutName.setErrorEnabled(false);
            }
        });

        final Date currentDate = new Date();
        if (!getIntent().hasExtra("id")){
            Calendar c = Calendar.getInstance(); c.setTime(currentDate);
            int dayOfWeek = c.get(Calendar.DAY_OF_WEEK);
            date1.setText(new SimpleDateFormat("dd.MM.yyyy").format(currentDate));
            if (dayOfWeek == Calendar.SATURDAY || dayOfWeek == Calendar.SUNDAY) {
                btime.setText("08:00");
                etime.setText("09:00");
            } else if (dayOfWeek == Calendar.FRIDAY) {
                btime.setText("17:00");
                etime.setText("18:00");
            } else {
                btime.setText("17:30");
                etime.setText("18:30");
            }
            FloatingActionButton Del = findViewById(R.id.Del);
            Del.hide();
        }

        Button btSave = findViewById(R.id.btSave);
        btSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                // Перевірка дати
                String vDate = "";
                final Date tDate;
                try {
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.yyyy");
                    simpleDateFormat.setLenient(false);
                    tDate = simpleDateFormat.parse(date1.getText().toString());
                    vDate = new SimpleDateFormat("yyyy-MM-dd").format(tDate);
                } catch (ParseException e) {
                    e.printStackTrace();
                    ShowError(date1, R.string.msg_err_1);
                    return;
                }
                final String vDate1 = vDate;
                Calendar c = Calendar.getInstance();
                c.setTime(currentDate);
                c.add(Calendar.DATE, -1);
                Date dtminus1 = c.getTime();
                c.add(Calendar.DATE, 11);
                Date dtplus10 = c.getTime();
                if (tDate.before(dtminus1)) {
                    ShowError(date1, R.string.msg_err_2);
                    return;
                }
                if (tDate.after(dtplus10)) {
                    ShowError(date1, R.string.msg_err_3);
                    return;
                }
                //---------------
                // Перевірка часу
                final Time tBtime, tEtime;
                try {
                    tBtime = Time.valueOf(btime.getText().toString() + ":00");
                } catch (Exception e) {
                    ShowError(btime, R.string.msg_err_9);
                    return;
                }
                try {
                    tEtime = Time.valueOf(etime.getText().toString() + ":00");
                } catch (Exception e) {
                    ShowError(etime, R.string.msg_err_9);
                    return;
                }
                if (tBtime.getTime() < 18000000 || tBtime.getTime() > 68400000) {
                    ShowError(btime, R.string.msg_err_4);
                    return;
                }
                if (tEtime.getTime() < 18000000 || tEtime.getTime() > 68400000) {
                    ShowError(etime, R.string.msg_err_4);
                    return;
                }
                if (tEtime.getTime() - tBtime.getTime() > 7200000) {
                    ShowError(etime, R.string.msg_err_5);
                    return;
                }
                if (tEtime.getTime() - tBtime.getTime() < 1800000) {
                    ShowError(etime, R.string.msg_err_6);
                    return;
                }
                NetworkService.getInstance()
                        .getJSONApi()
                        .getVisitsByDate(new SimpleDateFormat("yyyy-MM-dd").format(tDate))
                        .enqueue(new Callback<List<Visits>>() {
                            @Override
                            public void onResponse(@NonNull Call<List<Visits>> call, @NonNull Response<List<Visits>> response) {
                                for (Visits visits: response.body()) { // перевірка початкового часу
                                    if (getIntent().hasExtra("id") &&
                                            getIntent().getLongExtra("id", 0) == visits.getId()) { // Пропускаємо свій запис
                                        continue;
                                    }
                                    if (tBtime.getTime() >= visits.getBtime().getTime() && tBtime.getTime() < visits.getEtime().getTime()){
                                        ShowError(btime, R.string.msg_err_7);
                                        return;
                                    }
                                }
                                for (Visits visits: response.body()) { // перевірка кінцевого часу
                                    if (getIntent().hasExtra("id") &&
                                            getIntent().getLongExtra("id", 0) == visits.getId()) { // Пропускаємо свій запис
                                        continue;
                                    }
                                    if (tEtime.getTime() > visits.getBtime().getTime() && tEtime.getTime() <= visits.getEtime().getTime()){
                                        ShowError(etime, R.string.msg_err_7);
                                        return;
                                    }
                                }
                                // Перевірка прізвища
                                name1.setText(name1.getText().toString().trim());
                                if (name1.getText().length() < 3){
                                    ShowError(name1, R.string.msg_err_8);
                                    return;
                                }
                                final String tname = name1.getText().toString();
                                //---------------
                                // Перевірка чи не попадає на робочі часи та збереження
                                Calendar c = Calendar.getInstance(); c.setTime(tDate);
                                int dayOfWeek = c.get(Calendar.DAY_OF_WEEK);
                                if ((dayOfWeek == Calendar.FRIDAY) &&
                                    (tBtime.getTime() >= 18000000 && tBtime.getTime() < 50400000)) { // 08-00 17-00
                                    AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                                    builder.setMessage(getText(R.string.msg_wrn_1));
                                    builder.setPositiveButton("Так", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            SaveChanges(tBtime, tEtime, vDate1, tname, view);
                                        }
                                    });
                                    builder.setNegativeButton("Ні", null);
                                    builder.create().show();
                                } else if ((dayOfWeek != Calendar.SATURDAY && dayOfWeek != Calendar.SUNDAY) &&
                                    (tBtime.getTime() >= 18000000 && tBtime.getTime() < 52200000)) { // 08-00 17-30
                                        AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                                        builder.setMessage(getText(R.string.msg_wrn_1));
                                        builder.setPositiveButton("Так", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                SaveChanges(tBtime, tEtime, vDate1, tname, view);
                                            }
                                        });
                                        builder.setNegativeButton("Ні", null);
                                        builder.create().show();
                                } else {
                                    SaveChanges(tBtime, tEtime, vDate1, tname, view);
                                }
                                //---------------
                            }
                            @Override
                            public void onFailure(@NonNull Call<List<Visits>> call, @NonNull Throwable t) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                                        builder.setMessage(getText(R.string.msg_err_10));
                                        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                return;
                                            }
                                        });
                                        builder.create().show();
                                    }
                                });
                            }
                        });
                //---------------
            }
        });


        FloatingActionButton del = findViewById(R.id.Del);
        del.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                builder.setMessage("Ви дійсно бажаєте вилучити запис?");
                builder.setPositiveButton("Так", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        NetworkService.getInstance()
                                .getJSONApi()
                                .deleteVisits(getIntent().getLongExtra("id", 0))
                                .enqueue(new Callback<Long>() {
                                    @Override
                                    public void onResponse(@NonNull Call<Long> call, @NonNull Response<Long> response) {
                                        if (response.isSuccessful()) {
                                            RecyclerView recyclerView = findViewById(R.id.rv_visits);
                                            recyclerView.getAdapter().notifyDataSetChanged();
                                            finish();
                                        }
                                    }
                                    @Override
                                    public void onFailure(@NonNull Call<Long> call, @NonNull Throwable t) {
                                        AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                                        builder.setMessage(getText(R.string.msg_err_12));
                                        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                return;
                                            }
                                        });
                                        builder.create().show();
                                    }
                                });
                    }
                });
                builder.setNegativeButton("Ні", null);
                builder.create().show();
            }
        });

    }

    private void ShowError(EditText element, int msg_err_id) {
        if (element == date1) {
            layoutDate.setError(getText(msg_err_id));
            layoutDate.setErrorEnabled(true);
            date1.requestFocus();
        } else layoutDate.setErrorEnabled(false);
        if (element == btime) {
            layoutBtime.setError(getText(msg_err_id));
            layoutBtime.setErrorEnabled(true);
            btime.requestFocus();
        } else layoutBtime.setErrorEnabled(false);
        if (element == etime) {
            layoutEtime.setError(getText(msg_err_id));
            layoutEtime.setErrorEnabled(true);
            etime.requestFocus();
        } else layoutEtime.setErrorEnabled(false);
        if (element == name1) {
            layoutName.setError(getText(msg_err_id));
            layoutName.setErrorEnabled(true);
            name1.requestFocus();
        } else layoutName.setErrorEnabled(false);
        element.requestFocus();
    }

    private void SaveChanges(Time tBtime, Time tEtime, String vDate, String name, final View view) {
        String vBtime = tBtime.toString();
        String vEtime = tEtime.toString();
        NetworkService.getInstance()
                .getJSONApi()
                .putVisits(getIntent().hasExtra("id")?Long.toString(getIntent().getLongExtra("id", 0)):"",
                        vDate, vBtime, vEtime, name)
                .enqueue(new Callback<Visits>() {
                    @Override
                    public void onResponse(@NonNull Call<Visits> call, @NonNull Response<Visits> response) {
                        if (response.isSuccessful()) {
                            RecyclerView recyclerView = findViewById(R.id.rv_visits);
                            recyclerView.getAdapter().notifyDataSetChanged();
                            finish();
                        }
                    }
                    @Override
                    public void onFailure(@NonNull Call<Visits> call, @NonNull Throwable t) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                        builder.setMessage(getText(R.string.msg_err_11));
                        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                return;
                            }
                        });
                        builder.create().show();
                    }
                });
    }

    @Override
    public View onCreateView(String name, Context context, AttributeSet attrs) {
        if (FirstTime && getIntent().hasExtra("id")){
            FirstTime = false;
            NetworkService.getInstance()
                    .getJSONApi()
                    .getVisitsById(getIntent().getLongExtra("id", 0))
                    .enqueue(new Callback<Visits>() {
                        @Override
                        public void onResponse(@NonNull Call<Visits> call, @NonNull Response<Visits> response) {
                            if (response.isSuccessful()){
                                EditText date = findViewById(R.id.mDate);
                                date.setText(new SimpleDateFormat("dd.MM.yyyy").format(response.body().getDate()));
                                EditText btime = findViewById(R.id.mBtime);
                                btime.setText(DateFormat.getTimeInstance(DateFormat.SHORT).format(response.body().getBtime()));
                                EditText etime = findViewById(R.id.mEtime);
                                etime.setText(DateFormat.getTimeInstance(DateFormat.SHORT).format(response.body().getEtime()));
                                EditText name = findViewById(R.id.mName);
                                name.setText(response.body().getName());
                            }
                        }

                        @Override
                        public void onFailure(@NonNull Call<Visits> call, @NonNull Throwable t) {
                            t.printStackTrace();
                        }
                    });
        }
        return super.onCreateView(name, context, attrs);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
