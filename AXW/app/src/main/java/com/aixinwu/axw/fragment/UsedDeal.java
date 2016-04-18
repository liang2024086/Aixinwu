package com.aixinwu.axw.fragment;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.aixinwu.axw.R;
import com.aixinwu.axw.tools.Bean;
import com.aixinwu.axw.tools.SearchAdapter;
import com.aixinwu.axw.view.SearchView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by liangyuding on 2016/4/6.
 */
public class UsedDeal extends Fragment implements SearchView.SearchViewListener{
    private ListView lvResults;
    private SearchView searchView;
    private SearchAdapter resultAdapter;
    private List<Bean> dbData;
    private List<Bean> resultData;
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.used_deal,null);
        getDbData();
        getResultData(null);
        lvResults = (ListView) view.findViewById(R.id.main_lv_search_results);
        searchView = (SearchView)view.findViewById(R.id.main_search_layout);
        searchView.setSearchViewListener(this);
        lvResults.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Toast.makeText(getActivity(),i+" is what you want!", Toast.LENGTH_SHORT).show();
                /*
                Intent intent = new Intent();
                intent.putExtra("itemId",resultData.get(i).getItemId());
                intent.setClass(getActivity(),Buy.class);
                getActivity().startActivity(intent);
                */

            }
        });


        return view;
    }

    @Override
    public void onRefreshAutoComplete(String text) {

    }

    @Override
    public void onSearch(String text) {
        getResultData(text);
        lvResults.setVisibility(View.VISIBLE);
        if (lvResults.getAdapter() == null){
            lvResults.setAdapter(resultAdapter);
        }else{
            resultAdapter.notifyDataSetChanged();
        }
        Toast.makeText(getActivity(),"Finish Search", Toast.LENGTH_SHORT).show();
    }

    /**
     * 获取所有商品信息，用dbData存储
     * itemId：商品编号
     * picId：一张示意图的编号
     * type：种类
     * doc：说明
     */
    private void getDbData(){
        int size = 5;
        dbData = new ArrayList<>(size);
        for (int i = 0; i < size; i++){
            dbData.add(new Bean(i,R.drawable.icon,"shangping" +i+1,"sds"+i+1));
        }
    }
    private void getResultData(String text){
        if (resultData == null){
            resultData = dbData;
        } else {
            resultData.clear();
            for (int i = 0; i < dbData.size(); i++){
                if ((dbData.get(i).getDoc()+dbData.get(i).getType()).contains(text.trim())){
                    resultData.add(dbData.get(i));
                }
            }
        }
        if (resultAdapter == null) {
            resultAdapter = new SearchAdapter(getActivity(), resultData, R.layout.item_bean_list);
        } else {
            resultAdapter.notifyDataSetChanged();
        }
    }
}
