package com.invoicing.dao.business;


import org.springframework.stereotype.Repository;

import com.invoicing.dao.global.BaseDao;
import com.invoicing.entity.business.Stock;

@Repository("stockDao")
public class StockDao extends BaseDao<Stock> {

}
