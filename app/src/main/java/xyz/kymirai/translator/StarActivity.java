package xyz.kymirai.translator;

import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class StarActivity extends AppCompatActivity {
    @BindView(R2.id.lv_star)
    ListView lv;
    @BindView(R2.id.refresh)
    SwipeRefreshLayout swipeRefreshLayout;
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

        init();
        load();
    }

    private void init() {
        swipeRefreshLayout.setOnRefreshListener(() -> {
            load();
            swipeRefreshLayout.setRefreshing(false);
        });
        lv.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if ((firstVisibleItem + visibleItemCount) >= totalItemCount) {
                    if (stars != null && stars.length > 0 && list != null) {
                        int ii = now;
                        for (; now < (stars.length - ii > 10 ? ii + 10 : stars.length); now++) {
                            Map<String, String> map = new HashMap<>();
                            map.put("translation", stars[now].text);
                            list.add(map);
                            simpleAdapter.notifyDataSetChanged();
                        }
                    }
                }
            }
        });
    }

    private void load() {
        starDao = ((AppApplication) getApplication()).getAppDatabase().starDao();
        stars = starDao.getAll();
        list = new ArrayList<>();
        for (now = 0; now < (stars.length > 10 ? 10 : stars.length); now++) {
            Map<String, String> map = new HashMap<>();
            map.put("translation", stars[now].text);
            list.add(map);
        }
        simpleAdapter = new SimpleAdapter(this, list, R.layout.item, new String[]{"translation", "phonetic"}, new int[]{R.id.tv, R.id.phonetic});
        lv.setAdapter(simpleAdapter);
    }


    public void click_item(View v) {
        setResult(666, new Intent().putExtra("text", ((TextView) v.findViewById(R.id.tv)).getText()));
        finish();
    }
}
