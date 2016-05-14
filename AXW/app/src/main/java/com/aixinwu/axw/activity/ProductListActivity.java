package com.aixinwu.axw.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import com.aixinwu.axw.Adapter.ProductAdapter;
import com.aixinwu.axw.R;
import com.aixinwu.axw.model.Product;

import java.util.ArrayList;
import java.util.List;

public class ProductListActivity extends AppCompatActivity {
    private List<Product> productList = new ArrayList<Product>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_list);

        initProduct();
    }

    @Override
    public void onStart () {

        ProductAdapter adapter1 = new ProductAdapter(
                ProductListActivity.this,
                R.layout.product_item,
                productList
        );

        GridView gridView1 = (GridView) findViewById(R.id.grid_product);
        gridView1.setAdapter (adapter1);
        gridView1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Product product = productList.get(i);
                Intent intent = new Intent(ProductListActivity.this, ProductDetailActivity.class);
                intent.putExtra("param1", product.getProduct_name());
                startActivity(intent);
            }
        });

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
    }
}
