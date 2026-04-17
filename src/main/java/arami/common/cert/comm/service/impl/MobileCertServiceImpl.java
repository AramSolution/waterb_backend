package arami.common.cert.comm.service.impl;

import arami.common.cert.comm.MobileCertDTO;
import arami.common.cert.comm.service.MobileCertDAO;
import arami.common.cert.comm.service.MobileCertService;
import jakarta.annotation.Resource;
import org.egovframe.rte.fdl.cmmn.EgovAbstractServiceImpl;
import org.springframework.stereotype.Service;

@Service("MobileCertService")
public class MobileCertServiceImpl extends EgovAbstractServiceImpl implements MobileCertService {

    @Resource(name = "mobileCertDAO")
    private MobileCertDAO mobileCertDAO;

    @Override
    public int insertMobileCert(MobileCertDTO mobileCertDTO) throws Exception {
        return mobileCertDAO.insertMobileCert(mobileCertDTO);
    }

}
