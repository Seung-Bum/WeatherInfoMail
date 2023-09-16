CREATE TABLE AIR_INFO
(
		ICAO_CODE	VARCHAR2(6),
		AIRPORT_NAME	VARCHAR2(10),
		TMFC		VARCHAR2(13),
		WD		VARCHAR2(4),
		WS		VARCHAR2(4),
		TA		VARCHAR2(4),
		QNH		VARCHAR2(5),
		CONSTRAINT AIR_INFO_PK PRIMARY KEY (ICAO_CODE, AIRPORT_NAME, TMFC)
);

COMMENT ON COLUMN AIR_INFO.ICAO_CODE IS 'ICAO �ڵ�, ���������� ICAO �ڵ�';
COMMENT ON COLUMN AIR_INFO.AIRPORT_NAME IS '���׸�';
COMMENT ON COLUMN AIR_INFO.TMFC IS '�����Ͻ�, ����Ͻú�(UTC) (yyyyMMddHHmm) - ��ǥ�Ͻ÷κ��� +3�ð����� ����';
COMMENT ON COLUMN AIR_INFO.WD IS '�ٶ��� ǳ��(��)';
COMMENT ON COLUMN AIR_INFO.WS IS 'ǳ��(��Ʈ, kt)';
COMMENT ON COLUMN AIR_INFO.TA IS '���� ���(��)';
COMMENT ON COLUMN AIR_INFO.QNH IS '���(0.01inHg)';

	MERGE INTO USER2.AIR_INFO A
	USING DUAL
    ON (A.ICAO_CODE = #{icaoCode})
    AND (A.AIRPORT_NAME = #{airportName})
    AND (A.TMFC = #{tmFc})
 	WHEN MATCHED THEN
 	  UPDATE
 	  	 SET A.TMFC = #{tmFc}
	WHEN NOT MATCHED THEN
      INSERT (
      	A.ICAO_CODE, 
      	A.AIRPORT_NAME,
      	A.TMFC,
      	A.WD,
      	A.WS,
      	A.TA,
      	A.QNH
      )
      VALUES (
      	#{icaoCode}, 
      	#{airportName},
      	#{tmFc},
      	#{wd},
      	#{ws},
      	#{ta},
      	#{qnh}
      )