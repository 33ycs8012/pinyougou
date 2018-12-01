package com.pinyougou.sellergoods.service.impl;

import java.util.List;
import java.util.Map;

import javax.print.attribute.standard.PageRanges;

import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.pinyougou.mapper.TbBrandMapper;
import com.pinyougou.pojo.TbBrand;
import com.pinyougou.pojo.TbBrandExample;
import com.pinyougou.pojo.TbBrandExample.Criteria;
import com.pinyougou.sellergoods.service.BrandServiceI;
import com.pinyougou.utils.PageResult;

@Service
public class BrandServiceImpl implements BrandServiceI{
	
	@Autowired
	private TbBrandMapper brandMapper;

	@Override
	public List<TbBrand> findAll() {
		return brandMapper.selectByExample(null);
	}

	@Override
	public PageResult findPage(int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);
		Page<TbBrand> page = (Page<TbBrand>) brandMapper.selectByExample(null);
		return new PageResult(page.getTotal(), page.getResult());
	}

	@Override
	public void add(TbBrand brand) {
		brandMapper.insert(brand);
	}

	@Override
	public TbBrand findOne(Long id) {
		return brandMapper.selectByPrimaryKey(id);
	}

	@Override
	public void update(TbBrand brand) {
		brandMapper.updateByPrimaryKey(brand);
	}

	@Override
	public void delete(Long[] ids) {
		for (Long id : ids) {
			brandMapper.deleteByPrimaryKey(id);
		}
	}

	@Override
	public PageResult findPage(TbBrand brand, int pageNum, int pageSize) {
		
		PageHelper.startPage(pageNum, pageSize); //分页
		TbBrandExample example = new TbBrandExample();//根据条件查询
		Criteria criteria = example.createCriteria();//构建条件
		
		if(brand != null) {
			if(brand.getName() != null && brand.getName().length() > 0) {
				criteria.andNameLike("%" + brand.getName() + "%");
			}
			if(!"".equals(brand.getFirstChar()) &&  brand.getFirstChar() != null ) {
				criteria.andFirstCharLike("%" + brand.getFirstChar() + "%");
			}
		}
		
		Page<TbBrand> page = (Page<TbBrand>) brandMapper.selectByExample(example);
		
		return new PageResult(page.getTotal(), page.getResult());
	}

	@Override
	public List<Map> selectOptionList() {
		return brandMapper.selectOptionList();
	}
	
	

}
