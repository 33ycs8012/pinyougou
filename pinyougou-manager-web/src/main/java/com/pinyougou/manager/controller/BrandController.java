package com.pinyougou.manager.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pojo.TbBrand;
import com.pinyougou.sellergoods.service.BrandServiceI;
import com.pinyougou.utils.PageResult;
import com.pinyougou.utils.Result;

@RestController
@RequestMapping("/brand")
public class BrandController {
	
	@Reference
	private BrandServiceI brandServiceI;
	
	@RequestMapping("findAll")
	public List<TbBrand> findAll() {
		System.out.println("==findAll==");
		return brandServiceI.findAll();
	}
	
	@RequestMapping("findPage")
	public PageResult findPage(int page, int size) {
		return brandServiceI.findPage(page, size);
	}

	@RequestMapping("add")
	public Result add(@RequestBody TbBrand brand) {
		try {
			brandServiceI.add(brand);
			return new Result(true, "新增信息成功");
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(true, "新增失败");
		}
	}
	
	@RequestMapping("findOne")
	public TbBrand findOne(Long id) {
		return brandServiceI.findOne(id);
	}
	
	@RequestMapping("update")
	public Result update (@RequestBody TbBrand brand) {
		try {
			brandServiceI.update(brand);
			return new Result(true, "信息修改成功");
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(true, "修改失败");
		}
	}
	
	@RequestMapping("delete")
	public Result delete(Long[] ids) {
		try {
			brandServiceI.delete(ids);
			return new Result(true, "删除成功");
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(true, "删除失败");
		}
	}
	
	@RequestMapping("search")
	public PageResult search(@RequestBody TbBrand brand, int page, int size) {
		return brandServiceI.findPage(brand,page, size);
	}
	
	@RequestMapping("selectOptionList")
	public List<Map> selectOptionList(){
		return brandServiceI.selectOptionList();
	}
	
	
	
	
}
