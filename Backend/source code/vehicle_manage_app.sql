-- phpMyAdmin SQL Dump
-- version 5.0.2
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: May 05, 2022 at 01:08 PM
-- Server version: 10.4.14-MariaDB
-- PHP Version: 7.3.27

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `vehicle_manage_app`
--

-- --------------------------------------------------------

--
-- Table structure for table `accident_detail`
--

CREATE TABLE `accident_detail` (
  `accident_id` bigint(20) NOT NULL,
  `vehicle_id` int(11) NOT NULL,
  `accident_date` datetime NOT NULL,
  `accident_time` time NOT NULL,
  `accident_driver_name` varchar(100) NOT NULL,
  `accident_amount` double(13,2) NOT NULL,
  `accident_km_reading` double(12,2) NOT NULL,
  `accident_description` text NOT NULL,
  `date_create` datetime NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `expense_detail`
--

CREATE TABLE `expense_detail` (
  `expense_detail_id` int(11) NOT NULL,
  `expense_detail_type` varchar(20) NOT NULL,
  `expense_detail_amount` double(13,2) NOT NULL,
  `expense_detail_km_reading` double(12,2) NOT NULL,
  `expense_detail_description` text NOT NULL,
  `vehicle_id` int(11) NOT NULL,
  `date_create` datetime NOT NULL DEFAULT current_timestamp(),
  `expense_date` datetime NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `insurance_detail`
--

CREATE TABLE `insurance_detail` (
  `insurance_id` int(11) NOT NULL,
  `insurance_company` varchar(250) NOT NULL,
  `insurance_policy_type` varchar(100) NOT NULL,
  `insurance_policy_no` varchar(100) NOT NULL,
  `insurance_issue_date` datetime NOT NULL,
  `insurance_expiry_date` datetime NOT NULL,
  `insurance_payment_mode` varchar(10) NOT NULL,
  `insurance_amount` double(13,2) NOT NULL,
  `insurance_premium` double(13,2) NOT NULL,
  `insurance_agent_name` varchar(150) NOT NULL,
  `insurance_agent_phone` varchar(50) NOT NULL,
  `insurance_description` text NOT NULL,
  `vehicle_id` int(11) NOT NULL,
  `date_create` datetime NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `permit_detail`
--

CREATE TABLE `permit_detail` (
  `permit_id` bigint(20) NOT NULL,
  `permit_type` varchar(150) CHARACTER SET utf8 NOT NULL,
  `permit_issue_date` datetime NOT NULL,
  `permit_expiry_date` datetime NOT NULL,
  `permit_no` varchar(50) NOT NULL,
  `permit_cost` double(13,2) NOT NULL,
  `permit_description` text NOT NULL,
  `vehicle_id` int(11) NOT NULL,
  `date_create` datetime NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `puc_detail`
--

CREATE TABLE `puc_detail` (
  `puc_id` bigint(20) NOT NULL,
  `puc_no` varchar(200) NOT NULL,
  `puc_issue_date` datetime NOT NULL,
  `puc_expiry_date` datetime NOT NULL,
  `puc_amount` double(13,2) NOT NULL,
  `puc_description` text NOT NULL,
  `vehicle_id` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `refuel_details`
--

CREATE TABLE `refuel_details` (
  `refuel_id` int(11) NOT NULL,
  `device_id` varchar(250) NOT NULL,
  `device_token` varchar(250) NOT NULL,
  `action_type` varchar(9) NOT NULL,
  `vehicle_id` int(10) NOT NULL,
  `refuel_date` datetime NOT NULL,
  `refuel_type` varchar(10) NOT NULL,
  `refuel_amount` double(13,2) NOT NULL,
  `refuel_fuel_price` double(12,2) NOT NULL,
  `refuel_quantity` double(12,2) NOT NULL,
  `refuel_station` varchar(250) NOT NULL,
  `refuel_km_reading` double(12,2) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `service_detail`
--

CREATE TABLE `service_detail` (
  `service_id` int(11) NOT NULL,
  `service_body` int(1) NOT NULL DEFAULT 0,
  `service_general` int(1) NOT NULL DEFAULT 0,
  `service_clutch` int(1) NOT NULL DEFAULT 0,
  `service_engine` int(1) NOT NULL DEFAULT 0,
  `service_oil_change` int(1) NOT NULL DEFAULT 0,
  `service_brakes` int(1) NOT NULL DEFAULT 0,
  `service_colling_systerm` int(1) NOT NULL DEFAULT 0,
  `service_sparkplug` int(1) DEFAULT 0,
  `service_other` int(1) NOT NULL DEFAULT 0,
  `service_battery` int(1) NOT NULL DEFAULT 0,
  `service_garrage_name` varchar(200) NOT NULL,
  `service_contact_no` bigint(20) NOT NULL,
  `service_amout` decimal(13,2) NOT NULL,
  `service_km_reading` double(12,2) NOT NULL,
  `service_description` text NOT NULL,
  `vehicle_id` int(11) NOT NULL,
  `service_date` datetime NOT NULL,
  `date_create` datetime NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `tbl_admin`
--

CREATE TABLE `tbl_admin` (
  `admin_id` int(11) NOT NULL,
  `email` varchar(124) NOT NULL,
  `username` varchar(64) NOT NULL,
  `password` varchar(255) NOT NULL,
  `firstname` varchar(64) NOT NULL,
  `lastname` varchar(64) NOT NULL,
  `access_level` tinyint(1) NOT NULL DEFAULT 1,
  `status` enum('0','1') NOT NULL DEFAULT '1'
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `tbl_admin`
--

INSERT INTO `tbl_admin` (`admin_id`, `email`, `username`, `password`, `firstname`, `lastname`, `access_level`, `status`) VALUES
(1, 'admin', 'admin', 'f865b53623b121fd34ee5426c792e5c33af8c227', 'Benzatine', 'Infotech', 1, '1');

-- --------------------------------------------------------

--
-- Table structure for table `vehicle_master`
--

CREATE TABLE `vehicle_master` (
  `Vehicle_id` int(11) NOT NULL,
  `Vehicle_type` varchar(100) NOT NULL,
  `vehicle_title` varchar(250) NOT NULL,
  `vehicle_brand` varchar(250) NOT NULL,
  `vehicle_model` varchar(100) NOT NULL,
  `vehicle_builde_year` int(4) NOT NULL,
  `vehicle_regi_no` varchar(100) NOT NULL,
  `vehicle_fuel_type` varchar(50) NOT NULL,
  `vehicle_tank_capacity` varchar(20) NOT NULL,
  `vehicle_disply_name` varchar(100) NOT NULL,
  `vehicle_purchase_date` datetime NOT NULL,
  `vehicle_purchase_price` decimal(13,2) NOT NULL,
  `vehicle_km_reading` double(12,2) NOT NULL,
  `device_id` varchar(200) NOT NULL,
  `email_id` varchar(200) NOT NULL,
  `device_token` text NOT NULL,
  `created_date` datetime NOT NULL DEFAULT current_timestamp(),
  `updated_date` datetime NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Indexes for dumped tables
--

--
-- Indexes for table `accident_detail`
--
ALTER TABLE `accident_detail`
  ADD PRIMARY KEY (`accident_id`),
  ADD KEY `accident_index_tbl` (`vehicle_id`);

--
-- Indexes for table `expense_detail`
--
ALTER TABLE `expense_detail`
  ADD PRIMARY KEY (`expense_detail_id`),
  ADD KEY `test123` (`vehicle_id`) COMMENT 'test123';

--
-- Indexes for table `insurance_detail`
--
ALTER TABLE `insurance_detail`
  ADD PRIMARY KEY (`insurance_id`),
  ADD KEY `vehicle_id` (`vehicle_id`) COMMENT 'insurance_index';

--
-- Indexes for table `permit_detail`
--
ALTER TABLE `permit_detail`
  ADD PRIMARY KEY (`permit_id`),
  ADD KEY `permit_index` (`vehicle_id`) COMMENT 'permit_index';

--
-- Indexes for table `puc_detail`
--
ALTER TABLE `puc_detail`
  ADD PRIMARY KEY (`puc_id`),
  ADD KEY `puc_index` (`vehicle_id`) COMMENT 'puc_index';

--
-- Indexes for table `refuel_details`
--
ALTER TABLE `refuel_details`
  ADD PRIMARY KEY (`refuel_id`),
  ADD KEY `vehicle_id` (`vehicle_id`);

--
-- Indexes for table `service_detail`
--
ALTER TABLE `service_detail`
  ADD PRIMARY KEY (`service_id`),
  ADD KEY `service_index` (`vehicle_id`) COMMENT 'service_index';

--
-- Indexes for table `tbl_admin`
--
ALTER TABLE `tbl_admin`
  ADD PRIMARY KEY (`admin_id`);

--
-- Indexes for table `vehicle_master`
--
ALTER TABLE `vehicle_master`
  ADD PRIMARY KEY (`Vehicle_id`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `accident_detail`
--
ALTER TABLE `accident_detail`
  MODIFY `accident_id` bigint(20) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `expense_detail`
--
ALTER TABLE `expense_detail`
  MODIFY `expense_detail_id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `insurance_detail`
--
ALTER TABLE `insurance_detail`
  MODIFY `insurance_id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `permit_detail`
--
ALTER TABLE `permit_detail`
  MODIFY `permit_id` bigint(20) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `puc_detail`
--
ALTER TABLE `puc_detail`
  MODIFY `puc_id` bigint(20) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `refuel_details`
--
ALTER TABLE `refuel_details`
  MODIFY `refuel_id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `service_detail`
--
ALTER TABLE `service_detail`
  MODIFY `service_id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `tbl_admin`
--
ALTER TABLE `tbl_admin`
  MODIFY `admin_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=2;

--
-- AUTO_INCREMENT for table `vehicle_master`
--
ALTER TABLE `vehicle_master`
  MODIFY `Vehicle_id` int(11) NOT NULL AUTO_INCREMENT;

--
-- Constraints for dumped tables
--

--
-- Constraints for table `accident_detail`
--
ALTER TABLE `accident_detail`
  ADD CONSTRAINT `accident_index_tbl1232` FOREIGN KEY (`vehicle_id`) REFERENCES `vehicle_master` (`Vehicle_id`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Constraints for table `expense_detail`
--
ALTER TABLE `expense_detail`
  ADD CONSTRAINT `fgdgdfg` FOREIGN KEY (`vehicle_id`) REFERENCES `vehicle_master` (`Vehicle_id`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Constraints for table `insurance_detail`
--
ALTER TABLE `insurance_detail`
  ADD CONSTRAINT `insurance_index` FOREIGN KEY (`vehicle_id`) REFERENCES `vehicle_master` (`Vehicle_id`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Constraints for table `permit_detail`
--
ALTER TABLE `permit_detail`
  ADD CONSTRAINT `permit_index` FOREIGN KEY (`vehicle_id`) REFERENCES `vehicle_master` (`Vehicle_id`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Constraints for table `puc_detail`
--
ALTER TABLE `puc_detail`
  ADD CONSTRAINT `puc_index` FOREIGN KEY (`vehicle_id`) REFERENCES `vehicle_master` (`Vehicle_id`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Constraints for table `refuel_details`
--
ALTER TABLE `refuel_details`
  ADD CONSTRAINT `refuel_details_ibfk_1` FOREIGN KEY (`vehicle_id`) REFERENCES `vehicle_master` (`Vehicle_id`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Constraints for table `service_detail`
--
ALTER TABLE `service_detail`
  ADD CONSTRAINT `service_index` FOREIGN KEY (`vehicle_id`) REFERENCES `vehicle_master` (`Vehicle_id`) ON DELETE CASCADE ON UPDATE CASCADE;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
