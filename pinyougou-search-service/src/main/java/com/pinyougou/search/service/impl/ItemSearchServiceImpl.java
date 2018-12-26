package com.pinyougou.search.service.impl;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.Criteria;
import org.springframework.data.solr.core.query.Query;
import org.springframework.data.solr.core.query.SimpleQuery;
import org.springframework.data.solr.core.query.result.ScoredPage;

import com.alibaba.dubbo.config.annotation.Service;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.search.service.ItemSearchService;

@Service(timeout = 4000)
public class ItemSearchServiceImpl implements ItemSearchService {

	
	@Autowired
	private SolrTemplate solrTemplate;
	
	
	@Override
	public Map<String, Object> search(Map searchMap) {
		
		Map<String, Object> map = new HashMap<>();
		Query query = new SimpleQuery();
		//item_keywords:复制域    | Is: 表示匹配,为了实现分词效果
		Criteria criteria = new Criteria("item_keywords").is(searchMap.get("keywords"));
		query.addCriteria(criteria);
		ScoredPage<TbItem> page = solrTemplate.queryForPage(query, TbItem.class);
		map.put("rows", page.getContent());//rows代表查询出来多少条
		
		return map;
	}

}
