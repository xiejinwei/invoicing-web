package com.invoicing.entity.global;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(name = "s_model")
public class Smodel implements Serializable {

	private static final long serialVersionUID = 1L;

	private String id;
	private String name;
	private String controller;
	private String url;
	private String parentid;
	private int ishide;// 是否隐藏 0： 否 1：是
	private String sort;// 自定义排序
	private Date createtime;
	private List<Modelright> modelrights;// 菜单拥有的权限

	private boolean has = false;// 是否已经拥用

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

	@Column(length = 55)
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Column(length = 155)
	public String getController() {
		return controller;
	}

	public void setController(String controller) {
		this.controller = controller;
	}

	@Column(length = 55)
	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	@Column(length = 32)
	public String getParentid() {
		return parentid;
	}

	public void setParentid(String parentid) {
		this.parentid = parentid;
	}

	@Column(length = 1)
	public int getIshide() {
		return ishide;
	}

	public void setIshide(int ishide) {
		this.ishide = ishide;
	}

	@Column(length=5)
	public String getSort() {
		return sort;
	}

	public void setSort(String sort) {
		this.sort = sort;
	}

	public Date getCreatetime() {
		return createtime;
	}

	public void setCreatetime(Date createtime) {
		this.createtime = createtime;
	}

	@Transient
	public List<Modelright> getModelrights() {
		return modelrights;
	}

	public void setModelrights(List<Modelright> modelrights) {
		this.modelrights = modelrights;
	}

	@Transient
	public boolean isHas() {
		return has;
	}

	public void setHas(boolean has) {
		this.has = has;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof Smodel))
			return false;
		final Smodel s = (Smodel) obj;
		if (!getId().equals(s.getId()))
			return false;
		return true;
	}

}
