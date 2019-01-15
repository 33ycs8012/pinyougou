package com.pinyougou.cart.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.dubbo.config.annotation.Service;
import com.pinyougou.cart.service.CartService;
import com.pinyougou.mapper.TbItemMapper;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.pojo.TbOrderItem;
import com.pinyougou.pojogroup.Cart;

@Service(timeout=50000)
public class CartServiceImpl implements CartService {
	
	@Autowired
	private TbItemMapper itemMapper;
	
	@Autowired
	private RedisTemplate redisTemplate;

	@Override
	public List<Cart> addGoodsToCarList(List<Cart> cartList, Long itemId, Integer num) {
		//1.根据商品SKU ID查询SKU商品信息
		TbItem tbItem = itemMapper.selectByPrimaryKey(itemId);
		
		if(tbItem == null) {
			throw new RuntimeException("商品不存在");
		} 
		if(!"1".equals(tbItem.getStatus())) {
			throw new RuntimeException("商品状态无效");
		}
		//2.获取商家ID
		String sellerId = tbItem.getSellerId();
		
		//3.根据商家ID判断购物车列表中是否存在该商家的购物车
		Cart cart = searchCartBySellerId(cartList, sellerId);
		
		//4.如果购物车列表中不存在该商家的购物车
		if(cart == null) {
			//4.1 新建购物车对象
			cart = new Cart();
			//4.2 将新建的购物车对象添加到购物车列表	
			cart.setSellerId(sellerId);
			cart.setSellerName(tbItem.getSeller());
			TbOrderItem tbOrderItem = createOrderItem(tbItem, num);
			List list = new ArrayList();
			list.add(tbOrderItem);
			cart.setOrderItemList(list);
		} else {
			//5.如果购物车列表中存在该商家的购物车
			TbOrderItem tbOrderItem = searchOrderItemByItemId(cart.getOrderItemList(), itemId);
			// 查询购物车明细列表中是否存在该商品
			//5.1. 如果没有，新增购物车明细
			if(tbOrderItem==null) {
				tbOrderItem = createOrderItem(tbItem, num);
				cart.getOrderItemList().add(tbOrderItem);
			} else {
				//5.2. 如果有，在原购物车明细上添加数量，更改金额
				tbOrderItem.setNum(tbOrderItem.getNum()+num);
				tbOrderItem.setTotalFee(new BigDecimal(tbOrderItem.getNum()*tbOrderItem.getPrice().doubleValue())  );
				
				//如果数量操作后小于等于0，则移除
				if(tbOrderItem.getNum()<=0) {
					cart.getOrderItemList().remove(tbOrderItem);//移除购物车明细
				}
				
				//如果移除后cart的明细数量为0，则将cart移除
				if(cart.getOrderItemList().size()==0){
					cartList.remove(cart);
				}
			}
		}
		return cartList;
	}
	
	/**
	 * 根据商家ID判断购物车列表中是否存在该商家的购物车
	 * @param carList
	 * @param sellerId
	 * @return
	 */
	public Cart searchCartBySellerId(List<Cart> carList,String sellerId) {
		for (Cart cart : carList) {
			if(cart.getSellerId().equals(sellerId)) {
				return cart;
			}
		}
		return null;
	}
	
	/**
	 * 创建订单明细
	 * @param item
	 * @param num
	 * @return
	 */
	public TbOrderItem createOrderItem(TbItem item,Integer num) {
		if(num<0) {
			throw new RuntimeException("数量非法");
		}
		TbOrderItem orderItem = new TbOrderItem();
		orderItem.setGoodsId(item.getGoodsId());
		orderItem.setItemId(item.getId());
		orderItem.setNum(num);
		orderItem.setPicPath(item.getImage());
		orderItem.setPrice(item.getPrice());
		orderItem.setSellerId(item.getSellerId());
		orderItem.setTitle(item.getTitle());
		orderItem.setTotalFee(new BigDecimal(item.getPrice().doubleValue()*num));
		return orderItem;
	}
	
	/**
	 * 根据商品明细ID查询
	 * @param orderItemList
	 * @param itemId
	 * @return
	 */
	public TbOrderItem searchOrderItemByItemId(List<TbOrderItem> orderItemList ,Long itemId) {
		for (TbOrderItem tbOrderItem : orderItemList) {
			if(tbOrderItem.getItemId().longValue() == itemId.longValue()) {
				return tbOrderItem;
			}
		}
		return null;
	}

	@Override
	public List<Cart> findCartListFromRedis(String username) {
		// TODO Auto-generated method stub
		List<Cart> cartList = (List<Cart>)redisTemplate.boundHashOps("cartList").get(username);
		if(cartList==null){
			cartList=new ArrayList();
		}
		return cartList;
	}

	@Override
	public void saveCartListToRedis(String username, List<Cart> cartList) {
		// TODO Auto-generated method stub
		System.out.println("向redis存入购物车数据....."+username);
		redisTemplate.boundHashOps("cartList").put(username, cartList);
	}

}
