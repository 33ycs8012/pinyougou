package com.pinyougou.search.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.Criteria;
import org.springframework.data.solr.core.query.FilterQuery;
import org.springframework.data.solr.core.query.GroupOptions;
import org.springframework.data.solr.core.query.HighlightOptions;
import org.springframework.data.solr.core.query.HighlightQuery;
import org.springframework.data.solr.core.query.Query;
import org.springframework.data.solr.core.query.SimpleFilterQuery;
import org.springframework.data.solr.core.query.SimpleHighlightQuery;
import org.springframework.data.solr.core.query.SimpleQuery;
import org.springframework.data.solr.core.query.result.GroupEntry;
import org.springframework.data.solr.core.query.result.GroupPage;
import org.springframework.data.solr.core.query.result.GroupResult;
import org.springframework.data.solr.core.query.result.HighlightEntry;
import org.springframework.data.solr.core.query.result.HighlightPage;

import com.alibaba.dubbo.config.annotation.Service;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.search.service.ItemSearchService;

@Service(timeout = 3000)
public class ItemSearchServiceImpl implements ItemSearchService {

	
	@Autowired
	private SolrTemplate solrTemplate;
	
	
	@Override
	public Map<String, Object> search(Map searchMap) {
		
		Map<String, Object> map = new HashMap<>();
		
		//关键字空格处理
		String keywords = (String) searchMap.get("keywords");
		searchMap.put("keywords", keywords.replace(" ", ""));
		
		//1.按关键字查询:高亮显示
		map.putAll(searchList(searchMap));
		
		//2.根据关键字查询商品房分类
		List<String> list = searchCategoryList(searchMap);
		map.put("categoryList",list);
		
		//3.查询品牌和规格列表
		String categoryName = (String) searchMap.get("category");
		if(!"".equals(categoryName)) {
			map.putAll(searchBrandAndSpecList(categoryName));
		}else {//如果没有分类列表,按照第一个查询
			if(list.size() > 0) {
				map.putAll(searchBrandAndSpecList(list.get(0)));
			}
		}
		return map;
	}
	
	/**
	 * 根据关键字搜索列表
	 * @param keywords
	 * @return
	 */
	public Map searchList (Map searchMap) {
		Map map = new HashMap();
		HighlightQuery query = new SimpleHighlightQuery();
		//设置高亮的域
		HighlightOptions highlightOptions = new HighlightOptions().addField("item_title");
		highlightOptions.setSimplePrefix("<em style='color:red'>");//高亮前缀
		highlightOptions.setSimplePostfix("</em>");//高亮后缀
		query.setHighlightOptions(highlightOptions);//设置高亮选项
		//1.按照条件查询
		Criteria criteria = new Criteria("item_keywords").is(searchMap.get("keywords"));
		query.addCriteria(criteria);
		
		//2.按照分类查询
		if(!"".equals(searchMap.get("category"))) {
			System.out.println("==searchMap.get(category)==" + searchMap.get("category"));
			Criteria filterCriteria = new Criteria("item_category").is(searchMap.get("category"));
			FilterQuery filterQuery = new SimpleFilterQuery(filterCriteria);
			query.addFilterQuery(filterQuery);
		}
		
		//3.按品牌筛选
		if(!"".equals(searchMap.get("brand"))) {
			System.out.println("按品牌筛选");
			Criteria filterCriteria = new Criteria("item_brand").is(searchMap.get("brand"));
			FilterQuery filterQuery = new SimpleFilterQuery(filterCriteria);
			query.addFilterQuery(filterQuery);
		}
		
		//4.过滤规格
		if(searchMap.get("spec") != null) {
			System.out.println("过滤规格");
			Map<String, String> specMap = (Map) searchMap.get("spec");
			for (String key : specMap.keySet()) {
				Criteria filterCriteria = new Criteria("item_spec_" + key).is(specMap.get(key));
				FilterQuery filterQuery = new SimpleFilterQuery(filterCriteria);
				query.addFilterQuery(filterQuery);
			}
		}
		
		//5.按价格筛选
		if(!"".equals(searchMap.get("price"))) {
			System.out.println("按价格筛选");
			String[] price = ((String) searchMap.get("price")).split("-");
			if(!price[0].equals("0")) {//如果起始价格不等于0,就加入起始价格要求
				Criteria filterCriteria = new Criteria("item_price").greaterThanEqual(price[0]);
				FilterQuery filterQuery = new SimpleFilterQuery(filterCriteria);
				query.addFilterQuery(filterQuery);
			}
			if(!price[1].equals("*")) {//如果最高价格不是*,就加入最高价格要求
				Criteria filterCriteria = new Criteria("item_price").lessThanEqual(price[1]);
				FilterQuery filterQuery = new SimpleFilterQuery(filterCriteria);
				query.addFilterQuery(filterQuery);
			}
		}
		
		//6.搜索结果分页查询
		Integer pageNo = (Integer) searchMap.get("pageNo");//获取页码
		if(pageNo ==  null) {
			pageNo = 1;//默认走第一页
		}
		Integer pageSize = (Integer) searchMap.get("pageSize");//获取每页展示数
		if(pageSize ==  null) {
			pageSize = 40;//默认让一页展示40条
		}
		System.out.println("执行分页");
		query.setOffset( (pageNo - 1) * pageSize );//从第几条记录查
		query.setRows(pageSize);//每页展示多少条
		
		//7.排序
		 String sortValue = (String) searchMap.get("sort");//获取ASC或DESC
		 String sortField = (String) searchMap.get("sortField");//排序的字段
		 if(sortValue != null && !sortValue.equals("")) {
			 System.out.println("执行排序:"+sortValue);
			 if(sortValue.equals("ASC")) {
				 Sort sort = new Sort(Sort.Direction.ASC, "item_"+sortField);
				 query.addSort(sort);
			 }
			 if(sortValue.equals("DESC")) {
				 Sort sort = new Sort(Sort.Direction.DESC, "item_"+sortField);
				 query.addSort(sort);
			 }
		 }
		 
		
		//高亮显示处理
		HighlightPage<TbItem> page = solrTemplate.queryForHighlightPage(query, TbItem.class);
		for (HighlightEntry<TbItem> high : page.getHighlighted()) {//循环高亮入口集合
			TbItem item = high.getEntity();//获取原实体类
			if(high.getHighlights().size() > 0 && high.getHighlights().get(0)
					.getSnipplets().size() > 0) {
				item.setTitle(high.getHighlights().get(0).getSnipplets().get(0));//设置高亮结果
			}
		}
		map.put("rows", page.getContent());
		map.put("totalPages", page.getTotalPages());//返回总页数
		map.put("total", page.getTotalElements());//返回总记录数
		
		return map;
	}
	
	public List<String> searchCategoryList(Map searchMap){
		
		List<String> list = new ArrayList<String>();
		Query query = new SimpleQuery("*:*");
		//按照关键字查询
		Criteria criteria = new Criteria("item_keywords").is(searchMap.get("keywords"));
		query.addCriteria(criteria);
		//设置分组选项
		GroupOptions groupOptions = new GroupOptions().addGroupByField("item_category");
		query.setGroupOptions(groupOptions);
		//得到分组页
		GroupPage<TbItem> page = solrTemplate.queryForGroupPage(query, TbItem.class);
		//根据列得到分组结果集
		GroupResult<TbItem> groupResult = page.getGroupResult("item_category");
		//得到分组结果入口页
		Page<GroupEntry<TbItem>> groupEntries = groupResult.getGroupEntries();
		//得到分组入口集合
		List<GroupEntry<TbItem>> content = groupEntries.getContent();
		for (GroupEntry<TbItem> entry : content) {
			//将分组结果的名称封装到返回值中
			list.add(entry.getGroupValue());
		}
		return list; 
	}
	
	@Autowired
	private RedisTemplate redisTemplate;
	
	/**
	 * 查询品牌和规格列表
	 * @param category 分类名称
	 * @return
	 */
	private Map searchBrandAndSpecList(String category) {
		
		Map map = new HashMap();
		//获取模版ID
		Long typeId = (Long) redisTemplate.boundHashOps("itemCat").get(category);
		if(typeId != null) {
			//根据模版ID查询品牌列表
			List brandList = (List) redisTemplate.boundHashOps("brandList").get(typeId);
			map.put("brandList", brandList);
			//根据模版ID查询规格列表
			List specList = (List) redisTemplate.boundHashOps("specList").get(typeId);
			map.put("specList", specList);
		}
		return map;
	}

	@Override
	public void importList(List list) {
		solrTemplate.saveBeans(list);
		solrTemplate.commit();
	}

	@Override
	public void deleteByGoodsIds(List goodsIdList) {
		System.out.println("删除商品的ID为:"+goodsIdList);
		Query query = new SimpleQuery();
		Criteria criteria = new Criteria("item_goodsid").in(goodsIdList);
		query.addCriteria(criteria);
		solrTemplate.delete(query);
		solrTemplate.commit();
	}
}
