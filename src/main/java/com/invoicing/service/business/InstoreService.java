package com.invoicing.service.business;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.invoicing.dao.business.InstoreDao;
import com.invoicing.dao.business.StockDao;
import com.invoicing.dao.business.SupplierDao;
import com.invoicing.entity.business.Instore;
import com.invoicing.entity.business.Stock;
import com.invoicing.entity.business.Supplier;
import com.invoicing.service.global.BaseService;
import com.invoicing.utils.StringUtil;

@Service("/instoreService")
public class InstoreService extends BaseService<Instore> {

	@Autowired
	private InstoreDao instoreDao;
	@Autowired
	private StockDao productDao;
	@Autowired
	private SupplierDao supplierDao;

	@Transactional
	public void addInstore(Instore instore) {
		if (StringUtil.isNullOrEmpty(instore.getBrand()))
			throw new RuntimeException("品牌不能为空");
		if (StringUtil.isNullOrEmpty(instore.getSpec()))
			throw new RuntimeException("型号不能为空");
		if (StringUtil.isNullOrEmpty(instore.getSname()))
			throw new RuntimeException("供货商不能为空");
		if (instore.getPrice() <= 0)
			throw new RuntimeException("价格不能为0或负数");
		if (instore.getSupply() <= 0)
			throw new RuntimeException("数量不能为0或负数");
		if(instore.getCreatetime()==null)
			instore.setCreatetime(new Date());
		Supplier supplier = supplierDao.findById(instore.getSupid(), Supplier.class);
		if(supplier==null)
			throw new RuntimeException("没有找到供应商信息");
		instore.setSupplier(supplier);
		instore.setAmount(instore.getSupply() * instore.getPrice());
		instoreDao.save(instore);
	}

	@Transactional
	public void updateInstore(Instore instore) {
		Instore inst = instoreDao.findById(instore.getId(), Instore.class);
		if (inst == null)
			throw new RuntimeException("没有找到要修改的数据");
		if (inst.getStatus() != 0)
			throw new RuntimeException("只能对未审核的入库单进行修改");
		if (StringUtil.isNullOrEmpty(instore.getBrand()))
			throw new RuntimeException("品牌不能为空");
		if (StringUtil.isNullOrEmpty(instore.getSpec()))
			throw new RuntimeException("型号不能为空");
		if (StringUtil.isNullOrEmpty(instore.getSname()))
			throw new RuntimeException("供货商不能为空");
		if (instore.getPrice() <= 0)
			throw new RuntimeException("价格不能为0或负数");
		if (instore.getSupply() <= 0)
			throw new RuntimeException("数量不能为0或负数");
		if (!instore.getSupid().equals(inst.getSupplier().getId())) {
			Supplier supplier = supplierDao.findById(instore.getSupid(), Supplier.class);
			if(supplier==null)
				throw new RuntimeException("没有找到供应商信息");
			inst.setSupplier(supplier);
		}
		inst.setBrand(instore.getBrand());
		inst.setSpec(instore.getSpec());
		inst.setColor(instore.getColor());
		inst.setCreatetime(instore.getCreatetime());
		inst.setPrice(instore.getPrice());
		inst.setSupply(instore.getSupply());
		inst.setStatus(0);
		inst.setAmount(instore.getSupply() * instore.getPrice());
		inst.setSellprice(instore.getSellprice());
		inst.setRemarks(instore.getRemarks());
		instoreDao.update(inst);
	}

	@Transactional
	public void auditInstore(String id) {
		Instore instore = instoreDao.findById(id, Instore.class);
		if (instore == null)
			throw new RuntimeException("没有找到要修改的数据");
		if (instore.getStatus() != 0)
			throw new RuntimeException("只能对新增的数据进行审核");
		instore.setStatus(1);
		instoreDao.update(instore);
		Stock product = new Stock();
		product.setBrand(instore.getBrand());
		product.setColor(instore.getColor());
		product.setPrice(instore.getPrice());
		product.setSpec(instore.getSpec());
		Supplier supplier = instore.getSupplier();
		if(supplier!=null)
			product.setSupplier(instore.getSupplier().getName());
		product.setSupply(instore.getSupply());
		product.setInstoreid(instore.getId());
		product.setSellprice(instore.getSellprice());
		product.setCreatetime(instore.getCreatetime());
		product.setLocksupply(0);
		product.setRemarks(instore.getRemarks());
		productDao.save(product);
	}

	@Transactional
	public void unauditInstore(String id) {
		Instore instore = instoreDao.findById(id, Instore.class);
		if (instore == null)
			throw new RuntimeException("没有找到要修改的数据");
		if (instore.getStatus() != 1)
			throw new RuntimeException("只能对新增的数据进行审核");
		List<Stock> products = productDao.findListById("from Product p where p.instoreid=?", instore.getId());
		if (products == null)
			throw new RuntimeException("没有找到库存信息，不能取消审核");
		Stock product = products.get(0);
		if (product.getSupply() != instore.getSupply())
			throw new RuntimeException("库存已经发生变动，不能进行取消审核操作");
		productDao.delete(product);
		instore.setStatus(0);
		instoreDao.update(instore);

	}

}
