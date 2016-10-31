package com.aixinwu.axw.model;

/**
 * Created by hello on 2016/10/30.
 */
public class Record {

    private String id;
    private String customer_id;
    private String consignee_id;
    private String order_sn;
    private String total_product_price;
    private String updateTime;
    private String imgUrls;

    public Record(String id, String customer_id, String consignee_id, String order_sn, String total_product_price, String updateTime,String imgUrls){
        this.id = id;
        this.customer_id = customer_id;
        this.consignee_id = consignee_id;
        this.order_sn = order_sn;
        this.total_product_price = total_product_price;
        this.updateTime = updateTime;
        this.imgUrls = imgUrls;
    }

    public String getId(){
        return this.id;
    }

    public String getCustomer_id(){
        return this.customer_id;
    }

    public String getConsignee_id(){
        return this.consignee_id;
    }

    public String getOrder_sn(){
        return this.order_sn;
    }

    public String getTotal_product_price(){
        return this.total_product_price;
    }

    public String getUpdateTime(){
        return this.updateTime;
    }

    public String getImgUrls(){
        return imgUrls;
    }
}
