package com.aixinwu.axw.fragment;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.aixinwu.axw.Adapter.ProductAdapter;
import com.aixinwu.axw.R;
import com.aixinwu.axw.activity.HelloWorld;
import com.aixinwu.axw.activity.ProductDetailActivity;
import com.aixinwu.axw.model.Product;

import java.util.ArrayList;
import java.util.List;
import android.widget.Toast;

/**
 * Created by liangyuding on 2016/4/6.
 */
public class LoveCoin extends Fragment {

    private List<Product> productList = new ArrayList<Product>();
    private List<Product> leaseList = new ArrayList<Product>();
    private List<Product> volList = new ArrayList<Product>();

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.love_coin,null);
        return view;
    }

    @Override
    public void onStart () {
        initProduct();
        ProductAdapter adapter1 = new ProductAdapter(
                getActivity(),
                R.layout.product_item,
                productList
        );
        ProductAdapter adapter2 = new ProductAdapter(
                getActivity(),
                R.layout.product_item,
                leaseList
        );
        ProductAdapter adapter3 = new ProductAdapter(
                getActivity(),
                R.layout.product_item,
                volList
        );
        GridView gridView1 = (GridView) getActivity().findViewById(R.id.grid1);
        gridView1.setAdapter (adapter1);
        gridView1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Product product = productList.get(i);
                Intent intent = new Intent(getActivity(), ProductDetailActivity.class);
                intent.putExtra("param1",product.getProduct_name());
                getActivity().startActivity(intent);

            }
        });
        GridView gridView2 = (GridView) getActivity().findViewById(R.id.grid2);
        gridView2.setAdapter (adapter2);
        GridView gridView3 = (GridView) getActivity().findViewById(R.id.grid3);
        gridView3.setAdapter (adapter3);
        super.onStart();
    }


    private void initProduct () {
        Product product1 = new Product("A new Compiler Principle book", 100, R.mipmap.product1);
        productList.add(product1);
        Product product2 = new Product("A new Hadoop book", 100, R.mipmap.product2);
        productList.add(product2);
        Product product3 = new Product("A new Coding book", 100, R.mipmap.product3);
        productList.add(product3);
        Product product4 = new Product("A new C++ primer book", 100, R.mipmap.product4);
        productList.add(product4);
        Product leaseproduct1 = new Product("A new Compiler Principle book", 100, R.mipmap.product1);
        leaseList.add(leaseproduct1);
        Product leaseproduct2 = new Product("A new Hadoop book", 100, R.mipmap.product2);
        leaseList.add(leaseproduct2);
        Product leaseproduct3 = new Product("A new Coding book", 100, R.mipmap.product3);
        leaseList.add(leaseproduct3);
        Product leaseproduct4 = new Product("A new C++ primer book", 100, R.mipmap.product4);
        leaseList.add(leaseproduct4);
        Product volproduct1 = new Product("A new Compiler Principle book", 100, R.mipmap.product1);
        volList.add(volproduct1);
        Product volproduct2 = new Product("A new Hadoop book", 100, R.mipmap.product2);
        volList.add(volproduct2);
        Product volproduct3 = new Product("A new Coding book", 100, R.mipmap.product3);
        volList.add(volproduct3);
        Product volproduct4 = new Product("A new C++ primer book", 100, R.mipmap.product4);
        volList.add(volproduct4);
    }
}
