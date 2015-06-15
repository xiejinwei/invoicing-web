package com.invoicing.entity.business;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

import com.invoicing.entity.global.User;

/**
 * 出库单
 * 
 * @author xjw
 *
 */
@Entity
@Table(name = "c_outstore")
public class Outstore implements Serializable {

	private static final long serialVersionUID = 2334467990602514020L;

	private String id;
	private String brand;// 品牌
	private String spec;// 型号
	private String color;// 颜色
	private Date createtime;// 添加时间\销售时间
	private double inprice;// 采购价
	private double sellprice;// 建议零售价
	private double price;// 销售价
	private int supply;// 销售数量
	private double amount;// 合计销售金额
	private User user;// 销售人员
	private double reward;// 提成
	private String productid;// 库存ID
	private int status;// 状态0：新增1：审核2：确认
	private String supplier;// 供应商
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

	@Column(length = 8)
	public double getInprice() {
		return inprice;
	}

	public void setInprice(double inprice) {
		this.inprice = inprice;
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

	@Column(length = 10)
	public double getAmount() {
		return amount;
	}

	public void setAmount(double amount) {
		this.amount = amount;
	}

	@ManyToOne
	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	@Column(length = 8)
	public double getSellprice() {
		return sellprice;
	}

	public void setSellprice(double sellprice) {
		this.sellprice = sellprice;
	}

	@Column(length = 6)
	public double getReward() {
		return reward;
	}

	public void setReward(double reward) {
		this.reward = reward;
	}

	@Column(length = 32)
	public String getProductid() {
		return productid;
	}

	public void setProductid(String productid) {
		this.productid = productid;
	}

	@Column(length = 3)
	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public String getSupplier() {
		return supplier;
	}

	public void setSupplier(String supplier) {
		this.supplier = supplier;
	}

}
