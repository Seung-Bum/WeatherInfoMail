--ALTER USER user2 DEFAULT TABLESPACE USERS QUOTA UNLIMITED ON USERS;

CREATE TABLE MEMBER
(
	NO NUMBER(4) NOT NULL,
	name VARCHAR2(10),
	email VARCHAR2(100),
	password VARCHAR2(15),
	registDate DATE
);

CREATE TABLE BOARD
(
	NO NUMBER(4) NOT NULL,
	title VARCHAR2(50),
	content VARCHAR2(4000),
	viewCount NUMBER(4),
	createDate DATE
);

CREATE TABLE review (
	NO NUMBER NOT NULL PRIMARY KEY,
	board_no NUMBER NOT NULL,
	MEMBER_no NUMBER NOT NULL,
	review_content varchar(100)
);

ALTER TABLE BOARD ADD writer varchar2(100);
ALTER TABLE MEMBER ADD CONSTRAINT NO PRIMARY KEY (NO);
ALTER TABLE BOARD ADD PRIMARY KEY (NO);
ALTER TABLE review ADD CONSTRAINT board_no FOREIGN key(board_no) REFERENCES board (NO);
ALTER TABLE review ADD CONSTRAINT member_no FOREIGN key(member_no) REFERENCES member (NO);

--ALTER TABLE auto_test MODIFY id INT NOT NULL AUTO_INCREMENT;

--member
INSERT INTO MEMBER (NO, name, email, password, registDate)
VALUES(1, 'user1', 'user1@naver.com', '1234', to_date('2023-05-01 00:30:30', 'YYYY-MM-DD HH24:MI:SS'));
INSERT INTO MEMBER (NO, name, email, password, registDate)
VALUES(2, 'user2', 'user2@daum.net', '1234', sysdate);

-- board
INSERT INTO BOARD (NO, title, content, viewCount, createDate, writer)
VALUES(1, '�Խñ� ó���ø�', '�ȳ��ϼ��� �� ó�� �÷����׿�', 0, sysdate);
INSERT INTO BOARD (NO, title, content, viewCount, createDate)
VALUES(2, '�Խñ� �ø��ϴ�.', 'ȣȣȣ �ݰ����ϴ�.', 0, sysdate);

-- review
INSERT INTO review (NO, BOARD_NO, MEMBER_NO, REVIEW_CONTENT)
VALUES(1, 1, 1, '����� ���ܿ�');
INSERT INTO review (NO, BOARD_NO, MEMBER_NO, REVIEW_CONTENT)
VALUES(2, 1, 1, '����� �� ���ܿ�');
INSERT INTO review (NO, BOARD_NO, MEMBER_NO, REVIEW_CONTENT)
VALUES(3, 1, 2, '���� user2 �Դϴ�. �ݰ����ϴ�.');

UPDATE BOARD SET writer = 'user1@naver.com' WHERE NO = 1;
UPDATE BOARD SET writer = 'user2@daum.net' WHERE NO = 2;

ALTER TABLE	BOARD ADD photo_image blob;

COMMIT;

