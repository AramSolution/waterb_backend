package arami.adminWeb.support.service;

import java.util.List;

import arami.adminWeb.support.service.dto.request.SupportFeePayerBasicInfoRequest;
import arami.adminWeb.support.service.dto.request.SupportFeePayerDeleteRequest;
import arami.adminWeb.support.service.dto.request.SupportFeePayerListRequest;
import arami.adminWeb.support.service.dto.request.SupportFeePayerUnpaidListRequest;
import arami.adminWeb.support.service.dto.request.SupportFeePayerPaymentDeleteRequest;
import arami.adminWeb.support.service.dto.request.SupportFeePayerPaymentSaveRequest;
import arami.adminWeb.support.service.dto.request.SupportFeePayerRegisterRequest;
import arami.adminWeb.support.service.dto.response.SupportFeePayerBasicUpdateResponse;
import arami.adminWeb.support.service.dto.response.SupportFeePayerCalculateResponse;
import arami.adminWeb.support.service.dto.response.SupportFeePayerDeleteResponse;
import arami.adminWeb.support.service.dto.response.SupportFeePayerDetailDataResponse;
import arami.adminWeb.support.service.dto.response.SupportFeePayerExcelListResponse;
import arami.adminWeb.support.service.dto.response.SupportFeePayerListResponse;
import arami.adminWeb.support.service.dto.response.SupportFeePayerUnpaidListResponse;
import arami.adminWeb.support.service.dto.response.SupportFeePayerPaymentDetailDataResponse;
import arami.adminWeb.support.service.dto.response.SupportFeePayerPaymentSaveResponse;
import arami.adminWeb.support.service.dto.response.SupportFeePayerRegisterResponse;

public interface SupportFeePayerManageService {

    SupportFeePayerRegisterResponse register(SupportFeePayerRegisterRequest request, String chgUserId);

    SupportFeePayerCalculateResponse calculateCost(SupportFeePayerRegisterRequest request, String chgUserId);

    SupportFeePayerBasicUpdateResponse updateBasic(String itemId, SupportFeePayerBasicInfoRequest request, String chgUserId);

    SupportFeePayerListResponse selectFeePayerList(SupportFeePayerListRequest request);

    /**
     * 미납(PAY_STA=01) 건만 목록 조회.
     * {@code baseMonth}에 해당하는 연도 전체 통지일({@code REQ_DATE}) 기준.
     */
    SupportFeePayerUnpaidListResponse selectFeePayerUnpaidList(SupportFeePayerUnpaidListRequest request);

    SupportFeePayerExcelListResponse selectFeePayerExcelList(SupportFeePayerListRequest request);

    SupportFeePayerDetailDataResponse selectFeePayerDetail(String itemId);

    SupportFeePayerPaymentDetailDataResponse selectFeePayerPaymentDetail(String itemId);

    SupportFeePayerPaymentSaveResponse saveFeePayerPayments(SupportFeePayerPaymentSaveRequest request, String chgUserId);

    SupportFeePayerDeleteResponse deleteFeePayerPayment(SupportFeePayerPaymentDeleteRequest request, String chgUserId);

    SupportFeePayerDeleteResponse deleteFeePayerDetail(SupportFeePayerDeleteRequest request, String chgUserId);
}
