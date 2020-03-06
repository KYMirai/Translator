package xyz.kymirai.translator.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import xyz.kymirai.translator.AppApplication;
import xyz.kymirai.translator.GSSwipeRefreshLayout;
import xyz.kymirai.translator.LeftAndRightDeleteListView;
import xyz.kymirai.translator.R;
import xyz.kymirai.translator.R2;
import xyz.kymirai.translator.bean.Star;
import xyz.kymirai.translator.dao.StarDao;
import xyz.kymirai.translator.utills.Language;

public class StarActivity extends AppCompatActivity {
    @BindView(R2.id.lv_star)
    LeftAndRightDeleteListView lv;
    @BindView(R2.id.refresh)
    GSSwipeRefreshLayout swipeRefreshLayout;
    StarDao starDao;
    Star[] stars;
    int now = 0;
    ArrayList<Map<String, String>> list;
    SimpleAdapter simpleAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_star);
        ButterKnife.bind(this);

        load();
        init();
    }

    private void init() {
        swipeRefreshLayout.setOnRefreshListener(() -> {
            load();
            swipeRefreshLayout.setRefreshing(false);
        });
        lv.setOnScrollChangeListener((v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
            check();
        });
        lv.setRemoveListener((LeftAndRightDeleteListView.RemoveDirection direction, int position) -> {

                }
        );
        lv.setOnItemClickListener((AdapterView<?> parent, View view, int position, long id) -> {
            setResult(666, new Intent()
                    .putExtra("text", ((TextView) view.findViewById(R.id.tv)).getText())
                    .putExtra("type", ((TextView) view.findViewById(R.id.phonetic)).getText()));
            finish();
        });
    }

    private void load() {
        starDao = ((AppApplication) getApplication()).getAppDatabase().starDao();
        stars = starDao.getAll();
        list = new ArrayList<>();
        for (now = 0; now < Math.min(stars.length, 10); now++) {
            Star star = stars[now];
            for (String s : star.values.keySet()) {
                Map<String, String> map = new HashMap<>();
                map.put("translation", star.text);
                map.put("phonetic", Language.decode(s));
                list.add(map);
            }
        }

        simpleAdapter = new SimpleAdapter(this, list, R.layout.item_star, new String[]{"translation", "phonetic"}, new int[]{R.id.tv, R.id.phonetic});
        lv.setAdapter(simpleAdapter);
        check();
    }

    private void check() {
        if (lv.getLastVisiblePosition() + 1 == lv.getChildCount() && stars != null && stars.length > 0 && now < stars.length && list != null) {
            for (int childCount = now + 1; now < Math.min(stars.length, childCount + 10); now++) {
                Star star = stars[now];
                for (String s : star.values.keySet()) {
                    Map<String, String> map = new HashMap<>();
                    map.put("translation", star.text);
                    map.put("phonetic", Language.decode(s));
                    list.add(map);
                }
            }
            simpleAdapter.notifyDataSetChanged();
            check();
        }
    }
}
