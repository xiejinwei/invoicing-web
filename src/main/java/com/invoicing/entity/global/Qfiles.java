package com.invoicing.entity.global;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

/**
 * 上传文件
 * 
 * @author fang
 * 
 */

@Entity
@Table(name = "s_qfiles")
public class Qfiles implements Serializable {

	private static final long serialVersionUID = 1L;

	public static final int EDITOR = -1;
	public static final int QUESTION = 0;
	public static final int ANSWER = 1;
	public static final int USER = 2;

	private String id;
	private String srcid;// 来源ID
	private int type;// 来源类型-1：文本辑器文件 0：问题1:答案2:头像
	private String url;// 文件调用地址
	private String path;// 文件保存地址
	private String filename;
	private Date createtime;

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

	@Column(length = 32)
	public String getSrcid() {
		return srcid;
	}

	public void setSrcid(String srcid) {
		this.srcid = srcid;
	}

	@Column(length = 1)
	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	@Column(length = 199)
	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	@Column(length = 55)
	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	@Column(nullable = false)
	public Date getCreatetime() {
		return createtime;
	}

	public void setCreatetime(Date createtime) {
		this.createtime = createtime;
	}

	@Column(length = 177)
	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

}
