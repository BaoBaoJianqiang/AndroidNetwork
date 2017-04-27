package jianqiang.com.mylibrary.cache;

import android.os.Environment;

import java.io.File;

import jianqiang.com.mylibrary.utils.BaseUtils;

/**
 * 缓存管理器
 */
public class CacheManager {
	/** 缓存文件路径 */
	public static final String APP_CACHE_PATH = Environment
			.getExternalStorageDirectory().getPath() + "/YoungHeart/appdata/";

	/** sdcard 最小空间，如果小于10M，不会再向sdcard里面写入任何数据 */
	public static final long SDCARD_MIN_SPACE = 1024 * 1024 * 10;

	private static CacheManager cacheManager;

	private CacheManager() {
	}

	/**
	 * 获取CacheManager实例
	 * 
	 * @return
	 */
	public static synchronized CacheManager getInstance() {
		if (CacheManager.cacheManager == null) {
			CacheManager.cacheManager = new CacheManager();
		}
		return CacheManager.cacheManager;
	}


	/**
	 * 从文件缓存中取出缓存，没有则返回空
	 * 
	 * @param key
	 * @return
	 */
	public String getFileCache(final String key) {
		String md5Key = BaseUtils.getMd5(key);
		if (contains(md5Key)) {
			final CacheItem item = getFromCache(md5Key);
			if (item != null) {
				return item.getData();
			}
		}
		return null;
	}

	/**
	 * API data 缓存到文件
	 * 
	 * @param key
	 * @param data
	 * @param outDate
	 */
	public void putFileCache(final String key, final String data,
			long expiredTime) {
		String md5Key = BaseUtils.getMd5(key);

		final CacheItem item = new CacheItem(md5Key, data, expiredTime);
		putIntoCache(item);
	}

	/**
	 * 查询是否有key对应的缓存文件
	 * 
	 * @param key
	 * @return
	 */
	public boolean contains(final String key) {
		final File file = new File(APP_CACHE_PATH + key);
		return file.exists();
	}

	public void initCacheDir() {
		// sdcard已经挂载并且空间不小于10M，可以写入文件;小于10M时，清除缓存
		if (BaseUtils.sdcardMounted()) {
			if (BaseUtils.getSDSize() < SDCARD_MIN_SPACE) {
				clearAllData();
			} else {
				final File dir = new File(APP_CACHE_PATH);
				if (!dir.exists()) {
					dir.mkdirs();
				}
			}
		}
	}

	/**
	 * 将CacheItem从磁盘读取出来
	 * 
	 * @param path
	 * @return 缓存数据CachItem
	 */
	synchronized CacheItem getFromCache(final String key) {
		CacheItem cacheItem = null;
		Object findItem = BaseUtils.restoreObject(APP_CACHE_PATH + key);
		if (findItem != null) {
			cacheItem = (CacheItem) findItem;
		}

		// 缓存不存在
		if (cacheItem == null)
			return null;

		// 缓存过期
		long a = System.currentTimeMillis();
		if (System.currentTimeMillis() > cacheItem.getTimeStamp()) {
			return null;
		}

		return cacheItem;
	}

	/**
	 * 将CacheItem缓存到磁盘
	 * 
	 * @param item
	 * @return 是否缓存，True：缓存成功，False：不能缓存
	 */

	synchronized boolean putIntoCache(final CacheItem item) {
		if (BaseUtils.getSDSize() > SDCARD_MIN_SPACE) {
			BaseUtils.saveObject(APP_CACHE_PATH + item.getKey(), item);
			return true;
		}

		return false;
	}

	/**
	 * 清除缓存文件
	 */
	void clearAllData() {
		File file = null;
		File[] files = null;
		if (BaseUtils.sdcardMounted()) {
			file = new File(APP_CACHE_PATH);
			files = file.listFiles();
			if (files != null) {
				for (final File file2 : files) {
					file2.delete();
				}
			}
		}
	}
}
