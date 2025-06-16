mysql -uroot -p123456 -s -e 'drop database trading_db'
mysql -uroot -p123456 -s -e 'create database trading_db'

mysql -uroot -p123456 trading_db < schema.sql 

mvn test -Dtest=com.example.trading.account.service.AccountServiceTest,\
com.example.trading.inventory.service.InventoryServiceTest,\
com.example.trading.order.service.OrderServiceTest


mvn test -Dtest=com.example.trading.account.controller.AccountControllerTest,\
com.example.trading.inventory.controller.InventoryControllerTest,\
com.example.trading.order.controller.OrderControllerTest,\
com.example.trading.settlement.controller.SettlementControllerTest


