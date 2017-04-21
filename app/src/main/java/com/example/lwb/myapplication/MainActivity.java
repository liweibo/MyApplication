package com.example.lwb.myapplication;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.cjj.MaterialRefreshLayout;
import com.cjj.MaterialRefreshListener;
import com.daimajia.slider.library.SliderLayout;
import com.example.lwb.myapplication.adapter.BaseAdapter;
import com.example.lwb.myapplication.adapter.CategoryAdapter;
import com.example.lwb.myapplication.adapter.WaresAdapter;
import com.example.lwb.myapplication.bean.Category;
import com.example.lwb.myapplication.bean.Page;
import com.example.lwb.myapplication.bean.Wares;
import com.example.lwb.myapplication.http.BaseCallback;
import com.example.lwb.myapplication.http.OkHttpHelper;
import com.example.lwb.myapplication.http.SpotsCallBack;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    @ViewInject(R.id.recyclerview_category)
    private RecyclerView mRecyclerView;


    @ViewInject(R.id.recyclerview_wares)
    private RecyclerView mRecyclerviewWares;

    @ViewInject(R.id.refresh_layout)
    private MaterialRefreshLayout mRefreshLaout;

    @ViewInject(R.id.slider)
    private SliderLayout mSliderLayout;

    private CategoryAdapter mCategoryAdapter;
    private WaresAdapter mWaresAdatper;


    private OkHttpHelper mHttpHelper = OkHttpHelper.getInstance();


    private int currPage=1;
    private int totalPage=1;
    private int pageSize=10;
    private long category_id=0;


    private  static final int STATE_NORMAL=0;
    private  static final int STATE_REFREH=1;
    private  static final int STATE_MORE=2;

    private int state=STATE_NORMAL;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ViewUtils.inject(this);


        requestCategoryData();
        requestBannerData();

        initRefreshLayout();

    }

    private void initRefreshLayout() {
        mRefreshLaout.setLoadMore(true);
        mRefreshLaout.setMaterialRefreshListener(new MaterialRefreshListener() {
            @Override
            public void onRefresh(MaterialRefreshLayout materialRefreshLayout) {

                refreshData();

            }

            @Override
            public void onRefreshLoadMore(MaterialRefreshLayout materialRefreshLayout) {

                if(currPage <=totalPage)
                    loadMoreData();
                else{
//                    Toast.makeText()
                    mRefreshLaout.finishRefreshLoadMore();
                }
            }
        });
    }

    private void loadMoreData() {
        currPage = ++currPage;
        state = STATE_MORE;
        requestWares(category_id);
    }

    private void refreshData() {
        currPage =1;

        state=STATE_REFREH;
        requestWares(category_id);
    }

    private void requestBannerData() {

    }

    private void requestCategoryData() {


        mHttpHelper.get(Contants.API.CATEGORY_LIST, new SpotsCallBack<List<Category>>(this) {


            @Override
            public void onSuccess(Response response, List<Category> categories) {

                showCategoryData(categories);

                if (categories != null && categories.size() > 0)
                    category_id = categories.get(0).getId();
                requestWares(category_id);
            }

            @Override
            public void onError(Response response, int code, Exception e) {

            }
        });

    }

    private void showCategoryData(List<Category> categories) {

        mCategoryAdapter = new CategoryAdapter(this,categories);

        mCategoryAdapter.setOnItemClickListener(new BaseAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {

                Category category = mCategoryAdapter.getItem(position);

                category_id = category.getId();
                currPage=1;
                state=STATE_NORMAL;

                requestWares(category_id);


            }
        });

        mRecyclerView.setAdapter(mCategoryAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this,DividerItemDecoration.VERTICAL_LIST));


    }

    private void requestWares(long categoryId) {

        String url = Contants.API.WARES_LIST+"?categoryId="+categoryId+"&curPage="+currPage+"&pageSize="+pageSize;

        mHttpHelper.get(url, new BaseCallback<Page<Wares>>() {
            @Override
            public void onBeforeRequest(Request request) {

            }

            @Override
            public void onFailure(Request request, Exception e) {

            }

            @Override
            public void onResponse(Response response) {

            }

            @Override
            public void onSuccess(Response response, Page<Wares> waresPage) {


                currPage = waresPage.getCurrentPage();
                totalPage =waresPage.getTotalPage();

                showWaresData(waresPage.getList());


            }


            @Override
            public void onError(Response response, int code, Exception e) {

            }
        });

    }

    private void showWaresData(List<Wares> wares) {

        switch (state){

            case  STATE_NORMAL:

                if(mWaresAdatper ==null) {
                    mWaresAdatper = new WaresAdapter(this, wares);

                    mRecyclerviewWares.setAdapter(mWaresAdatper);

                    mRecyclerviewWares.setLayoutManager(new GridLayoutManager(this, 2));
                    mRecyclerviewWares.setItemAnimator(new DefaultItemAnimator());
                    mRecyclerviewWares.addItemDecoration(new DividerGridItemDecoration(this));
                }
                else{
                    mWaresAdatper.clear();
                    mWaresAdatper.addData(wares);
                }




                break;

            case STATE_REFREH:
                mWaresAdatper.clear();
                mWaresAdatper.addData(wares);

                mRecyclerviewWares.scrollToPosition(0);
                mRefreshLaout.finishRefresh();
                break;

            case STATE_MORE:
                mWaresAdatper.addData(mWaresAdatper.getDatas().size(),wares);
                mRecyclerviewWares.scrollToPosition(mWaresAdatper.getDatas().size());
                mRefreshLaout.finishRefreshLoadMore();
                break;





        }

    }
}
