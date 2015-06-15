package com.invoicing.service.business;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.invoicing.dao.business.OutstoreDao;
import com.invoicing.dao.business.StockDao;
import com.invoicing.entity.business.Outstore;
import com.invoicing.entity.business.Stock;
import com.invoicing.entity.global.User;
import com.invoicing.service.global.BaseService;

@Service("outstoreService")
public class OutstoreService extends BaseService<Outstore> {

	@Autowired
	private OutstoreDao outstoreDao;
	@Autowired
	private StockDao productDao;

	/**
	 * 新增出库单，需要做以下几个事情：1、添加了条出库出数2、对库存数量跟据出库单数量进行锁定
	 */
	@Transactional
	public void addOutstore(HttpServletRequest request, String pid, int supply,
			double price, double reward, String remarks) {
		User user = (User) request.getSession().getAttribute("suser");
		if (user == null)
			throw new RuntimeException("出库单添加人员添加异常");
		if (supply <= 0)
			throw new RuntimeException("出库数必须大于等于1");
		if (price <= 0)
			throw new RuntimeException("单价有误，请重新输入");
		Stock product = productDao.findById(pid, Stock.class);
		if (product == null)
			throw new RuntimeException("没有找到库存信息，刷新后重试");
		if (product.getSupply() <= 0 || product.getSupply() < supply)
			throw new RuntimeException("库存不足！此次最多可出库：" + product.getSupply()
					+ " 个。");
		double amout = supply * price;
		Outstore outstore = new Outstore();
		outstore.setAmount(amout);
		outstore.setBrand(product.getBrand());
		outstore.setColor(product.getColor());
		outstore.setCreatetime(new Date());
		outstore.setInprice(product.getPrice());
		outstore.setPrice(price);
		outstore.setProductid(product.getId());
		outstore.setReward(reward);
		outstore.setSellprice(product.getSellprice());
		outstore.setSpec(product.getSpec());
		outstore.setSupply(supply);
		outstore.setUser(user);
		outstore.setStatus(0);
		outstore.setRemarks(remarks);
		outstore.setSupplier(product.getSupplier());
		outstoreDao.save(outstore);
		product.setSupply(product.getSupply() - supply);
		product.setLocksupply(product.getLocksupply() + supply);
		productDao.update(product);
	}

	@Transactional
	public void auditOutstore(HttpServletRequest request, String oid) {
		Outstore outstore = outstoreDao.findById(oid, Outstore.class);
		if (outstore == null)
			throw new RuntimeException("没有找到出库单信息");
		User user = (User) request.getSession().getAttribute("suser");
		if (user == null)
			throw new RuntimeException("出库单审核人员添加异常");
		if (!user.getId().equals(outstore.getUser().getId()))
			throw new RuntimeException("只能审核自己添加的出库单");
		outstore.setStatus(1);
		outstoreDao.update(outstore);
		Stock product = productDao.findById(outstore.getProductid(),
				Stock.class);
		if (product == null)
			throw new RuntimeException("没有找到库存信息，刷新后重试");
		product.setLocksupply(product.getLocksupply() - outstore.getSupply());
		productDao.update(product);
	}

	@Transactional
	public void updateOutstore(HttpServletRequest request, Outstore os) {
		Outstore outstore = outstoreDao.findById(os.getId(), Outstore.class);
		if (outstore == null)
			throw new RuntimeException("没有找到出库单信息");
		User user = (User) request.getSession().getAttribute("suser");
		if (user == null)
			throw new RuntimeException("出库单审核人员添加异常");
		if (!user.getId().equals(outstore.getUser().getId()))
			throw new RuntimeException("只能修改自己添加的出库单");
		if (outstore.getStatus() != 0)
			throw new RuntimeException("只能修改未审核的出库单");
		if (os.getSupply() != outstore.getSupply()) {
			// 处理库存
			Stock product = productDao.findById(outstore.getProductid(),
					Stock.class);
			if (product == null)
				throw new RuntimeException("没有找到库存信息，刷新后重试");
			if (product.getSupply() < os.getSupply()
					&& product.getLocksupply() < os.getSupply())
				throw new RuntimeException("库存中可销售量不足，不能进行修改操作");
			// 还原库存数量及锁定量
			product.setSupply(product.getSupply() + outstore.getSupply());
			product.setLocksupply(product.getLocksupply()
					- outstore.getSupply());
			// 重新修改库存数量及锁定量
			product.setSupply(product.getSupply() - os.getSupply());
			product.setLocksupply(product.getLocksupply() + os.getSupply());
			productDao.update(product);
			outstore.setSupply(os.getSupply());
		}
		// 修改出库单
		outstore.setPrice(os.getPrice());
		outstore.setReward(os.getReward());
		outstoreDao.update(outstore);
	}

	@Transactional
	public String deleteOutstore(HttpServletRequest request, String oid) {
		Outstore outstore = outstoreDao.findById(oid, Outstore.class);
		if (outstore == null)
			return "没有找到出库单信息";
		User user = (User) request.getSession().getAttribute("suser");
		if (user == null)
			return "出库单审核人员添加异常";
		if (!user.getId().equals(outstore.getUser().getId()))
			return "只能修改自己添加的出库单";
		if (outstore.getStatus() != 0)
			return "只能删除未审核的出库单";
		// 处理库存
		Stock product = productDao.findById(outstore.getProductid(),
				Stock.class);
		if (product == null)
			return "没有找到库存信息，刷新后重试";
		// 还源库存数量
		product.setLocksupply(product.getLocksupply() - outstore.getSupply());
		product.setSupply(product.getSupply() + outstore.getSupply());
		productDao.update(product);
		outstoreDao.delete(outstore);
		return "success";
	}

	public void unauditOutstore(HttpServletRequest request, String id) {
		Outstore outstore = outstoreDao.findById(id, Outstore.class);
		if (outstore == null)
			throw new RuntimeException("没有找到出库单信息");
		User user = (User) request.getSession().getAttribute("suser");
		if (user == null)
			throw new RuntimeException("出库单审核人员添加异常");
		if (!user.getId().equals(outstore.getUser().getId()))
			throw new RuntimeException("只能取消审核自己添加的出库单");
		outstore.setStatus(0);
		outstoreDao.update(outstore);
		Stock product = productDao.findById(outstore.getProductid(),
				Stock.class);
		if (product == null)
			throw new RuntimeException("没有找到库存信息，刷新后重试");
		product.setLocksupply(product.getLocksupply() + outstore.getSupply());
		productDao.update(product);
	}

	@Transactional
	public void comfirmOutstore(String id, double reward) {
		Outstore outstore = outstoreDao.findById(id, Outstore.class);
		if (outstore == null)
			throw new RuntimeException("没有找到出库单信息");
		if (outstore.getStatus() != 1)
			throw new RuntimeException("不能对未审核的出库单进行审核");
		outstore.setStatus(2);
		if (outstore.getReward() != reward)
			outstore.setReward(reward);
		outstoreDao.update(outstore);
	}

}
