package arami.adminWeb.support.service;

import java.util.List;

import arami.adminWeb.support.service.dto.request.SupportFeePayerBasicInfoRequest;
import arami.adminWeb.support.service.dto.request.SupportFeePayerDeleteRequest;
import arami.adminWeb.support.service.dto.request.SupportFeePayerListRequest;
import arami.adminWeb.support.service.dto.request.SupportFeePayerPaymentSaveRequest;
import arami.adminWeb.support.service.dto.request.SupportFeePayerRegisterRequest;
import arami.adminWeb.support.service.dto.response.SupportFeePayerBasicUpdateResponse;
import arami.adminWeb.support.service.dto.response.SupportFeePayerCalculateResponse;
import arami.adminWeb.support.service.dto.response.SupportFeePayerDeleteResponse;
import arami.adminWeb.support.service.dto.response.SupportFeePayerDetailDataResponse;
import arami.adminWeb.support.service.dto.response.SupportFeePayerListItemResponse;
import arami.adminWeb.support.service.dto.response.SupportFeePayerPaymentDetailDataResponse;
import arami.adminWeb.support.service.dto.response.SupportFeePayerPaymentSaveResponse;
import arami.adminWeb.support.service.dto.response.SupportFeePayerRegisterResponse;

public interface SupportFeePayerManageService {

    SupportFeePayerRegisterResponse register(SupportFeePayerRegisterRequest request, String chgUserId);

    SupportFeePayerCalculateResponse calculateCost(SupportFeePayerRegisterRequest request, String chgUserId);

    SupportFeePayerBasicUpdateResponse updateBasic(String itemId, SupportFeePayerBasicInfoRequest request, String chgUserId);

    List<SupportFeePayerListItemResponse> selectFeePayerList(SupportFeePayerListRequest request);

    SupportFeePayerDetailDataResponse selectFeePayerDetail(String itemId);

    SupportFeePayerPaymentDetailDataResponse selectFeePayerPaymentDetail(String itemId);

    SupportFeePayerPaymentSaveResponse saveFeePayerPayments(SupportFeePayerPaymentSaveRequest request, String chgUserId);

    SupportFeePayerDeleteResponse deleteFeePayerDetail(SupportFeePayerDeleteRequest request);
}
