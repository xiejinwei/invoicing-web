package com.invoicing.entity.global;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Entity;

import org.hibernate.annotations.GenericGenerator;

/**
 * 用户朋友分组表
 * 
 * @author fang
 *
 */
@Entity
@Table(name = "s_friend")
public class Friends implements Serializable {

	private static final long serialVersionUID = 1L;

	private String id;
	private String uid;
	private String fid;// 朋友ID User->ID
	private String gid;// 分组ID Group->ID如果用户未被分组，则值为空

	@Column(length = 32)
	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	@Column(length = 32)
	public String getFid() {
		return fid;
	}

	public void setFid(String fid) {
		this.fid = fid;
	}

	@Column(length = 32)
	public String getGid() {
		return gid;
	}

	public void setGid(String gid) {
		this.gid = gid;
	}

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

}
