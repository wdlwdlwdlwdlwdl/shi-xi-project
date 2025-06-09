DROP TABLE IF EXISTS `tc_user_pick_logistics`;
CREATE TABLE `tc_user_pick_logistics`  (
     `id` int  AUTO_INCREMENT  NOT NULL COMMENT 'id',
     `cust_id` bigint NOT NULL COMMENT '用户ID',
     `delivery_type` varchar(64)  NOT NULL COMMENT '物流方式',
     `gmt_create` datetime NOT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '创建时间',
     `gmt_modified` datetime NULL DEFAULT NULL COMMENT '更新时间',
     PRIMARY KEY (`id`) USING BTREE,
     INDEX `index_user_pick_id`(`cust_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 273 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;