package com.invoicing.entity.business;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.GenericGenerator;

/**
 * 库存
 * 
 * 当用户审核入库单后，系统需要根据用户录入的串号数量进行数据封装，奖多条数据分别一条条保存
 * 
 * @author xjw
 * 
 */
@Entity
@Table(name = "c_product")
public class Stock implements Serializable {

	private static final long serialVersionUID = 7646024420520028940L;

	private String id;
	private String brand;// 品牌
	private String spec;// 型号
	private String color;// 颜色
	private Date createtime;// 添加时间\进货时间
	private String supplierid;// 供应商
	private double price;// 采购单价
	private int supply;// 数量
	private int locksupply;// 锁定数量
	private double sellprice;// 建议零售价
	private String supplier;// 供货商
	private String instoreid;// 入库单ID
	private String imei;// 串号，每台手机对应一个
	private String remarks;// 备注

	@Id
	@GenericGenerator(name = "UUID", strategy = "uuid")
	@GeneratedValue(generator = "UUID")
	@Column(length = 32)
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	@Column(length = 11)
	public String getBrand() {
		return brand;
	}

	public void setBrand(String brand) {
		this.brand = brand;
	}

	@Column(length = 11)
	public String getSpec() {
		return spec;
	}

	public void setSpec(String spec) {
		this.spec = spec;
	}

	@Column(length = 7)
	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}

	public Date getCreatetime() {
		return createtime;
	}

	public void setCreatetime(Date createtime) {
		this.createtime = createtime;
	}

	@Transient
	public String getSupplierid() {
		return supplierid;
	}

	public void setSupplierid(String supplierid) {
		this.supplierid = supplierid;
	}

	@Column(length = 8)
	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = price;
	}

	@Column(length = 4)
	public int getSupply() {
		return supply;
	}

	public void setSupply(int supply) {
		this.supply = supply;
	}

	public String getSupplier() {
		return supplier;
	}

	public void setSupplier(String supplier) {
		this.supplier = supplier;
	}

	@Column(length = 32)
	public String getInstoreid() {
		return instoreid;
	}

	public void setInstoreid(String instoreid) {
		this.instoreid = instoreid;
	}

	@Column(length = 8)
	public double getSellprice() {
		return sellprice;
	}

	public void setSellprice(double sellprice) {
		this.sellprice = sellprice;
	}

	@Column(length = 5)
	public int getLocksupply() {
		return locksupply;
	}

	public void setLocksupply(int locksupply) {
		this.locksupply = locksupply;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public String getImei() {
		return imei;
	}

	public void setImei(String imei) {
		this.imei = imei;
	}

}
