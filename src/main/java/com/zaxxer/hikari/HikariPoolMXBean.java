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

import javax.sql.DataSource;

/**
 * The javax.management MBean for a Hikari pool instance.
 *
 * @author Brett Wooldridge
 */
public interface HikariPoolMXBean
{
   /**
    * Get the number of currently idle connections in the pool.
    * <p>
    * The return value is extremely transient and is a point-in-time measurement.  Therefore, due to a time
    * difference between invoking this method and {@link #getActiveConnections()}, it is possible for the sum
    * of idle plus active connections to be either less than or greater than the value returned by
    * {@link #getTotalConnections()}.
    *
    * @return the current number of idle connections in the pool
    */
   int getIdleConnections();

   /**
    * Get the number of currently active connections in the pool.
    * <p>
    * The return value is extremely transient and is a point-in-time measurement.  Therefore, due to a time
    * difference between invoking this method and {@link #getIdleConnections()}, it is possible for the sum
    * of idle plus active connections to be either less than or greater than the value returned by
    * {@link #getTotalConnections()}.
    *
    * @return the current number of active (in-use) connections in the pool
    */
   int getActiveConnections();

   /**
    * Get the total number of connections currently in the pool.  The return value is transient and is a
    * point-in-time measurement.
    *
    * @return the total number of connections in the pool
    */
   int getTotalConnections();

   /**
    * Get the number of threads awaiting connections from the pool.  The return value is extremely transient and is
    * a point-in-time measurement.
    *
    * @return the number of threads awaiting a connection from the pool
    */
   int getThreadsAwaitingConnection();

   /**
    * Evict currently idle connections from the pool, and mark active (in-use) connections for eviction when they are
    * returned to the pool.
    */
   void softEvictConnections();

   /**
    * 暂停连接池.当连接池被暂停,调用{@link DataSource#getConnection()}的线程将被阻塞
    * <i>且不会超时</i>,直到通过调用{@link #resumePool()}方法恢复连接池.
    *
    * <br>
    * 除非{@link HikariConfig#setAllowPoolSuspension(boolean)}方法或等效属性被设置为{@code true},否则此方法无效.
    */
   void suspendPool();

   /**
    * 恢复连接池. Enables connection borrowing to resume on a pool that has been suspended via the
    * {@link #suspendPool()} method.
    * <br>
    * 除非{@link HikariConfig#setAllowPoolSuspension(boolean)}方法或等效属性被设置为{@code true},否则此方法无效.
    */
   void resumePool();
}
