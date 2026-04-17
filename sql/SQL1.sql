select *
  from ARMUSER

show index from ARMUSER

select *
  from lettnemplyrinfo


			SELECT MBER_ID 				AS ID
			     , MBER_NM 				AS NAME
			     , PASSWORD 			AS PASSWORD
			     , IHIDNUM 				AS IHID_NUM
			     , MBER_EMAIL_ADRES 	AS EMAIL
			     , 'GNR' 				AS USER_SE
			     , '-' 					AS ORGNZT_ID
			     , ESNTL_ID 			AS UNIQ_ID
			  FROM ARMUSER A1
			 WHERE MBER_ID = #{id}
			   AND PASSWORD = #{password}
			   AND USER_SE = 'USR'
			   AND MBER_STTUS = 'P'

            SELECT A1.USER_ID 			AS ID
                 , A1.USER_NM 			AS NAME
                 , A1.PASSWORD 			AS PASSWORD
                 , A1.IHIDNUM 			AS IHID_NUM
                 , A1.EMAIL_ADRES 		AS EMAIL
                 , A1.USER_SE 	   	    AS USER_SE
                 , A1.ORGNZT_ID 		AS ORGNZT_ID
                 , A1.ESNTL_ID 			AS UNIQ_ID
                 , A1.GROUP_ID 			AS GROUP_ID
                 , B1.GROUP_NM 			AS GROUP_NM
              FROM ARMUSER A1 LEFT OUTER JOIN LETTNAUTHORGROUPINFO B1
			                   ON A1.GROUP_ID = B1.GROUP_ID
             WHERE A1.USER_SE = 'USR'
               AND A1.USER_ID = 'admin'
               AND A1.PASSWORD = 'NyJb8uzcAsrFpUn83GxshiISHAnfqfWKK5KV5QZBOJo='
               AND A1.MBER_STTUS = 'P'

SELECT A1.ROLE_CODE
     , A1.ROLE_TYPE
     , COALESCE(A1.ROLE_NAME  , '') AS ROLE_NAME
     , COALESCE(A1.HTTP_METHOD, '') AS HTTP_METHOD
     , A1.ROLE_PTTRN
  FROM ARMROLE_PTRN A1
 WHERE A1.STTUS_CODE = 'A'
 ORDER BY A1.SORT_NO

select '' AS ROLE
  from ARMROLE_PTRN
