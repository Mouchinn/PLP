--drop tables
DROP TABLE program_scheduled CASCADE CONSTRAINTS;
DROP TABLE Application CASCADE CONSTRAINTS;
DROP TABLE program_offered CASCADE CONSTRAINTS;
DROP TABLE users CASCADE CONSTRAINTS;

--drop sequences
DROP SEQUENCE scheduled_program_id;
DROP SEQUENCE application_id;

 
CREATE TABLE program_scheduled(
Scheduled_program_id VARCHAR2(5) PRIMARY KEY, 
ProgramName VARCHAR2(20), 
Location VARCHAR2(10), 
start_date DATE, 
end_date DATE,
sessions_per_week NUMBER);
 
CREATE TABLE Application(
Application_id VARCHAR2(30) PRIMARY KEY,
full_name VARCHAR(20), 
date_of_birth DATE, 
highest_qualification VARCHAR2(10), 
marks_obtained NUMBER, 
goals VARCHAR2(20), 
email_id VARCHAR2(20), 
Scheduled_program_id VARCHAR2(5) REFERENCES program_scheduled(Scheduled_program_id), 
status VARCHAR2(10) DEFAULT 'applied' CHECK ( status='applied' OR status='accepted' OR status='rejected' OR status='confirmed'),
Date_Of_Interview DATE);

CREATE TABLE program_offered(
ProgramName VARCHAR2(20),
description VARCHAR2(90),
applicant_eligibility VARCHAR2(60), 
duration NUMBER, 
degree_certificate_offered VARCHAR2(10)
);
 
 
CREATE TABLE users(
login_id VARCHAR2(5),
password VARCHAR2(10),
role VARCHAR2(5) CHECK(role='admin' OR role='mac')); 

--create sequences
CREATE SEQUENCE Scheduled_program_id increment by 1;
CREATE SEQUENCE application_id increment by 1;


--alter constraints

ALTER TABLE Application ADD CONSTRAINT user_applications CHECK (status in ('applied','accepted','rejected','confirmed'));
ALTER TABLE Users ADD CONSTRAINT user_check CHECK (role in ('admin','mac'));


--Insert statements

--PROGRAM_SCHEDULED
INSERT INTO Program_Scheduled values(501,'.NET',103,'13-JUN-2017','13-SEP-2017',6);
INSERT INTO Program_Scheduled values(502,'Core-Java',101,'13-JUN-2017','13-SEP-2017',5);
INSERT INTO Program_Scheduled values(503,'Database',104,'13-JUN-2017','13-SEP-2017',5);
INSERT INTO Program_Scheduled values(504,'Social-Analytics',105,'13-JUN-2017','13-SEP-2017',4);

--APPLICATION
Insert into Application values(1001,'Lucky Shobnani','21-NOV-1994','BE',99,'Java Developer','lucky@gmail.com',501,'accepted','20-MAR-2017');                          
Insert into Application values(1002,'Adithya','01-SEP-1993','BE',75,'Manager','adithya@gmail.com',503,'confirmed','21-APR-2017');
Insert into Application values(1003,'HemaLatha','13-FEB-1995','BE',79,'Team Leader','hema@ymail.com',502,'accepted','22-MAR-2017');
Insert into Application values(1004,'Deeksha','30-JULY-1994','BE',83,'Tester','deeksha@yahoo.com',501,'applied','12-MAY-2017');
Insert into Application values(1005,'Preetham','25-DEC-1995','BE',69,'Group Leader','preetham@gmail.com',503,'confirmed','21-APR-2017');
Insert into Application values(1006,'Sushma','11-MAR-1993','BE',86,'App Developer','sushma@gmail.com',502,'applied','12-MAY-2017');
Insert into Application values(1007,'Priyanka','13-OCT-1994','BE',59,'Java Devloper','priyanka@gmail.com',501,'rejected','12-MAY-2017');

--PROGRAM_OFFERED
Insert into Program_Offered values('Core Java','Basics of java','IT students/ professionals/ engineers',8,'OCPJP');
Insert into Program_Offered values('.NET','Basics of .NET','IT students/ professionals/ engineers',7,'MCTS'); 
Insert into Program_Offered values('SocialAnalytics',' Social and analytics helps organisations to improve customer satisfaction.','Graduates/ post-graduates/ MBA',9,'UCI');
Insert into Program_Offered values('Database','Learn concepts and application of database handling with SQL Server 2008.','Engineers/Students/Graduates/Anyone',7,'MCITP');
 
--USERS
Insert into  Users values('admin','admin','admin');
Insert into  Users values('mac','mac','mac');

 
commit;

