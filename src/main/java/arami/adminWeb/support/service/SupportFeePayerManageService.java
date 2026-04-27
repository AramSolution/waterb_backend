package arami.adminWeb.support.service;

import java.util.List;

import arami.adminWeb.support.service.dto.request.SupportFeePayerBasicInfoRequest;
import arami.adminWeb.support.service.dto.request.SupportFeePayerListRequest;
import arami.adminWeb.support.service.dto.request.SupportFeePayerRegisterRequest;
import arami.adminWeb.support.service.dto.response.SupportFeePayerBasicUpdateResponse;
import arami.adminWeb.support.service.dto.response.SupportFeePayerCalculateResponse;
import arami.adminWeb.support.service.dto.response.SupportFeePayerDetailDataResponse;
import arami.adminWeb.support.service.dto.response.SupportFeePayerListItemResponse;
import arami.adminWeb.support.service.dto.response.SupportFeePayerRegisterResponse;

public interface SupportFeePayerManageService {

    SupportFeePayerRegisterResponse register(SupportFeePayerRegisterRequest request);

    SupportFeePayerCalculateResponse calculateCost(SupportFeePayerRegisterRequest request);

    SupportFeePayerBasicUpdateResponse updateBasic(String itemId, SupportFeePayerBasicInfoRequest request);

    List<SupportFeePayerListItemResponse> selectFeePayerList(SupportFeePayerListRequest request);

    SupportFeePayerDetailDataResponse selectFeePayerDetail(String itemId);
}
