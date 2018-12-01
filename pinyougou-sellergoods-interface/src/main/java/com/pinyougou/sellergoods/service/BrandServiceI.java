package com.pinyougou.sellergoods.service;

import java.util.List;
import java.util.Map;

import com.pinyougou.pojo.TbBrand;
import com.pinyougou.utils.PageResult;

/**
 * 品牌接口
 * @return
 */
public interface BrandServiceI {
	
	public List<TbBrand> findAll();
	
	/**
	 * 分页查询
	 */
	public PageResult findPage(int pageNum, int pageSize);

	public void add(TbBrand brand);

	public TbBrand findOne(Long id);

	public void update(TbBrand brand);

	public void delete(Long[] ids);

	public PageResult findPage(TbBrand brand, int pageNum, int pageSize);

	public List<Map> selectOptionList();
	
	
}
