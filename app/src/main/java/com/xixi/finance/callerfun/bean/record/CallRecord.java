package com.xixi.finance.callerfun.bean.record;

import java.io.Serializable;

/**
 * Created by AlohaQoQ on 2017/4/14.
 */
public class CallRecord implements Serializable {

    private int code;

    /**
     * 主键
     */
    private String id;

    /**
     * 最大额度
     */
    private String maxAmount;

    /**
     * 最小额度
     */
    private String minAmount;

    /**
     * 产品名称
     */
    private String productName;

    /**
     * 产品备注
     */
    private String remark;


    /**
     * 资金方状态, 1为可选择 0反之
     */
    private String status;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMaxAmount() {
        return maxAmount;
    }

    public void setMaxAmount(String maxAmount) {
        this.maxAmount = maxAmount;
    }

    public String getMinAmount() {
        return minAmount;
    }

    public void setMinAmount(String minAmount) {
        this.minAmount = minAmount;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
