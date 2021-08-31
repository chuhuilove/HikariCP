/*
 * Copyright (C) 2013 Brett Wooldridge
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.zaxxer.hikari;

/**
 * The javax.management MBean for a Hikari pool configuration.
 * 定义Connection的核心属性
 *
 * @author Brett Wooldridge
 */
public interface HikariConfigMXBean
{
   /**
    * 获取客户端等待池中Connection的最大毫秒数.
    * 如果在没有连接可用的情况下超过此时间,则会从{@link javax.sql.DataSource#getConnection()} 抛出 SQLException.
    *
    * @return the connection timeout in milliseconds
    */
   long getConnectionTimeout();

   /**
    * 设置客户端等待池中Connection的最大毫秒数.
    * 如果在没有连接可用的情况下超过此时间,则会从{@link javax.sql.DataSource#getConnection()}抛出SQLException.
    *
    * @param connectionTimeoutMs the connection timeout in milliseconds
    */
   void setConnectionTimeout(long connectionTimeoutMs);

   /**
    * 获取池等待Connection被验证为活跃的最大毫秒数.
    *
    * @return the validation timeout in milliseconds
    */
   long getValidationTimeout();

   /**
    * 设置池等待Connection被验证为活跃的最大毫秒数.
    *
    * @param validationTimeoutMs the validation timeout in milliseconds
    */
   void setValidationTimeout(long validationTimeoutMs);

   /**
    * 此属性控制允许Connection在池中闲置的最长时间(以毫秒为单位).
    * Connection是否以空闲状态被清除,最大变化为+30秒,平均变化为+15秒.
    * 在此超时之前,Connection永远不会以空闲状态被清除.
    * 值为0意味着永远不会从池中删除空闲Connection.
    *
    * @return the idle timeout in milliseconds
    */
   long getIdleTimeout();

   /**
    * This property controls the maximum amount of time (in milliseconds) that a connection is allowed to sit
    * idle in the pool. Whether a connection is retired as idle or not is subject to a maximum variation of +30
    * seconds, and average variation of +15 seconds. A connection will never be retired as idle before this timeout.
    * A value of 0 means that idle connections are never removed from the pool.
    *
    * @param idleTimeoutMs the idle timeout in milliseconds
    */
   void setIdleTimeout(long idleTimeoutMs);

   /**
    * This property controls the amount of time that a connection can be out of the pool before a message is
    * logged indicating a possible connection leak. A value of 0 means leak detection is disabled.
    *
    * @return the connection leak detection threshold in milliseconds
    */
   long getLeakDetectionThreshold();

   /**
    * 此属性控制在记录指示可能的连接泄漏的消息之前连接可以离开池的时间量.0表示关闭泄漏检测.
    *
    * @param leakDetectionThresholdMs the connection leak detection threshold in milliseconds
    */
   void setLeakDetectionThreshold(long leakDetectionThresholdMs);

   /**
    * This property controls the maximum lifetime of a connection in the pool. When a connection reaches this
    * timeout, even if recently used, it will be retired from the pool. An in-use connection will never be
    * retired, only when it is idle will it be removed.
    *
    * @return the maximum connection lifetime in milliseconds
    */
   long getMaxLifetime();

   /**
    * 此属性控制池中Connection的最大生存时间.当连接达到此超时时,即使是最近使用的连接,也将从连接池中退出.
    * 一个正在使用中的Connection永远不会被清除,只有当它空闲时,它才会被删除
    *
    * @param maxLifetimeMs the maximum connection lifetime in milliseconds
    */
   void setMaxLifetime(long maxLifetimeMs);

   /**
    * 该属性控制HikariCP试图在池中维护的最小空闲连接数,包括空闲连接和正在使用的连接.
    * 如果空闲连接低于这个值,HikariCP将尽最大努力快速有效地恢复它们.
    * @return the minimum number of connections in the pool
    */
   int getMinimumIdle();

   /**
    *
    * 该属性控制HikariCP试图在池中维护的最小空闲连接数,包括空闲连接和正在使用的连接.
    * 如果空闲连接低于这个值,HikariCP将尽最大努力快速有效地恢复它们.
    *
    * @param minIdle the minimum number of idle connections in the pool to maintain
    */
   void setMinimumIdle(int minIdle);

   /**
    * 该属性控制HikariCP将保留在池中的最大连接数,包括空闲连接和正在使用的连接.
    *
    * @return the maximum number of connections in the pool
    */
   int getMaximumPoolSize();

   /**
    * 该属性控制允许池达到的最大大小,包括空闲和使用中的连接.
    * 基本上这个值将决定到数据库后端的最大实际连接数.
    * <p>
    * 当池达到此大小并且没有空闲连接可用时,对getConnection()的调用将在超时前阻塞最多connectionTimeout毫秒.
    *
    * @param maxPoolSize the maximum number of connections in the pool
    */
   void setMaximumPoolSize(int maxPoolSize);

   /**
    * Set the password used for authentication. Changing this at runtime will apply to new connections only.
    * Altering this at runtime only works for DataSource-based connections, not Driver-class or JDBC URL-based
    * connections.
    *
    * @param password the database password
    */
   void setPassword(String password);

   /**
    * Set the username used for authentication. Changing this at runtime will apply to new connections only.
    * Altering this at runtime only works for DataSource-based connections, not Driver-class or JDBC URL-based
    * connections.
    *
    * @param username the database username
    */
   void setUsername(String username);


   /**
    * connection pool的名称
    *
    * @return the name of the connection pool
    */
   String getPoolName();

   /**
    * Get the default catalog name to be set on connections.
    *
    * @return the default catalog name
    */
   String getCatalog();

   /**
    * Set the default catalog name to be set on connections.
    * <p>
    * WARNING: THIS VALUE SHOULD ONLY BE CHANGED WHILE THE POOL IS SUSPENDED, AFTER CONNECTIONS HAVE BEEN EVICTED.
    *
    * @param catalog the catalog name, or null
    */
   void setCatalog(String catalog);
}
