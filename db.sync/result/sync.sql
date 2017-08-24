--BXT_MSG_SIMUL_EXEC_HIST
CREATE TABLE BXT_MSG_SIMUL_EXEC_HIST (
	 EVENT_UID VARCHAR(32) NOT NULL,
	 SIMUL_ID VARCHAR(100) NULL,
	 PROC_DTTM VARCHAR(23) NULL,
	 PROC_USER_ID VARCHAR(100) NULL,
	 PROC_STATUS_CD VARCHAR(1) NULL,
	 ERR_MSG VARCHAR(2000) NULL
);

CREATE UNIQUE INDEX PK_BXT_MSG_SIMUL_EXEC_HIST ON BXT_MSG_SIMUL_EXEC_HIST 
( 
	 EVENT_UID ASC
);

ALTER TABLE BXT_MSG_SIMUL_EXEC_HIST ADD CONSTRAINT PK_BXT_MSG_SIMUL_EXEC_HIST PRIMARY KEY USING INDEX PK_BXT_MSG_SIMUL_EXEC_HIST;

COMMENT ON TABLE BXT_MSG_SIMUL_EXEC_HIST IS '시뮬레이터 기동내역';
COMMENT ON COLUMN BXT_MSG_SIMUL_EXEC_HIST.EVENT_UID IS '이벤트UID';
COMMENT ON COLUMN BXT_MSG_SIMUL_EXEC_HIST.SIMUL_ID IS '시뮬레이터ID';
COMMENT ON COLUMN BXT_MSG_SIMUL_EXEC_HIST.PROC_DTTM IS '처리일시';
COMMENT ON COLUMN BXT_MSG_SIMUL_EXEC_HIST.PROC_USER_ID IS '처리사용자ID';
COMMENT ON COLUMN BXT_MSG_SIMUL_EXEC_HIST.PROC_STATUS_CD IS '처리상태코드';
COMMENT ON COLUMN BXT_MSG_SIMUL_EXEC_HIST.ERR_MSG IS '에러메시지';


--BXT_MSG_SIMUL
CREATE TABLE BXT_MSG_SIMUL (
	 SIMUL_ID VARCHAR(100) NOT NULL,
	 SIMUL_NM VARCHAR(100) NULL,
	 SIMUL_DESC VARCHAR(2000) NULL,
	 TRG_PRTCL_USE_YN VARCHAR(1) NULL,
	 TRG_PRTCL_ID VARCHAR(100) NULL,
	 TRG_PRTCL_CLCF_CD VARCHAR(2) NULL,
	 CHARSET VARCHAR(8) NULL,
	 MSG_TYPE_CD VARCHAR(1) NULL,
	 REQ_LAYOUT_USE_YN VARCHAR(1) NULL,
	 RESP_LAYOUT_USE_YN VARCHAR(1) NULL,
	 REQ_HDR_LAYOUT_ID VARCHAR(100) NULL,
	 RESP_HDR_LAYOUT_ID VARCHAR(100) NULL,
	 ECHO_USE_YN VARCHAR(1) NULL,
	 PRTCL_CONN_UID VARCHAR(32) NULL,
	 PROC_STATUS_CD VARCHAR(1) NULL,
	 ENV_VAR_ID VARCHAR(100) NULL,
	 REG_USER_ID VARCHAR(100) NULL,
	 REG_DTTM VARCHAR(23) NULL,
	 LAST_UPDT_USER_ID VARCHAR(100) NULL,
	 LAST_UPDT_DTTM VARCHAR(23) NULL
);

CREATE UNIQUE INDEX PK_BXT_MSG_SIMUL ON BXT_MSG_SIMUL 
( 
	 SIMUL_ID ASC
);

ALTER TABLE BXT_MSG_SIMUL ADD CONSTRAINT PK_BXT_MSG_SIMUL PRIMARY KEY USING INDEX PK_BXT_MSG_SIMUL;

COMMENT ON TABLE BXT_MSG_SIMUL IS '시뮬레이터';
COMMENT ON COLUMN BXT_MSG_SIMUL.SIMUL_ID IS '시뮬레이터ID';
COMMENT ON COLUMN BXT_MSG_SIMUL.SIMUL_NM IS '시뮬레이터명';
COMMENT ON COLUMN BXT_MSG_SIMUL.SIMUL_DESC IS '시뮬레이터설명';
COMMENT ON COLUMN BXT_MSG_SIMUL.TRG_PRTCL_USE_YN IS '대상프로토콜사용여부';
COMMENT ON COLUMN BXT_MSG_SIMUL.TRG_PRTCL_ID IS '대상프로토콜ID';
COMMENT ON COLUMN BXT_MSG_SIMUL.TRG_PRTCL_CLCF_CD IS '대상프로토콜구분코드';
COMMENT ON COLUMN BXT_MSG_SIMUL.CHARSET IS '캐릭터셋';
COMMENT ON COLUMN BXT_MSG_SIMUL.MSG_TYPE_CD IS '메시지타입코드';
COMMENT ON COLUMN BXT_MSG_SIMUL.REQ_LAYOUT_USE_YN IS '요청레이아웃사용여부';
COMMENT ON COLUMN BXT_MSG_SIMUL.RESP_LAYOUT_USE_YN IS '응답레이아웃사용여부';
COMMENT ON COLUMN BXT_MSG_SIMUL.REQ_HDR_LAYOUT_ID IS '요청헤더레이아웃ID';
COMMENT ON COLUMN BXT_MSG_SIMUL.RESP_HDR_LAYOUT_ID IS '응답헤더레이아웃ID';
COMMENT ON COLUMN BXT_MSG_SIMUL.ECHO_USE_YN IS '에코사용여부';
COMMENT ON COLUMN BXT_MSG_SIMUL.PRTCL_CONN_UID IS '프로토콜통신UID';
COMMENT ON COLUMN BXT_MSG_SIMUL.PROC_STATUS_CD IS '처리상태코드';
COMMENT ON COLUMN BXT_MSG_SIMUL.ENV_VAR_ID IS '환경변수ID';
COMMENT ON COLUMN BXT_MSG_SIMUL.REG_USER_ID IS '등록사용자ID';
COMMENT ON COLUMN BXT_MSG_SIMUL.REG_DTTM IS '등록일시';
COMMENT ON COLUMN BXT_MSG_SIMUL.LAST_UPDT_USER_ID IS '최종변경사용자ID';
COMMENT ON COLUMN BXT_MSG_SIMUL.LAST_UPDT_DTTM IS '최종변경일시';


--BXT_MSG_PRTCL
ALTER TABLE BXT_MSG_PRTCL DROP COLUMN SYS_DESC;
ALTER TABLE BXT_MSG_PRTCL DROP COLUMN SYS_CD;

--BXT_MSG_SIMUL_REQ_RESP_HIST
CREATE TABLE BXT_MSG_SIMUL_REQ_RESP_HIST (
	 REQ_TRX_UID VARCHAR(32) NOT NULL,
	 SIMUL_ID VARCHAR(100) NULL,
	 PROC_RESULT_CD VARCHAR(1) NULL,
	 TEST_ID VARCHAR(100) NULL,
	 REQ_MSG_RCV_DTTM VARCHAR(23) NULL,
	 REQ_MSG_DATA BYTEA NULL,
	 RESP_MSG_UID VARCHAR(32) NULL,
	 RESP_MSG_DATA BYTEA NULL,
	 ERR_MSG VARCHAR(2000) NULL
);

CREATE UNIQUE INDEX PK_BXT_MSG_SIMUL_REQ_RESP_HIST ON BXT_MSG_SIMUL_REQ_RESP_HIST 
( 
	 REQ_TRX_UID ASC
);

ALTER TABLE BXT_MSG_SIMUL_REQ_RESP_HIST ADD CONSTRAINT PK_BXT_MSG_SIMUL_REQ_RESP_HIST PRIMARY KEY USING INDEX PK_BXT_MSG_SIMUL_REQ_RESP_HIST;

COMMENT ON TABLE BXT_MSG_SIMUL_REQ_RESP_HIST IS '시뮬레이터 입출력내역';
COMMENT ON COLUMN BXT_MSG_SIMUL_REQ_RESP_HIST.REQ_TRX_UID IS '요청거래UID';
COMMENT ON COLUMN BXT_MSG_SIMUL_REQ_RESP_HIST.SIMUL_ID IS '시뮬레이터ID';
COMMENT ON COLUMN BXT_MSG_SIMUL_REQ_RESP_HIST.PROC_RESULT_CD IS '처리결과코드';
COMMENT ON COLUMN BXT_MSG_SIMUL_REQ_RESP_HIST.TEST_ID IS '테스트ID';
COMMENT ON COLUMN BXT_MSG_SIMUL_REQ_RESP_HIST.REQ_MSG_RCV_DTTM IS '요청전문수신일시';
COMMENT ON COLUMN BXT_MSG_SIMUL_REQ_RESP_HIST.REQ_MSG_DATA IS '요청전문데이터';
COMMENT ON COLUMN BXT_MSG_SIMUL_REQ_RESP_HIST.RESP_MSG_UID IS '응답전문UID';
COMMENT ON COLUMN BXT_MSG_SIMUL_REQ_RESP_HIST.RESP_MSG_DATA IS '응답전문데이터';
COMMENT ON COLUMN BXT_MSG_SIMUL_REQ_RESP_HIST.ERR_MSG IS '에러메시지';


--BXT_MSG_SIMUL_RESP_MSG
CREATE TABLE BXT_MSG_SIMUL_RESP_MSG (
	 RESP_MSG_UID VARCHAR(32) NOT NULL,
	 SIMUL_ID VARCHAR(100) NULL,
	 RESP_MSG_NM VARCHAR(100) NULL,
	 TEST_ID VARCHAR(100) NULL,
	 REQ_BODY_LAYOUT_ID VARCHAR(100) NULL,
	 RESP_BODY_LAYOUT_ID VARCHAR(100) NULL,
	 DEFT_RESP_MSG_YN VARCHAR(1) NULL,
	 PRIORITY NUMERIC(8) NULL,
	 HTTP_COND_USE_YN VARCHAR(1) NULL,
	 HTTP_EXTRA_URL VARCHAR(2000) NULL,
	 HTTP_METHOD VARCHAR(100) NULL,
	 COND_DATA BYTEA NULL,
	 RESP_MSG_VAL_DATA BYTEA NULL,
	 REG_USER_ID VARCHAR(100) NULL,
	 REG_DTTM VARCHAR(23) NULL,
	 LAST_UPDT_USER_ID VARCHAR(100) NULL,
	 LAST_UPDT_DTTM VARCHAR(23) NULL
);

CREATE UNIQUE INDEX PK_BXT_MSG_SIMUL_RESP_MSG ON BXT_MSG_SIMUL_RESP_MSG 
( 
	 RESP_MSG_UID ASC
);

ALTER TABLE BXT_MSG_SIMUL_RESP_MSG ADD CONSTRAINT PK_BXT_MSG_SIMUL_RESP_MSG PRIMARY KEY USING INDEX PK_BXT_MSG_SIMUL_RESP_MSG;

COMMENT ON TABLE BXT_MSG_SIMUL_RESP_MSG IS '시뮬레이터 응답전문';
COMMENT ON COLUMN BXT_MSG_SIMUL_RESP_MSG.RESP_MSG_UID IS '응답전문UID';
COMMENT ON COLUMN BXT_MSG_SIMUL_RESP_MSG.SIMUL_ID IS '시뮬레이터ID';
COMMENT ON COLUMN BXT_MSG_SIMUL_RESP_MSG.RESP_MSG_NM IS '응답전문명';
COMMENT ON COLUMN BXT_MSG_SIMUL_RESP_MSG.TEST_ID IS '테스트ID';
COMMENT ON COLUMN BXT_MSG_SIMUL_RESP_MSG.REQ_BODY_LAYOUT_ID IS '요청바디레이아웃ID';
COMMENT ON COLUMN BXT_MSG_SIMUL_RESP_MSG.RESP_BODY_LAYOUT_ID IS '응답바디레이아웃ID';
COMMENT ON COLUMN BXT_MSG_SIMUL_RESP_MSG.DEFT_RESP_MSG_YN IS '기본응답전문여부';
COMMENT ON COLUMN BXT_MSG_SIMUL_RESP_MSG.PRIORITY IS '우선순위';
COMMENT ON COLUMN BXT_MSG_SIMUL_RESP_MSG.HTTP_COND_USE_YN IS 'HTTP조건사용여부';
COMMENT ON COLUMN BXT_MSG_SIMUL_RESP_MSG.HTTP_EXTRA_URL IS 'HTTP추가URL';
COMMENT ON COLUMN BXT_MSG_SIMUL_RESP_MSG.HTTP_METHOD IS 'HTTP메소드';
COMMENT ON COLUMN BXT_MSG_SIMUL_RESP_MSG.COND_DATA IS '조건데이터';
COMMENT ON COLUMN BXT_MSG_SIMUL_RESP_MSG.RESP_MSG_VAL_DATA IS '응답전문값데이터';
COMMENT ON COLUMN BXT_MSG_SIMUL_RESP_MSG.REG_USER_ID IS '등록사용자ID';
COMMENT ON COLUMN BXT_MSG_SIMUL_RESP_MSG.REG_DTTM IS '등록일시';
COMMENT ON COLUMN BXT_MSG_SIMUL_RESP_MSG.LAST_UPDT_USER_ID IS '최종변경사용자ID';
COMMENT ON COLUMN BXT_MSG_SIMUL_RESP_MSG.LAST_UPDT_DTTM IS '최종변경일시';


--BXT_MSG_SYS_EXEC_STAT
ALTER TABLE BXT_MSG_SYS_EXEC_STAT ADD COLUMN SIMUL_ID VARCHAR(100) NOT NULL DEFAULT '';

--BXT_MSG_ROLE_PERM_REL
ALTER TABLE BXT_MSG_ROLE_PERM_REL ALTER COLUMN ROLE_ID TYPE VARCHAR(100), ALTER COLUMN ROLE_ID SET NOT NULL;
ALTER TABLE BXT_MSG_ROLE_PERM_REL ALTER COLUMN PERM_ID TYPE VARCHAR(100), ALTER COLUMN PERM_ID SET NOT NULL;

--BXT_MSG_PERM
ALTER TABLE BXT_MSG_PERM ALTER COLUMN PERM_ID TYPE VARCHAR(100), ALTER COLUMN PERM_ID SET NOT NULL;
ALTER TABLE BXT_MSG_PERM ALTER COLUMN DEFT_MENU_ID TYPE VARCHAR(100);

--BXT_MSG_ROLE
ALTER TABLE BXT_MSG_ROLE ALTER COLUMN ROLE_ID TYPE VARCHAR(100), ALTER COLUMN ROLE_ID SET NOT NULL;
ALTER TABLE BXT_MSG_ROLE ALTER COLUMN REG_USER_ID TYPE VARCHAR(100);
ALTER TABLE BXT_MSG_ROLE ALTER COLUMN LAST_UPDT_USER_ID TYPE VARCHAR(100);

--BXT_MSG_MENU_ROLE_REL
ALTER TABLE BXT_MSG_MENU_ROLE_REL ALTER COLUMN MENU_ID TYPE VARCHAR(100), ALTER COLUMN MENU_ID SET NOT NULL;
ALTER TABLE BXT_MSG_MENU_ROLE_REL ALTER COLUMN ROLE_ID TYPE VARCHAR(100), ALTER COLUMN ROLE_ID SET NOT NULL;

--BXT_MSG_MENU
ALTER TABLE BXT_MSG_MENU ALTER COLUMN MENU_ID TYPE VARCHAR(100), ALTER COLUMN MENU_ID SET NOT NULL;
ALTER TABLE BXT_MSG_MENU ALTER COLUMN PARENT_MENU_ID TYPE VARCHAR(100);

--BXT_MSG_COMM_CD
ALTER TABLE BXT_MSG_COMM_CD ALTER COLUMN REG_USER_ID TYPE VARCHAR(100);
ALTER TABLE BXT_MSG_COMM_CD ALTER COLUMN LAST_UPDT_USER_ID TYPE VARCHAR(100);

--BXT_MSG_ENVVAR_RSVD
ALTER TABLE BXT_MSG_ENVVAR_RSVD ALTER COLUMN REG_USER_ID TYPE VARCHAR(100);
ALTER TABLE BXT_MSG_ENVVAR_RSVD ALTER COLUMN LAST_UPDT_USER_ID TYPE VARCHAR(100);

--BXT_MSG_LAYOUT
ALTER TABLE BXT_MSG_LAYOUT ALTER COLUMN REG_USER_ID TYPE VARCHAR(100);
ALTER TABLE BXT_MSG_LAYOUT ALTER COLUMN LAST_UPDT_USER_ID TYPE VARCHAR(100);

--BXT_MSG_SVC_EXEC_STATS
ALTER TABLE BXT_MSG_SVC_EXEC_STATS ALTER COLUMN SVC_ID TYPE VARCHAR(100), ALTER COLUMN SVC_ID SET NOT NULL;
ALTER TABLE BXT_MSG_SVC_EXEC_STATS ALTER COLUMN SYS_ID TYPE VARCHAR(100);

--BXT_MSG_SVC_TC_EXEC_STATS
ALTER TABLE BXT_MSG_SVC_TC_EXEC_STATS ALTER COLUMN SVC_ID TYPE VARCHAR(100);
ALTER TABLE BXT_MSG_SVC_TC_EXEC_STATS ALTER COLUMN SYS_ID TYPE VARCHAR(100);

--BXT_MSG_TEST_INFO
ALTER TABLE BXT_MSG_TEST_INFO ADD COLUMN SND_PRTCL_ID VARCHAR(100);
ALTER TABLE BXT_MSG_TEST_INFO ADD COLUMN RCV_PRTCL_ID VARCHAR(100);
ALTER TABLE BXT_MSG_TEST_INFO DROP COLUMN SND_SYS_ID;
ALTER TABLE BXT_MSG_TEST_INFO DROP COLUMN RCV_SYS_ID;

--BXT_MSG_TC_INFO
ALTER TABLE BXT_MSG_TC_INFO ADD COLUMN COMP_PRTCL_ID VARCHAR(100);
ALTER TABLE BXT_MSG_TC_INFO DROP COLUMN COMP_SYS_ID;

--BXT_MSG_TC_EXEC_HIST
ALTER TABLE BXT_MSG_TC_EXEC_HIST ADD COLUMN PRTCL_ID VARCHAR(100);
ALTER TABLE BXT_MSG_TC_EXEC_HIST ADD COLUMN COMP_PRTCL_ID VARCHAR(100);
ALTER TABLE BXT_MSG_TC_EXEC_HIST DROP COLUMN SYS_ID;
ALTER TABLE BXT_MSG_TC_EXEC_HIST DROP COLUMN COMP_SYS_ID;

--BXT_MSG_BULK_EXEC_HIST
ALTER TABLE BXT_MSG_BULK_EXEC_HIST ALTER COLUMN PROC_USER_ID TYPE VARCHAR(100);

--BXT_MSG_TEST_SCNR_EXEC_HIST
ALTER TABLE BXT_MSG_TEST_SCNR_EXEC_HIST ALTER COLUMN EXEC_USER_ID TYPE VARCHAR(100);

--BXT_MSG_FLOW_RES_EXEC_HIST
ALTER TABLE BXT_MSG_FLOW_RES_EXEC_HIST ALTER COLUMN REG_USER_ID TYPE VARCHAR(100);
