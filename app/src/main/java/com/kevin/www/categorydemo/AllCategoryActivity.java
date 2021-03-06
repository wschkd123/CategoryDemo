package com.kevin.www.categorydemo;

import android.content.Context;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;

import com.alibaba.fastjson.JSONObject;
import com.facebook.drawee.backends.pipeline.Fresco;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * author：wangzihang
 * date： 2017/8/8 19:15
 * desctiption：category demo
 * e-mail：wangzihang@xiaohongchun.com
 */
public class AllCategoryActivity extends AppCompatActivity {

    private List<String> menuList = new ArrayList<>();
    private List<CategoryBean.DataBean> homeList = new ArrayList<>();
    private List<Integer> showTitle;

    private ListView lv_menu;
    private ListView lv_home;

    private MenuCategoryAdapter mMenuCategoryAdapter;
    private TopCategoryAdapter mTopCategoryAdapter;
    private int currentItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_category);
        Fresco.initialize(this);
        initView();
        loadData();
    }


    private void loadData() {

        String json = getJson(this, "category.json");
        CategoryBean categoryBean = JSONObject.parseObject(json, CategoryBean.class);
        showTitle = new ArrayList<>();
        for (int i = 0; i < categoryBean.getData().size(); i++) {
            CategoryBean.DataBean dataBean = categoryBean.getData().get(i);
            menuList.add(dataBean.getModuleTitle());
            showTitle.add(i);
            homeList.add(dataBean);
        }

        mMenuCategoryAdapter.notifyDataSetChanged();
        mTopCategoryAdapter.notifyDataSetChanged();
    }

    private void initView() {
        lv_menu = (ListView) findViewById(R.id.lv_menu);
        lv_home = (ListView) findViewById(R.id.lv_home);
        mMenuCategoryAdapter = new MenuCategoryAdapter(this, menuList);
        lv_menu.setAdapter(mMenuCategoryAdapter);

        mTopCategoryAdapter = new TopCategoryAdapter(this, homeList);
        lv_home.setAdapter(mTopCategoryAdapter);

        lv_menu.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mMenuCategoryAdapter.setSelectItem(position);
                mMenuCategoryAdapter.notifyDataSetInvalidated();
                lv_home.setSelection(showTitle.get(position));
            }
        });


        lv_home.setOnScrollListener(new AbsListView.OnScrollListener() {
            private int scrollState;

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                this.scrollState = scrollState;
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem,
                                 int visibleItemCount, int totalItemCount) {
                if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {
                    return;
                }
                int current = showTitle.indexOf(firstVisibleItem);
//				lv_home.setSelection(current);
                if (currentItem != current && current >= 0) {
                    currentItem = current;
                    mMenuCategoryAdapter.setSelectItem(currentItem);
                    mMenuCategoryAdapter.notifyDataSetInvalidated();
                }
            }
        });
    }

    /**
     * 得到json文件中的内容
     *
     * @param context
     * @param fileName
     * @return
     */
    public static String getJson(Context context, String fileName) {
        StringBuilder stringBuilder = new StringBuilder();
        //获得assets资源管理器
        AssetManager assetManager = context.getAssets();
        //使用IO流读取json文件内容
        try {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(
                    assetManager.open(fileName), "utf-8"));
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return stringBuilder.toString();
    }


}
