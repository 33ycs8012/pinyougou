package com.pinyougou.cart.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.pinyougou.cart.service.CartService;
import com.pinyougou.pojogroup.Cart;
import com.pinyougou.utils.Result;

import util.CookieUtil;

@RestController
@RequestMapping("/cart")
public class CartController {
	
	@Reference
	private CartService cartService;
	
	@Autowired
	private  HttpServletRequest request;
	
	@Autowired
	private  HttpServletResponse response;
	
	
	/**
	 * 购物车列表
	 * @param request
	 * @return
	 */
	@RequestMapping("/findCartList")
	public List<Cart>  findCartList(){
		String cookieValue = CookieUtil.getCookieValue(request, "cartList", "UTF-8");
		
		if(cookieValue==null || cookieValue.equals("")){
			cookieValue="[]";
		}
		
		List<Cart> cartList_cookie = JSON.parseArray(cookieValue, Cart.class);
		return cartList_cookie;
	}
	
	/**
	 * 添加商品到购物车
	 * @param request
	 * @param response
	 * @param itemId
	 * @param num
	 * @return
	 */
	@RequestMapping("/addGoodsToCartList")
	public Result addGoodsToCartList(Long itemId,Integer num){
		
		//得到登陆人账号,判断当前是否有人登陆
		String username = SecurityContextHolder.getContext().getAuthentication().getName();
		try {
			List<Cart> cartList = findCartList();
			System.err.println("===============");
			cartList = cartService.addGoodsToCarList(cartList, itemId, num);
			util.CookieUtil.setCookie(request, response, "cartList", 
					JSON.toJSONString(cartList),3600*24,"UTF-8");
			System.err.println("===============");
			return new Result(true, "添加成功");
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "添加失败");
		}
	}
	

}
