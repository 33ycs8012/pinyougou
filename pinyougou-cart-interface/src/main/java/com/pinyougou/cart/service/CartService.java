package com.pinyougou.cart.service;

import java.util.List;

import com.pinyougou.pojogroup.Cart;

public interface CartService {
	
	/**
	 * 添加到购物车
	 * @param carList
	 * @param itemId
	 * @param num
	 * @return
	 */
	public List<Cart> addGoodsToCarList(List<Cart> carList,Long itemId,Integer num);
	
	/**
	 * 从redis中查询购物车
	 * @param username
	 * @return
	 */
	public List<Cart> findCartListFromRedis(String username);
	
	/**
	 * 将购物车保存到redis
	 * @param username
	 * @param cartList
	 */
	public void saveCartListToRedis(String username,List<Cart> cartList);
}
