package com.pinyougou.page.service.impl;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfig;

import com.pinyougou.mapper.TbGoodsDescMapper;
import com.pinyougou.mapper.TbGoodsMapper;
import com.pinyougou.mapper.TbItemCatMapper;
import com.pinyougou.mapper.TbItemMapper;
import com.pinyougou.page.service.ItemPageService;
import com.pinyougou.pojo.TbGoods;
import com.pinyougou.pojo.TbGoodsDesc;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.pojo.TbItemExample;
import com.pinyougou.pojo.TbItemExample.Criteria;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
@Service
public class ItemPageServiceImpl implements ItemPageService {
	
	@Value("${pageDir}")
	private String pageDir;
	
	@Autowired
	private FreeMarkerConfig freeMarkerConfig;
	
	@Autowired
	private TbGoodsMapper tbGoodsMapper;
	
	@Autowired
	private TbGoodsDescMapper tbGoodsDescMapper;
	
	@Autowired
	private TbItemMapper itemMapper;
	
	@Autowired
	private TbItemCatMapper itemCatMapper;

	@Override
	public boolean genItemHtml(Long goodsId) {
		
		try {
		//创建配置类
		Configuration configuration = freeMarkerConfig.getConfiguration();
		
		//加载模版
		Template template = configuration.getTemplate("item.ftl");
		
		Map dataModel=new HashMap<>();
		
		//1.加载商品表数据
		TbGoods goods = tbGoodsMapper.selectByPrimaryKey(goodsId);
		dataModel.put("goods", goods);
		
		//2.加载商品扩展表数据
		TbGoodsDesc goodsDesc = tbGoodsDescMapper.selectByPrimaryKey(goodsId);
		dataModel.put("goodsDesc", goodsDesc);
		
		//读取商品分类
		String itemCat1 = itemCatMapper.selectByPrimaryKey(goods.getCategory1Id()).getName();
		String itemCat2 = itemCatMapper.selectByPrimaryKey(goods.getCategory2Id()).getName();
		String itemCat3 = itemCatMapper.selectByPrimaryKey(goods.getCategory3Id()).getName();
		dataModel.put("itemCat1", itemCat1);
		dataModel.put("itemCat2", itemCat2);
		dataModel.put("itemCat3", itemCat3);
		
		//读取SKU列表
		TbItemExample example=new TbItemExample();
		Criteria criteria = example.createCriteria();
		criteria.andGoodsIdEqualTo(goodsId);//SPU ID
		criteria.andStatusEqualTo("1");//状态有效			
		example.setOrderByClause("is_default desc");//按是否默认字段进行降序排序，目的是返回的结果第一条为默认SKU
		
		List<TbItem> itemList = itemMapper.selectByExample(example);
		dataModel.put("itemList", itemList);
		
		Writer writer = new FileWriter(pageDir + goodsId + ".html");
		
		template.process(dataModel, writer);
		
		writer.close();
		
		return true;
		} catch (IOException | TemplateException e) {
			e.printStackTrace();
			return false;
		}
	}

	@Override
	public boolean deleteItemHtml(Long[] goodsIds) {
		
		try {
			for (Long goodsId : goodsIds) {
				new File(pageDir + goodsId + ".html").delete();
			}
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		
	}

}
