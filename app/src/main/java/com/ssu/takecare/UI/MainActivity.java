package com.ssu.takecare.UI;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.ssu.takecare.ApplicationClass;
import com.ssu.takecare.Dialog.PressureDialog;
import com.ssu.takecare.Dialog.SugarDialog;
import com.ssu.takecare.Dialog.WeightDialog;
import com.ssu.takecare.Fragment.HomeFragment;
import com.ssu.takecare.Fragment.MyPageFragment;
import com.ssu.takecare.Fragment.RoleCaredFragment;
import com.ssu.takecare.Fragment.ShareFragment;
import com.ssu.takecare.R;
import com.ssu.takecare.Retrofit.Match.DataResponseCare;
import com.ssu.takecare.Retrofit.Match.ResponseCare;
import com.ssu.takecare.Retrofit.RetrofitCallback;
import com.ssu.takecare.Retrofit.RetrofitCustomCallback.RetrofitCareCallback;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private long backKeyPressedTime = 0;

    private ImageButton tab_btn1, tab_btn2, tab_btn3;
    private TextView hp_input, lp_input;
    private TextView bs_input, as_input;
    private TextView w_input;

    private String ROLE_CARED_OR_ROLE_CARER;

    SharedPreferences.Editor editor = ApplicationClass.sharedPreferences.edit();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ROLE_CARED_OR_ROLE_CARER = ApplicationClass.sharedPreferences.getString("role", "");

        tab_btn1 = findViewById(R.id.tab_btn1);
        tab_btn2 = findViewById(R.id.tab_btn2);
        tab_btn3 = findViewById(R.id.tab_btn3);

        getSupportFragmentManager().beginTransaction().replace(R.id.home_fragment, new HomeFragment()).commit();
    }

    @Override
    public void onBackPressed() {
        // 2초 이내에 뒤로가기 버튼을 한번 더 클릭시 앱 종료
        if (System.currentTimeMillis() > backKeyPressedTime + 2000) {
            backKeyPressedTime = System.currentTimeMillis();
            Toast.makeText(this, "\'뒤로\' 버튼을 한번 더 누르시면 종료됩니다.", Toast.LENGTH_SHORT).show();
        }
        else {
            moveTaskToBack(true); // 태스크를 백그라운드로 이동
            finishAndRemoveTask(); // 액티비티 종료 + 태스크 리스트에서 지우기
            System.exit(0);
        }
    }

    public void inputPressure(View view) {
        PressureDialog pDialog = new PressureDialog(this);
        pDialog.setPressureDialogListener(new PressureDialog.PressureDialogListener() {
            @Override
            public void okClicked(String high_pressure, String low_pressure) {
                hp_input = findViewById(R.id.input_high_pressure);
                lp_input = findViewById(R.id.input_low_pressure);

                if (!high_pressure.equals(""))
                    hp_input.setText(high_pressure);

                if (!low_pressure.equals(""))
                    lp_input.setText(low_pressure);
            }
        });

        pDialog.show();
    }

    public void inputSugar(View view) {
        SugarDialog sDialog = new SugarDialog(this);
        sDialog.setSugarDialogListener(new SugarDialog.SugarDialogListener() {
            @Override
            public void okClicked(String before_sugar, String after_sugar) {
                bs_input = findViewById(R.id.input_before_sugar);
                as_input = findViewById(R.id.input_after_sugar);

                if (!before_sugar.equals(""))
                    bs_input.setText(before_sugar);

                if (!after_sugar.equals(""))
                    as_input.setText(after_sugar);
            }
        });

        sDialog.show();
    }

    public void inputWeight(View view) {
        WeightDialog wDialog = new WeightDialog(this);
        wDialog.setWeightDialogListener(new WeightDialog.WeightDialogListener() {
            @Override
            public void okClicked(String weight) {
                w_input = findViewById(R.id.input_weight);

                if (!weight.equals(""))
                    w_input.setText(weight);
            }
        });

        wDialog.show();
    }

    public void makeReport(View view) {
        hp_input = findViewById(R.id.input_high_pressure);
        lp_input = findViewById(R.id.input_low_pressure);
        bs_input = findViewById(R.id.input_before_sugar);
        as_input = findViewById(R.id.input_after_sugar);
        w_input = findViewById(R.id.input_weight);

        int systolic = 0;
        int diastolic = 0;
        List<Integer> sugarLevels = new ArrayList<>();
        int weight = 0;

        if (!hp_input.getText().toString().equals("____")) {
            systolic = Integer.parseInt(hp_input.getText().toString());
        }

        if (!lp_input.getText().toString().equals("____")) {
            diastolic = Integer.parseInt(lp_input.getText().toString());
        }

        if (!bs_input.getText().toString().equals("____")) {
            sugarLevels.add(Integer.parseInt(bs_input.getText().toString()));
        }

        if (!as_input.getText().toString().equals("____")) {
            sugarLevels.add(Integer.parseInt(as_input.getText().toString()));
        }

        if (!w_input.getText().toString().equals("____")) {
            weight = Integer.parseInt(w_input.getText().toString());
        }

        ApplicationClass.retrofit_manager.makeReport(systolic, diastolic, sugarLevels, weight, new RetrofitCallback() {
            @Override
            public void onError(Throwable t) {

            }

            @Override
            public void onSuccess(String message, String token) {
                String str = "____";
                hp_input.setText(str);
                lp_input.setText(str);
                bs_input.setText(str);
                as_input.setText(str);
                w_input.setText(str);
            }

            @Override
            public void onFailure(int error_code) {

            }
        });
    }

    public void logout(View view) {
        clearInfo();

        finish();
        startActivity(new Intent(getApplicationContext(), LoginActivity.class));
    }

    public void clearInfo() {
        editor.putString("email_login", "");
        editor.putString("password_login", "");
        editor.putString("accessToken", "");
        editor.putInt("userId", 0);
        editor.putString("name", "");
        editor.putString("gender", "");
        editor.putInt("age", 0);
        editor.putInt("height", 0);
        editor.putString("role", "");

        editor.apply();
    }

    public void click_event(View view) {
        switch (view.getId()) {
            case R.id.tab_btn1:
                getSupportFragmentManager().beginTransaction().replace(R.id.home_fragment, new HomeFragment()).commit();
                tab_btn1.setImageResource(R.drawable.tab_btn1_select);
                tab_btn2.setImageResource(R.drawable.tab_btn2);
                tab_btn3.setImageResource(R.drawable.tab_btn3);
                break;
            case R.id.tab_btn2:
                if (ROLE_CARED_OR_ROLE_CARER.equals("보호자")) {
                    ApplicationClass.retrofit_manager.getCareDBMatchInfo(new RetrofitCareCallback() {
                        @Override
                        public void onError(Throwable t) {
                            Log.d(TAG, "에러 : " + t);
                        }

                        @Override
                        public void onSuccess(String message, ResponseCare data) {
                            List<String> UserName = new ArrayList<>();
                            List<Integer> UserId = new ArrayList<>();

                            List<DataResponseCare> list = data.getData();
                            Log.d(TAG,"맵핑된 수 : " + list.size());

                            editor.putInt("Mapping_Count", list.size());
                            editor.apply();

                            for (int i = 0; i < list.size(); i++){
                                Log.d(TAG,"i : " + i + ", status : " + list.get(i).getStatus());

                                if (list.get(i).getStatus().equals("ACCEPTED")) {
                                    UserName.add(list.get(i).getUserName());
                                    UserId.add(list.get(i).getUserId());
                                    Log.d(TAG, "userName : " + list.get(i).getUserName() + ", userId : " + list.get(i).getUserId());
                                }
                            }

                            getSupportFragmentManager().beginTransaction().replace(R.id.home_fragment, new ShareFragment(UserName, UserId)).commit();
                        }

                        @Override
                        public void onFailure(int error_code) {
                            Log.d(TAG, "실패 : " + error_code);
                        }
                    });
                }
                else {
                    ApplicationClass.retrofit_manager.getCareDBMatchInfo(new RetrofitCareCallback() {
                        @Override
                        public void onError(Throwable t) {
                            Log.d(TAG, "에러 : " + t);
                        }

                        @Override
                        public void onSuccess(String message, ResponseCare data) {
                            List<String> UserName = new ArrayList<>();
                            List<Integer> UserId = new ArrayList<>();

                            List<DataResponseCare> list = data.getData();
                            Log.d(TAG,"맵핑된 수 : " + list.size());

                            editor.putInt("Mapping_Count", list.size());
                            editor.apply();

                            for (int i = 0; i < list.size(); i++){
                                Log.d(TAG,"i : " + i + ", status : " + list.get(i).getStatus());

                                if (list.get(i).getStatus().equals("ACCEPTED")) {
                                    UserName.add(list.get(i).getUserName());
                                    UserId.add(list.get(i).getUserId());
                                    Log.d(TAG, "userName : " + list.get(i).getUserName() + ", userId : " + list.get(i).getUserId());
                                }
                            }

                            getSupportFragmentManager().beginTransaction().replace(R.id.home_fragment, new RoleCaredFragment(UserName, UserId)).commit();
                        }

                        @Override
                        public void onFailure(int error_code) {
                            Log.d(TAG, "실패 : " + error_code);
                        }
                    });
                }

                tab_btn1.setImageResource(R.drawable.tab_btn1);
                tab_btn2.setImageResource(R.drawable.tab_btn2_select);
                tab_btn3.setImageResource(R.drawable.tab_btn3);
                break;
            case R.id.tab_btn3:
                getSupportFragmentManager().beginTransaction().replace(R.id.home_fragment, new MyPageFragment()).commit();
                tab_btn1.setImageResource(R.drawable.tab_btn1);
                tab_btn2.setImageResource(R.drawable.tab_btn2);
                tab_btn3.setImageResource(R.drawable.tab_btn3_select);
                break;
            default:
                break;
        }
    }
}