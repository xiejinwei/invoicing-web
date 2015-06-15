package com.invoicing.service.business;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.invoicing.entity.business.Instore;
import com.invoicing.entity.business.Outstore;

@Service("financialstatisticsService")
public class FinancialstatisticsService{

	public List<Instore> packageInstores(List<Instore> its) {
		List<Instore> instores = new ArrayList<Instore>();
		if (its == null || its.size() == 0)
			return instores;
		for (Instore instore : its) {
			if(equlesInstore(instores, instore)){
				SumInstore(instores, instore);
			}else{
				instores.add(instore);
			}
		}
		return instores;
	}

	private boolean equlesInstore(List<Instore> instores, Instore instore) {
		boolean has = false;
		if (instores == null || instores.size() == 0)
			return has;
		for (Instore inst : instores) {
			if (
				inst.getBrand().equals(instore.getBrand())
				&& inst.getSpec().equals(instore.getSpec())
				&& inst.getColor().equals(instore.getColor())) {
				has=true;
				break;
			}
		}
		return has;
	}
	
	private void SumInstore(List<Instore> instores,Instore instore){
		for (Instore inst : instores) {
			if (
				inst.getBrand().equals(instore.getBrand())
				&& inst.getSpec().equals(instore.getSpec())
				&& inst.getColor().equals(instore.getColor())) {
				inst.setAmount(inst.getAmount()+instore.getAmount());
				inst.setSupply(inst.getSupply()+instore.getSupply());
				break;
			}
		}
	}

	public List<String> getPhoneTypelist(List<Outstore> outstores) {
		List<String> types = new ArrayList<String>();
		if(outstores==null || outstores.size()==0)
			return types;
		for(Outstore outstore : outstores){
			String type = outstore.getBrand()+" "+outstore.getSpec()+" "+outstore.getColor();
			if(!types.contains(type))
				types.add(type);
		}
		return types;
	}

	public int getUserTypeSupply(String userid, String type,List<Outstore> outstores) {
		if(outstores==null || outstores.size()==0)
			return 0;
		int supply = 0;
		for(Outstore outstore : outstores){
			if(userid.equals(outstore.getUser().getId())){
				String ptype = outstore.getBrand()+" "+outstore.getSpec()+" "+outstore.getColor();
				if(ptype.equals(type)){
					supply += outstore.getSupply();
				}
			}
		}
		return supply;
	}

}
