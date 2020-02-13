package xyz.kymirai.translator.ui.activity;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnLongClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import xyz.kymirai.translator.AppApplication;
import xyz.kymirai.translator.bean.Data;
import xyz.kymirai.translator.R;
import xyz.kymirai.translator.R2;
import xyz.kymirai.translator.bean.Star;
import xyz.kymirai.translator.dao.StarDao;
import xyz.kymirai.translator.utills.Utils;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    @BindView(R2.id.text)
    EditText text;
    @BindView(R2.id.lv)
    ListView lv;
    @BindView(R2.id.cv)
    CardView cv;
    @BindView(R2.id.from)
    Spinner Spinner_from;
    @BindView(R2.id.to)
    Spinner Spinner_to;
    @BindView(R2.id.star)
    ImageView iv_star;
    StarDao starDao;

    ArrayList<Map<String, String>> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        initView();
    }

    @OnLongClick(R2.id.star)
    public void OnLongClick_star() {
        startActivityForResult(new Intent(this, StarActivity.class), 666);
    }

    private void initView() {
        starDao = ((AppApplication) getApplication()).getAppDatabase().starDao();
        text.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String text = s.toString();
                Star[] stars;
                Star star;
                if (!TextUtils.isEmpty(text)
                        && (stars = starDao.get(text)).length > 0
                        && (star = stars[0]).values != null
                        && star.values.get(Utils.encode(Spinner_from.getSelectedItemPosition(), Spinner_to.getSelectedItemPosition())) != null) {
                    updateByDataBase(stars[0].values);
                } else {
                    iv_star.setImageResource(R.drawable.ic_star_false);
                    lv.setAdapter(null);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        text.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                cv.setElevation(Utils.dip2px(this, 8));
            } else {
                cv.setElevation(0);
            }
        });
        text.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH || (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                hideKeyboard(v.getWindowToken());
                //text.clearFocus();
                lv.setFocusable(true);
                click_go();
            }
            return false;
        });

        Spinner_to.setOnItemSelectedListener(this);
        Spinner_from.setOnItemSelectedListener(this);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        Star[] stars;
        if ((stars = starDao.get(text.getText().toString())).length > 0) {
            updateByDataBase(stars[0].values);
        } else {
            lv.setAdapter(null);
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }

    @OnClick(R.id.star)
    public void click_star(ImageView iv) {
        String text = this.text.getText().toString();
        if (!TextUtils.isEmpty(text)) {
            Star[] stars = starDao.get(text);
            Star star = null;
            String type = Utils.encode(Spinner_from.getSelectedItemPosition(), Spinner_to.getSelectedItemPosition());
            if (stars.length > 0 && (star = stars[0]).values != null && star.values.get(type) != null) {
                star.values.put(type, null);
                starDao.insert(star);
                iv.setImageResource(R.drawable.ic_star_false);
            } else {
                if (star == null) star = new Star(text, new HashMap<>());
                iv.setImageResource(R.drawable.ic_star_true);
                ArrayList<Star.Value> arrayList = new ArrayList<>();
                for (Map<String, String> map : list) {
                    arrayList.add(new Star.Value(map.get("translation"), map.get("phonetic")));
                }
                star.values.put(Utils.encode(Spinner_from.getSelectedItemPosition(), Spinner_to.getSelectedItemPosition()), arrayList.toArray(new Star.Value[0]));
                starDao.insert(star);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 666 && data != null) {
            text.setText(data.getStringExtra("text"));
            click_go();
        }
    }

    private void updateByDataBase(HashMap<String, Star.Value[]> hashMap) {
        list = new ArrayList<>();
        Star.Value[] values;
        if (hashMap != null && (values = hashMap.get(Utils.encode(Spinner_from.getSelectedItemPosition(), Spinner_to.getSelectedItemPosition()))) != null) {
            for (Star.Value value : values) {
                Map<String, String> map = new HashMap<>();
                map.put("translation", value.text);
                if (!TextUtils.isEmpty(value.tip)) map.put("phonetic", value.tip);
                list.add(map);
            }
            iv_star.setImageResource(R.drawable.ic_star_true);
            SimpleAdapter simpleAdapter = new SimpleAdapter(MainActivity.this, list, R.layout.item, new String[]{"translation", "phonetic"}, new int[]{R.id.tv, R.id.phonetic});
            lv.setAdapter(simpleAdapter);
        } else {
            lv.setAdapter(null);
            iv_star.setImageResource(R.drawable.ic_star_false);
        }
    }

    public void click_item(View v) {
        ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData mClipData = ClipData.newPlainText("Label", ((TextView) v.findViewById(R.id.tv)).getText().toString());
        if (cm != null) cm.setPrimaryClip(mClipData);
        Toast.makeText(this, "已复制", Toast.LENGTH_SHORT).show();
    }

    @OnClick(R2.id.go)
    public void click_go() {
        String text = this.text.getText().toString();
        if (!TextUtils.isEmpty(text)) {
            Star[] stars = starDao.get(text);
            Star star;
            if (stars.length > 0 && (star = stars[0]).values != null && star.values.get(Utils.encode(Spinner_from.getSelectedItemPosition(), Spinner_to.getSelectedItemPosition())) != null) {
                updateByDataBase(stars[0].values);
            } else {
                iv_star.setImageResource(R.drawable.ic_star_false);
                Utils.getData(text, Utils.getType(Spinner_from.getSelectedItemPosition()), Utils.getType(Spinner_to.getSelectedItemPosition()), new Callback<Data>() {
                    @Override
                    public void onResponse(Call<Data> call, Response<Data> response) {
                        new Thread(() -> {
                            Data data = response.body();
                            list = new ArrayList<>();

                            //String[] l = data.l.split("2");
                            //String from = l[0], to = l[1];

                            if (data.basic != null && !TextUtils.isEmpty(data.basic.phonetic)) {
                                Map<String, String> map = new HashMap<>();
                                map.put("translation", data.query);
                                map.put("phonetic", "[" + data.basic.phonetic + "]");
                                list.add(map);
                            }

                            Iterator<String> translation = data.translation.iterator();
                            while (translation.hasNext()) {
                                Map<String, String> map = new HashMap<>();
                                map.put("translation", translation.next());
                                list.add(map);
                            }

                            if (data.web != null) {
                                Iterator<Data.web> web = data.web.iterator();
                                while (web.hasNext()) {
                                    Iterator<String> value = web.next().value.iterator();
                                    while (value.hasNext()) {
                                        Map<String, String> map = new HashMap<>();
                                        map.put("translation", value.next());
                                        list.add(map);
                                    }
                                }
                            }

                            runOnUiThread(() -> {
                                SimpleAdapter simpleAdapter = new SimpleAdapter(MainActivity.this, list, R.layout.item, new String[]{"translation", "phonetic"}, new int[]{R.id.tv, R.id.phonetic});
                                lv.setAdapter(simpleAdapter);
                            });
                        }).start();


                    }

                    @Override
                    public void onFailure(Call<Data> call, Throwable t) {
                        t.printStackTrace();
                    }
                });
            }
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if (isShouldHideKeyboard(v, ev)) {
                hideKeyboard(v.getWindowToken());
                text.clearFocus();
            }
        }
        return super.dispatchTouchEvent(ev);
    }

    private boolean isShouldHideKeyboard(View v, MotionEvent event) {
        if (v instanceof EditText) {
            int[] l = {0, 0};
            v.getLocationInWindow(l);
            int left = l[0],
                    top = l[1],
                    bottom = top + v.getHeight(),
                    right = left + v.getWidth();
            return event.getX() <= left || event.getX() >= right || event.getY() <= top || event.getY() >= bottom;
        }
        // 如果焦点不是EditText则忽略，这个发生在视图刚绘制完，第一个焦点不在EditText上，和用户用轨迹球选择其他的焦点
        return false;
    }

    private void hideKeyboard(IBinder token) {
        if (token != null) {
            InputMethodManager im = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            if (im != null) im.hideSoftInputFromWindow(token, InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }
}
