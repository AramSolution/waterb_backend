-- =============================================================================
-- DBeaver test: dashboard month-over-month agg (same shape as selectPaymentMonthOverMonthAgg)
--
-- Usage:
--   1) Change @base_month below (format yyyy-MM).
--   2) If tables are not in default schema, replace ARTITEP / ARTITED with e.g. water.ARTITEP.
--
-- Payment count/amount: ARTITEP, PAY_DAY 구간, 금액은 SUM(PAY).
-- Unpaid: ARTITED PAY_STA='01', REQ_DATE.
-- =============================================================================

SET @base_month = '2026-05';

SELECT COALESCE((
        SELECT COUNT(1)
          FROM ARTITEP P1
         WHERE P1.PAY_DAY IS NOT NULL
           AND P1.PAY_DAY >= STR_TO_DATE(CONCAT(@base_month, '-01'), '%Y-%m-%d')
           AND P1.PAY_DAY < DATE_ADD(STR_TO_DATE(CONCAT(@base_month, '-01'), '%Y-%m-%d'), INTERVAL 1 MONTH)
       ), 0) AS CURR_PAY_CNT,
       COALESCE((
        SELECT COUNT(1)
          FROM ARTITEP P2
         WHERE P2.PAY_DAY IS NOT NULL
           AND P2.PAY_DAY >= DATE_SUB(STR_TO_DATE(CONCAT(@base_month, '-01'), '%Y-%m-%d'), INTERVAL 1 MONTH)
           AND P2.PAY_DAY < STR_TO_DATE(CONCAT(@base_month, '-01'), '%Y-%m-%d')
       ), 0) AS PREV_PAY_CNT,
       COALESCE((
        SELECT SUM(COALESCE(P3.PAY, 0))
          FROM ARTITEP P3
         WHERE P3.PAY_DAY IS NOT NULL
           AND P3.PAY_DAY >= STR_TO_DATE(CONCAT(@base_month, '-01'), '%Y-%m-%d')
           AND P3.PAY_DAY < DATE_ADD(STR_TO_DATE(CONCAT(@base_month, '-01'), '%Y-%m-%d'), INTERVAL 1 MONTH)
       ), 0) AS CURR_PAY_AMT,
       COALESCE((
        SELECT SUM(COALESCE(P4.PAY, 0))
          FROM ARTITEP P4
         WHERE P4.PAY_DAY IS NOT NULL
           AND P4.PAY_DAY >= DATE_SUB(STR_TO_DATE(CONCAT(@base_month, '-01'), '%Y-%m-%d'), INTERVAL 1 MONTH)
           AND P4.PAY_DAY < STR_TO_DATE(CONCAT(@base_month, '-01'), '%Y-%m-%d')
       ), 0) AS PREV_PAY_AMT,
       COALESCE((
        SELECT COUNT(1)
          FROM ARTITED U1
         WHERE U1.PAY_STA = '01'
           AND U1.REQ_DATE IS NOT NULL
           AND U1.REQ_DATE >= STR_TO_DATE(CONCAT(@base_month, '-01'), '%Y-%m-%d')
           AND U1.REQ_DATE < DATE_ADD(STR_TO_DATE(CONCAT(@base_month, '-01'), '%Y-%m-%d'), INTERVAL 1 MONTH)
       ), 0) AS CURR_UNPAID_CNT,
       COALESCE((
        SELECT COUNT(1)
          FROM ARTITED U2
         WHERE U2.PAY_STA = '01'
           AND U2.REQ_DATE IS NOT NULL
           AND U2.REQ_DATE >= DATE_SUB(STR_TO_DATE(CONCAT(@base_month, '-01'), '%Y-%m-%d'), INTERVAL 1 MONTH)
           AND U2.REQ_DATE < STR_TO_DATE(CONCAT(@base_month, '-01'), '%Y-%m-%d')
       ), 0) AS PREV_UNPAID_CNT,
       COALESCE((
        SELECT SUM(GREATEST(COALESCE(U3.WATER_COST, 0) - COALESCE(U3.WATER_PAY, 0), 0))
          FROM ARTITED U3
         WHERE U3.PAY_STA = '01'
           AND U3.REQ_DATE IS NOT NULL
           AND U3.REQ_DATE >= STR_TO_DATE(CONCAT(@base_month, '-01'), '%Y-%m-%d')
           AND U3.REQ_DATE < DATE_ADD(STR_TO_DATE(CONCAT(@base_month, '-01'), '%Y-%m-%d'), INTERVAL 1 MONTH)
       ), 0) AS CURR_UNPAID_AMT,
       COALESCE((
        SELECT SUM(GREATEST(COALESCE(U4.WATER_COST, 0) - COALESCE(U4.WATER_PAY, 0), 0))
          FROM ARTITED U4
         WHERE U4.PAY_STA = '01'
           AND U4.REQ_DATE IS NOT NULL
           AND U4.REQ_DATE >= DATE_SUB(STR_TO_DATE(CONCAT(@base_month, '-01'), '%Y-%m-%d'), INTERVAL 1 MONTH)
           AND U4.REQ_DATE < STR_TO_DATE(CONCAT(@base_month, '-01'), '%Y-%m-%d')
       ), 0) AS PREV_UNPAID_AMT
;

-- -----------------------------------------------------------------------------
-- Optional: compare with CURRENT app logic (payment by PAY_DAY only).
-- Run as a second statement after changing @base_month again if needed.
-- -----------------------------------------------------------------------------
/*
SELECT COALESCE((
        SELECT COUNT(1) FROM ARTITEP P1
         WHERE P1.PAY_DAY IS NOT NULL
           AND P1.PAY_DAY >= STR_TO_DATE(CONCAT(@base_month, '-01'), '%Y-%m-%d')
           AND P1.PAY_DAY < DATE_ADD(STR_TO_DATE(CONCAT(@base_month, '-01'), '%Y-%m-%d'), INTERVAL 1 MONTH)
       ), 0) AS CURR_PAY_CNT_PAYDAY,
       COALESCE((
        SELECT COUNT(1) FROM ARTITEP P2
         WHERE P2.PAY_DAY IS NOT NULL
           AND P2.PAY_DAY >= DATE_SUB(STR_TO_DATE(CONCAT(@base_month, '-01'), '%Y-%m-%d'), INTERVAL 1 MONTH)
           AND P2.PAY_DAY < STR_TO_DATE(CONCAT(@base_month, '-01'), '%Y-%m-%d')
       ), 0) AS PREV_PAY_CNT_PAYDAY,
       COALESCE((
        SELECT SUM(COALESCE(P3.PAY, 0)) FROM ARTITEP P3
         WHERE P3.PAY_DAY IS NOT NULL
           AND P3.PAY_DAY >= STR_TO_DATE(CONCAT(@base_month, '-01'), '%Y-%m-%d')
           AND P3.PAY_DAY < DATE_ADD(STR_TO_DATE(CONCAT(@base_month, '-01'), '%Y-%m-%d'), INTERVAL 1 MONTH)
       ), 0) AS CURR_PAY_AMT_PAYDAY,
       COALESCE((
        SELECT SUM(COALESCE(P4.PAY, 0)) FROM ARTITEP P4
         WHERE P4.PAY_DAY IS NOT NULL
           AND P4.PAY_DAY >= DATE_SUB(STR_TO_DATE(CONCAT(@base_month, '-01'), '%Y-%m-%d'), INTERVAL 1 MONTH)
           AND P4.PAY_DAY < STR_TO_DATE(CONCAT(@base_month, '-01'), '%Y-%m-%d')
       ), 0) AS PREV_PAY_AMT_PAYDAY
;
*/
