package cache

import java.io.File

import model.CacheUnit
import org.ehcache.PersistentUserManagedCache
import org.ehcache.config.builders.{ResourcePoolsBuilder, UserManagedCacheBuilder}
import org.ehcache.config.units.{EntryUnit, MemoryUnit}
import org.ehcache.impl.config.persistence.{DefaultPersistenceConfiguration, UserManagedPersistenceContext}
import org.ehcache.impl.persistence.DefaultLocalPersistenceService

/**
  * Created by linsixin on 2017/9/14.
  */
class HttpCache(storageDir:String) {

  private val service = new DefaultLocalPersistenceService(
    new DefaultPersistenceConfiguration(new File(storageDir))
  )

  private val context =
    new UserManagedPersistenceContext[String,CacheUnit]("context",service)

  private val resourcePool =
    ResourcePoolsBuilder.newResourcePoolsBuilder()
      .heap(50L,EntryUnit.ENTRIES)
      .offheap(50L,MemoryUnit.MB)
      .disk(100,MemoryUnit.MB)

  val cache: PersistentUserManagedCache[String, CacheUnit] =
    UserManagedCacheBuilder
    .newUserManagedCacheBuilder(classOf[String],classOf[CacheUnit])
    .`with`(context)
    .withResourcePools(resourcePool)
    .build(true)


  def containsKey(key:String): Boolean ={
    if(key == null)
      false
    else cache.containsKey(key)
  }

  def get(key:String): Option[CacheUnit] = {
    if(containsKey(key))
      Some(cache.get(key))
    else None
  }

  def put(key:String,data:CacheUnit): Unit = {
    if(key == null)
      throw new IllegalArgumentException(s"key should not be null")
    else if(containsKey(key))
      throw new IllegalStateException(s"$key has exists , use replace() method")
    cache.put(key,data)
  }

  def replace(key:String,newValue:CacheUnit): Option[CacheUnit] = {
    if(containsKey(key))
      Some(cache.replace(key,newValue))
    else None
  }

  def remove(key:String): Unit ={
    if(key == null || !containsKey(key))
      return
    cache.remove(key)
  }
  def close(): Unit ={
    cache.close()
    service.stop()
  }

  def closeAndDestroy(): Unit ={
    cache.close()
    cache.destroy()
    service.stop()
  }
}
