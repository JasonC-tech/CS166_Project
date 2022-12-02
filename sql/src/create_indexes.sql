DROP INDEX IF EXISTS user_indx;
DROP INDEX IF EXISTS userType_indx;
DROP INDEX IF EXISTS orders_indx;
DROP INDEX IF EXISTS product_indx;
DROP INDEX IF EXISTS productUpdates_indx;
DROP INDEX IF EXISTS productSRequests_indx;

CREATE INDEX user_indx
ON Users
USING btree (userID);

CREATE INDEX userType_indx
ON Users
USING btree (type);

CREATE INDEX orders_indx
ON Orders
USING btree (orderNumber);

CREATE INDEX product_indx
ON Product
USING btree (storeID);

CREATE INDEX productUpdates_indx
ON ProductUpdates
USING btree (updateNumber);

CREATE INDEX productSRequests_indx
ON ProductSupplyRequests
USING btree (requestNumber);