-- --------------------------------------------------------
-- Host:                         127.0.0.1
-- Server version:               8.orders4.3 - MySQL Community Server - GPL
-- Server OS:                    Win64
-- HeidiSQL Version:             12.8.0.6908
-- --------------------------------------------------------

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET NAMES utf8 */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;


-- Dumping database structure for noithat_db
CREATE DATABASE IF NOT EXISTS `noithat_db` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;
USE `noithat_db`;

-- Dumping structure for table noithat_db.categories
CREATE TABLE IF NOT EXISTS `categories` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  `description` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `image_url` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Dumping data for table noithat_db.categories: ~10 rows (approximately)
INSERT INTO `categories` (`id`, `name`, `description`, `image_url`) VALUES
	(1, 'Sofa', '', ''),
	(2, 'Bàn ghế', '', NULL),
	(3, 'Tủ kệ', '', NULL),
	(4, 'Đèn trang trí', '', ''),
	(5, 'Tủ lạnh', '', ''),
	(6, 'Tivi', '', ''),
	(7, 'Giường', '', ''),
	(8, 'Máy lạnh', '', ''),
	(9, 'Máy giặt', '', ''),
	(10, 'Tủ bếp', '', '');

-- Dumping structure for table noithat_db.orders
CREATE TABLE IF NOT EXISTS `orders` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint DEFAULT NULL,
  `total_price` decimal(15,2) NOT NULL,
  `address` text COLLATE utf8mb4_unicode_ci,
  `phone` varchar(15) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `status` enum('PENDING','CONFIRMED','SHIPPING','DELIVERED','CANCELLED') COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'PENDING',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  `cancelled_by_user` tinyint(1) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  KEY `fk_order_user` (`user_id`),
  CONSTRAINT `fk_order_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE SET NULL
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Dumping data for table noithat_db.orders: ~4 rows (approximately)
INSERT INTO `orders` (`id`, `user_id`, `total_price`, `address`, `phone`, `status`, `created_at`, `cancelled_by_user`) VALUES
	(1, 3, 6799000.00, '106/2A', '0784467113', 'DELIVERED', '2026-06-06 16:57:29', 0),
	(2, 3, 399000.00, '106/2a', '0784467113', 'CONFIRMED', '2026-06-06 16:58:30', 0),
	(3, 3, 699000.00, '106/2a', '0748867113', 'CANCELLED', '2026-06-06 16:58:50', 0),
	(4, 3, 999000.00, '106/2a', '0784467113', 'PENDING', '2026-06-06 17:00:22', 0);

-- Dumping structure for table noithat_db.order_details
CREATE TABLE IF NOT EXISTS `order_details` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `order_id` bigint DEFAULT NULL,
  `product_id` bigint DEFAULT NULL,
  `quantity` int NOT NULL,
  `price` decimal(15,2) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_od_order` (`order_id`),
  KEY `fk_od_product` (`product_id`),
  CONSTRAINT `fk_od_order` FOREIGN KEY (`order_id`) REFERENCES `orders` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_od_product` FOREIGN KEY (`product_id`) REFERENCES `products` (`id`) ON DELETE SET NULL
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Dumping data for table noithat_db.order_details: ~5 rows (approximately)
INSERT INTO `order_details` (`id`, `order_id`, `product_id`, `quantity`, `price`) VALUES
	(1, 1, 2, 1, 799000.00),
	(2, 1, 3, 1, 6000000.00),
	(3, 2, 12, 1, 399000.00),
	(4, 3, 4, 1, 699000.00),
	(5, 4, 30, 1, 999000.00);

-- Dumping structure for table noithat_db.products
CREATE TABLE IF NOT EXISTS `products` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(200) COLLATE utf8mb4_unicode_ci NOT NULL,
  `description` text COLLATE utf8mb4_unicode_ci,
  `price` decimal(15,2) NOT NULL,
  `stock` int DEFAULT NULL,
  `image_url` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `is_active` tinyint(1) DEFAULT '1',
  `category_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_product_category` (`category_id`),
  CONSTRAINT `fk_product_category` FOREIGN KEY (`category_id`) REFERENCES `categories` (`id`) ON DELETE SET NULL
) ENGINE=InnoDB AUTO_INCREMENT=31 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Dumping data for table noithat_db.products: ~30 rows (approximately)
INSERT INTO `products` (`id`, `name`, `description`, `price`, `stock`, `image_url`, `is_active`, `category_id`) VALUES
	(1, 'Sofa', 'êm và mềm', 499000.00, 1, 'https://sofaspectacular.co.uk/wp-content/uploads/2024/10/Expertly_Crafted_Comfort_in_Our_Luxury_Corner_Sofas.webp', 1, 1),
	(2, 'Sofa cao cấp', '', 799000.00, 4, 'https://i.pinimg.com/736x/25/81/7e/25817e463f6d87edf71b1f4c23d18817.jpg', 1, 1),
	(3, 'Sofa cao cấp cổ điển', '', 6000000.00, 5, 'https://hungphatsaigon.vn/wp-content/uploads/2023/09/sofa-tan-co-dien-cao-cap-da-bo-KW-888.jpg', 1, 1),
	(4, 'Sofa hình vỏ sò ', '', 699000.00, 12, 'https://kika.vn/wp-content/uploads/2022/08/sofa-don-SF40-1-300x300.jpg', 1, 1),
	(5, 'Bàn ghế phòng khách cao cấp', '', 1390000.00, 7, 'https://mocchat.vn/wp-content/uploads/2023/08/bo-sofa-ban-ghe-phong-khach-go-oc-cho-dep-hien-dai-cao-cap-chi-thu-ocenpark.jpg', 1, 2),
	(6, 'Bàn ghế ăn hiện đại', '', 599000.00, 5, 'https://noithatlacgia.vn/wp-content/uploads/2020/06/ban-ghe-an-go-dep-ba15-3.jpg', 1, 2),
	(7, 'Bàn ghế đá tự nhiên ', '', 699000.00, 8, 'https://damynghedps.vn/wp-content/uploads/2023/07/ban-ghe-da-san-vuon-7.jpg', 1, 2),
	(8, 'Tủ gỗ tự nhiên ', '', 5000000.00, 5, 'https://noithatthaibinh.com/wp-content/uploads/2022/02/kich-thuoc-tu-quan-ao-ap-tran-2110.jpg', 1, 3),
	(9, 'Tủ quần áo ', '', 1399000.00, 5, 'https://bandecor.vn/upload/data/tu-quan-ao-cua-lua-cl-537.jpg', 1, 3),
	(10, 'Tủ nhựa ', '', 499000.00, 5, 'https://daithanhfurniture.vn/uploads/details/2022/11/images/tu-quan-ao-nhua-co-guong-hien-dai.jpg', 1, 3),
	(11, 'Đèn trang trí nội thất cao cấp', '', 3999000.00, 5, 'https://hoangminhhome.vn/uploads/images/635762b11ddab15029243a0b/den-trang-tri-1.webp', 1, 4),
	(12, 'Đèn vuông ', '', 399000.00, 4, 'https://hoangminhhome.vn/uploads/images/638b17321ddab135d43bc393/%C4%90%C3%A8n-m%C3%A2m-%E1%BB%91p-tr%E1%BA%A7n-trang-tr%C3%AD-h%C3%ACnh-vu%C3%B4ng-MOLH-9073.jpg', 1, 4),
	(13, 'Đèn ống nghệ thuật', '', 400000.00, 6, 'https://denlongxua.com/image/cache/catalog/den-long/den-may-tre/den-may-tre-29052023-1200x750.jpg', 1, 4),
	(14, 'Đèn trang trí ', '', 299000.00, 1, 'https://jamhomedecor.com/wp-content/uploads/2019/11/Day-den-led-hinh-tron-3.jpg', 1, 4),
	(15, 'Tủ lạnh Samsung Inverter 208 lít ', '', 3999000.00, 0, 'https://cdn.tgdd.vn/Products/Images/1943/220320/samsung-rt20har8dbu-sv-2-700x467.jpg', 1, 5),
	(16, 'Tủ lạnh Xiaomi Mijia 430L 2026', '', 10199000.00, 4, 'https://caothienphat.com/wp-content/uploads/2026/02/Xiaomi-430L-2026-510x510.jpg', 1, 5),
	(17, 'Tủ lạnh mini Hisense HR05DB 45 lít', '', 2100000.00, 4, 'https://www.startpage.com/sp/afs/encrypted-tbn/tbn3/shopping?q=tbn:ANd9GcQGxMyTagsVUYuQh2syDLFuv3u8q4UMsSHs8EVa6IscIz6dw3ZHGRUVaZ1CgSULAcIqlmFMF5d6ZP2DCA6HDWAMtdRxZ1zyPi85pQA2AyCG_6KptQ2aN3c&usqp=CAs', 1, 5),
	(18, 'Tivi Xiaomi A Pro 2026 QLED HD 32 Inch L32MB-APSEA', '', 4100000.00, 3, 'https://cdn11.dienmaycholon.vn/filewebdmclnew/DMCL21/Picture//Apro/Apro_product_36440/google-tivi-xia_main_971_1020.png.webp', 1, 6),
	(19, 'Smart Tivi Hisense QLED 4K 43 Inch 43Q6Q', '', 8490000.00, 1, 'https://cdn11.dienmaycholon.vn/filewebdmclnew/DMCL21/Picture//Apro/Apro_product_36776/smart-tivi-hise_main_559_1020.png.webp', 1, 6),
	(20, 'Giường Ngủ Gỗ Tự Nhiên Cao Cấp Haigo GN224', '', 5390000.00, 3, 'https://api.togihome.vn/storage/images/originals/giuong-ngu-go-tu-nhien-cao-cap-haigo-gn224-22-offr8lr3wf0g5bx.webp', 1, 7),
	(21, 'Giường Ngủ Gỗ Tự Nhiên Cao Cấp Haigo GN032', '', 3199000.00, 4, 'https://api.togihome.vn/storage/images/originals/giuong-ngu-go-tu-nhien-cao-cap-gn032-14-4d41oyav6wr9eyb.webp', 1, 7),
	(22, 'Giường Ngủ Dream Series Togismart GM 052', '', 899000.00, 4, 'https://api.togihome.vn/storage/images/originals/giuong-ngu-dream-series-togismart-gm-052-7-dojqp0pnrv4ujli.webp', 1, 7),
	(23, 'Điều hòa LG DUALCOOL™Inverter AI Air 2 chiều ', '', 799000.00, 2, 'https://www.lg.com/content/dam/channel/wcms/vn/h_a/airsolution/rac/idh24m1/gallery/DZ_1.jpg/jcr:content/renditions/thum-1600x1062.jpeg?w=800', 1, 8),
	(24, 'Máy lạnh Nagakawa 1 HP NS-C09R2U86', '', 7999000.00, 0, 'https://dienmayhoanghai.vn/wp-content/uploads/2026/03/4-7.png', 1, 8),
	(25, 'Máy Lạnh Hitachi Inverter 1.5 Hp RAK/RAC-CH13PCASV', '', 13000000.00, 6, 'https://dienmayhoanghai.vn/wp-content/uploads/2025/10/may-lanh-hitachi-inverter-1-5-hp-rak-rac-ch13pcasv.jpg', 1, 8),
	(26, 'Máy Giặt Samsung Cửa Trước 9.5 kg WW95TA046AX/SV', '', 7770000.00, 2, 'https://down-vn.img.susercontent.com/file/vn-11134207-81ztc-mnb4kckimolc30.webp', 1, 9),
	(27, 'Máy Giặt Electrolux Inverter 9 Kg EWF9025DQWB', '', 650000.00, 3, 'https://dienmayhoanghai.vn/wp-content/uploads/2025/10/may-giat-electrolux-inverter-9-kg-ewf9025dqwb.jpg', 1, 9),
	(28, 'Máy Giặt Sharp 7.5 Kg ES-Y75HV-S', '', 900000.00, 3, 'https://dienmayhoanghai.vn/wp-content/uploads/2025/10/may-giat-sharp-7-5-kg-es-y75hv-s_7976.jpg', 1, 9),
	(29, 'Tủ Bếp Vincoplast Cao Cấp Trắng 03', '', 1290000.00, 1, 'https://noithatthanhnhan.net/thumbb/500x500/2/upload/sanpham/f5df722a260cd4528d1d-16186246435.jpg', 1, 10),
	(30, 'Tủ Bếp Acrylic 14', '', 999000.00, 5, 'https://noithatthanhnhan.net/thumbb/500x500/2/upload/sanpham/z4906824895504572e7b7829309e35be7e5c4fe8c30adf-17007068648.jpg', 1, 10);

-- Dumping structure for table noithat_db.users
CREATE TABLE IF NOT EXISTS `users` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `full_name` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  `email` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  `password` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `phone` varchar(15) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `address` text COLLATE utf8mb4_unicode_ci,
  `role` enum('USER','ADMIN') COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'USER',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `email` (`email`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Dumping data for table noithat_db.users: ~1 rows (approximately)
INSERT INTO `users` (`id`, `full_name`, `email`, `password`, `phone`, `address`, `role`, `created_at`) VALUES
	(1, 'Admin', 'admin@gmail.com', '123456', '0000000000', 'Admin', 'ADMIN', '2026-06-06 16:24:54'),
	(3, 'Giêng Phát Ninh', 'giengphatninh111@gmail.com', '123456', '', '', 'USER', '2026-06-06 16:56:58');

/*!40103 SET TIME_ZONE=IFNULL(@OLD_TIME_ZONE, 'system') */;
/*!40101 SET SQL_MODE=IFNULL(@OLD_SQL_MODE, '') */;
/*!40014 SET FOREIGN_KEY_CHECKS=IFNULL(@OLD_FOREIGN_KEY_CHECKS, 1) */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40111 SET SQL_NOTES=IFNULL(@OLD_SQL_NOTES, 1) */;
