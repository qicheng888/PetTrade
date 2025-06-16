# 项目设计文档

## 模块划分

本项目按照领域驱动设计（DDD）的原则划分为以下几个模块：

1. **账户模块(account)**
   - 负责管理用户和商家的账户信息。
   - 功能包括账户充值、余额查询等。

2. **库存模块(inventory)**
   - 负责管理商品库存信息。
   - 功能包括添加库存、库存查询等。

3. **订单模块(order)**
   - 负责处理用户的订单。
   - 功能包括创建订单、订单状态更新等。
     - [OrderService](src/main/java/com/example/trading/order/service/OrderService.java): 核心业务逻辑层，负责具体的订单操作。包含的方法有：
       - `createOrder()`: 创建订单并扣减库存和账户余额。

4. **结算模块(settlement)**
   - 负责每日的销售结算。
   - 功能包括自动结算、结算记录查询等。

## 功能描述

- **账户充值**: 用户可以通过API向自己的预存账户充值。
- **添加库存**: 商家可以通过API添加商品数量到库存。
- **创建订单**: 用户可以下单购买商品，扣减账户余额和库存数量。
- **每日结算**: 商家每天自动进行结算，核对销售总额与实际收入。

## 创建数据库
```
mysql -uroot -p123456 -s -e "create database trading_db"
mysql -uroot -p123456  trading_db < schema.sql
```


## 执行单元测试

单元测试位于`src/test/java/com/example/trading`目录下，每个模块都有对应的测试类。

执行单元测试的方法如下：
1. 打开终端或命令行工具。
2. 导航至项目根目录。
3. 运行以下Maven命令：
```bash
mvn test -Dtest=com.example.trading.account.service.AccountServiceTest,\
com.example.trading.inventory.service.InventoryServiceTest,\
com.example.trading.order.service.OrderServiceTest


```

执行集成测试的方法如下：

```
mvn test -Dtest=com.example.trading.account.controller.AccountControllerTest,\
com.example.trading.inventory.controller.InventoryControllerTest,\
com.example.trading.order.controller.OrderControllerTest,\
com.example.trading.settlement.controller.SettlementControllerTest

```

这个命令会运行集成测试阶段定义的测试用例。

请注意，集成测试可能需要特定的环境配置，比如数据库连接字符串、用户名和密码等，这些配置应该在`application.yml`文件中正确设置。

此外，集成测试可能会涉及真实的数据操作，因此建议在一个隔离的测试环境中运行它们，以免影响生产数据。

以上就是本项目的设计文档概览。
