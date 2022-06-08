/**
 * Copyright (c) 2016-2019 人人开源 All rights reserved.
 *
 * https://www.renren.io
 *
 * 版权所有，侵权必究！
 */

package cc.caozx.ware;



import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;

import java.util.HashMap;
import java.util.Map;

/**
 * 返回数据
 *
 * @author Mark sunlightcs@gmail.com
 */
public class R extends HashMap<String, Object> {
	private static final long serialVersionUID = 1L;

	public <T> T getData(String key,TypeReference<T> typeReference){
		Object data = get(key);
		String s = JSON.toJSONString(data);
		return JSON.parseObject(s, typeReference);

	}


	public <T> T getData(TypeReference<T> typeReference){
		Object data = get("data");
		String s = JSON.toJSONString(data);
		return JSON.parseObject(s, typeReference);

	}

	public R setData(Object data){
		put("data",data);
		return this;
	}

//	private T data;
//
//	public T getData() {
//		return data;
//	}
//
//	public void setData(T data) {
//		this.data = data;
//	}

	public R() {
		put("code", 0);
		put("msg", "success");
	}
	
	public static R fail() {
		return error(500, "未知异常，请联系管理员");
	}
	
	public static R fail(String msg) {
		return error(500, msg);
	}
	
	public static R error(int code, String msg) {
		R r = new R();
		r.put("code", code);
		r.put("msg", msg);
		return r;
	}

	public static R success(String msg) {
		R r = new R();
		r.put("msg", msg);
		return r;
	}
	
	public static R success(Map<String, Object> map) {
		R r = new R();
		r.putAll(map);
		return r;
	}
	
	public static R success() {
		return new R();
	}

	public R put(String key, Object value) {
		super.put(key, value);
		return this;
	}

	public  Integer getCode() {

		return (Integer) this.get("code");
	}
}
