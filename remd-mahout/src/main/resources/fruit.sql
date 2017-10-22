/*
Navicat MySQL Data Transfer

Source Server         : localhost
Source Server Version : 50709
Source Host           : localhost:3306
Source Database       : fruit

Target Server Type    : MYSQL
Target Server Version : 50709
File Encoding         : 65001

Date: 2016-03-08 12:00:00
*/

CREATE DATABASE `fruit` /*!40100 COLLATE 'latin1_swedish_ci' */;
/* 进入会话 "192.168.33.72" */
USE `fruit`;

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for songs
-- ----------------------------
DROP TABLE IF EXISTS `songs`;
CREATE TABLE `songs` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(50) DEFAULT '',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=41 DEFAULT CHARSET=utf8 COMMENT='物品';

-- ----------------------------
-- Records of songs
-- ----------------------------
INSERT INTO `songs` VALUES ('1', '一万个理由');
INSERT INTO `songs` VALUES ('2', '天使的翅膀');
INSERT INTO `songs` VALUES ('3', '童话');
INSERT INTO `songs` VALUES ('4', '我的中国心');
INSERT INTO `songs` VALUES ('5', '霸王别姬');
INSERT INTO `songs` VALUES ('6', '龙的传人');
INSERT INTO `songs` VALUES ('7', '大中国');
INSERT INTO `songs` VALUES ('8', '可惜不是你');
INSERT INTO `songs` VALUES ('9', '不再联系');
INSERT INTO `songs` VALUES ('10', '有一种爱叫做放手');
INSERT INTO `songs` VALUES ('11', '有没有人告诉你');
INSERT INTO `songs` VALUES ('12', '为什么相爱的人不能够在一起');
INSERT INTO `songs` VALUES ('13', '冬天里的一把火');
INSERT INTO `songs` VALUES ('14', 'high歌');
INSERT INTO `songs` VALUES ('15', '江南style');
INSERT INTO `songs` VALUES ('16', '李白');
INSERT INTO `songs` VALUES ('17', '遗憾');
INSERT INTO `songs` VALUES ('18', '该死的温柔');
INSERT INTO `songs` VALUES ('19', '一次就好');
INSERT INTO `songs` VALUES ('20', '小苹果');
INSERT INTO `songs` VALUES ('21', '当你老了');
INSERT INTO `songs` VALUES ('22', '回家的路');
INSERT INTO `songs` VALUES ('23', '这就是爱');
INSERT INTO `songs` VALUES ('24', '喜欢你');
INSERT INTO `songs` VALUES ('25', '匆匆那年');
INSERT INTO `songs` VALUES ('26', '最炫民族风');
INSERT INTO `songs` VALUES ('27', '自由飞翔');
INSERT INTO `songs` VALUES ('28', '花儿为什么这样红 ');
INSERT INTO `songs` VALUES ('29', '军港之夜 ');
INSERT INTO `songs` VALUES ('30', '红星照我去战斗 ');
INSERT INTO `songs` VALUES ('31', '翻身农奴把歌唱');
INSERT INTO `songs` VALUES ('32', '我是一个兵');
INSERT INTO `songs` VALUES ('33', '你是我心内的一首歌');
INSERT INTO `songs` VALUES ('34', '今天你要嫁给我 ');
INSERT INTO `songs` VALUES ('35', '花好月圆夜');
INSERT INTO `songs` VALUES ('36', '南山南 ');
INSERT INTO `songs` VALUES ('37', '去大理 ');
INSERT INTO `songs` VALUES ('38', '少年往事');
INSERT INTO `songs` VALUES ('39', '同桌的你');
INSERT INTO `songs` VALUES ('40', '火柴天堂');

-- ----------------------------
-- Table structure for song_user_score
-- ----------------------------
DROP TABLE IF EXISTS `song_user_score`;
CREATE TABLE `song_user_score` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `user_name` varchar(50) DEFAULT NULL,
  `song_id` int(11) DEFAULT NULL,
  `score` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  UNIQUE KEY `song_user_score_name_song_id_pk` (`user_name`,`song_id`)
) ENGINE=InnoDB AUTO_INCREMENT=162 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of song_user_score
-- ----------------------------
INSERT INTO `song_user_score` VALUES ('1', '冲天炮', '3', '5');
INSERT INTO `song_user_score` VALUES ('3', '冲天炮', '36', '5');
INSERT INTO `song_user_score` VALUES ('5', '冲天炮', '39', '5');
INSERT INTO `song_user_score` VALUES ('6', '冲天炮', '10', '5');
INSERT INTO `song_user_score` VALUES ('8', '冲天炮', '26', '5');
INSERT INTO `song_user_score` VALUES ('10', '冲天炮', '11', '5');
INSERT INTO `song_user_score` VALUES ('12', '冲天炮', '14', '5');
INSERT INTO `song_user_score` VALUES ('14', '冲天炮', '32', '5');
INSERT INTO `song_user_score` VALUES ('17', '杨晓伟', '17', '1');
INSERT INTO `song_user_score` VALUES ('18', '杨晓伟', '22', '2');
INSERT INTO `song_user_score` VALUES ('19', '杨晓伟', '38', '1');
INSERT INTO `song_user_score` VALUES ('20', '杨晓伟', '8', '4');
INSERT INTO `song_user_score` VALUES ('21', '杨晓伟', '26', '5');
INSERT INTO `song_user_score` VALUES ('22', '杨晓伟', '13', '4');
INSERT INTO `song_user_score` VALUES ('23', '杨晓伟', '29', '5');
INSERT INTO `song_user_score` VALUES ('24', '杨晓伟', '31', '1');
INSERT INTO `song_user_score` VALUES ('25', '胡师为', '35', '4');
INSERT INTO `song_user_score` VALUES ('26', '胡师为', '21', '5');
INSERT INTO `song_user_score` VALUES ('27', '胡师为', '5', '3');
INSERT INTO `song_user_score` VALUES ('28', '胡师为', '7', '2');
INSERT INTO `song_user_score` VALUES ('29', '胡师为', '8', '4');
INSERT INTO `song_user_score` VALUES ('30', '胡师为', '29', '3');
INSERT INTO `song_user_score` VALUES ('31', '胡师为', '30', '1');
INSERT INTO `song_user_score` VALUES ('32', '胡师为', '15', '1');
INSERT INTO `song_user_score` VALUES ('33', 'jellyabd', '1', '3');
INSERT INTO `song_user_score` VALUES ('34', 'jellyabd', '35', '4');
INSERT INTO `song_user_score` VALUES ('35', 'jellyabd', '39', '4');
INSERT INTO `song_user_score` VALUES ('36', 'jellyabd', '11', '3');
INSERT INTO `song_user_score` VALUES ('37', 'jellyabd', '29', '2');
INSERT INTO `song_user_score` VALUES ('38', 'jellyabd', '31', '1');
INSERT INTO `song_user_score` VALUES ('39', 'jellyabd', '16', '4');
INSERT INTO `song_user_score` VALUES ('40', 'jellyabd', '32', '2');
INSERT INTO `song_user_score` VALUES ('41', '金京一', '1', '4');
INSERT INTO `song_user_score` VALUES ('42', '金京一', '6', '4');
INSERT INTO `song_user_score` VALUES ('43', '金京一', '38', '1');
INSERT INTO `song_user_score` VALUES ('44', '金京一', '28', '1');
INSERT INTO `song_user_score` VALUES ('45', '金京一', '29', '1');
INSERT INTO `song_user_score` VALUES ('46', '金京一', '31', '1');
INSERT INTO `song_user_score` VALUES ('47', '金京一', '16', '1');
INSERT INTO `song_user_score` VALUES ('48', '金京一', '32', '4');
INSERT INTO `song_user_score` VALUES ('49', '王赫', '1', '3');
INSERT INTO `song_user_score` VALUES ('50', '王赫', '18', '3');
INSERT INTO `song_user_score` VALUES ('51', '王赫', '37', '4');
INSERT INTO `song_user_score` VALUES ('52', '王赫', '23', '4');
INSERT INTO `song_user_score` VALUES ('53', '王赫', '24', '5');
INSERT INTO `song_user_score` VALUES ('54', '王赫', '26', '4');
INSERT INTO `song_user_score` VALUES ('55', '王赫', '31', '4');
INSERT INTO `song_user_score` VALUES ('56', '王赫', '15', '3');
INSERT INTO `song_user_score` VALUES ('57', '你爸爸', '17', '2');
INSERT INTO `song_user_score` VALUES ('58', '你爸爸', '33', '4');
INSERT INTO `song_user_score` VALUES ('59', '你爸爸', '1', '2');
INSERT INTO `song_user_score` VALUES ('60', '你爸爸', '36', '4');
INSERT INTO `song_user_score` VALUES ('61', '你爸爸', '8', '4');
INSERT INTO `song_user_score` VALUES ('62', '你爸爸', '40', '4');
INSERT INTO `song_user_score` VALUES ('63', '你爸爸', '10', '4');
INSERT INTO `song_user_score` VALUES ('64', '你爸爸', '14', '4');
INSERT INTO `song_user_score` VALUES ('65', '肖广展', '1', '4');
INSERT INTO `song_user_score` VALUES ('66', '肖广展', '3', '4');
INSERT INTO `song_user_score` VALUES ('67', '肖广展', '5', '1');
INSERT INTO `song_user_score` VALUES ('68', '肖广展', '38', '1');
INSERT INTO `song_user_score` VALUES ('69', '肖广展', '23', '3');
INSERT INTO `song_user_score` VALUES ('70', '肖广展', '10', '5');
INSERT INTO `song_user_score` VALUES ('71', '肖广展', '12', '2');
INSERT INTO `song_user_score` VALUES ('72', '肖广展', '16', '5');
INSERT INTO `song_user_score` VALUES ('73', '田忠强', '34', '3');
INSERT INTO `song_user_score` VALUES ('74', '田忠强', '20', '5');
INSERT INTO `song_user_score` VALUES ('75', '田忠强', '36', '5');
INSERT INTO `song_user_score` VALUES ('76', '田忠强', '39', '1');
INSERT INTO `song_user_score` VALUES ('77', '田忠强', '10', '2');
INSERT INTO `song_user_score` VALUES ('78', '田忠强', '11', '2');
INSERT INTO `song_user_score` VALUES ('79', '田忠强', '13', '3');
INSERT INTO `song_user_score` VALUES ('80', '田忠强', '31', '1');
INSERT INTO `song_user_score` VALUES ('81', 'likai', '1', '1');
INSERT INTO `song_user_score` VALUES ('82', 'likai', '20', '1');
INSERT INTO `song_user_score` VALUES ('83', 'likai', '38', '1');
INSERT INTO `song_user_score` VALUES ('84', 'likai', '7', '1');
INSERT INTO `song_user_score` VALUES ('85', 'likai', '8', '2');
INSERT INTO `song_user_score` VALUES ('86', 'likai', '40', '2');
INSERT INTO `song_user_score` VALUES ('87', 'likai', '9', '2');
INSERT INTO `song_user_score` VALUES ('88', 'likai', '30', '1');
INSERT INTO `song_user_score` VALUES ('89', 'xsy', '1', '3');
INSERT INTO `song_user_score` VALUES ('90', 'xsy', '36', '4');
INSERT INTO `song_user_score` VALUES ('91', 'xsy', '39', '3');
INSERT INTO `song_user_score` VALUES ('92', 'xsy', '8', '4');
INSERT INTO `song_user_score` VALUES ('93', 'xsy', '26', '4');
INSERT INTO `song_user_score` VALUES ('94', 'xsy', '28', '3');
INSERT INTO `song_user_score` VALUES ('95', 'xsy', '12', '4');
INSERT INTO `song_user_score` VALUES ('96', 'xsy', '16', '4');
INSERT INTO `song_user_score` VALUES ('97', 'zsr', '3', '3');
INSERT INTO `song_user_score` VALUES ('98', 'zsr', '36', '4');
INSERT INTO `song_user_score` VALUES ('99', 'zsr', '38', '2');
INSERT INTO `song_user_score` VALUES ('100', 'zsr', '24', '4');
INSERT INTO `song_user_score` VALUES ('101', 'zsr', '11', '4');
INSERT INTO `song_user_score` VALUES ('102', 'zsr', '13', '1');
INSERT INTO `song_user_score` VALUES ('103', 'zsr', '30', '1');
INSERT INTO `song_user_score` VALUES ('104', 'zsr', '31', '1');
INSERT INTO `song_user_score` VALUES ('105', '杜战', '2', '5');
INSERT INTO `song_user_score` VALUES ('106', '杜战', '3', '5');
INSERT INTO `song_user_score` VALUES ('107', '杜战', '19', '3');
INSERT INTO `song_user_score` VALUES ('108', '杜战', '20', '2');
INSERT INTO `song_user_score` VALUES ('109', '杜战', '21', '5');
INSERT INTO `song_user_score` VALUES ('110', '杜战', '23', '4');
INSERT INTO `song_user_score` VALUES ('111', '杜战', '25', '5');
INSERT INTO `song_user_score` VALUES ('112', '杜战', '12', '5');
INSERT INTO `song_user_score` VALUES ('113', 'jiangyanhong', '33', '2');
INSERT INTO `song_user_score` VALUES ('114', 'jiangyanhong', '19', '4');
INSERT INTO `song_user_score` VALUES ('115', 'jiangyanhong', '22', '5');
INSERT INTO `song_user_score` VALUES ('116', 'jiangyanhong', '24', '4');
INSERT INTO `song_user_score` VALUES ('117', 'jiangyanhong', '9', '3');
INSERT INTO `song_user_score` VALUES ('118', 'jiangyanhong', '12', '2');
INSERT INTO `song_user_score` VALUES ('119', 'jiangyanhong', '30', '1');
INSERT INTO `song_user_score` VALUES ('120', 'jiangyanhong', '16', '3');
INSERT INTO `song_user_score` VALUES ('121', 'jx', '4', '1');
INSERT INTO `song_user_score` VALUES ('122', 'jx', '38', '1');
INSERT INTO `song_user_score` VALUES ('123', 'jx', '6', '2');
INSERT INTO `song_user_score` VALUES ('124', 'jx', '23', '3');
INSERT INTO `song_user_score` VALUES ('125', 'jx', '11', '3');
INSERT INTO `song_user_score` VALUES ('126', 'jx', '29', '1');
INSERT INTO `song_user_score` VALUES ('127', 'jx', '31', '1');
INSERT INTO `song_user_score` VALUES ('128', 'jx', '15', '3');
INSERT INTO `song_user_score` VALUES ('129', '狂龙', '35', '5');
INSERT INTO `song_user_score` VALUES ('130', '狂龙', '3', '3');
INSERT INTO `song_user_score` VALUES ('131', '狂龙', '38', '2');
INSERT INTO `song_user_score` VALUES ('132', '狂龙', '40', '1');
INSERT INTO `song_user_score` VALUES ('133', '狂龙', '24', '5');
INSERT INTO `song_user_score` VALUES ('134', '狂龙', '13', '5');
INSERT INTO `song_user_score` VALUES ('135', '狂龙', '14', '4');
INSERT INTO `song_user_score` VALUES ('136', '狂龙', '32', '3');
INSERT INTO `song_user_score` VALUES ('137', '张超', '33', '4');
INSERT INTO `song_user_score` VALUES ('138', '张超', '18', '2');
INSERT INTO `song_user_score` VALUES ('139', '张超', '2', '4');
INSERT INTO `song_user_score` VALUES ('140', '张超', '25', '4');
INSERT INTO `song_user_score` VALUES ('141', 'icic', '7', '4');
INSERT INTO `song_user_score` VALUES ('142', 'icic', '8', '4');
INSERT INTO `song_user_score` VALUES ('143', 'icic', '27', '4');
INSERT INTO `song_user_score` VALUES ('144', 'icic', '11', '3');
INSERT INTO `song_user_score` VALUES ('145', 'icic', '12', '3');
INSERT INTO `song_user_score` VALUES ('146', 'mx', '33', '3');
INSERT INTO `song_user_score` VALUES ('147', 'mx', '18', '4');
INSERT INTO `song_user_score` VALUES ('148', 'mx', '2', '4');
INSERT INTO `song_user_score` VALUES ('149', 'mx', '37', '5');
INSERT INTO `song_user_score` VALUES ('150', 'mx', '21', '3');
INSERT INTO `song_user_score` VALUES ('151', 'mx', '25', '4');
INSERT INTO `song_user_score` VALUES ('152', 'mx', '28', '3');
INSERT INTO `song_user_score` VALUES ('153', 'mx', '32', '2');
INSERT INTO `song_user_score` VALUES ('154', '郭恒', '33', '2');
INSERT INTO `song_user_score` VALUES ('155', '郭恒', '2', '2');
INSERT INTO `song_user_score` VALUES ('156', '郭恒', '34', '2');
INSERT INTO `song_user_score` VALUES ('157', '郭恒', '22', '3');
INSERT INTO `song_user_score` VALUES ('158', '郭恒', '26', '3');
INSERT INTO `song_user_score` VALUES ('159', '郭恒', '12', '3');
INSERT INTO `song_user_score` VALUES ('160', '郭恒', '28', '3');
INSERT INTO `song_user_score` VALUES ('161', '郭恒', '32', '3');

-- ----------------------------
-- Table structure for t_feature
-- ----------------------------
DROP TABLE IF EXISTS `t_feature`;
CREATE TABLE `t_feature` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `f_name` varchar(100) NOT NULL COMMENT '特征名称',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8 COMMENT='特征值明细';

-- ----------------------------
-- Records of t_feature
-- ----------------------------
INSERT INTO `t_feature` VALUES ('1', '男');
INSERT INTO `t_feature` VALUES ('2', '女');
INSERT INTO `t_feature` VALUES ('3', '80后');
INSERT INTO `t_feature` VALUES ('4', '90后');
INSERT INTO `t_feature` VALUES ('5', '奢饰品');
INSERT INTO `t_feature` VALUES ('6', '电子产品');
INSERT INTO `t_feature` VALUES ('7', '电子书');
INSERT INTO `t_feature` VALUES ('8', '10-100元');
INSERT INTO `t_feature` VALUES ('9', '100-1000元');
INSERT INTO `t_feature` VALUES ('10', '1000-10000元');

-- ----------------------------
-- Table structure for t_feature_item
-- ----------------------------
DROP TABLE IF EXISTS `t_feature_item`;
CREATE TABLE `t_feature_item` (
  `feature_id` int(11) DEFAULT NULL COMMENT '特征id',
  `item_id` int(11) DEFAULT NULL COMMENT '物品id',
  `weight` int(11) DEFAULT NULL COMMENT '权重',
  UNIQUE KEY `t_feature_item_feature_id_item_id` (`feature_id`,`item_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of t_feature_item
-- ----------------------------
INSERT INTO `t_feature_item` VALUES ('1', '1', '1');
INSERT INTO `t_feature_item` VALUES ('2', '1', '2');
INSERT INTO `t_feature_item` VALUES ('5', '1', '3');
INSERT INTO `t_feature_item` VALUES ('6', '1', '1');
INSERT INTO `t_feature_item` VALUES ('9', '1', '1');
INSERT INTO `t_feature_item` VALUES ('3', '1', '3');
INSERT INTO `t_feature_item` VALUES ('4', '1', '2');
INSERT INTO `t_feature_item` VALUES ('1', '2', '2');
INSERT INTO `t_feature_item` VALUES ('2', '2', '1');
INSERT INTO `t_feature_item` VALUES ('5', '2', '4');
INSERT INTO `t_feature_item` VALUES ('6', '2', '1');
INSERT INTO `t_feature_item` VALUES ('8', '2', '2');
INSERT INTO `t_feature_item` VALUES ('3', '2', '2');

-- ----------------------------
-- Table structure for t_item_filter
-- ----------------------------
DROP TABLE IF EXISTS `t_item_filter`;
CREATE TABLE `t_item_filter` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `item_id` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of t_item_filter
-- ----------------------------

-- ----------------------------
-- Table structure for t_user_feature
-- ----------------------------
DROP TABLE IF EXISTS `t_user_feature`;
CREATE TABLE `t_user_feature` (
  `u_id` int(11) DEFAULT NULL COMMENT '用户id',
  `feature_id` int(11) DEFAULT NULL COMMENT '特征id',
  `score` varchar(100) DEFAULT NULL COMMENT '用户特征值',
  UNIQUE KEY `t_user_feature_u_id_feature_id_pk` (`u_id`,`feature_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of t_user_feature
-- ----------------------------
INSERT INTO `t_user_feature` VALUES ('1', '1', '1');
INSERT INTO `t_user_feature` VALUES ('1', '3', '1');
INSERT INTO `t_user_feature` VALUES ('2', '2', '1');

-- ----------------------------
-- Table structure for t_user_item_top
-- ----------------------------
DROP TABLE IF EXISTS `t_user_item_top`;
CREATE TABLE `t_user_item_top` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `user_id` int(11) DEFAULT NULL,
  `item_id` int(11) DEFAULT NULL,
  `score` double DEFAULT '0',
  `time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of t_user_item_top
-- ----------------------------

-- ----------------------------
-- Table structure for t_user_item_top_offline
-- ----------------------------
DROP TABLE IF EXISTS `t_user_item_top_offline`;
CREATE TABLE `t_user_item_top_offline` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `user_id` int(11) DEFAULT NULL,
  `recommender` varchar(200) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of t_user_item_top_offline
-- ----------------------------

-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
  `userId` varchar(20) NOT NULL,
  `userName` varchar(20) DEFAULT NULL,
  `password` varchar(20) DEFAULT NULL,
  `sex` char(1) DEFAULT '0',
  `email` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`userId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of user
-- ----------------------------
INSERT INTO `user` VALUES ('1', 'lm', '1', '1', 'aaaaaa@126.com');
INSERT INTO `user` VALUES ('2', 'dlm', '1', '0', 'bbbbbb@qq.com');
