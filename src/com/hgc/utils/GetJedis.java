package com.hgc.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.stringtemplate.v4.compiler.CodeGenerator.primary_return;

import com.hgc.entity.ProviceCount;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class GetJedis {
	public static Jedis getRedis() {
		JedisPool jedisPool = null;
		try {
			JedisPoolConfig config = new JedisPoolConfig();
			config.setMaxIdle(5);
			config.setMaxWaitMillis(1000l);
			config.setTestOnBorrow(false);
			jedisPool = new JedisPool(config, "127.0.0.1", 6379, 10000, "123456");
			return jedisPool.getResource();
		} catch (Exception ex) {
			return null;
		} finally {
			jedisPool.close();
		}
	}

	public static byte[] serialize(Object object) {
		ObjectOutputStream oos = null;
		ByteArrayOutputStream baos = null;
		try {
			// ���л�
			baos = new ByteArrayOutputStream();
			oos = new ObjectOutputStream(baos);
			oos.writeObject(object);
			byte[] bytes = baos.toByteArray();
			return bytes;
		} catch (Exception e) {

		}
		return null;
	}

	public static Object unserialize(byte[] bytes) {
		ByteArrayInputStream bais = null;
		try {
			// �����л�
			bais = new ByteArrayInputStream(bytes);
			ObjectInputStream ois = new ObjectInputStream(bais);
			return ois.readObject();
		} catch (Exception e) {

		}
		return null;
	}

	public static void main(String[] args) {
		GetJedis getJedis = new GetJedis();
		Jedis jedis = getJedis.getRedis();
		// �������list
		// jedis.lpush("province","����");
		// jedis.lpush("province","���");
		// jedis.lpush("province","NewYork");
		// ���Ի�ȡlist
		// System.out.println("����Ԫ��-stringlists��"+jedis.lrange("province", 0, -1));


		  ProviceCount proviceCount=new ProviceCount("����",0);
		  ProviceCount proviceCount02=new ProviceCount("���",0);
		  jedis.lpush("province".getBytes(),GetJedis.serialize(proviceCount));
		  jedis.lpush("province".getBytes(),GetJedis.serialize(proviceCount02));
		  ProviceCount proviceCount2=(ProviceCount) GetJedis.unserialize(jedis.lrange("province".getBytes(), 0, -1).get(0));
		  System.out.println(proviceCount2.getProvice());


	}
}
