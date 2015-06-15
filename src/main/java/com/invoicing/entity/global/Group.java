package com.invoicing.entity.global;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(name = "s_group")
public class Group implements Serializable {

	private static final long serialVersionUID = 1L;

	private String id;
	private String uid;// 所属用户ID
	private String name;// 分组名称

	@Id
	@GenericGenerator(name = "UUID", strategy = "uuid")
	@GeneratedValue(generator = "UUID")
	@Column(length=32)
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	@Column(length=32)
	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	@Column(length=55)
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
