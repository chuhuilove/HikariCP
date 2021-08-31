/*
 * Copyright (C) 2013, 2014 Brett Wooldridge
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
package com.zaxxer.hikari.util;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.Enumeration;
import java.util.Map.Entry;
import java.util.Properties;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class DriverDataSource implements DataSource
{
   private static final Logger LOGGER = LoggerFactory.getLogger(DriverDataSource.class);

   /**
    * 创建Connection对象所需的url
    */
   private final String jdbcUrl;
   /**
    * 创建Connection对象所需的属性
    * @see Driver#connect(String, Properties)
    */
   private final Properties driverProperties;
   /**
    * 创建Connection对象所需的驱动类,比如:com.mysql.jdbc.Driver
    */
   private Driver driver;

   public DriverDataSource(String jdbcUrl, String driverClassName, Properties properties, String username, String password)
   {
      this.jdbcUrl = jdbcUrl;
      this.driverProperties = new Properties();

      for (Entry<Object, Object> entry : properties.entrySet()) {
         driverProperties.setProperty(entry.getKey().toString(), entry.getValue().toString());
      }

      if (username != null) {
         driverProperties.put("user", driverProperties.getProperty("user", username));
      }
      if (password != null) {
         driverProperties.put("password", driverProperties.getProperty("password", password));
      }

      if (driverClassName != null) {
         Enumeration<Driver> drivers = DriverManager.getDrivers();
         while (drivers.hasMoreElements()) {
            Driver d = drivers.nextElement();
            if (d.getClass().getName().equals(driverClassName)) {
               driver = d;
               break;
            }
         }

         if (driver == null) {
            LOGGER.warn("Registered driver with driverClassName={} was not found, trying direct instantiation.", driverClassName);
            Class<?> driverClass = null;
            ClassLoader threadContextClassLoader = Thread.currentThread().getContextClassLoader();
            try {
               if (threadContextClassLoader != null) {
                  try {
                     driverClass = threadContextClassLoader.loadClass(driverClassName);
                     LOGGER.debug("Driver class {} found in Thread context class loader {}", driverClassName, threadContextClassLoader);
                  }
                  catch (ClassNotFoundException e) {
                     LOGGER.debug("Driver class {} not found in Thread context class loader {}, trying classloader {}",
                                  driverClassName, threadContextClassLoader, this.getClass().getClassLoader());
                  }
               }

               if (driverClass == null) {
                  driverClass = this.getClass().getClassLoader().loadClass(driverClassName);
                  LOGGER.debug("Driver class {} found in the HikariConfig class classloader {}", driverClassName, this.getClass().getClassLoader());
               }
            } catch (ClassNotFoundException e) {
               LOGGER.debug("Failed to load driver class {} from HikariConfig class classloader {}", driverClassName, this.getClass().getClassLoader());
            }

            if (driverClass != null) {
               try {
                  driver = (Driver) driverClass.newInstance();
               } catch (Exception e) {
                  LOGGER.warn("Failed to create instance of driver class {}, trying jdbcUrl resolution", driverClassName, e);
               }
            }
         }
      }

      final String sanitizedUrl = jdbcUrl.replaceAll("([?&;]password=)[^&#;]*(.*)", "$1<masked>$2");
      try {
         if (driver == null) {
            driver = DriverManager.getDriver(jdbcUrl);
            LOGGER.debug("Loaded driver with class name {} for jdbcUrl={}", driver.getClass().getName(), sanitizedUrl);
         }
         else if (!driver.acceptsURL(jdbcUrl)) {
            throw new RuntimeException("Driver " + driverClassName + " claims to not accept jdbcUrl, " + sanitizedUrl);
         }
      }
      catch (SQLException e) {
         throw new RuntimeException("Failed to get driver instance for jdbcUrl=" + sanitizedUrl, e);
      }
   }

   @Override
   public Connection getConnection() throws SQLException
   {
      // 简单粗暴,直接通过Driver创建新的Connection
      Connection connect = driver.connect(jdbcUrl, driverProperties);
      return connect;
   }

   @Override
   public Connection getConnection(final String username, final String password) throws SQLException
   {
      final Properties cloned = (Properties) driverProperties.clone();
      if (username != null) {
         cloned.put("user", username);
         if (cloned.containsKey("username")) {
            cloned.put("username", username);
         }
      }
      if (password != null) {
         cloned.put("password", password);
      }
      // 简单粗暴,直接通过Driver创建新的Connection
      Connection connect = driver.connect(jdbcUrl, cloned);

      return connect;
   }

   @Override
   public PrintWriter getLogWriter() throws SQLException
   {
      throw new SQLFeatureNotSupportedException();
   }

   @Override
   public void setLogWriter(PrintWriter logWriter) throws SQLException
   {
      throw new SQLFeatureNotSupportedException();
   }

   @Override
   public void setLoginTimeout(int seconds) throws SQLException
   {
      DriverManager.setLoginTimeout(seconds);
   }

   @Override
   public int getLoginTimeout() throws SQLException
   {
      return DriverManager.getLoginTimeout();
   }

   @Override
   public java.util.logging.Logger getParentLogger() throws SQLFeatureNotSupportedException
   {
      return driver.getParentLogger();
   }

   @Override
   public <T> T unwrap(Class<T> iface) throws SQLException
   {
      throw new SQLFeatureNotSupportedException();
   }

   @Override
   public boolean isWrapperFor(Class<?> iface) throws SQLException
   {
      return false;
   }
}
