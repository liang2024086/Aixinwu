package com.aixinwu.axw.database;

import android.provider.BaseColumns;

public final class ProductReaderContract {

    public ProductReaderContract() {
    }

    public static abstract class ProductEntry implements BaseColumns {
        public static final String TABLE_NAME = "entry";
        public static final String COLUMN_NAME_ENTRY_ID = "product_id";
        public static final String COLUMN_NAME_CATEGORY = "category";
        public static final String COLUMN_NAME_NUMBER = "number";
        public static final String COLUMN_NAME_PRICE = "price";
        public static final String COLUMN_NAME_NAME = "name";
        public static final String COLUMN_NAME_IMG = "imgurl";
        public static final String COLUMN_NAME_STOCK = "stock";
    }
}
