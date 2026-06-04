package arami.adminWeb.support.service;

import arami.adminWeb.support.service.dto.request.SupportDrainageEquipBasicInfoRequest;
import arami.adminWeb.support.service.dto.request.SupportDrainageEquipDeleteRequest;
import arami.adminWeb.support.service.dto.request.SupportDrainageEquipListRequest;
import arami.adminWeb.support.service.dto.request.SupportDrainageEquipPaymentDeleteRequest;
import arami.adminWeb.support.service.dto.request.SupportDrainageEquipPaymentSaveRequest;
import arami.adminWeb.support.service.dto.request.SupportDrainageEquipRegisterRequest;
import arami.adminWeb.support.service.dto.request.SupportDrainageEquipUnpaidListRequest;
import arami.adminWeb.support.service.dto.response.SupportDrainageEquipBasicUpdateResponse;
import arami.adminWeb.support.service.dto.response.SupportDrainageEquipDeleteResponse;
import arami.adminWeb.support.service.dto.response.SupportDrainageEquipDetailDataResponse;
import arami.adminWeb.support.service.dto.response.SupportDrainageEquipExcelListResponse;
import arami.adminWeb.support.service.dto.response.SupportDrainageEquipListResponse;
import arami.adminWeb.support.service.dto.response.SupportDrainageEquipPaymentDetailDataResponse;
import arami.adminWeb.support.service.dto.response.SupportDrainageEquipPaymentSaveResponse;
import arami.adminWeb.support.service.dto.response.SupportDrainageEquipRegisterResponse;
import arami.adminWeb.support.service.dto.response.SupportDrainageEquipUnpaidListResponse;

public interface SupportDrainageEquipManageService {

    SupportDrainageEquipRegisterResponse register(SupportDrainageEquipRegisterRequest request, String chgUserId);

    SupportDrainageEquipBasicUpdateResponse updateBasic(
            String itemId, SupportDrainageEquipBasicInfoRequest request, String chgUserId);

    SupportDrainageEquipListResponse selectDrainageEquipList(SupportDrainageEquipListRequest request);

    SupportDrainageEquipUnpaidListResponse selectDrainageEquipUnpaidList(
            SupportDrainageEquipUnpaidListRequest request);

    SupportDrainageEquipExcelListResponse selectDrainageEquipExcelList(SupportDrainageEquipListRequest request);

    SupportDrainageEquipDetailDataResponse selectDrainageEquipDetail(String itemId);

    SupportDrainageEquipPaymentDetailDataResponse selectDrainageEquipPaymentDetail(String itemId);

    SupportDrainageEquipPaymentSaveResponse saveDrainageEquipPayments(
            SupportDrainageEquipPaymentSaveRequest request, String chgUserId);

    SupportDrainageEquipDeleteResponse deleteDrainageEquipPayment(
            SupportDrainageEquipPaymentDeleteRequest request, String chgUserId);

    SupportDrainageEquipDeleteResponse deleteDrainageEquipDetail(
            SupportDrainageEquipDeleteRequest request, String chgUserId);
}
