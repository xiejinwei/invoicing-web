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
 * 文件列表
 * 
 * @author xjw
 * 
 */

@Entity
@Table(name = "v_files")
public class Vfiles implements Serializable {

	private static final long serialVersionUID = -8696651576810051655L;

	/**
	 * 没有来源类型
	 */
	public static final int TYPE_FILE = -1;
	/**
	 * 用户头像
	 */
	public static final int TYPE_USER = 0;
	/**
	 * 编辑器
	 */
	public static final int TYPE_EDITOR = 1;
	/**
	 * APP开机画面
	 */
	public static final int TYPE_WELCOME = 2;
	/**
	 * 轮播图片
	 */
	public static final int TYPE_SLIDER = 3;
	/**
	 * 文章
	 */
	public static final int TYPE_INFOR = 4;
	/**
	 * 评论
	 */
	public static final int TYPE_COMMENT = 5;
	/**
	 * 推广类型
	 */
	public static final int TYPE_EXTENSIONTYPE = 6;
	/**
	 * 商品
	 */
	public static final int TYPE_PRODUCT = 7;
	/**
	 * 奖品
	 */
	public static final int TYPE_PRIZE = 8;
	/**
	 * 视频
	 */
	public static final int TYPE_VIDEO = 9;
	/**
	 * 广告
	 */
	public static final int TYPE_ADV = 10;
	/**
	 * 视频直播
	 */
	public static final int TYPE_VIDEO_BROADCAST = 11;

	private String id;
	private String srcid;// 来源ID
	private int type;// 来源类型
	// private String path;// 文件保存地址
	private String url;// 文件浏览器调用地址
	private Date createtime;// 上传时间
	private String userid;// 上传文件用户
	private String filename;// 文件名
	private String href;// 图片超链接地址
	private int issm;// 是否缩略图0：否 1：是
	private int isextension;// 是否加入推广0：未加入1：已加入

	@Id
	@GenericGenerator(name = "UUID", strategy = "uuid")
	@GeneratedValue(generator = "UUID")
	@Column(length = 32)
	public String getId() {
		return id;
	}

	public void setId(String fid) {
		this.id = fid;
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

	@Column(length = 199)
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

	// @Column(length = 177)
	// public String getPath() {
	// return path;
	// }
	//
	// public void setPath(String path) {
	// this.path = path;
	// }

	@Column(length = 32)
	public String getUserid() {
		return userid;
	}

	public void setUserid(String userid) {
		this.userid = userid;
	}

	@Column(length = 199)
	public String getHref() {
		return href;
	}

	public void setHref(String href) {
		this.href = href;
	}

	@Column(length = 1, nullable = false, columnDefinition = "int default 0")
	public int getIssm() {
		return issm;
	}

	public void setIssm(int issm) {
		this.issm = issm;
	}

	@Column(length = 1, nullable = false, columnDefinition = "int default 0")
	public int getIsextension() {
		return isextension;
	}

	public void setIsextension(int isextension) {
		this.isextension = isextension;
	}
}
